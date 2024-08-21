package net.george.citadel.server.capability;

import net.george.citadel.util.INbtSerializable;
import net.minecraft.nbt.NbtElement;

@SuppressWarnings("unused")
public interface ICapabilitySerializable<T extends NbtElement> extends ICapabilityProvider, INbtSerializable<T> {
}
