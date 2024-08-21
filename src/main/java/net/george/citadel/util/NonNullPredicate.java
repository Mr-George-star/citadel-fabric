package net.george.citadel.util;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@FunctionalInterface
public interface NonNullPredicate<T> {
    boolean test(@NotNull T value);
}
