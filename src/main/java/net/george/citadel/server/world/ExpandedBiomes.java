package net.george.citadel.server.world;

import com.google.common.collect.ImmutableSet;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;

import java.util.*;

@SuppressWarnings("unused")
public class ExpandedBiomes {
    private static final Map<RegistryKey<DimensionOptions>, List<RegistryKey<Biome>>> biomes = new HashMap<>();

    public static void addExpandedBiome(RegistryKey<Biome> biome, RegistryKey<DimensionOptions> dimension) {
        List<RegistryKey<Biome>> list;
        if (!biomes.containsKey(dimension)) {
            list = new ArrayList<>();
        } else {
            list = biomes.get(dimension);
        }
        if (!list.contains(biome)) {
            list.add(biome);
        }
        biomes.put(dimension, list);
    }

    public static Set<RegistryEntry<Biome>> buildBiomeList(DynamicRegistryManager registryAccess, RegistryKey<DimensionOptions> dimension) {
        List<RegistryKey<Biome>> list = biomes.get(dimension);
        if (list == null || list.isEmpty()) {
            return Set.of();
        }
        Registry<Biome> allBiomes = registryAccess.get(Registry.BIOME_KEY);
        ImmutableSet.Builder<RegistryEntry<Biome>> biomeHolders = ImmutableSet.builder();
        for (RegistryKey<Biome> biomeResourceKey : list) {
            Optional<RegistryEntry<Biome>> holderOptional = allBiomes.getEntry(biomeResourceKey);
            holderOptional.ifPresent(biomeHolders::add);
        }
        return biomeHolders.build();
    }
}
