package net.george.citadel.util;

import net.minecraft.nbt.NbtElement;

@SuppressWarnings("unused")
public interface INbtSerializable<T extends NbtElement> {
    T serialize();

    void deserialize(T nbt);
}
