package net.george.citadel.api.tag;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public class CitadelRegistryTag<T> implements ITag<T> {
    private final TagKey<T> key;
    private final RegistryEntryList.Named<T> entries;
    private final List<T> contents;

    public CitadelRegistryTag(@NotNull TagKey<T> key, @NotNull Registry<T> registry) {
        this(key, new RegistryEntryList.Named<>(registry, key));
    }

    public CitadelRegistryTag(@NotNull TagKey<T> key, @NotNull RegistryEntryList.Named<T> entries) {
        this.key = key;
        this.entries = entries;
        this.contents = entries.stream().map(RegistryEntry::value).toList();
    }

    @Override
    public TagKey<T> getKey() {
        return this.key;
    }

    @Override
    public RegistryEntryList.Named<T> getEntries() {
        return this.entries;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return this.getContents().iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return this.getContents().spliterator();
    }

    @Override
    public boolean isEmpty() {
        return this.getContents().isEmpty();
    }

    @Override
    public int size() {
        return this.getContents().size();
    }

    @Override
    public Stream<T> stream() {
        return this.getContents().stream();
    }

    @Override
    public boolean contains(T value) {
        return this.getContents().contains(value);
    }

    @Override
    public Optional<T> getRandomElement(Random random) {
        return Util.getRandomOrEmpty(this.getContents(), random);
    }

    public List<T> getContents() {
        return this.contents;
    }

    @Override
    public String toString() {
        return "CitadelRegistryTag{" +
                "key=" + this.key +
                ", entries=" + this.entries +
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
        CitadelRegistryTag<?> that = (CitadelRegistryTag<?>) object;
        return Objects.equals(this.key, that.key) && Objects.equals(this.entries, that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.entries);
    }
}
