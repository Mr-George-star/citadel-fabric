package net.george.citadel.util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public interface NonNullLazy<T> extends NonNullSupplier<T> {
    static <T> NonNullLazy<T> of(@NotNull NonNullSupplier<T> supplier) {
        Objects.requireNonNull(supplier);
        Lazy<T> lazy = Lazy.of(supplier::get);
        return () -> Objects.requireNonNull(lazy.get());
    }

    static <T> NonNullLazy<T> concurrentOf(@NotNull NonNullSupplier<T> supplier) {
        Objects.requireNonNull(supplier);
        Lazy<T> lazy = Lazy.concurrentOf(supplier::get);
        return () -> Objects.requireNonNull(lazy.get());
    }
}
