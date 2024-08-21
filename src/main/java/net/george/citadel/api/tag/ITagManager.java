package net.george.citadel.api.tag;

import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface ITagManager<V> extends Iterable<ITag<V>> {
    @NotNull ITag<V> getTag(@NotNull TagKey<V> key);

    boolean isKnownTagName(@NotNull TagKey<V> key);

    @NotNull Stream<ITag<V>> stream();

    @NotNull Stream<TagKey<V>> getTagNames();

    @NotNull TagKey<V> createTagKey(@NotNull Identifier id);
}
