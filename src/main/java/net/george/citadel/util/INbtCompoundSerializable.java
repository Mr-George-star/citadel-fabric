package net.george.citadel.util;

import net.minecraft.nbt.NbtCompound;

@SuppressWarnings("unused")
public interface INbtCompoundSerializable extends INbtSerializable<NbtCompound> {
    @Override
    NbtCompound serialize();

    @Override
    void deserialize(NbtCompound nbt);
}
