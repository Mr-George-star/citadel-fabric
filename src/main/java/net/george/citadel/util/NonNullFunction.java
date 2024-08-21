package net.george.citadel.util;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@FunctionalInterface
public interface NonNullFunction<T, R> {
    @NotNull R apply(@NotNull T value);
}
