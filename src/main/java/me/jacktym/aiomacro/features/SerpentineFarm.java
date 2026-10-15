package me.jacktym.aiomacro.features;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Layered serpentine farm:
 * strafe along a row -> walk forward/back onto the next row -> strafe back the other way, and so on.
 * At the end of the last row the player drops to the next layer, whose rows run the opposite way,
 * so the forward/back row shift flips every time a drop is detected. The look direction never changes.
 *
 * Mushroom Serpentine (2-layer loop): farm the top layer, drop to the bottom layer, farm it, then swim up the water column
 * at its end back to the top layer (whose crops have regrown) and start over, forever.
 */
public class SerpentineFarm {

    private enum State {FARM_ROW, PAUSE_BEFORE_SHIFT, SHIFT, PAUSE_BEFORE_ROW, CLIMB}

    public static boolean strafeLeft = true;
    public static boolean shiftForward = false;
    // Direction of the shift in progress (normally shiftForward, or towards the water column)
    private static boolean currentShiftForward = false;

    private static State state = State.FARM_ROW;
    private static boolean justLanded = false;
    private static double groundY = Double.NaN;
    private static Vec3 shiftStart = null;
    private static int stateTicks = 0;
    private static int stillTicks = 0;
    private static int pauseTicks = 0;

    // Mushroom Serpentine (2-layer loop)
    private static boolean twoLayerLoop = false;
    private static boolean onTopLayer = true;
    private static boolean rowStartedOnLayer = false;
    private static boolean wasInWater = false;
    private static boolean exitLeft = false;
    private static double climbStartY = 0;
    private static double climbMaxY = 0;
    private static int climbStallTicks = 0;
    private static int exitTicks = 0;

    private static final Random random = new Random();

    public static void start(boolean twoLayers) {
        strafeLeft = AIOMVigilanceConfig.serpentineFirstRow == 0;
        shiftForward = AIOMVigilanceConfig.serpentineFirstShift == 1;
        state = State.FARM_ROW;
        justLanded = false;
        groundY = Double.NaN;
        shiftStart = null;
        stateTicks = 0;
        stillTicks = 0;
        pauseTicks = 0;
        twoLayerLoop = twoLayers;
        onTopLayer = AIOMVigilanceConfig.serpentineStartLayer == 0;
        // The macro is started at the beginning of a row
        rowStartedOnLayer = true;
        wasInWater = Main.mcPlayer.isInWater();
    }

    @SubscribeEvent
    public void farmTick(@NotNull TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !MacroHandler.isMacroOn || !MacroHandler.isSerpentine || !Main.notNull || AntiStuck.antistuckOn) {
            return;
        }
        if (!MacroHandler.waitForLook()) {
            return;
        }
        EntityPlayerSP player = Main.mcPlayer;

        detectLayerDrop(player);

        // Bottom layer finished: walked into the water column, swim up to the top layer
        boolean inWater = player.isInWater();
        if (twoLayerLoop && state != State.CLIMB && !onTopLayer && rowStartedOnLayer && inWater && !wasInWater) {
            startClimb(player);
        }
        wasInWater = inWater;

        boolean stopped = player.onGround && Math.abs(player.motionX) <= 0.001 && Math.abs(player.motionZ) <= 0.001;
        stillTicks = stopped ? stillTicks + 1 : 0;
        stateTicks++;

        setKey(Main.mc.gameSettings.keyBindAttack, true);

        switch (state) {
            case FARM_ROW:
                setMovement(strafeLeft, !strafeLeft, false, false);

                // Stood still for a few ticks after properly starting the row = end of the row
                if (stillTicks >= 3 && stateTicks >= 10) {
                    if (stateTicks < 15 && !justLanded && AIOMVigilanceConfig.antiStuckFailsafe) {
                        // Stopped almost immediately, something is in the way
                        AntiStuck.antistuckOn = true;
                        enter(State.FARM_ROW);
                        return;
                    }
                    if (AIOMVigilanceConfig.setSpawnFailsafe) {
                        player.sendChatMessage("/setspawn");
                    }
                    setMovement(false, false, false, false);
                    pauseTicks = randomBetween(3, 8);
                    enter(State.PAUSE_BEFORE_SHIFT);
                }
                break;

            case PAUSE_BEFORE_SHIFT:
                setMovement(false, false, false, false);
                if (--pauseTicks <= 0) {
                    if (justLanded) {
                        // Just dropped onto a new layer: already on its first row, so only turn around
                        justLanded = false;
                        rowStartedOnLayer = true;
                        strafeLeft = !strafeLeft;
                        enter(State.FARM_ROW);
                    } else {
                        // Bottom layer: the water column can sit one block forward/back from the end of the last row
                        Boolean waterForward = twoLayerLoop && !onTopLayer ? findWaterBesideRowEnd(player) : null;
                        currentShiftForward = waterForward != null ? waterForward : shiftForward;
                        shiftStart = player.getPositionVector();
                        enter(State.SHIFT);
                    }
                }
                break;

            case SHIFT:
                setMovement(false, false, currentShiftForward, !currentShiftForward);
                double moved = distanceAlongLook(player, shiftStart);
                // Release a little early, the player slides about a quarter block after letting go
                if (moved >= AIOMVigilanceConfig.serpentineRowShift - 0.25) {
                    finishShift();
                } else if (stillTicks >= 10 && stateTicks >= 10) {
                    if (moved >= 0.5) {
                        // Walked into the next row's wall early, good enough
                        finishShift();
                    } else {
                        Main.sendMarkedChatMessage("Serpentine: can't move to the next row (end of the farm?). Stopping.");
                        MacroHandler.toggleMacro();
                    }
                }
                break;

            case PAUSE_BEFORE_ROW:
                setMovement(false, false, false, false);
                if (--pauseTicks <= 0) {
                    strafeLeft = !strafeLeft;
                    enter(State.FARM_ROW);
                }
                break;

            case CLIMB:
                setKey(Main.mc.gameSettings.keyBindJump, true);
                if (player.posY > climbMaxY + 0.05) {
                    climbMaxY = player.posY;
                    climbStallTicks = 0;
                } else {
                    climbStallTicks++;
                }

                // Step out once the top layer's walkway is beside us (or we stopped rising at the top of the water)
                boolean risen = player.posY - climbStartY >= 1.5;
                if (exitTicks > 0 || (risen && exitOpen(player, exitLeft)) || climbStallTicks >= 40) {
                    exitTicks++;
                    setMovement(exitLeft, !exitLeft, false, false);
                } else {
                    setMovement(false, false, false, false);
                }

                if (exitTicks > 0 && player.onGround && !inWater) {
                    // On the top layer, already strafing along its first row
                    setKey(Main.mc.gameSettings.keyBindJump, false);
                    onTopLayer = true;
                    rowStartedOnLayer = true;
                    shiftForward = !shiftForward;
                    strafeLeft = exitLeft;
                    groundY = player.posY;
                    enter(State.FARM_ROW);
                    if (AIOMVigilanceConfig.devmode) {
                        Main.sendMarkedChatMessage("Serpentine: back on the top layer, rows now shift " + (shiftForward ? "forward" : "back"));
                    }
                } else if (exitTicks > 60) {
                    setKey(Main.mc.gameSettings.keyBindJump, false);
                    Main.sendMarkedChatMessage("Serpentine: couldn't get out of the water column at the top. Stopping.");
                    MacroHandler.toggleMacro();
                }
                break;
        }
    }

    // Landing more than 1.5 blocks lower than the last ground level means we dropped to the next layer
    private static void detectLayerDrop(EntityPlayerSP player) {
        if (!player.onGround) {
            return;
        }
        if (!Double.isNaN(groundY) && player.posY < groundY - 1.5 && state != State.CLIMB) {
            justLanded = true;
            onTopLayer = false;
            rowStartedOnLayer = false;
            shiftForward = !shiftForward;
            if (state != State.FARM_ROW) {
                enter(State.FARM_ROW);
            }
            if (AIOMVigilanceConfig.devmode) {
                Main.sendMarkedChatMessage("Serpentine: dropped to next layer, rows now shift " + (shiftForward ? "forward" : "back"));
            }
        }
        groundY = player.posY;
    }

    private static void startClimb(EntityPlayerSP player) {
        // Leave the water the opposite way we came in (turning around onto the next row), or the same way
        exitLeft = AIOMVigilanceConfig.serpentineWaterExit == 0 ? !strafeLeft : strafeLeft;
        climbStartY = player.posY;
        climbMaxY = player.posY;
        climbStallTicks = 0;
        exitTicks = 0;
        setMovement(false, false, false, false);
        enter(State.CLIMB);
    }

    // True when the block beside the player (in the exit direction) can be walked into and has a floor
    private static boolean exitOpen(EntityPlayerSP player, boolean left) {
        double yawRad = Math.toRadians(player.rotationYaw);
        double side = left ? 1 : -1;
        BlockPos feet = new BlockPos(player.posX + Math.cos(yawRad) * side * 0.8, player.posY, player.posZ + Math.sin(yawRad) * side * 0.8);
        return isPassable(feet) && isPassable(feet.up()) && !isPassable(feet.down());
    }

    // Water one block in front of / behind the player: true = forward, false = back, null = no water there
    private static Boolean findWaterBesideRowEnd(EntityPlayerSP player) {
        double yawRad = Math.toRadians(player.rotationYaw);
        double lookX = -Math.sin(yawRad);
        double lookZ = Math.cos(yawRad);
        if (isWater(new BlockPos(player.posX + lookX, player.posY, player.posZ + lookZ))) {
            return true;
        }
        if (isWater(new BlockPos(player.posX - lookX, player.posY, player.posZ - lookZ))) {
            return false;
        }
        return null;
    }

    private static boolean isWater(BlockPos pos) {
        return Main.mcWorld.getBlockState(pos).getBlock().getMaterial() == Material.water;
    }

    private static boolean isPassable(BlockPos pos) {
        IBlockState blockState = Main.mcWorld.getBlockState(pos);
        return blockState.getBlock().getCollisionBoundingBox(Main.mcWorld, pos, blockState) == null;
    }

    private static void finishShift() {
        setMovement(false, false, false, false);
        pauseTicks = randomBetween(2, 6);
        enter(State.PAUSE_BEFORE_ROW);
    }

    private static void enter(State newState) {
        state = newState;
        stateTicks = 0;
        stillTicks = 0;
    }

    // Horizontal distance moved along the direction the player is looking
    private static double distanceAlongLook(EntityPlayerSP player, Vec3 from) {
        double yawRad = Math.toRadians(player.rotationYaw);
        double lookX = -Math.sin(yawRad);
        double lookZ = Math.cos(yawRad);
        return Math.abs((player.posX - from.xCoord) * lookX + (player.posZ - from.zCoord) * lookZ);
    }

    private static void setMovement(boolean left, boolean right, boolean forward, boolean back) {
        setKey(Main.mc.gameSettings.keyBindLeft, left);
        setKey(Main.mc.gameSettings.keyBindRight, right);
        setKey(Main.mc.gameSettings.keyBindForward, forward);
        setKey(Main.mc.gameSettings.keyBindBack, back);
    }

    private static void setKey(KeyBinding key, boolean pressed) {
        KeyBinding.setKeyBindState(key.getKeyCode(), pressed);
    }

    private static int randomBetween(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
