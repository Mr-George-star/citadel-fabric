package net.george.citadel.mixin.client;

import net.george.citadel.Citadel;
import net.george.citadel.client.gui.GuiCitadelCapesConfig;
import net.george.citadel.client.gui.GuiCitadelPatreonConfig;
import net.george.citadel.client.rewards.CitadelCapes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(SkinOptionsScreen.class)
public abstract class SkinOptionsScreenMixin extends GameOptionsScreen {
    public SkinOptionsScreenMixin(Screen parent, GameOptions gameOptions) {
        super(parent, gameOptions, Text.translatable("options.skinCustomisation.title"));
    }

    @SuppressWarnings("CallToPrintStackTrace")
    @Inject(method = "init", at = @At("HEAD"))
    private void citadel$init(CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null) {
            try {
                String username = MinecraftClient.getInstance().player.getName().getString();
                int height = -20;
                if (Citadel.PATRONS.contains(username)) {
                    this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 150 + height, 200, 20, Text.translatable("citadel.gui.patreon_rewards_option").formatted(Formatting.GREEN), (button) ->
                            MinecraftClient.getInstance().setScreen(new GuiCitadelPatreonConfig(this, MinecraftClient.getInstance().options))));
                    height += 25;
                }
                if (!CitadelCapes.getCapesFor(MinecraftClient.getInstance().player.getUuid()).isEmpty()) {
                    this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 150 + height, 200, 20, Text.translatable("citadel.gui.capes_option").formatted(Formatting.GREEN), (button) ->
                            MinecraftClient.getInstance().setScreen(new GuiCitadelCapesConfig(this, MinecraftClient.getInstance().options))));
                    height += 25;
                }
            } catch (Exception exception) {
               Citadel.LOGGER.catching(exception);
            }
        }
    }
}
