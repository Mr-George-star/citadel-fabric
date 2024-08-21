package net.george.citadel.mixin;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import net.george.citadel.server.world.ExpandedBiomeSource;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import org.spongepowered.asm.mixin.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(BiomeSource.class)
public class BiomeSourceMixin implements ExpandedBiomeSource {
    @Mutable
    @Shadow
    @Final
    private Set<RegistryEntry<Biome>> biomes;
    @Unique
    private boolean expanded;
    @Unique
    private Map<RegistryKey<Biome>, RegistryEntry<Biome>> map = new HashMap<>();

    @Override
    public void setResourceKeyMap(Map<RegistryKey<Biome>, RegistryEntry<Biome>> map) {
        this.map = map;
    }

    @Override
    public Map<RegistryKey<Biome>, RegistryEntry<Biome>> getResourceKeyMap() {
        return this.map;
    }

    @Override
    public void expandBiomesWith(Set<RegistryEntry<Biome>> newGenBiomes) {
        if (!this.expanded) {
            ImmutableSet.Builder<RegistryEntry<Biome>> builder = ImmutableSet.builder();
            builder.addAll(this.biomes);
            builder.addAll(newGenBiomes);
            Supplier<Set<RegistryEntry<Biome>>> lazyBiomes = Suppliers.memoize(builder::build);
            this.biomes = lazyBiomes.get();
            this.expanded = true;
        }
    }
}
