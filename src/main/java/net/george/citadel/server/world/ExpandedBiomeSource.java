package net.george.citadel.server.world;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public interface ExpandedBiomeSource {
    void setResourceKeyMap(Map<RegistryKey<Biome>, RegistryEntry<Biome>> map);

    Map<RegistryKey<Biome>, RegistryEntry<Biome>> getResourceKeyMap();

    void expandBiomesWith(Set<RegistryEntry<Biome>> newGenBiomes);
}
