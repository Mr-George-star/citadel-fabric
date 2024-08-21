package net.george.citadel.api.tag;

import net.minecraft.tag.TagKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntryList;

import java.util.Optional;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface ITag<T> extends Iterable<T> {
    TagKey<T> getKey();

    RegistryEntryList.Named<T> getEntries();

    Stream<T> stream();

    boolean isEmpty();

    int size();

    boolean contains(T value);

    Optional<T> getRandomElement(Random random);
}
