package net.george.citadel.mixin.client;

import net.george.citadel.CitadelConstants;
import net.george.citadel.client.tick.ClientTickRateTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(
            method = {"getAdjustedPitch"},
            remap = CitadelConstants.REMAPREFS,
            cancellable = true,
            at = @At(value = "RETURN")
    )
    protected void citadel$getAdjustPitch(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(cir.getReturnValue() * ClientTickRateTracker.getForClient(MinecraftClient.getInstance()).modifySoundPitch(sound));
    }
}
