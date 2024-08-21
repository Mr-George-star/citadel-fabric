package net.george.citadel.mixin;

import net.george.citadel.CitadelConstants;
import net.george.citadel.server.generation.SurfaceRulesManager;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(NoiseChunkGenerator.class)
public class NoiseChunkGeneratorMixin {
    @Unique
    private boolean mergedSurfaceRules = false;
    @Unique
    private MaterialRules.MaterialRule mergedSurfaceRule = null;

    @Redirect(
            method = {"buildSurface(Lnet/minecraft/world/chunk/Chunk;Lnet/minecraft/world/gen/HeightContext;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/biome/source/BiomeAccess;Lnet/minecraft/util/registry/Registry;Lnet/minecraft/world/gen/chunk/Blender;)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/ChunkGeneratorSettings;surfaceRule()Lnet/minecraft/world/gen/surfacebuilder/MaterialRules$MaterialRule;")
    )
    private MaterialRules.MaterialRule citadel$buildSurface_surfaceRuleRedirect(ChunkGeneratorSettings instance) {
        return getMergedSurfaceRule(instance.surfaceRule());
    }

    @Redirect(
            method = {"carve"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/chunk/ChunkGeneratorSettings;surfaceRule()Lnet/minecraft/world/gen/surfacebuilder/MaterialRules$MaterialRule;")
    )
    private MaterialRules.MaterialRule citadel$applyCarvers_surfaceRuleRedirect(ChunkGeneratorSettings instance) {
        return getMergedSurfaceRule(instance.surfaceRule());
    }

    @Unique
    private MaterialRules.MaterialRule getMergedSurfaceRule(MaterialRules.MaterialRule rules) {
        if (!this.mergedSurfaceRules) {
            this.mergedSurfaceRules = true;
            this.mergedSurfaceRule = SurfaceRulesManager.mergeOverworldRules(rules);
        }
        return this.mergedSurfaceRule == null ? rules : this.mergedSurfaceRule;
    }
}
