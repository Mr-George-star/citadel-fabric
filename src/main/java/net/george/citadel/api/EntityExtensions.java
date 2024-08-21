package net.george.citadel.api;

@SuppressWarnings("unused")
public interface EntityExtensions {
    default boolean shouldRiderSit() {
        return true;
    }

    default boolean isAddedToWorld() {
        return false;
    }

    default void onAddedToWorld() {}

    default void onRemovedFromWorld() {}
}
