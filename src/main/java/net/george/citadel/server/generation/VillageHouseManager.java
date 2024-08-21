package net.george.citadel.server.generation;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.george.citadel.Citadel;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class VillageHouseManager {
    public static final List<String> VILLAGE_REPLACEMENT_POOLS = List.of(
            "minecraft:village/plains/houses",
            "minecraft:village/desert/houses",
            "minecraft:village/savanna/houses",
            "minecraft:village/snowy/houses",
            "minecraft:village/taiga/houses");
    private static final List<Pair<String, Consumer<StructurePool>>> REGISTRY = new ArrayList<>();

    public static void register(String pool, Consumer<StructurePool> addToPool) {
        REGISTRY.add(new Pair<>(pool, addToPool));
        Citadel.LOGGER.debug("registered addition to pool: " + pool);
    }

    public static StructurePool addToPool(StructurePool pool, StructurePoolElement element, int weight) {
        if (weight > 0) {
            if (pool != null) {
                ObjectArrayList<StructurePoolElement> elements = new ObjectArrayList<>(pool.elements);
                if (!elements.contains(element)) {
                    for (int i = 0; i < weight; i++) {
                        elements.add(element);
                    }
                    List<Pair<StructurePoolElement, Integer>> elementCounts = new ArrayList<>(pool.elementCounts);
                    elementCounts.add(new Pair<>(element, weight));
                    pool.elements = elements;
                    pool.elementCounts = elementCounts;
                    Citadel.LOGGER.info("Added to " + pool.getId() + " structure pool");
                }
            }
        }
        return pool;
    }

    public static void addAllHouses(DynamicRegistryManager registryAccess) {
        try {
            for (String villagePool : VILLAGE_REPLACEMENT_POOLS) {
                StructurePool pool = registryAccess.get(Registry.STRUCTURE_POOL_KEY).getOrEmpty(new Identifier(villagePool)).orElse(null);
                if (pool != null) {
                    String poolName = pool.getId().toString();
                    for (Pair<String, Consumer<StructurePool>> pair : REGISTRY) {
                        if (pair.getFirst().equals(poolName)) {
                            pair.getSecond().accept(pool);
                        }
                    }
                }
            }
        } catch (Exception exception) {
            Citadel.LOGGER.error("Could not add village houses!");
            Citadel.LOGGER.catching(exception);
        }
    }
}
