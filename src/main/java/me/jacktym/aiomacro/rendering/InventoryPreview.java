package me.jacktym.aiomacro.rendering;

import me.jacktym.aiomacro.Main;
import me.jacktym.aiomacro.config.AIOMVigilanceConfig;
import me.jacktym.aiomacro.proxy.ClientProxy;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Hold the "Preview Inventory" key to see your inventory as a see-through overlay.
 * It only draws on the HUD (no menu opens), so it can't be clicked or edited and macros keep running.
 */
public class InventoryPreview {

    private static final int SLOT = 18;
    private static final int PADDING = 6;
    private static final int HOTBAR_GAP = 4;
    private static final int ARMOR_GAP = 6;

    @SubscribeEvent
    public void onOverlay(@NotNull RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL || !AIOMVigilanceConfig.inventoryPreview) {
            return;
        }
        // Only on the normal game screen, while the key is held
        if (Main.mcPlayer == null || Main.mc.currentScreen != null || !ClientProxy.keyBindings[10].isKeyDown()) {
            return;
        }

        InventoryPlayer inventory = Main.mcPlayer.inventory;
        ScaledResolution resolution = event.resolution;
        FontRenderer font = Main.mc.fontRendererObj;

        int titleHeight = font.FONT_HEIGHT + 3;
        int width = PADDING * 2 + SLOT + ARMOR_GAP + 9 * SLOT;
        int height = PADDING * 2 + titleHeight + 4 * SLOT + HOTBAR_GAP;
        int left = (resolution.getScaledWidth() - width) / 2;
        int top = (resolution.getScaledHeight() - height) / 2;

        int alpha = Math.max(10, Math.min(100, AIOMVigilanceConfig.inventoryPreviewOpacity)) * 255 / 100;
        Gui.drawRect(left, top, left + width, top + height, (alpha << 24) | 0x101018);
        font.drawStringWithShadow("Inventory", left + PADDING, top + PADDING, 0xFFFFFF);

        int gridTop = top + PADDING + titleHeight;
        int armorLeft = left + PADDING;
        int gridLeft = armorLeft + SLOT + ARMOR_GAP;

        // Slot backgrounds: armor (helmet at the top), main inventory (3 rows), hotbar
        int slotColor = ((alpha * 2 / 3) << 24) | 0x8B8B8B;
        for (int row = 0; row < 4; row++) {
            drawSlot(armorLeft, gridTop + row * SLOT, slotColor);
        }
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(gridLeft + column * SLOT, slotY(gridTop, row), slotColor);
            }
        }
        // Outline the selected hotbar slot
        int selectedX = gridLeft + inventory.currentItem * SLOT;
        int hotbarY = slotY(gridTop, 3);
        drawOutline(selectedX, hotbarY, 0xFFFFFFFF);

        // Items
        RenderItem renderItem = Main.mc.getRenderItem();
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableDepth();
        for (int row = 0; row < 4; row++) {
            drawItem(renderItem, font, inventory.armorInventory[3 - row], armorLeft, gridTop + row * SLOT);
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawItem(renderItem, font, inventory.mainInventory[9 + row * 9 + column], gridLeft + column * SLOT, slotY(gridTop, row));
            }
        }
        for (int column = 0; column < 9; column++) {
            drawItem(renderItem, font, inventory.mainInventory[column], gridLeft + column * SLOT, hotbarY);
        }
        GlStateManager.disableDepth();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    // Hotbar sits a little below the 3 main inventory rows, like in the real inventory
    private static int slotY(int gridTop, int row) {
        return gridTop + row * SLOT + (row == 3 ? HOTBAR_GAP : 0);
    }

    private static void drawSlot(int x, int y, int color) {
        Gui.drawRect(x, y, x + SLOT - 2, y + SLOT - 2, color);
    }

    private static void drawOutline(int x, int y, int color) {
        int size = SLOT - 2;
        Gui.drawRect(x - 1, y - 1, x + size + 1, y, color);
        Gui.drawRect(x - 1, y + size, x + size + 1, y + size + 1, color);
        Gui.drawRect(x - 1, y, x, y + size, color);
        Gui.drawRect(x + size, y, x + size + 1, y + size, color);
    }

    private static void drawItem(RenderItem renderItem, FontRenderer font, ItemStack stack, int x, int y) {
        if (stack == null) {
            return;
        }
        renderItem.renderItemAndEffectIntoGUI(stack, x, y);
        renderItem.renderItemOverlays(font, stack, x, y);
    }
}
