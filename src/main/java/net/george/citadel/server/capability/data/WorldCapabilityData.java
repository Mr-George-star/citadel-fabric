package net.george.citadel.server.capability.data;

import net.george.citadel.util.INbtCompoundSerializable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class WorldCapabilityData extends PersistentState {
    public static final String ID = "capabilities";
    private INbtCompoundSerializable serializable;
    private NbtCompound capabilityNbt = null;

    public WorldCapabilityData(@Nullable INbtCompoundSerializable serializable) {
        this.serializable = serializable;
    }

    public static WorldCapabilityData load(NbtCompound nbt, @Nullable INbtCompoundSerializable serializable) {
        WorldCapabilityData data = new WorldCapabilityData(serializable);
        data.readNbt(nbt);
        return data;
    }

    public void readNbt(NbtCompound nbt) {
        this.capabilityNbt = nbt;
        if (this.serializable != null) {
            this.serializable.deserialize(this.capabilityNbt);
            this.capabilityNbt = null;
        }
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        if (this.serializable != null) {
            nbt = this.serializable.serialize();
        }

        return nbt;
    }

    public boolean isDirty() {
        return true;
    }

    public void setCapabilities(INbtCompoundSerializable capabilities) {
        this.serializable = capabilities;
        if (this.capabilityNbt != null && this.serializable != null) {
            this.serializable.deserialize(this.capabilityNbt);
            this.capabilityNbt = null;
        }
    }
}
