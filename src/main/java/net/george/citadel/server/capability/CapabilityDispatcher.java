package net.george.citadel.server.capability;

import com.google.common.collect.Lists;
import net.george.citadel.util.INbtCompoundSerializable;
import net.george.citadel.util.INbtSerializable;
import net.george.citadel.util.LazyOptional;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.annotation.MethodsReturnNonnullByDefault;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public final class CapabilityDispatcher implements INbtCompoundSerializable, ICapabilityProvider {
    private final ICapabilityProvider[] providers;
    private final INbtSerializable<NbtElement>[] writers;
    private final String[] names;
    private final List<Runnable> listeners;

    public CapabilityDispatcher(Map<Identifier, ICapabilityProvider> list, List<Runnable> listeners) {
        this(list, listeners, null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public CapabilityDispatcher(Map<Identifier, ICapabilityProvider> list, List<Runnable> listeners, @Nullable ICapabilityProvider parent) {
        List<ICapabilityProvider> providerList = Lists.newArrayList();
        List<INbtSerializable<NbtElement>> writerList = Lists.newArrayList();
        List<String> nameList = Lists.newArrayList();
        this.listeners = listeners;
        if (parent != null) {
            providerList.add(parent);
            if (parent instanceof INbtSerializable) {
                writerList.add((INbtSerializable)parent);
                nameList.add("Parent");
            }
        }

        for (Map.Entry<Identifier, ICapabilityProvider> entry : list.entrySet()) {
            ICapabilityProvider provider = entry.getValue();
            providerList.add(provider);
            if (provider instanceof INbtSerializable) {
                writerList.add((INbtSerializable) provider);
                nameList.add(entry.getKey().toString());
            }
        }

        this.providers = providerList.toArray(new ICapabilityProvider[0]);
        this.writers = (INbtSerializable[])writerList.toArray(new INbtSerializable[0]);
        this.names = nameList.toArray(new String[0]);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        for (ICapabilityProvider provider : this.providers) {
            LazyOptional<T> value = provider.getCapability(capability, side);
            if (value == null) {
                throw new RuntimeException(String.format(Locale.ENGLISH, "Provider %s.getCapability() returned null; return LazyOptional.empty() instead!", provider.getClass().getTypeName()));
            }

            if (value.isPresent()) {
                return value;
            }
        }

        return LazyOptional.empty();
    }

    @Override
    public NbtCompound serialize() {
        NbtCompound nbt = new NbtCompound();

        for (int x = 0; x < this.writers.length; ++x) {
            nbt.put(this.names[x], this.writers[x].serialize());
        }

        return nbt;
    }

    @Override
    public void deserialize(NbtCompound nbt) {
        for (int x = 0; x < this.writers.length; ++x) {
            if (nbt.contains(this.names[x])) {
                this.writers[x].deserialize(nbt.get(this.names[x]));
            }
        }
    }

    public boolean areCompatible(@Nullable CapabilityDispatcher other) {
        if (other == null) {
            return this.writers.length == 0;
        } else if (this.writers.length == 0) {
            return other.writers.length == 0;
        } else {
            return this.serialize().equals(other.serialize());
        }
    }

    public void invalidate() {
        this.listeners.forEach(Runnable::run);
    }
}
