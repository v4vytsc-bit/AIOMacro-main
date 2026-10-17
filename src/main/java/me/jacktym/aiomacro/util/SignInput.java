package me.jacktym.aiomacro.util;

import net.minecraft.tileentity.TileEntitySign;

/**
 * The sign currently being edited (set when a sign editor opens). Used to type into signs,
 * e.g. the auction house search.
 */
public class SignInput {
    public static TileEntitySign currentSign = null;
}
