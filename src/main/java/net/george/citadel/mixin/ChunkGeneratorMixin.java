package net.george.citadel.mixin;

import net.george.citadel.CitadelConstants;
import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.api.event.EventResult;
import net.george.citadel.server.event.EventMergeStructureSpawns;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(ChunkGenerator.class)
public class ChunkGeneratorMixin {
    @Inject(at = @At("RETURN"), remap = CitadelConstants.REMAPREFS, cancellable = true, method = "getEntitySpawnList")
    private void citadel$getEntitySpawnList(RegistryEntry<Biome> biome, StructureAccessor accessor, SpawnGroup group, BlockPos pos, CallbackInfoReturnable<Pool<SpawnSettings.SpawnEntry>> cir) {
        Pool<SpawnSettings.SpawnEntry> biomeSpawns = biome.value().getSpawnSettings().getSpawnEntries(group);
        if (biomeSpawns != cir.getReturnValue()) {
            EventMergeStructureSpawns event = new EventMergeStructureSpawns(accessor, pos, group, cir.getReturnValue(), biomeSpawns);
            if (CitadelEventManager.INSTANCE.send(event) == EventResult.ALLOW) {
                cir.setReturnValue(event.getStructureSpawns());
            }
        }
    }
}
