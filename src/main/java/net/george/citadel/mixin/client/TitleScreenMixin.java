package net.george.citadel.mixin.client;

import net.george.citadel.CitadelConstants;
import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.api.event.EventResult;
import net.george.citadel.client.event.EventRenderSplashText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Shadow
    @Nullable
    private String splashText;
    @Unique
    private int splashTextColor = -1;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(
            method = {"render"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V",
                    shift = At.Shift.BEFORE
            )
    )
    protected void citadel$preRenderSplashText(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        matrices.push();
        EventRenderSplashText.Pre event = new EventRenderSplashText.Pre(this.splashText, matrices, (TitleScreen) (Screen) this, delta, this.splashTextColor);
        if (CitadelEventManager.INSTANCE.send(event) == EventResult.ALLOW) {
            this.splashText = event.getSplashText();
            this.splashTextColor = event.getSplashTextColor();
        }
    }

    @Inject(
            method = {"render"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V",
                    shift = At.Shift.AFTER
            )
    )
    protected void citadel$postRenderSplashText(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        EventRenderSplashText.Post event = new EventRenderSplashText.Post(this.splashText, matrices, (TitleScreen)(Screen) this, delta);
        CitadelEventManager.INSTANCE.send(event);
        matrices.pop();
    }


    @ModifyConstant(
            method = {"render"},
            constant = @Constant(intValue = 16776960))
    private int citadel$splashTextColor(int value) {
        return this.splashTextColor == -1 ? value : this.splashTextColor;
    }
}
