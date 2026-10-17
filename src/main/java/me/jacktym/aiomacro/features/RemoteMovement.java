package me.jacktym.aiomacro.features;

import me.jacktym.aiomacro.Main;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

/**
 * Movement requested from Discord (!move, !jump). Actions run one after another on the game thread,
 * and each one reports how it went when it finishes. Only used while no macro is running.
 * All methods must be called on the game thread.
 */
public class RemoteMovement {

    private static final Deque<Action> queue = new ArrayDeque<>();
    private static Action current = null;

    public static void enqueue(Action action) {
        queue.add(action);
    }

    public static boolean isBusy() {
        return current != null || !queue.isEmpty();
    }

    // Stops whatever is running and drops anything queued
    public static void cancelAll(String reason) {
        if (current != null) {
            current.release();
            current.finish(reason);
            current = null;
        }
        while (!queue.isEmpty()) {
            queue.poll().finish(reason);
        }
    }

    @SubscribeEvent
    public void tick(@NotNull TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !isBusy()) {
            return;
        }
        if (!Main.notNull) {
            cancelAll("⚠️ Stopped: left the world.");
            return;
        }
        if (MacroHandler.isMacroOn) {
            cancelAll("⚠️ Stopped: the macro was started.");
            return;
        }
        if (current == null) {
            current = queue.poll();
        }
        if (current != null && current.tick(Main.mcPlayer)) {
            current.release();
            current = null;
        }
    }

    public abstract static class Action {
        private final Consumer<String> onDone;
        private boolean finished = false;
        protected int ticks = 0;

        Action(Consumer<String> onDone) {
            this.onDone = onDone;
        }

        /** Runs once per tick. Returns true when the action is done. */
        abstract boolean tick(EntityPlayerSP player);

        /** Lets go of any keys this action holds. */
        abstract void release();

        void finish(String result) {
            if (!finished) {
                finished = true;
                onDone.accept(result);
            }
        }
    }

    /** Walk a number of blocks with W, A, S or D, relative to where the player is looking. */
    public static class Move extends Action {
        private final char key;
        private final double blocks;
        private double startX = Double.NaN;
        private double startZ;
        private int stillTicks = 0;

        public Move(char key, double blocks, Consumer<String> onDone) {
            super(onDone);
            this.key = key;
            this.blocks = blocks;
        }

        @Override
        boolean tick(EntityPlayerSP player) {
            if (Double.isNaN(startX)) {
                startX = player.posX;
                startZ = player.posZ;
            }
            ticks++;
            setKey(keyBinding(), true);

            double moved = distanceMoved(player);
            // Let go a little early, the player slides about a quarter block after releasing the key
            if (moved >= blocks - 0.25) {
                release();
                finish("🚶 Moved " + format(blocks) + " blocks " + directionName() + ".");
                return true;
            }

            boolean stopped = Math.abs(player.motionX) <= 0.001 && Math.abs(player.motionZ) <= 0.001;
            stillTicks = stopped ? stillTicks + 1 : 0;
            if ((stillTicks >= 20 && ticks >= 10) || ticks > blocks * 20 + 100) {
                release();
                finish("⚠️ Stopped after " + format(moved) + " of " + format(blocks) + " blocks " + directionName() + " - something is in the way.");
                return true;
            }
            return false;
        }

        @Override
        void release() {
            setKey(keyBinding(), false);
        }

        // Distance moved in the chosen direction, relative to the way the player is facing
        private double distanceMoved(EntityPlayerSP player) {
            double yaw = Math.toRadians(player.rotationYaw);
            double forwardX = -Math.sin(yaw);
            double forwardZ = Math.cos(yaw);
            double leftX = Math.cos(yaw);
            double leftZ = Math.sin(yaw);
            double dx = player.posX - startX;
            double dz = player.posZ - startZ;
            switch (key) {
                case 'w':
                    return dx * forwardX + dz * forwardZ;
                case 's':
                    return -(dx * forwardX + dz * forwardZ);
                case 'a':
                    return dx * leftX + dz * leftZ;
                default:
                    return -(dx * leftX + dz * leftZ);
            }
        }

        private KeyBinding keyBinding() {
            switch (key) {
                case 'w':
                    return Main.mc.gameSettings.keyBindForward;
                case 's':
                    return Main.mc.gameSettings.keyBindBack;
                case 'a':
                    return Main.mc.gameSettings.keyBindLeft;
                default:
                    return Main.mc.gameSettings.keyBindRight;
            }
        }

        private String directionName() {
            switch (key) {
                case 'w':
                    return "forward (W)";
                case 's':
                    return "back (S)";
                case 'a':
                    return "left (A)";
                default:
                    return "right (D)";
            }
        }
    }

    /** Jump a number of times, waiting to land between jumps. */
    public static class Jump extends Action {
        private final int times;
        private int jumps = 0;
        private int phaseTicks = 0;
        private boolean inAir = false;

        public Jump(int times, Consumer<String> onDone) {
            super(onDone);
            this.times = times;
        }

        @Override
        boolean tick(EntityPlayerSP player) {
            ticks++;
            phaseTicks++;
            if (!inAir) {
                // Hold jump on the ground until we leave it
                setKey(Main.mc.gameSettings.keyBindJump, player.onGround);
                if (!player.onGround) {
                    inAir = true;
                    phaseTicks = 0;
                    setKey(Main.mc.gameSettings.keyBindJump, false);
                }
            } else if (player.onGround) {
                jumps++;
                inAir = false;
                phaseTicks = 0;
                if (jumps >= times) {
                    finish("🦘 Jumped " + times + (times == 1 ? " time." : " times."));
                    return true;
                }
            }
            // Stuck on the ground (e.g. a ceiling) or never landing: give up
            if (phaseTicks > 60) {
                release();
                finish("⚠️ Stopped after " + jumps + " of " + times + " jumps.");
                return true;
            }
            return false;
        }

        @Override
        void release() {
            setKey(Main.mc.gameSettings.keyBindJump, false);
        }
    }

    private static void setKey(KeyBinding key, boolean pressed) {
        KeyBinding.setKeyBindState(key.getKeyCode(), pressed);
    }

    private static String format(double blocks) {
        return blocks == Math.floor(blocks) ? String.valueOf((long) blocks) : String.format("%.1f", blocks);
    }
}
