package me.jacktym.aiomacro.features;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.rendering.BlockRendering;
import me.jacktym.aiomacro.util.Utils;
import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class MacroHandler {

    public static long macroStartMillis;
    // Time the macro ran before the current run, since the game started (for !time)
    public static long previousRunsMillis = 0;

    public static boolean isMacroOn = false;
    public static boolean isNetherWart = false;
    public static boolean isSugarCane = false;
    public static boolean isCocoaBean = false;
    public static boolean isOneRow = false;
    public static boolean isSerpentine = false;
    public static boolean isPumpkinMelon = false;

    // False until the player has turned to the set pitch/yaw after starting the macro
    public static boolean lookReady = false;

    // Farming macros call this first every tick: keeps the player still until the look reaches the set pitch/yaw
    public static boolean waitForLook() {
        if (lookReady) {
            return true;
        }
        if (SetPlayerLook.isLookCorrect()) {
            lookReady = true;
            return true;
        }
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindAttack.getKeyCode(), false);
        return false;
    }

    // Total macro running time since the game started, including the current run
    public static long totalRunMillis() {
        return previousRunsMillis + (isMacroOn ? Utils.currentTimeMillis() - macroStartMillis : 0);
    }

    public static void toggleMacro() {
        AuctionFlipper.uuids.clear();
        AuctionFlipper.auctionUUIDToBuy = "";
        AuctionFlipper.itemBeingBought = null;
        AuctionFlipper.needToList = false;
        AuctionFlipper.needToClaim = false;
        AuctionFlipper.sentWaitingForFlips = false;
        AuctionFlipper.sentListingFlip = false;
        AuctionFlipper.sentBuyingFlip = false;
        AuctionFlipper.sentClaimingFlip = false;

        if (!isMacroOn && Main.notNull) {
            isNetherWart = false;
            isSugarCane = false;
            isCocoaBean = false;
            isOneRow = false;
            isSerpentine = false;
            isPumpkinMelon = false;
            lookReady = false;
            isMacroOn = true;
            macroStartMillis = Utils.currentTimeMillis();

            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindLeft.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindForward.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindBack.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindAttack.getKeyCode(), false);

            System.out.println(AIOMVigilanceConfig.macroType);

            if (AIOMVigilanceConfig.macroType == 0) {
                NetherWart.left = true;
                NetherWart.forward = false;
                isNetherWart = true;
                SetPlayerLook.setDefault();
                SetPlayerLook.toggled = true;
                if (AIOMVigilanceConfig.ungrab) {
                    Utils.unGrab();
                }
            } else if (AIOMVigilanceConfig.macroType == 1) {
                SugarCane.left = true;
                isSugarCane = true;
                SetPlayerLook.setDefault();
                SetPlayerLook.toggled = true;
                if (AIOMVigilanceConfig.ungrab) {
                    Utils.unGrab();
                }
            } else if (AIOMVigilanceConfig.macroType == 5) {
                MinionAura.recentClickedEntity = null;
                MinionAura.claimedMinions.clear();
                MinionAura.inMinion = false;
            } else if (AIOMVigilanceConfig.macroType == 9) {
                SugarCane.left = true;
                isCocoaBean = true;
                SetPlayerLook.setDefault();
                SetPlayerLook.toggled = true;
                if (AIOMVigilanceConfig.ungrab) {
                    Utils.unGrab();
                }
            } else if (AIOMVigilanceConfig.macroType == 11) {
                SerpentineFarm.start(false);
                isSerpentine = true;
                SetPlayerLook.setDefault();
                SetPlayerLook.toggled = true;
                if (AIOMVigilanceConfig.ungrab) {
                    Utils.unGrab();
                }
            } else if (AIOMVigilanceConfig.macroType == 12) { // Mushroom Serpentine
                SerpentineFarm.start(true);
                isSerpentine = true;
                SetPlayerLook.setDefault();
                SetPlayerLook.toggled = true;
                if (AIOMVigilanceConfig.ungrab) {
                    Utils.unGrab();
                }
            } else if (AIOMVigilanceConfig.macroType == 13) {
                PumpkinMelon.start();
                isPumpkinMelon = true;
                SetPlayerLook.setDefault();
                SetPlayerLook.toggled = true;
                if (AIOMVigilanceConfig.ungrab) {
                    Utils.unGrab();
                }
            } else if (AIOMVigilanceConfig.macroType == 10) {
                OneRow.forwards = true;
                isOneRow = true;
                SetPlayerLook.setDefault();
                SetPlayerLook.toggled = true;
                if (AIOMVigilanceConfig.ungrab) {
                    Utils.unGrab();
                }
            }
            Main.sendMarkedChatMessage("Macro Enabled!");
        } else if (isMacroOn) {
            previousRunsMillis += Utils.currentTimeMillis() - macroStartMillis;
            Main.sendMarkedChatMessage("Macro Disabled!");
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindBack.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindLeft.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindForward.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindAttack.getKeyCode(), false);
            KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindJump.getKeyCode(), false);

            CarpentryMacro.claimDiamonds = true;
            CarpentryMacro.craftDiamonds = false;
            CarpentryMacro.putInBackPack = false;

            BlockRendering.renderMap.clear();

            isNetherWart = false;
            isSugarCane = false;
            isSerpentine = false;
            isPumpkinMelon = false;

            SetPlayerLook.toggled = false;

            isMacroOn = false;

            if (AIOMVigilanceConfig.ungrab) {
                Utils.reGrab();
            }
        }
    }
}