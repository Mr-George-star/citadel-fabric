package net.george.citadel.api.tag;

import com.google.common.collect.Iterators;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntryList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class CitadelRegistryTagManager<T> implements ITagManager<T> {
    private final Registry<T> owner;
    private Map<TagKey<T>, ITag<T>> tags = new IdentityHashMap<>();

    public CitadelRegistryTagManager(@NotNull Registry<T> registry) {
        this.owner = registry;
        getTagAndEntriesMap(registry).forEach((key, entries) -> this.tags.put(key, new CitadelRegistryTag<>(key, entries)));
    }

    protected Map<TagKey<T>, RegistryEntryList.Named<T>> getTagAndEntriesMap(@NotNull Registry<T> registry) {
        Map<TagKey<T>, RegistryEntryList.Named<T>> map = new HashMap<>();
        registry.streamTagsAndEntries().forEach(pair -> map.put(pair.getFirst(), pair.getSecond()));
        return map;
    }

    @Override
    public @NotNull ITag<T> getTag(@NotNull TagKey<T> name) {
        Objects.requireNonNull(name);
        ITag<T> tag = this.tags.get(name);
        if (tag == null) {
            tag = new CitadelRegistryTag<>(name, this.owner);
            IdentityHashMap<TagKey<T>, ITag<T>> map = new IdentityHashMap<>(this.tags);
            map.put(name, tag);
            this.tags = map;
        }

        return tag;
    }

    @Override
    public boolean isKnownTagName(@NotNull TagKey<T> name) {
        Objects.requireNonNull(name);
        ITag<T> tag = this.tags.get(name);
        return tag != null;
    }

    @Override
    public @NotNull Iterator<ITag<T>> iterator() {
        return Iterators.unmodifiableIterator(this.tags.values().iterator());
    }

    @Override
    public @NotNull Stream<ITag<T>> stream() {
        return this.tags.values().stream();
    }

    @Override
    public @NotNull Stream<TagKey<T>> getTagNames() {
        return this.tags.keySet().stream();
    }

    @Override
    public @NotNull TagKey<T> createTagKey(@NotNull Identifier id) {
        Objects.requireNonNull(id);
        return TagKey.of(this.owner.getKey(), id);
    }

    @Override
    public String toString() {
        return "CitadelRegistryTagManager{" +
                "owner=" + this.owner +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        CitadelRegistryTagManager<?> that = (CitadelRegistryTagManager<?>) object;
        return Objects.equals(this.owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.owner);
    }
}
