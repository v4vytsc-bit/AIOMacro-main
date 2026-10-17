package me.jacktym.aiomacro.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.essential.vigilance.data.Property;
import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Discord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Discord bot control. Several players can share one bot and channel: every player's game reads the channel
 * (using the bot's token) and only acts on commands with its own Minecraft username, or "all".
 * Commands that just show information (!list, !status, !time) are answered by every player when no name is given.
 * Anyone who can type in the channel can use it; messages from bots are ignored, and only messages sent after
 * the game started are read.
 */
public class DiscordBot {

    private static final long DISCORD_EPOCH = 1420070400000L;

    private static final ScheduledExecutorService poller = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "AIOM Discord Bot");
        thread.setDaemon(true);
        return thread;
    });

    private static String watching = null;
    private static long lastMessageId = 0;
    private static long pausedUntil = 0;
    private static String lastProblem = null;

    private static final String HELP = "**Commands** - `<name>` is the player's Minecraft name, or `all`\n"
            + "`!list <name>` - online and what the macro is doing (`!list all` = everyone)\n"
            + "`!time <name>` - how long the macro has been running (`!time all` = everyone)\n"
            + "`!status <name>` - full status with a screenshot (`!status all` = one line each)\n"
            + "`!start <name>` - start the macro selected in Macro Type\n"
            + "`!stop <name>` - stop the macro (or any movement)\n"
            + "`!screenshot <name>` - screenshot of the game\n"
            + "`!move <name> <w|a|s|d> <blocks>` - walk, e.g. `!move Steve w 5` (macro must be stopped)\n"
            + "`!jump <name> [times]` - jump, e.g. `!jump Steve 3`\n"
            + "`!leave <name>` - stop the macro and leave the server\n"
            + "`!help` - this list";

    public DiscordBot() {
        poller.scheduleWithFixedDelay(DiscordBot::poll, 5, 3, TimeUnit.SECONDS);
    }

    private static void poll() {
        try {
            if (!Discord.botConfigured()) {
                watching = null;
                return;
            }
            if (System.currentTimeMillis() < pausedUntil) {
                return;
            }

            // New token/channel (or just switched on): only react to messages sent from now on
            String key = Discord.botToken() + "|" + Discord.channelId();
            if (!key.equals(watching)) {
                watching = key;
                lastMessageId = (System.currentTimeMillis() - DISCORD_EPOCH) << 22;
                lastProblem = null;
            }

            JsonArray messages = new JsonParser().parse(Discord.get(channelUrl() + "?limit=20&after=" + lastMessageId)).getAsJsonArray();

            // Oldest first, so commands run in the order they were sent
            List<JsonObject> sorted = new ArrayList<>();
            for (JsonElement element : messages) {
                sorted.add(element.getAsJsonObject());
            }
            sorted.sort((a, b) -> Long.compare(a.get("id").getAsLong(), b.get("id").getAsLong()));

            for (JsonObject message : sorted) {
                lastMessageId = Math.max(lastMessageId, message.get("id").getAsLong());
                JsonObject author = message.getAsJsonObject("author");
                if (author.has("bot") && author.get("bot").getAsBoolean()) {
                    continue;
                }
                String content = message.has("content") ? message.get("content").getAsString().trim() : "";
                if (content.startsWith("!")) {
                    handleCommand(content.split("\\s+"), author.get("username").getAsString(), message.get("id").getAsString());
                }
            }
            lastProblem = null;
        } catch (Discord.DiscordException e) {
            handleError(e);
        } catch (Exception e) {
            // Network hiccup: try again a bit later
            pausedUntil = System.currentTimeMillis() + 10_000;
        }
    }

    private static String channelUrl() {
        return Discord.API + "/channels/" + Discord.channelId() + "/messages";
    }

    private static void handleError(Discord.DiscordException e) {
        switch (e.code) {
            case 429: {
                // Too many requests (likely several players sharing the bot): wait as long as Discord says
                double retryAfter = 5;
                try {
                    retryAfter = new JsonParser().parse(e.body).getAsJsonObject().get("retry_after").getAsDouble();
                } catch (Exception ignored) {
                }
                pausedUntil = System.currentTimeMillis() + (long) (retryAfter * 1000) + 500;
                return;
            }
            case 401:
                reportOnce("Discord bot: the Bot Token is wrong. Reset it in the Developer Portal and paste it again.");
                break;
            case 403:
                reportOnce("Discord bot: no access to that channel. Give the bot View Channel + Read Message History there.");
                break;
            case 404:
                reportOnce("Discord bot: channel not found. Check the Channel ID.");
                break;
            default:
                reportOnce("Discord bot: error " + e.code + " - " + Discord.errorMessage(e.body));
        }
        pausedUntil = System.currentTimeMillis() + 60_000;
    }

    private static void reportOnce(String problem) {
        if (!problem.equals(lastProblem)) {
            lastProblem = problem;
            Discord.chat(problem);
        }
    }

    // ---------- Commands ----------

    private static void handleCommand(String[] words, String from, String messageId) throws Exception {
        String command = words[0].toLowerCase();
        String target = words.length >= 2 ? words[1] : null;

        switch (command) {
            // Every command needs a player name (or all)
            case "!list":
                if (needsName(target, "!list <name>", messageId) && isForMe(target)) {
                    reply(onGameThread(DiscordBot::listLine), null, messageId);
                }
                break;
            case "!time":
                if (needsName(target, "!time <name>", messageId) && isForMe(target)) {
                    reply(onGameThread(DiscordBot::timeLine), null, messageId);
                }
                break;
            case "!status":
                // "all" gives one line per player; a single name gives the full status with a screenshot
                if (needsName(target, "!status <name>", messageId) && isForMe(target)) {
                    if (isEveryone(target)) {
                        reply(onGameThread(DiscordBot::listLine), null, messageId);
                    } else {
                        reply(onGameThread(DiscordBot::statusText), Discord.captureScreenshot(), messageId);
                    }
                }
                break;
            case "!start":
                if (needsName(target, "!start <name>", messageId) && isForMe(target)) {
                    reply(onGameThread(() -> startMacro(from)), null, messageId);
                }
                break;
            case "!stop":
                if (needsName(target, "!stop <name>", messageId) && isForMe(target)) {
                    reply(onGameThread(() -> stopMacro(from)), null, messageId);
                }
                break;
            case "!screenshot":
                if (needsName(target, "!screenshot <name>", messageId) && isForMe(target)) {
                    byte[] screenshot = Discord.captureScreenshot();
                    reply(screenshot != null ? "📸 Screenshot" : "⚠️ Couldn't take a screenshot (not on a server?).", screenshot, messageId);
                }
                break;
            case "!leave":
                if (needsName(target, "!leave <name>", messageId) && isForMe(target)) {
                    reply(onGameThread(DiscordBot::stopAndLeave), null, messageId);
                }
                break;
            case "!move":
                if (needsName(target, "!move <name> <w|a|s|d> <blocks>", messageId) && isForMe(target)) {
                    handleMove(words, from, messageId);
                }
                break;
            case "!jump":
                if (needsName(target, "!jump <name> [times]", messageId) && isForMe(target)) {
                    handleJump(words, from, messageId);
                }
                break;

            case "!help":
                replyOnce(HELP, messageId);
                break;
            default:
                // Not one of ours, ignore it
        }
    }

    // "all" / "everyone" or this game's own Minecraft name (any capitalization)
    private static boolean isForMe(String target) {
        return isEveryone(target) || target.equalsIgnoreCase(Discord.playerName());
    }

    private static boolean isEveryone(String target) {
        return target.equalsIgnoreCase("all") || target.equalsIgnoreCase("everyone");
    }

    // Commands need a name; a single letter like "w" means the name was left out of !move
    private static boolean needsName(String target, String usage, String messageId) {
        if (target == null || (usage.startsWith("!move") && target.length() == 1 && "wasd".contains(target.toLowerCase()))) {
            replyOnce("Add the player's Minecraft name (or `all`): `" + usage + "`\nUse `!list all` to see who's online.", messageId);
            return false;
        }
        return true;
    }

    private static String startMacro(String from) {
        if (!Main.notNull) {
            return "⚠️ Not on a server, can't start the macro.";
        }
        if (MacroHandler.isMacroOn) {
            return "Macro is already running (" + macroName() + ").";
        }
        Main.sendMarkedChatMessage("Starting macro from Discord (" + from + ")");
        MacroHandler.toggleMacro();
        return MacroHandler.isMacroOn ? "✅ Macro started: " + macroName() : "⚠️ Couldn't start the macro.";
    }

    private static String stopMacro(String from) {
        if (RemoteMovement.isBusy()) {
            RemoteMovement.cancelAll("⛔ Movement cancelled.");
            if (!MacroHandler.isMacroOn) {
                return "⛔ Stopped moving.";
            }
        }
        if (!MacroHandler.isMacroOn) {
            return "Macro is already stopped.";
        }
        Main.sendMarkedChatMessage("Stopping macro from Discord (" + from + ")");
        MacroHandler.toggleMacro();
        return "⛔ Macro stopped.";
    }

    // !move <name> <w|a|s|d> <blocks>
    private static void handleMove(String[] words, String from, String messageId) throws Exception {
        if (words.length < 4 || words[2].length() != 1 || "wasd".indexOf(Character.toLowerCase(words[2].charAt(0))) < 0) {
            reply("Usage: `!move <name> <w|a|s|d> <blocks>`, e.g. `!move " + Discord.playerName() + " w 5`", null, messageId);
            return;
        }
        double blocks;
        try {
            blocks = Double.parseDouble(words[3].replace(',', '.'));
        } catch (NumberFormatException e) {
            reply("⚠️ `" + words[3] + "` isn't a number of blocks.", null, messageId);
            return;
        }
        if (blocks < 0.5 || blocks > 64) {
            reply("⚠️ Blocks must be between 0.5 and 64.", null, messageId);
            return;
        }
        char key = Character.toLowerCase(words[2].charAt(0));
        double distance = blocks;
        String problem = onGameThread(() -> {
            String reason = whyCantMove();
            if (reason == null) {
                Main.sendMarkedChatMessage("Moving " + key + " " + distance + " blocks from Discord (" + from + ")");
                RemoteMovement.enqueue(new RemoteMovement.Move(key, distance, result -> Discord.replyAsync(result, messageId)));
            }
            return reason;
        });
        if (problem != null) {
            reply(problem, null, messageId);
        }
    }

    // !jump <name> [times]
    private static void handleJump(String[] words, String from, String messageId) throws Exception {
        int times = 1;
        if (words.length >= 3) {
            try {
                times = Integer.parseInt(words[2]);
            } catch (NumberFormatException e) {
                reply("Usage: `!jump <name> [times]`, e.g. `!jump " + Discord.playerName() + " 3`", null, messageId);
                return;
            }
        }
        if (times < 1 || times > 20) {
            reply("⚠️ Jump between 1 and 20 times.", null, messageId);
            return;
        }
        int count = times;
        String problem = onGameThread(() -> {
            String reason = whyCantMove();
            if (reason == null) {
                Main.sendMarkedChatMessage("Jumping " + count + "x from Discord (" + from + ")");
                RemoteMovement.enqueue(new RemoteMovement.Jump(count, result -> Discord.replyAsync(result, messageId)));
            }
            return reason;
        });
        if (problem != null) {
            reply(problem, null, messageId);
        }
    }

    // Movement fights with a running macro, so it's only allowed while the macro is stopped
    private static String whyCantMove() {
        if (!Main.notNull) {
            return "⚠️ Not on a server.";
        }
        if (MacroHandler.isMacroOn) {
            return "⚠️ The macro is running. Use `!stop " + Discord.playerName() + "` first, then move.";
        }
        return null;
    }

    // !leave: stop everything, then disconnect the same way the pause menu's Disconnect button does
    private static String stopAndLeave() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) {
            return "Not on a server.";
        }
        RemoteMovement.cancelAll("⛔ Movement cancelled: leaving the server.");
        if (MacroHandler.isMacroOn) {
            MacroHandler.toggleMacro();
        }
        String where = mc.getCurrentServerData() != null ? mc.getCurrentServerData().serverIP : "the world";
        boolean singleplayer = mc.isIntegratedServerRunning();

        mc.theWorld.sendQuittingDisconnectingPacket();
        mc.loadWorld(null);
        mc.displayGuiScreen(singleplayer ? new GuiMainMenu() : new GuiMultiplayer(new GuiMainMenu()));
        return "🚪 Macro stopped and left " + where + ".";
    }

    // ---------- What the replies say ----------

    // One line per player for !list / !status without a name
    private static String listLine() {
        String name = "**" + Discord.playerName() + "**";
        if (!Main.notNull) {
            return "⚪ " + name + " - not on a server";
        }
        if (MacroHandler.isMacroOn) {
            return "🟢 " + name + " - " + macroName() + " for " + formatDuration(System.currentTimeMillis() - MacroHandler.macroStartMillis);
        }
        return "🔴 " + name + " - online, macro stopped";
    }

    private static String timeLine() {
        String name = "**" + Discord.playerName() + "**";
        String total = formatDuration(MacroHandler.totalRunMillis());
        if (MacroHandler.isMacroOn) {
            return "⏱️ " + name + " - running " + macroName() + " for " + formatDuration(System.currentTimeMillis() - MacroHandler.macroStartMillis)
                    + " (total since game start: " + total + ")";
        }
        return "⏱️ " + name + " - stopped (total since game start: " + total + ")";
    }

    private static String statusText() {
        StringBuilder status = new StringBuilder();
        status.append("**Player:** ").append(Discord.playerName()).append("\n");
        status.append("**Macro:** ").append(MacroHandler.isMacroOn ? "🟢 Running" : "🔴 Stopped").append("\n");
        status.append("**Type:** ").append(macroName()).append("\n");
        if (MacroHandler.isMacroOn) {
            status.append("**Running for:** ").append(formatDuration(System.currentTimeMillis() - MacroHandler.macroStartMillis)).append("\n");
        }
        status.append("**Total since game start:** ").append(formatDuration(MacroHandler.totalRunMillis())).append("\n");
        status.append("**On a server:** ").append(Main.notNull ? "Yes" : "No");
        return status.toString();
    }

    // ---------- Sending ----------

    private static void reply(String text, byte[] screenshot, String replyTo) {
        String error = Discord.sendToChannel("", Discord.embed("AIO-Macro", text), screenshot, replyTo);
        if (error != null) {
            System.out.println("[AIOM] Discord bot reply failed: " + error);
        }
    }

    /**
     * For messages only one player should send (help, usage hints): wait a moment (a different wait for each player),
     * then only reply if no other player's game has answered that command yet.
     */
    private static void replyOnce(String text, String commandId) {
        long delay = 300 + Math.floorMod(Discord.playerName().toLowerCase().hashCode(), 2700);
        poller.schedule(() -> {
            try {
                JsonArray later = new JsonParser().parse(Discord.get(channelUrl() + "?limit=50&after=" + commandId)).getAsJsonArray();
                for (JsonElement element : later) {
                    JsonObject message = element.getAsJsonObject();
                    JsonObject author = message.getAsJsonObject("author");
                    boolean fromBot = author.has("bot") && author.get("bot").getAsBoolean();
                    if (fromBot && message.has("message_reference")
                            && commandId.equals(message.getAsJsonObject("message_reference").get("message_id").getAsString())) {
                        return;
                    }
                }
            } catch (Exception ignored) {
                // Couldn't check: reply anyway
            }
            reply(text, null, commandId);
        }, delay, TimeUnit.MILLISECONDS);
    }

    // Anything touching the game has to run on the game thread
    private static <T> T onGameThread(Callable<T> task) throws Exception {
        return Minecraft.getMinecraft().addScheduledTask(task).get(5, TimeUnit.SECONDS);
    }

    private static String macroName() {
        try {
            String[] options = AIOMVigilanceConfig.class.getField("macroType").getAnnotation(Property.class).options();
            return options[AIOMVigilanceConfig.macroType];
        } catch (Exception e) {
            return "Macro " + AIOMVigilanceConfig.macroType;
        }
    }

    private static String formatDuration(long millis) {
        long minutes = millis / 60_000;
        return minutes >= 60 ? (minutes / 60) + "h " + (minutes % 60) + "m" : minutes + "m";
    }
}
