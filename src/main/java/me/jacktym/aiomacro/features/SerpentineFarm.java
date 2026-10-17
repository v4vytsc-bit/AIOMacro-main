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

    private enum State {FARM_ROW, PAUSE_BEFORE_SHIFT, SHIFT, PAUSE_BEFORE_ROW, CLIMB, WALK_BACK}

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
    private static boolean rowStartedOnLayer = false;
    private static boolean wasInWater = false;
    // Walked (not fell) into the water we're in, so it may be the column to swim up
    private static boolean walkedIntoWater = false;
    private static double waterEntryY = 0;
    private static Vec3 rowStart = null;
    // Rows in a row that ended without moving along them (blocked on both sides = something's wrong)
    private static int blockedRows = 0;
    // One walk-back try when stuck; it's earned again after a few good rows
    private static boolean walkBackUsed = false;
    private static int goodRowsSinceWalkBack = 0;
    private static boolean lastShiftForward = false;
    private static boolean walkBackForward = false;
    private static Vec3 walkBackStart = null;
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
        // The macro is started at the beginning of a row
        rowStartedOnLayer = true;
        wasInWater = Main.mcPlayer.isInWater();
        walkedIntoWater = false;
        rowStart = Main.mcPlayer.getPositionVector();
        blockedRows = 0;
        walkBackUsed = false;
        goodRowsSinceWalkBack = 0;
        lastShiftForward = !shiftForward;
        debug("start: strafing " + strafeName(strafeLeft) + ", rows step " + (shiftForward ? "forward (W)" : "back (S)")
                + ", " + (twoLayers ? "mushroom 2-layer loop" : "layered"));
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

        // Bottom layer finished: walked into a water column that goes up, swim up to the top layer.
        // Falling into water, or sinking down through it, is a drop and not the way up, so it doesn't count.
        boolean inWater = player.isInWater();
        if (inWater && !wasInWater) {
            walkedIntoWater = player.motionY > -0.3;
            waterEntryY = player.posY;
        } else if (!inWater) {
            walkedIntoWater = false;
        }
        wasInWater = inWater;
        if (twoLayerLoop && state != State.CLIMB && rowStartedOnLayer && walkedIntoWater
                && player.posY > waterEntryY - 0.5
                && isUpColumn(new BlockPos(player.posX, player.posY, player.posZ))) {
            walkedIntoWater = false;
            startClimb(player);
        }

        boolean stopped = player.onGround && Math.abs(player.motionX) <= 0.001 && Math.abs(player.motionZ) <= 0.001;
        stillTicks = stopped ? stillTicks + 1 : 0;
        stateTicks++;

        setKey(Main.mc.gameSettings.keyBindAttack, true);

        switch (state) {
            case FARM_ROW:
                setMovement(strafeLeft, !strafeLeft, false, false);

                // Stood still after moving along the row = end of the row.
                // Right after a drop we're already against the end wall, so any stop counts.
                boolean movedAlongRow = distanceAlongStrafe(player, rowStart) >= 1.0;
                if (stillTicks >= 3 && stateTicks >= 10 && (movedAlongRow || justLanded || stillTicks >= 20)) {
                    double along = distanceAlongStrafe(player, rowStart);
                    if (!movedAlongRow && !justLanded) {
                        blockedRows++;
                        debug("blocked strafing " + strafeName(strafeLeft) + " after " + format(along) + " blocks at " + where(player)
                                + " (" + blockedRows + "x in a row)");
                        if (blockedRows >= 2) {
                            setMovement(false, false, false, false);
                            if (!walkBackUsed) {
                                // Walk back the way we came until the walkway opens up to the left or right
                                walkBackUsed = true;
                                goodRowsSinceWalkBack = 0;
                                walkBackForward = !lastShiftForward;
                                walkBackStart = player.getPositionVector();
                                debug("stuck: walking back " + (walkBackForward ? "forward (W)" : "back (S)") + " to find the row");
                                enter(State.WALK_BACK);
                                return;
                            }
                            Main.sendMarkedChatMessage("Serpentine: can't move along the row here (blocked at " + where(player)
                                    + "), even after walking back. Check Pitch/Yaw and First Row, or what's in the way. Stopping.");
                            MacroHandler.toggleMacro();
                            return;
                        }
                        if (AIOMVigilanceConfig.antiStuckFailsafe) {
                            // Stood still for a second without getting anywhere: something is in the way
                            setMovement(false, false, false, false);
                            AntiStuck.antistuckOn = true;
                            enter(State.FARM_ROW);
                            return;
                        }
                    } else {
                        blockedRows = 0;
                        // After a few good rows, being stuck again gets another walk-back try
                        if (walkBackUsed && ++goodRowsSinceWalkBack >= 3) {
                            walkBackUsed = false;
                        }
                        debug("row end after " + format(along) + " blocks strafing " + strafeName(strafeLeft) + (justLanded ? " (just landed)" : ""));
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
                        debug("new layer: strafing " + strafeName(strafeLeft));
                        enter(State.FARM_ROW);
                    } else {
                        // The water column up can sit one block forward/back from the end of the bottom layer's last row
                        Boolean waterForward = twoLayerLoop ? findWaterBesideRowEnd(player) : null;
                        currentShiftForward = waterForward != null ? waterForward : shiftForward;
                        lastShiftForward = currentShiftForward;
                        shiftStart = player.getPositionVector();
                        debug("stepping " + (currentShiftForward ? "forward (W)" : "back (S)")
                                + (waterForward != null ? " into the water column" : " up to " + AIOMVigilanceConfig.serpentineRowShift + " blocks"));
                        enter(State.SHIFT);
                    }
                }
                break;

            case SHIFT:
                setMovement(false, false, currentShiftForward, !currentShiftForward);
                double moved = distanceAlongLook(player, shiftStart);
                // Release a little early, the player slides about a quarter block after letting go
                if (moved >= AIOMVigilanceConfig.serpentineRowShift - 0.25) {
                    debug("stepped " + format(moved) + " blocks");
                    finishShift();
                } else if (stillTicks >= 10 && stateTicks >= 10) {
                    if (moved >= 0.5) {
                        // Walked into the next row's wall early, good enough
                        debug("step stopped early after " + format(moved) + " blocks at " + where(player));
                        finishShift();
                    } else {
                        debug("step blocked after " + format(moved) + " blocks at " + where(player));
                        Main.sendMarkedChatMessage("Serpentine: can't move to the next row (end of the farm?). Stopping.");
                        MacroHandler.toggleMacro();
                    }
                }
                break;

            case PAUSE_BEFORE_ROW:
                setMovement(false, false, false, false);
                if (--pauseTicks <= 0) {
                    strafeLeft = !strafeLeft;
                    debug("strafing " + strafeName(strafeLeft));
                    enter(State.FARM_ROW);
                }
                break;

            case WALK_BACK: {
                setMovement(false, false, walkBackForward, !walkBackForward);
                double walked = distanceAlongLook(player, walkBackStart);
                // Look for an opening only after leaving the stuck spot
                if (walked >= 0.6) {
                    boolean leftOpen = exitOpen(player, true);
                    boolean rightOpen = exitOpen(player, false);
                    if (leftOpen || rightOpen) {
                        // Which way to strafe afterwards: the setting, or towards the opening
                        boolean nextLeft;
                        if (AIOMVigilanceConfig.serpentineAfterWalkBackStrafe == 0) {
                            nextLeft = true;
                        } else if (AIOMVigilanceConfig.serpentineAfterWalkBackStrafe == 1) {
                            nextLeft = false;
                        } else {
                            nextLeft = leftOpen && (!rightOpen || !strafeLeft);
                        }
                        blockedRows = 0;
                        setMovement(false, false, false, false);

                        if (AIOMVigilanceConfig.serpentineAfterWalkBackStep == 2) {
                            debug("found an opening after walking back " + format(walked) + " blocks, strafing " + strafeName(nextLeft));
                            strafeLeft = nextLeft;
                            enter(State.FARM_ROW);
                        } else {
                            // Step to the next row like normal; the row after the step flips the strafe, so set the opposite now
                            currentShiftForward = AIOMVigilanceConfig.serpentineAfterWalkBackStep == 0;
                            lastShiftForward = currentShiftForward;
                            strafeLeft = !nextLeft;
                            shiftStart = player.getPositionVector();
                            debug("found an opening after walking back " + format(walked) + " blocks: stepping "
                                    + (currentShiftForward ? "forward (W)" : "back (S)") + ", then strafing " + strafeName(nextLeft));
                            enter(State.SHIFT);
                        }
                        break;
                    }
                }
                if ((stillTicks >= 20 && stateTicks >= 10) || walked > 32) {
                    setMovement(false, false, false, false);
                    Main.sendMarkedChatMessage("Serpentine: stuck at " + where(player)
                            + " and walking back didn't find the row. Check Pitch/Yaw and First Row, or what's in the way. Stopping.");
                    MacroHandler.toggleMacro();
                }
                break;
            }

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
                    rowStartedOnLayer = true;
                    shiftForward = !shiftForward;
                    strafeLeft = exitLeft;
                    groundY = player.posY;
                    enter(State.FARM_ROW);
                    if (AIOMVigilanceConfig.devmode) {
                        Main.sendMarkedChatMessage("Serpentine: back on the top layer, rows now shift " + (shiftForward ? "forward" : "back"));
                    }
                } else if (exitTicks == 1) {
                    debug("getting out of the water " + strafeName(exitLeft) + " at " + where(player));
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
        debug("walked into the water column at " + where(player) + ", swimming up");
        // Leave the water the opposite way we came in (turning around onto the next row), or the same way
        switch (AIOMVigilanceConfig.serpentineWaterExit) {
            case 1:
                exitLeft = strafeLeft;
                break;
            case 2:
                exitLeft = true;
                break;
            case 3:
                exitLeft = false;
                break;
            default:
                exitLeft = !strafeLeft;
        }
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

    // Water column going up one block in front of / behind the player: true = forward, false = back, null = none
    private static Boolean findWaterBesideRowEnd(EntityPlayerSP player) {
        double yawRad = Math.toRadians(player.rotationYaw);
        double lookX = -Math.sin(yawRad);
        double lookZ = Math.cos(yawRad);
        if (isUpColumn(new BlockPos(player.posX + lookX, player.posY, player.posZ + lookZ))) {
            return true;
        }
        if (isUpColumn(new BlockPos(player.posX - lookX, player.posY, player.posZ - lookZ))) {
            return false;
        }
        return null;
    }

    // Water here that carries on above head height, with a floor underneath = the bottom of a column to swim up
    private static boolean isUpColumn(BlockPos feet) {
        // The way up is entered at the bottom of the column: water carrying on above, a floor below.
        // At the top, the same column has water below too - that's the drop down, so don't climb it.
        return isWater(feet) && isWater(feet.up(2)) && !isWater(feet.down());
    }

    // Horizontal distance moved sideways (along the row) relative to where the player is looking
    private static double distanceAlongStrafe(EntityPlayerSP player, Vec3 from) {
        if (from == null) {
            return 0;
        }
        double yawRad = Math.toRadians(player.rotationYaw);
        return Math.abs((player.posX - from.xCoord) * Math.cos(yawRad) + (player.posZ - from.zCoord) * Math.sin(yawRad));
    }

    private static boolean isWater(BlockPos pos) {
        return Main.mcWorld.getBlockState(pos).getBlock().getMaterial() == Material.water;
    }

    private static boolean isPassable(BlockPos pos) {
        IBlockState blockState = Main.mcWorld.getBlockState(pos);
        return blockState.getBlock().getCollisionBoundingBox(Main.mcWorld, pos, blockState) == null;
    }

    // Step-by-step notes in chat when Developer Mode is on (Debugging > Developer Mode)
    private static void debug(String message) {
        if (AIOMVigilanceConfig.devmode) {
            Main.sendMarkedChatMessage("§7[Serpentine] " + message);
        }
    }

    private static String strafeName(boolean left) {
        return left ? "left (A)" : "right (D)";
    }

    private static String where(EntityPlayerSP player) {
        return (int) Math.floor(player.posX) + " " + (int) Math.floor(player.posY) + " " + (int) Math.floor(player.posZ);
    }

    private static String format(double blocks) {
        return String.format("%.1f", blocks);
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
        if (newState == State.FARM_ROW) {
            rowStart = Main.mcPlayer.getPositionVector();
        }
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
