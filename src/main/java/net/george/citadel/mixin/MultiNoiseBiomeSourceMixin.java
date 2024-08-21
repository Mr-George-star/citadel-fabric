package net.george.citadel.mixin;

import net.george.citadel.CitadelConstants;
import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.api.event.EventResult;
import net.george.citadel.server.event.EventReplaceBiome;
import net.george.citadel.server.world.ExpandedBiomeSource;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(MultiNoiseBiomeSource.class)
public class MultiNoiseBiomeSourceMixin {
    @Unique
    private int lastSampledX;
    @Unique
    private int lastSampledY;
    @Unique
    private int lastSampledZ;

    @Inject(at = @At("HEAD"), remap = CitadelConstants.REMAPREFS, method = "getBiome")
    private void citadel$getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise, CallbackInfoReturnable<RegistryEntry<Biome>> cir) {
        this.lastSampledX = x;
        this.lastSampledY = y;
        this.lastSampledZ = z;
    }

    @Inject(at = @At("RETURN"), cancellable = true, remap = CitadelConstants.REMAPREFS, method = "getBiomeAtPoint")
    private void citadel$getBiomeAtPoint(MultiNoiseUtil.NoiseValuePoint point, CallbackInfoReturnable<RegistryEntry<Biome>> cir) {
        float f = MultiNoiseUtil.toFloat(point.continentalnessNoise());
        float f1 = MultiNoiseUtil.toFloat(point.erosionNoise());
        float f2 = MultiNoiseUtil.toFloat(point.temperatureNoise());
        float f3 = MultiNoiseUtil.toFloat(point.humidityNoise());
        float f4 = MultiNoiseUtil.toFloat(point.weirdnessNoise());
        float f5 = MultiNoiseUtil.toFloat(point.depth());
        EventReplaceBiome event = new EventReplaceBiome((ExpandedBiomeSource) this, cir.getReturnValue(), this.lastSampledX, this.lastSampledY, this.lastSampledZ, f, f1, f2, f3, f4, f5);
        if (CitadelEventManager.INSTANCE.send(event) == EventResult.ALLOW){
            cir.setReturnValue(cir.getReturnValue());
        }
    }
}
