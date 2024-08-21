package net.george.citadel.util;

@SuppressWarnings("unused")
@FunctionalInterface
public interface NonNullConsumer<T> {
    void accept(T value);
}
