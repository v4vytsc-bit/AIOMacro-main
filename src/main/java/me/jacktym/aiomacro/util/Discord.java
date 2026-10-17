package me.jacktym.aiomacro.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ScreenShotHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Sends messages to Discord, through the Discord bot (control channel) and/or the webhook.
 * Screenshots are taken of the Minecraft window only and attached directly to the message.
 * All network work happens on a background thread so the game never freezes.
 */
public class Discord {

    public static final String API = "https://discord.com/api/v10";
    private static final String USER_AGENT = "DiscordBot (https://github.com/JackTYM, 1.0)";
    private static final int EMBED_COLOR = 5814783;
    private static final String[] PINGS = {"", "@here", "@everyone"};

    private static final ExecutorService sender = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "AIOM Discord Sender");
        thread.setDaemon(true);
        return thread;
    });

    // Bot settings packed into the jar at build time (discord-bot.properties), so players don't have to set anything up
    private static final Properties BUILT_IN = loadBuiltIn();

    private static Properties loadBuiltIn() {
        Properties properties = new Properties();
        try (InputStream in = Discord.class.getResourceAsStream("/aiom-discord.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (Exception ignored) {
        }
        return properties;
    }

    public static boolean hasBuiltInBot() {
        return !isBlank(BUILT_IN.getProperty("token")) && !isBlank(BUILT_IN.getProperty("channel_id"));
    }

    // A token/channel typed into the settings wins; otherwise the one built into the jar is used
    public static String botToken() {
        return !isBlank(AIOMVigilanceConfig.discordBotToken) ? AIOMVigilanceConfig.discordBotToken.trim() : BUILT_IN.getProperty("token", "").trim();
    }

    public static String channelId() {
        return !isBlank(AIOMVigilanceConfig.discordChannelId) ? AIOMVigilanceConfig.discordChannelId.trim() : BUILT_IN.getProperty("channel_id", "").trim();
    }

    public static boolean botConfigured() {
        return AIOMVigilanceConfig.discordBotEnabled && !isBlank(botToken()) && !isBlank(channelId());
    }

    public static boolean webhookConfigured() {
        return AIOMVigilanceConfig.webhookAlerts && !isBlank(AIOMVigilanceConfig.webhookLink);
    }

    /**
     * Failsafe / status alert: goes to the bot's channel (if the bot sends alerts) and to the webhook (if set up),
     * with a screenshot of the game.
     */
    public static void alert(String title, String description) {
        boolean toBot = botConfigured() && AIOMVigilanceConfig.discordBotAlerts;
        boolean toWebhook = webhookConfigured();
        if (!toBot && !toWebhook) {
            return;
        }
        JsonObject embed = embed(title, description + "\n\nAccount: " + playerName());
        byte[] screenshot = captureScreenshot();
        sender.submit(() -> {
            if (toBot) {
                String ping = PINGS[Math.max(0, Math.min(PINGS.length - 1, AIOMVigilanceConfig.discordAlertPing))];
                logFailure("Discord bot", sendToChannel(ping, embed, screenshot));
            }
            if (toWebhook) {
                logFailure("Webhook", sendToWebhook(embed, screenshot));
            }
        });
    }

    // Test buttons: send in the background and say in chat whether it worked
    public static void testBot() {
        if (!botConfigured()) {
            Main.sendMarkedChatMessage(AIOMVigilanceConfig.discordBotEnabled
                    ? "Discord bot: this jar has no built-in bot. Fill in the Bot Token and Channel ID."
                    : "Discord bot: turn on Discord Bot Enabled first.");
            return;
        }
        JsonObject embed = embed("Test Message", "The AIOM Discord bot is connected.\nType !help here to see the commands.\n\nAccount: " + playerName());
        byte[] screenshot = captureScreenshot();
        sender.submit(() -> reportTest("Discord bot", sendToChannel("", embed, screenshot)));
    }

    public static void testWebhook() {
        if (isBlank(AIOMVigilanceConfig.webhookLink)) {
            Main.sendMarkedChatMessage("Webhook: paste your Webhook Link first.");
            return;
        }
        JsonObject embed = embed("Test Message | No Actions Done", "A webhook test was requested. No actions were taken.\n\nAccount: " + playerName());
        byte[] screenshot = captureScreenshot();
        sender.submit(() -> reportTest("Webhook", sendToWebhook(embed, screenshot)));
    }

    // Reply in the bot's channel from any thread, without waiting
    public static void replyAsync(String text, String replyTo) {
        sender.submit(() -> logFailure("Discord bot", sendToChannel("", embed("AIO-Macro", text), null, replyTo)));
    }

    public static JsonObject embed(String title, String description) {
        JsonObject embed = new JsonObject();
        embed.addProperty("title", title);
        embed.addProperty("description", description);
        embed.addProperty("color", EMBED_COLOR);
        JsonObject author = new JsonObject();
        author.addProperty("name", "AIO-Macro · " + playerName());
        embed.add("author", author);
        return embed;
    }

    /** Posts to the bot's control channel. Returns null on success, otherwise what went wrong. */
    public static String sendToChannel(String content, JsonObject embed, byte[] screenshot) {
        return sendToChannel(content, embed, screenshot, null);
    }

    /** Same, as a reply to the message with id replyTo (shows the command it answers). */
    public static String sendToChannel(String content, JsonObject embed, byte[] screenshot, String replyTo) {
        JsonObject payload = new JsonObject();
        if (replyTo != null) {
            JsonObject reference = new JsonObject();
            reference.addProperty("message_id", replyTo);
            reference.addProperty("fail_if_not_exists", false);
            payload.add("message_reference", reference);
        }
        payload.addProperty("content", content == null ? "" : content);
        JsonObject allowedMentions = new JsonObject();
        JsonArray parse = new JsonArray();
        if (content != null && (content.contains("@here") || content.contains("@everyone"))) {
            parse.add(new JsonParser().parse("\"everyone\""));
        }
        allowedMentions.add("parse", parse);
        payload.add("allowed_mentions", allowedMentions);
        String url = API + "/channels/" + channelId() + "/messages";
        return post(url, "Bot " + botToken(), withEmbed(payload, embed, screenshot), screenshot);
    }

    /** Posts to the webhook. Returns null on success, otherwise what went wrong. */
    public static String sendToWebhook(JsonObject embed, byte[] screenshot) {
        JsonObject payload = new JsonObject();
        return post(AIOMVigilanceConfig.webhookLink.trim(), null, withEmbed(payload, embed, screenshot), screenshot);
    }

    private static JsonObject withEmbed(JsonObject payload, JsonObject embed, byte[] screenshot) {
        if (embed != null) {
            JsonObject copy = new JsonParser().parse(embed.toString()).getAsJsonObject();
            if (screenshot != null) {
                JsonObject image = new JsonObject();
                image.addProperty("url", "attachment://screenshot.jpg");
                copy.add("image", image);
            }
            JsonArray embeds = new JsonArray();
            embeds.add(copy);
            payload.add("embeds", embeds);
        }
        return payload;
    }

    /** GET for the bot. Returns the response body, or throws with Discord's error. */
    public static String get(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestProperty("Authorization", "Bot " + botToken());
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(10_000);
        int code = connection.getResponseCode();
        String body = readBody(code < 400 ? connection.getInputStream() : connection.getErrorStream());
        if (code >= 400) {
            throw new DiscordException(code, body);
        }
        return body;
    }

    public static class DiscordException extends Exception {
        public final int code;
        public final String body;

        DiscordException(int code, String body) {
            super("HTTP " + code + ": " + body);
            this.code = code;
            this.body = body;
        }
    }

    // Sends the message as multipart/form-data so the screenshot can be attached directly
    private static String post(String url, String authorization, JsonObject payload, byte[] screenshot) {
        try {
            String boundary = "AIOM" + System.currentTimeMillis();
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(20_000);
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            if (authorization != null) {
                connection.setRequestProperty("Authorization", authorization);
            }

            try (OutputStream out = connection.getOutputStream()) {
                writePart(out, boundary, "Content-Disposition: form-data; name=\"payload_json\"\r\nContent-Type: application/json\r\n\r\n");
                out.write(payload.toString().getBytes(StandardCharsets.UTF_8));
                if (screenshot != null) {
                    writePart(out, boundary, "Content-Disposition: form-data; name=\"files[0]\"; filename=\"screenshot.jpg\"\r\nContent-Type: image/jpeg\r\n\r\n");
                    out.write(screenshot);
                }
                out.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
            }

            int code = connection.getResponseCode();
            if (code >= 400) {
                return "HTTP " + code + " " + readBody(connection.getErrorStream());
            }
            connection.getInputStream().close();
            return null;
        } catch (Exception e) {
            return e.getClass().getSimpleName() + ": " + e.getMessage();
        }
    }

    private static void writePart(OutputStream out, String boundary, String headers) throws Exception {
        out.write(("\r\n--" + boundary + "\r\n" + headers).getBytes(StandardCharsets.UTF_8));
    }

    private static String readBody(InputStream stream) {
        if (stream == null) {
            return "";
        }
        try (Scanner scanner = new Scanner(stream, "UTF-8")) {
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }
    }

    /**
     * Screenshot of the Minecraft window only (not the desktop), as a small JPEG.
     * Must run on the game thread, so from other threads it is handed to the game and waited for.
     */
    public static byte[] captureScreenshot() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.theWorld == null) {
                return null;
            }
            File file = mc.addScheduledTask(() -> {
                File dir = new File(System.getProperty("java.io.tmpdir"), "aiom-shots");
                //noinspection ResultOfMethodCallIgnored
                new File(dir, "screenshots").mkdirs();
                String name = "shot-" + System.nanoTime() + ".png";
                ScreenShotHelper.saveScreenshot(dir, name, mc.displayWidth, mc.displayHeight, mc.getFramebuffer());
                return new File(new File(dir, "screenshots"), name);
            }).get(5, TimeUnit.SECONDS);

            BufferedImage original = ImageIO.read(file);
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            if (original == null) {
                return null;
            }

            // Scale down to at most 1280 wide and save as JPEG to keep uploads small
            double scale = Math.min(1.0, 1280.0 / original.getWidth());
            int width = (int) (original.getWidth() * scale);
            int height = (int) (original.getHeight() * scale);
            BufferedImage small = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = small.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(original, 0, 0, width, height, null);
            graphics.dispose();

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ImageIO.write(small, "jpg", bytes);
            return bytes.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    // The logged-in account's name: also known when not on a server (e.g. after !leave)
    public static String playerName() {
        try {
            return Minecraft.getMinecraft().getSession().getUsername();
        } catch (Exception e) {
            return Main.mcPlayer != null ? Main.mcPlayer.getName() : "unknown";
        }
    }

    public static boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }

    // Chat messages must be shown on the game thread
    public static void chat(String message) {
        Minecraft.getMinecraft().addScheduledTask(() -> Main.sendMarkedChatMessage(message));
    }

    private static void reportTest(String what, String error) {
        chat(error == null ? what + ": test message sent!" : what + ": failed - " + shorten(error));
    }

    private static void logFailure(String what, String error) {
        if (error != null) {
            System.out.println("[AIOM] " + what + " send failed: " + error);
            if (AIOMVigilanceConfig.devmode) {
                chat(what + ": failed - " + shorten(error));
            }
        }
    }

    public static String shorten(String text) {
        return text.length() > 150 ? text.substring(0, 150) + "..." : text;
    }

    // Body of a Discord error, e.g. "Missing Access"
    public static String errorMessage(String body) {
        try {
            JsonElement json = new JsonParser().parse(body);
            return json.getAsJsonObject().get("message").getAsString();
        } catch (Exception e) {
            return shorten(body);
        }
    }
}
