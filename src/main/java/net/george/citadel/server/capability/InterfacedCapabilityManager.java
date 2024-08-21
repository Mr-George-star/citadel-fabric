package net.george.citadel.server.capability;

import com.google.common.annotations.VisibleForTesting;
import net.george.citadel.server.event.AttachCapabilitiesEvent;
import net.george.citadel.util.LazyOptional;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface InterfacedCapabilityManager<V extends ICapabilityProviderImpl<V>> extends ICapabilityProviderImpl<V> {
    @VisibleForTesting
    boolean SUPPORTS_LAZY_CAPABILITIES = true;
    String CAPABILITY_STORAGE_KEY = "CitadelCapabilities";

    default void setBaseClass(@NotNull Class<V> baseClass) {
    }

    default Class<V> getBaseClass() {
        return null;
    }

    default void setDispatcher(@Nullable CapabilityDispatcher dispatcher) {
    }

    default @Nullable CapabilityDispatcher getDispatcher() {
        return null;
    }

    default void setValid(boolean valid) {
    }

    default boolean isValid() {
        return false;
    }

    default void setLazy(boolean lazy) {
    }

    default boolean isLazy() {
        return false;
    }

    default void setLazyParentSupplier(Supplier<ICapabilityProvider> lazyParentSupplier) {
    }

    default Supplier<ICapabilityProvider> getLazyParentSupplier() {
        return null;
    }

    default void setLazyData(NbtCompound lazyData) {
    }

    default NbtCompound getLazyData() {
        return null;
    }

    default void setInitialized(boolean initialized) {
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean isInitialized() {
        return false;
    }

    default void initBuiltinVars(Class<V> baseClass) {
        this.initBuiltinVars(baseClass, false);
    }

    default void initBuiltinVars(Class<V> baseClass, boolean isLazy) {
        this.setValid(true);
        this.setLazy(false);
        this.setLazyParentSupplier(null);
        this.setLazyData(null);
        this.setInitialized(false);
        this.setBaseClass(baseClass);
        this.setLazy(SUPPORTS_LAZY_CAPABILITIES && isLazy);
    }

    default void gatherCapabilities() {
        this.gatherCapabilities(() -> null);
    }

    default void gatherCapabilities(@Nullable ICapabilityProvider parent) {
        this.gatherCapabilities(() -> parent);
    }

    default void gatherCapabilities(@Nullable Supplier<ICapabilityProvider> parent) {
        if (this.isLazy() && !this.isInitialized()) {
            this.setLazyParentSupplier(parent == null ? () -> null : parent);
        } else {
            this.doGatherCapabilities(parent == null ? null : parent.get());
        }
    }

    private void doGatherCapabilities(@Nullable ICapabilityProvider parent) {
        this.setDispatcher(AttachCapabilitiesEvent.gatherCapabilities(this.getBaseClass(), this.getProvider(), parent));
        this.setInitialized(true);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    default V getProvider() {
        return (V) this;
    }

    @Nullable
    default CapabilityDispatcher getCapabilities() {
        if (this.isLazy() && !this.isInitialized()) {
            this.doGatherCapabilities(this.getLazyParentSupplier() == null ? null : this.getLazyParentSupplier().get());
            if (this.getLazyData() != null) {
                this.deserializeCaps(this.getLazyData());
            }
        }

        return this.getDispatcher();
    }

    @Override
    default boolean areCapsCompatible(CapabilityProvider<V> other) {
        return this.areCapsCompatible(other.getCapabilities());
    }

    @Override
    default boolean areCapsCompatible(@Nullable CapabilityDispatcher other) {
        CapabilityDispatcher dispatcher = this.getCapabilities();
        if (dispatcher == null) {
            return other == null || other.areCompatible(null);
        } else {
            return dispatcher.areCompatible(other);
        }
    }

    default @Nullable NbtCompound serializeCaps() {
        if (this.isLazy() && !this.isInitialized()) {
            return this.getLazyData();
        } else {
            CapabilityDispatcher dispatcher = this.getCapabilities();
            return dispatcher != null ? dispatcher.serialize() : null;
        }
    }

    default void deserializeCaps(NbtCompound nbt) {
        if (this.isLazy() && !this.isInitialized()) {
            this.setLazyData(nbt);
        } else {
            CapabilityDispatcher dispatcher = this.getCapabilities();
            if (dispatcher != null) {
                dispatcher.deserialize(nbt);
            }
        }
    }

    @Override
    default void invalidateCaps() {
        this.setValid(false);
        CapabilityDispatcher dispatcher = this.getCapabilities();
        if (dispatcher != null) {
            dispatcher.invalidate();
        }
    }

    @Override
    default void reviveCaps() {
        this.setValid(true);
    }

    @Override
    default <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        CapabilityDispatcher dispatcher = this.getCapabilities();
        return this.isValid() && dispatcher != null ? dispatcher.getCapability(capability, side) : LazyOptional.empty();
    }
}
