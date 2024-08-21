package net.george.citadel.config.biome;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.george.citadel.Citadel;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings({"UnstableApiUsage", "deprecation"})
public class SpawnBiomeConfig {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(SpawnBiomeData.class, new SpawnBiomeData.Deserializer()).create();
    private final Identifier fileName;

    private SpawnBiomeConfig(Identifier fileName) {
        if (!fileName.getNamespace().endsWith(".json")) {
            this.fileName = new Identifier(fileName.getNamespace(), fileName.getPath() + ".json");
        } else {
            this.fileName = fileName;
        }
    }

    public static SpawnBiomeData create(Identifier fileName, SpawnBiomeData dataDefault) {
        SpawnBiomeConfig config = new SpawnBiomeConfig(fileName);
        return config.getConfigData(dataDefault);
    }

    public static <T> T getOrCreateConfigFile(File configDir, String configName, T defaults, Type type) {
        File configFile = new File(configDir, configName);
        if (!configFile.exists()) {
            try {
                FileUtils.write(configFile, GSON.toJson(defaults));
            } catch (IOException exception) {
                Citadel.LOGGER.error("Spawn Biome Config: Could not write " + configFile, exception);
            }
        }
        try {
            return GSON.fromJson(FileUtils.readFileToString(configFile), type);
        } catch (Exception exception) {
            Citadel.LOGGER.error("Spawn Biome Config: Could not load " + configFile, exception);
        }

        return defaults;
    }

    private File getConfigDirFile() {
        Path configPath = FabricLoader.getInstance().getConfigDir();
        Path jsonPath = Paths.get(configPath.toAbsolutePath().toString(), fileName.getNamespace());
        return jsonPath.toFile();
    }

    private SpawnBiomeData getConfigData(SpawnBiomeData defaultConfigData) {
        return getOrCreateConfigFile(getConfigDirFile(), fileName.getPath(), defaultConfigData, new TypeToken<SpawnBiomeData>() {
        }.getType());
    }
}
