package net.george.citadel.config.biome;

import com.google.gson.*;
import net.george.citadel.Citadel;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpawnBiomeData {
    private List<List<SpawnBiomeEntry>> biomes = new ArrayList<>();

    public SpawnBiomeData() {
    }

    private SpawnBiomeData(SpawnBiomeEntry[][] biomesRead) {
        this.biomes = new ArrayList<>();
        for (SpawnBiomeEntry[] innerArray : biomesRead) {
            this.biomes.add(Arrays.asList(innerArray));
        }
    }

    public SpawnBiomeData addBiomeEntry(BiomeEntryType type, boolean negate, String value, int pool) {
        if (this.biomes.isEmpty() || this.biomes.size() < pool + 1) {
            this.biomes.add(new ArrayList<>());
        }
        this.biomes.get(pool).add(new SpawnBiomeEntry(type, negate, value));
        return this;
    }

    public boolean matches(@Nullable RegistryEntry<Biome> biomeHolder, Identifier registryName) {
        for (List<SpawnBiomeEntry> all : this.biomes) {
            boolean overall = true;
            for (SpawnBiomeEntry cond : all) {
                if (!cond.matches(biomeHolder, registryName)) {
                    overall = false;
                }
            }
            if (overall) {
                return true;
            }
        }
        return false;
    }

    public static class Deserializer implements JsonDeserializer<SpawnBiomeData>, JsonSerializer<SpawnBiomeData> {
        @Override
        public SpawnBiomeData deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            SpawnBiomeEntry[][] biomesRead = JsonHelper.deserialize(object, "biomes", new SpawnBiomeEntry[0][0], context, SpawnBiomeEntry[][].class);
            return new SpawnBiomeData(biomesRead);
        }

        @Override
        public JsonElement serialize(SpawnBiomeData data, Type type, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add("biomes", context.serialize(data.biomes));
            return object;
        }
    }

    private static class SpawnBiomeEntry {
        BiomeEntryType type;
        boolean negate;
        String value;

        public SpawnBiomeEntry(BiomeEntryType type, boolean remove, String value) {
            this.type = type;
            this.negate = remove;
            this.value = value;
        }

        public boolean matches(@Nullable RegistryEntry<Biome> biomeHolder, Identifier registryName) {
            if (this.type.isDepreciated()) {
                Citadel.LOGGER.warn("biome config: BIOME_DICT and BIOME_CATEGORY are no longer valid in 1.19+. Please use BIOME_TAG instead.");
                return false;
            } else {
                if (this.type == BiomeEntryType.BIOME_TAG) {
                    if (biomeHolder.streamTags().anyMatch((biomeTagKey -> biomeTagKey.id() != null && biomeTagKey.id().toString().equals(this.value)))) {
                        return !this.negate;
                    }
                } else {
                    if (registryName.toString().equals(this.value)) {
                        return !this.negate;
                    }
                }
                return this.negate;
            }
        }
    }
}
