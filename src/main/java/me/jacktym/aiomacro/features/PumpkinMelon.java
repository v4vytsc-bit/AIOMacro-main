package me.jacktym.aiomacro.features;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Pumpkin/Melon drop farm: one row per layer, any number of layers.
 * Strafe along the row into the drop chute at its end, fall to the next layer, then strafe back the other way.
 * Standing at a row end without dropping means the bottom of the farm was reached, so the macro stops.
 */
public class PumpkinMelon {

    public static boolean strafeLeft = false;

    private static double groundY = Double.NaN;
    private static int rowTicks = 0;
    private static int stillTicks = 0;
    private static int pauseTicks = 0;

    private static final Random random = new Random();

    public static void start() {
        strafeLeft = AIOMVigilanceConfig.pumpkinMelonFirstDirection == 0;
        groundY = Double.NaN;
        rowTicks = 0;
        stillTicks = 0;
        pauseTicks = 0;
    }

    @SubscribeEvent
    public void farmTick(@NotNull TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !MacroHandler.isMacroOn || !MacroHandler.isPumpkinMelon || !Main.notNull || AntiStuck.antistuckOn) {
            return;
        }
        if (!MacroHandler.waitForLook()) {
            return;
        }
        EntityPlayerSP player = Main.mcPlayer;

        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindAttack.getKeyCode(), true);

        // Landing more than 1.5 blocks lower means we fell down the chute onto the next layer
        if (player.onGround) {
            if (!Double.isNaN(groundY) && player.posY < groundY - 1.5) {
                strafeLeft = !strafeLeft;
                pauseTicks = 2 + random.nextInt(5);
                rowTicks = 0;
                stillTicks = 0;
                if (AIOMVigilanceConfig.setSpawnFailsafe) {
                    player.sendChatMessage("/setspawn");
                }
            }
            groundY = player.posY;
        }

        // Short pause after landing before strafing the other way
        if (pauseTicks > 0) {
            pauseTicks--;
            setStrafe(false, false);
            return;
        }

        setStrafe(strafeLeft, !strafeLeft);
        rowTicks++;

        boolean stopped = player.onGround && Math.abs(player.motionX) <= 0.001 && Math.abs(player.motionZ) <= 0.001;
        stillTicks = stopped ? stillTicks + 1 : 0;

        if (stillTicks >= 3 && rowTicks < 15 && AIOMVigilanceConfig.antiStuckFailsafe) {
            // Stopped almost immediately, something is in the way
            AntiStuck.antistuckOn = true;
            rowTicks = 0;
            stillTicks = 0;
        } else if (stillTicks >= 20) {
            // Stood at the end of a row for a second without dropping: no more layers below
            Main.sendMarkedChatMessage("Pumpkin/Melon: reached the bottom of the farm (no drop). Stopping.");
            MacroHandler.toggleMacro();
        }
    }

    private static void setStrafe(boolean left, boolean right) {
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindLeft.getKeyCode(), left);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), right);
    }
}
