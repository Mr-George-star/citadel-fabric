package net.george.citadel.api.tag;

import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public enum CitadelTagManager {
    INSTANCE;

    private final Map<Registry<?>, CitadelRegistryTagManager<?>> knownTagManagers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> CitadelRegistryTagManager<T> get(@NotNull Registry<T> registry) {
        return (CitadelRegistryTagManager<T>) this.knownTagManagers.get(registry);
    }

    public <T> void append(@NotNull Registry<T> registry, @NotNull CitadelRegistryTagManager<T> manager) {
        this.knownTagManagers.put(registry, manager);
    }

    public <T> boolean tryAppend(@NotNull Registry<T> registry, @NotNull CitadelRegistryTagManager<T> manager) {
        if (!this.knownTagManagers.containsKey(registry)) {
            append(registry, manager);
            return true;
        }
        return false;
    }

    public <T> CitadelRegistryTagManager<T> fromRegistry(@NotNull Registry<T> registry) {
        CitadelRegistryTagManager<T> manager = new CitadelRegistryTagManager<>(registry);
        if (tryAppend(registry, manager)) {
            return get(registry);
        } else {
            return manager;
        }
    }
}
