package me.jacktym.aiomacro.features;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Random;

public class SetPlayerLook {

    public static float yaw = AIOMVigilanceConfig.getYaw();

    public static float pitch = AIOMVigilanceConfig.getPitch();

    public static boolean toggled = false;

    // How close (in degrees) the current look has to be to the target to count as correct.
    private static final float LOOK_TOLERANCE = 0.01f;

    public static void setDefault() {
        yaw = AIOMVigilanceConfig.getYaw();

        pitch = AIOMVigilanceConfig.getPitch();
    }

    // Wraps any angle into the range (-180, 180].
    public static float wrapYaw(float yaw) {
        yaw %= 360f;
        if (yaw > 180f) {
            yaw -= 360f;
        } else if (yaw <= -180f) {
            yaw += 360f;
        }
        return yaw;
    }

    public static float fixStaticYaw(float yaw) {
        for (int i = 0; i < 10000; i++) {
            if (yaw >= 0) {
                if (yaw - i * 360 <= 180) {
                    yaw = Float.parseFloat(String.format("%.1f", yaw - i * 360));
                    if (yaw == -180) {
                        yaw = 180;
                    }
                    return yaw;
                }
            } else {
                if (yaw + i * 360 >= -180) {
                    yaw = Float.parseFloat(String.format("%.1f", yaw + i * 360));
                    if (yaw == -180) {
                        yaw = 180;
                    }
                    return yaw;
                }
            }
        }
        Main.sendMarkedChatMessage("Error With Fixing Yaw. yaw: " + yaw + ". yaw: " + Float.parseFloat(String.format("%.1f", yaw)) + ".");
        return 0;
    }

    public static boolean isYawLookCorrect() {
        return Math.abs(wrapYaw(yaw - Main.mcPlayer.rotationYaw)) <= LOOK_TOLERANCE;
    }

    public static boolean isPitchLookCorrect() {
        return Math.abs(pitch - Main.mcPlayer.rotationPitch) <= LOOK_TOLERANCE;
    }

    public static boolean isLookCorrect() {
        return isYawLookCorrect() && isPitchLookCorrect();
    }

    // Look Turn Order setting
    private static final int ORDER_TOGETHER = 0;
    private static final int ORDER_PITCH_FIRST = 1;
    private static final int ORDER_YAW_FIRST = 2;
    private static final int ORDER_RANDOM = 3;

    // The turn currently being carried out
    private static float plannedYaw = Float.NaN;
    private static float plannedPitch = Float.NaN;
    private static int turnOrder = ORDER_TOGETHER;
    private static int turnTicks = 0;
    private static int firstAxisTicks = 0;
    private static int turnElapsed = 0;

    private static final Random random = new Random();

    // Fastest allowed turn: turnSpeed * 0.1 degrees, twice per tick (the old turning ran on both tick phases)
    private static float maxStepPerTick() {
        return AIOMVigilanceConfig.turnSpeed * 0.1f * 2;
    }

    private static void planTurn() {
        plannedYaw = yaw;
        plannedPitch = pitch;
        turnElapsed = 0;

        turnOrder = AIOMVigilanceConfig.lookTurnOrder;
        if (turnOrder == ORDER_RANDOM) {
            turnOrder = random.nextBoolean() ? ORDER_PITCH_FIRST : ORDER_YAW_FIRST;
        }

        float yawLeft = Math.abs(wrapYaw(yaw - Main.mcPlayer.rotationYaw));
        float pitchLeft = Math.abs(pitch - Main.mcPlayer.rotationPitch);

        // Tiny corrections (e.g. after being pushed) use the normal speed instead of the configured turn time
        if (yawLeft + pitchLeft < 1f) {
            turnTicks = 0;
        } else {
            int minMs = Math.min(AIOMVigilanceConfig.lookTurnTimeMin, AIOMVigilanceConfig.lookTurnTimeMax);
            int maxMs = Math.max(AIOMVigilanceConfig.lookTurnTimeMin, AIOMVigilanceConfig.lookTurnTimeMax);
            turnTicks = (minMs + random.nextInt(maxMs - minMs + 1)) / 50;
        }

        // When turning one axis after the other, split the time by how far each axis has to turn
        float firstAngle = turnOrder == ORDER_PITCH_FIRST ? pitchLeft : yawLeft;
        firstAxisTicks = turnOrder == ORDER_TOGETHER || yawLeft + pitchLeft == 0 ? turnTicks : Math.round(turnTicks * firstAngle / (yawLeft + pitchLeft));
    }

    // Moves towards the target, spreading what is left over the ticks left, but never faster than the max speed
    private static float stepTowards(float difference, int endTick) {
        int ticksLeft = Math.max(1, endTick - turnElapsed + 1);
        float step = Math.min(maxStepPerTick(), Math.abs(difference) / ticksLeft);
        if (Math.abs(difference) <= step) {
            return difference;
        }
        return Math.signum(difference) * step;
    }

    @SubscribeEvent
    public void goToLook(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!toggled || Main.mcPlayer == null || isLookCorrect()) {
            plannedYaw = Float.NaN;
            return;
        }
        if (Float.isNaN(plannedYaw) || plannedYaw != yaw || plannedPitch != pitch) {
            planTurn();
        }
        turnElapsed++;

        boolean yawDone = isYawLookCorrect();
        boolean pitchDone = isPitchLookCorrect();

        if (!yawDone && (turnOrder != ORDER_PITCH_FIRST || pitchDone)) {
            int yawEnd = turnOrder == ORDER_YAW_FIRST ? firstAxisTicks : turnTicks;
            Main.mcPlayer.rotationYaw += stepTowards(wrapYaw(yaw - Main.mcPlayer.rotationYaw), yawEnd);
        }
        if (!pitchDone && (turnOrder != ORDER_YAW_FIRST || yawDone)) {
            int pitchEnd = turnOrder == ORDER_PITCH_FIRST ? firstAxisTicks : turnTicks;
            Main.mcPlayer.rotationPitch += stepTowards(pitch - Main.mcPlayer.rotationPitch, pitchEnd);
        }
    }

    private static long currentTimeMillis180 = Utils.currentTimeMillis();

    public static void flip180Check() {
        if (Main.mcPlayer.posY != 0 && Main.mcPlayer.motionY <= -0.3 && AIOMVigilanceConfig.drop180 && Utils.currentTimeMillis() - currentTimeMillis180 >= 15000) {
            currentTimeMillis180 = Utils.currentTimeMillis();
            if (SetPlayerLook.yaw >= 0) {
                SetPlayerLook.yaw = SetPlayerLook.yaw - 180;
            } else {
                SetPlayerLook.yaw = SetPlayerLook.yaw + 180;
            }
        }
    }
}
