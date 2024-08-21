package net.george.citadel.mixin;

import net.george.citadel.client.event.ScreenRenderEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    private void citadel$redirect_render(Screen instance, MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ScreenRenderEvents.drawScreen(instance, matrices, mouseX, mouseY, delta);
    }
}
