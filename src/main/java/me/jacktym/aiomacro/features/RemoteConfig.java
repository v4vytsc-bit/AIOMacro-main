package me.jacktym.aiomacro.features;

import gg.essential.vigilance.data.Property;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.util.Discord;

/**
 * Viewing and changing macro settings from Discord (!config). Must run on the game thread.
 * Changes are saved to the config file like changes made in the settings menu.
 */
public class RemoteConfig {

    private static final String[] TURN_ORDERS = {"together", "pitchfirst", "yawfirst", "random"};
    private static final String[] PINGS = {"none", "here", "everyone"};
    private static final String[] WATER_EXITS = {"opposite", "same", "left", "right"};
    private static final String[] WALKBACK_STEPS = {"forward", "back", "none"};
    private static final String[] WALKBACK_STRAFES = {"left", "right", "opening"};

    public static String show() {
        String name = Discord.playerName();
        return "**" + name + "'s settings**\n"
                + "`macro` " + macroName(AIOMVigilanceConfig.macroType) + "\n"
                + "`pitch` " + AIOMVigilanceConfig.pitchText + " · `yaw` " + AIOMVigilanceConfig.yawText + "\n"
                + "`rowshift` " + AIOMVigilanceConfig.serpentineRowShift
                + " · `firstrow` " + (AIOMVigilanceConfig.serpentineFirstRow == 0 ? "left" : "right")
                + " · `firstshift` " + (AIOMVigilanceConfig.serpentineFirstShift == 1 ? "forward" : "back")
                + " · `waterexit` " + WATER_EXITS[clamp(AIOMVigilanceConfig.serpentineWaterExit, 0, 3)] + "\n"
                + "`walkbackstep` " + WALKBACK_STEPS[clamp(AIOMVigilanceConfig.serpentineAfterWalkBackStep, 0, 2)]
                + " · `walkbackstrafe` " + WALKBACK_STRAFES[clamp(AIOMVigilanceConfig.serpentineAfterWalkBackStrafe, 0, 2)] + "\n"
                + "`pumpkindir` " + (AIOMVigilanceConfig.pumpkinMelonFirstDirection == 0 ? "left" : "right") + "\n"
                + "`turnmin` " + AIOMVigilanceConfig.lookTurnTimeMin + " · `turnmax` " + AIOMVigilanceConfig.lookTurnTimeMax
                + " · `turnorder` " + TURN_ORDERS[clamp(AIOMVigilanceConfig.lookTurnOrder, 0, 3)]
                + " · `turnspeed` " + AIOMVigilanceConfig.turnSpeed + "\n"
                + "`antistuck` " + onOff(AIOMVigilanceConfig.antiStuckFailsafe) + " · `setspawn` " + onOff(AIOMVigilanceConfig.setSpawnFailsafe)
                + " · `alerts` " + onOff(AIOMVigilanceConfig.discordBotAlerts) + " · `ping` " + PINGS[clamp(AIOMVigilanceConfig.discordAlertPing, 0, 2)]
                + " · `devmode` " + onOff(AIOMVigilanceConfig.devmode) + "\n"
                + "Change one with `!config " + name + " <setting> <value>`";
    }

    /** Changes one setting. Returns what to reply. */
    public static String set(String key, String value) {
        String v = value.toLowerCase();
        String result;
        switch (key.toLowerCase()) {
            case "macro": {
                if (MacroHandler.isMacroOn) {
                    return "⚠️ Stop the macro first (`!stop " + Discord.playerName() + "`), then change the macro type.";
                }
                int type = findMacro(value);
                if (type < 0) {
                    return "⚠️ Unknown macro `" + value + "`. Use a number (0-" + (macroNames().length - 1) + ") or part of the name, e.g. `mushroom`.";
                }
                AIOMVigilanceConfig.macroType = type;
                result = "macro → " + macroName(type);
                break;
            }
            case "pitch":
            case "yaw": {
                double angle;
                try {
                    angle = Double.parseDouble(value.replace(',', '.'));
                } catch (NumberFormatException e) {
                    return "⚠️ `" + value + "` isn't a number.";
                }
                double limit = key.equalsIgnoreCase("pitch") ? 90 : 180;
                if (angle < -limit || angle > limit) {
                    return "⚠️ " + key + " must be between -" + (int) limit + " and " + (int) limit + ".";
                }
                if (key.equalsIgnoreCase("pitch")) {
                    AIOMVigilanceConfig.pitchText = value.replace(',', '.');
                } else {
                    AIOMVigilanceConfig.yawText = value.replace(',', '.');
                }
                // A running macro turns to the new angle straight away
                if (MacroHandler.isMacroOn) {
                    SetPlayerLook.setDefault();
                }
                result = key.toLowerCase() + " → " + value;
                break;
            }
            case "rowshift":
                AIOMVigilanceConfig.serpentineRowShift = number(value, 1, 10);
                result = "rowshift → " + AIOMVigilanceConfig.serpentineRowShift + " blocks";
                break;
            case "firstrow":
                AIOMVigilanceConfig.serpentineFirstRow = choice(v, new String[]{"left", "a"}, new String[]{"right", "d"});
                result = "firstrow → " + (AIOMVigilanceConfig.serpentineFirstRow == 0 ? "left" : "right");
                break;
            case "firstshift":
                AIOMVigilanceConfig.serpentineFirstShift = choice(v, new String[]{"back", "backward", "s"}, new String[]{"forward", "w"});
                result = "firstshift → " + (AIOMVigilanceConfig.serpentineFirstShift == 1 ? "forward" : "back");
                break;
            case "waterexit":
                AIOMVigilanceConfig.serpentineWaterExit = index(v.equals("a") ? "left" : v.equals("d") ? "right" : v, WATER_EXITS);
                result = "waterexit → " + WATER_EXITS[AIOMVigilanceConfig.serpentineWaterExit];
                break;
            case "walkbackstep":
                AIOMVigilanceConfig.serpentineAfterWalkBackStep = index(v.equals("w") ? "forward" : v.equals("s") ? "back" : v, WALKBACK_STEPS);
                result = "walkbackstep → " + WALKBACK_STEPS[AIOMVigilanceConfig.serpentineAfterWalkBackStep];
                break;
            case "walkbackstrafe":
                AIOMVigilanceConfig.serpentineAfterWalkBackStrafe = index(v.equals("a") ? "left" : v.equals("d") ? "right" : v, WALKBACK_STRAFES);
                result = "walkbackstrafe → " + WALKBACK_STRAFES[AIOMVigilanceConfig.serpentineAfterWalkBackStrafe];
                break;
            case "devmode":
                AIOMVigilanceConfig.devmode = onOff(v);
                result = "devmode → " + onOff(AIOMVigilanceConfig.devmode);
                break;
            case "pumpkindir":
                AIOMVigilanceConfig.pumpkinMelonFirstDirection = choice(v, new String[]{"left", "a"}, new String[]{"right", "d"});
                result = "pumpkindir → " + (AIOMVigilanceConfig.pumpkinMelonFirstDirection == 0 ? "left" : "right");
                break;
            case "turnmin":
                AIOMVigilanceConfig.lookTurnTimeMin = number(value, 0, 5000);
                result = "turnmin → " + AIOMVigilanceConfig.lookTurnTimeMin + " ms";
                break;
            case "turnmax":
                AIOMVigilanceConfig.lookTurnTimeMax = number(value, 0, 5000);
                result = "turnmax → " + AIOMVigilanceConfig.lookTurnTimeMax + " ms";
                break;
            case "turnorder":
                AIOMVigilanceConfig.lookTurnOrder = index(v, TURN_ORDERS);
                result = "turnorder → " + TURN_ORDERS[AIOMVigilanceConfig.lookTurnOrder];
                break;
            case "turnspeed":
                AIOMVigilanceConfig.turnSpeed = number(value, 1, 50);
                result = "turnspeed → " + AIOMVigilanceConfig.turnSpeed;
                break;
            case "antistuck":
                AIOMVigilanceConfig.antiStuckFailsafe = onOff(v);
                result = "antistuck → " + onOff(AIOMVigilanceConfig.antiStuckFailsafe);
                break;
            case "setspawn":
                AIOMVigilanceConfig.setSpawnFailsafe = onOff(v);
                result = "setspawn → " + onOff(AIOMVigilanceConfig.setSpawnFailsafe);
                break;
            case "alerts":
                AIOMVigilanceConfig.discordBotAlerts = onOff(v);
                result = "alerts → " + onOff(AIOMVigilanceConfig.discordBotAlerts);
                break;
            case "ping":
                AIOMVigilanceConfig.discordAlertPing = index(v.replace("@", ""), PINGS);
                result = "ping → " + PINGS[AIOMVigilanceConfig.discordAlertPing];
                break;
            default:
                return "⚠️ Unknown setting `" + key + "`. Use `!config " + Discord.playerName() + "` to see them all.";
        }
        AIOMVigilanceConfig.INSTANCE.markDirty();
        AIOMVigilanceConfig.INSTANCE.writeData();
        return "⚙️ " + result;
    }

    // Values that don't fit throw, and the message is shown in Discord
    static class BadValue extends RuntimeException {
        BadValue(String message) {
            super(message);
        }
    }

    private static int number(String value, int min, int max) {
        try {
            int n = Integer.parseInt(value.trim());
            if (n < min || n > max) {
                throw new BadValue("⚠️ Must be between " + min + " and " + max + ".");
            }
            return n;
        } catch (NumberFormatException e) {
            throw new BadValue("⚠️ `" + value + "` isn't a whole number.");
        }
    }

    private static int choice(String value, String[] first, String[] second) {
        for (String option : first) {
            if (option.equals(value)) {
                return 0;
            }
        }
        for (String option : second) {
            if (option.equals(value)) {
                return 1;
            }
        }
        throw new BadValue("⚠️ Use `" + first[0] + "` or `" + second[0] + "`.");
    }

    private static int index(String value, String[] options) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(value)) {
                return i;
            }
        }
        throw new BadValue("⚠️ Use one of: " + String.join(", ", options) + ".");
    }

    private static boolean onOff(String value) {
        if (value.equals("on") || value.equals("true") || value.equals("yes")) {
            return true;
        }
        if (value.equals("off") || value.equals("false") || value.equals("no")) {
            return false;
        }
        throw new BadValue("⚠️ Use `on` or `off`.");
    }

    private static String onOff(boolean value) {
        return value ? "on" : "off";
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    // Macro by number or by (part of) its name, e.g. "12", "mushroom", "pumpkin"
    private static int findMacro(String value) {
        String[] names = macroNames();
        try {
            int n = Integer.parseInt(value.trim());
            return n >= 0 && n < names.length ? n : -1;
        } catch (NumberFormatException ignored) {
        }
        String wanted = value.toLowerCase().replace(" ", "");
        for (int i = 0; i < names.length; i++) {
            if (names[i].toLowerCase().replace(" ", "").equals(wanted)) {
                return i;
            }
        }
        int found = -1;
        for (int i = 0; i < names.length; i++) {
            if (names[i].toLowerCase().replace(" ", "").contains(wanted)) {
                if (found >= 0) {
                    return -1; // more than one match, be specific
                }
                found = i;
            }
        }
        return found;
    }

    static String[] macroNames() {
        try {
            return AIOMVigilanceConfig.class.getField("macroType").getAnnotation(Property.class).options();
        } catch (Exception e) {
            return new String[0];
        }
    }

    static String macroName(int type) {
        String[] names = macroNames();
        return type >= 0 && type < names.length ? names[type] : "Macro " + type;
    }
}
