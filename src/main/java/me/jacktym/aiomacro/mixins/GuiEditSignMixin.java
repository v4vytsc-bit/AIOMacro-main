package me.jacktym.aiomacro.mixins;

import me.jacktym.aiomacro.features.AHReSkin;
import me.jacktym.aiomacro.util.SignInput;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiEditSign.class)
public class GuiEditSignMixin {
    @Inject(at = @At(value = "RETURN"), method = "<init>")
    private void getSign(TileEntitySign tileEntitySign, CallbackInfo ci) {
        SignInput.currentSign = tileEntitySign;
    }

    @Inject(method = "drawScreen", at = @At("HEAD"), cancellable = true)
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (AHReSkin.auctionHouseOpen) {
            ci.cancel();
        }
    }
}
