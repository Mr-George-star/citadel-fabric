package net.george.citadel.mixin.client;

import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.client.event.ScreenOpeningEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    @Nullable
    public Screen currentScreen;
    @Shadow @Nullable
    public ClientPlayerEntity player;

    @SuppressWarnings("UnusedAssignment")
    @Inject(method = "setScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", shift = At.Shift.BEFORE, ordinal = 2), cancellable = true)
    private void citadel$field(Screen screen, CallbackInfo ci) {
        Screen old = this.currentScreen;
        if (screen != null) {
            ScreenOpeningEvent context = new ScreenOpeningEvent(old, screen);
            if (CitadelEventManager.INSTANCE.send(context)) {
                ci.cancel();
            }
            screen = context.getNewScreen();
        }
    }
}
