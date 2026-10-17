package me.jacktym.aiomacro.features;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Discord;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Activity updates for the activity webhook: who joined or left a server, and who started or stopped the macro.
 */
public class ActivityTracker {

    private static boolean wasOnServer = false;
    private static String server = "";

    public ActivityTracker() {
        // Closing the game while on a server counts as leaving it
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (wasOnServer) {
                Discord.activity("🔴 **" + Discord.playerName() + "** left `" + server + "` (game closed)", Discord.RED);
                try {
                    Thread.sleep(1500); // give the message a moment to send
                } catch (InterruptedException ignored) {
                }
            }
        }, "AIOM Activity Shutdown"));
    }

    // Joining = a world appears; leaving = it goes away (switching islands/lobbies on the same server doesn't count)
    @SubscribeEvent
    public void tick(@NotNull TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        boolean onServer = Main.mc.theWorld != null && Main.mc.thePlayer != null;
        if (onServer && !wasOnServer) {
            server = Main.mc.getCurrentServerData() != null ? Main.mc.getCurrentServerData().serverIP : "singleplayer";
            Discord.activity("🟢 **" + Discord.playerName() + "** joined `" + server + "`", Discord.GREEN);
        } else if (!onServer && wasOnServer) {
            Discord.activity("🔴 **" + Discord.playerName() + "** left `" + server + "`", Discord.RED);
        }
        wasOnServer = onServer;
    }

    public static void macroStarted() {
        Discord.activity("▶️ **" + Discord.playerName() + "** started **" + RemoteConfig.macroName(AIOMVigilanceConfig.macroType) + "**", Discord.BLUE);
    }

    public static void macroStopped(long ranForMillis) {
        long minutes = ranForMillis / 60_000;
        String time = minutes >= 60 ? (minutes / 60) + "h " + (minutes % 60) + "m" : minutes + "m";
        Discord.activity("⏹️ **" + Discord.playerName() + "** stopped **" + RemoteConfig.macroName(AIOMVigilanceConfig.macroType) + "** after " + time, Discord.GREY);
    }
}
