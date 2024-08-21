package net.george.citadel.server.capability;

import com.google.common.annotations.VisibleForTesting;
import net.george.citadel.server.event.AttachCapabilitiesEvent;
import net.george.citadel.util.LazyOptional;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.annotation.MethodsReturnNonnullByDefault;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
public abstract class CapabilityProvider<V extends ICapabilityProviderImpl<V>> implements ICapabilityProviderImpl<V> {
    @VisibleForTesting
    static boolean SUPPORTS_LAZY_CAPABILITIES = true;
    private final @NotNull Class<V> baseClass;
    private @Nullable CapabilityDispatcher capabilities;
    private boolean valid;
    private boolean isLazy;
    private Supplier<ICapabilityProvider> lazyParentSupplier;
    private NbtCompound lazyData;
    private boolean initialized;

    protected CapabilityProvider(Class<V> baseClass) {
        this(baseClass, false);
    }

    protected CapabilityProvider(@NotNull Class<V> baseClass, boolean isLazy) {
        this.valid = true;
        this.isLazy = false;
        this.lazyParentSupplier = null;
        this.lazyData = null;
        this.initialized = false;
        this.baseClass = baseClass;
        this.isLazy = SUPPORTS_LAZY_CAPABILITIES && isLazy;
    }

    protected final void gatherCapabilities() {
        this.gatherCapabilities(() -> null);
    }

    protected final void gatherCapabilities(@Nullable ICapabilityProvider parent) {
        this.gatherCapabilities(() -> parent);
    }

    protected final void gatherCapabilities(@Nullable Supplier<ICapabilityProvider> parent) {
        if (this.isLazy && !this.initialized) {
            this.lazyParentSupplier = parent == null ? () -> null : parent;
        } else {
            this.doGatherCapabilities(parent == null ? null : parent.get());
        }
    }

    private void doGatherCapabilities(@Nullable ICapabilityProvider parent) {
        this.capabilities = AttachCapabilitiesEvent.gatherCapabilities(this.baseClass, this.getProvider(), parent);
        this.initialized = true;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    V getProvider() {
        return (V) this;
    }

    protected final @Nullable CapabilityDispatcher getCapabilities() {
        if (this.isLazy && !this.initialized) {
            this.doGatherCapabilities(this.lazyParentSupplier == null ? null : this.lazyParentSupplier.get());
            if (this.lazyData != null) {
                this.deserializeCaps(this.lazyData);
            }
        }

        return this.capabilities;
    }

    @Override
    public final boolean areCapsCompatible(CapabilityProvider<V> other) {
        return this.areCapsCompatible(other.getCapabilities());
    }

    @Override
    public final boolean areCapsCompatible(@Nullable CapabilityDispatcher other) {
        CapabilityDispatcher dispatcher = this.getCapabilities();
        if (dispatcher == null) {
            return other == null || other.areCompatible(null);
        } else {
            return dispatcher.areCompatible(other);
        }
    }

    protected final @Nullable NbtCompound serializeCaps() {
        if (this.isLazy && !this.initialized) {
            return this.lazyData;
        } else {
            CapabilityDispatcher dispatcher = this.getCapabilities();
            return dispatcher != null ? dispatcher.serialize() : null;
        }
    }

    protected final void deserializeCaps(NbtCompound nbt) {
        if (this.isLazy && !this.initialized) {
            this.lazyData = nbt;
        } else {
            CapabilityDispatcher dispatcher = this.getCapabilities();
            if (dispatcher != null) {
                dispatcher.deserialize(nbt);
            }
        }
    }

    @Override
    public void invalidateCaps() {
        this.valid = false;
        CapabilityDispatcher dispatcher = this.getCapabilities();
        if (dispatcher != null) {
            dispatcher.invalidate();
        }
    }

    @Override
    public void reviveCaps() {
        this.valid = true;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        CapabilityDispatcher dispatcher = this.getCapabilities();
        return this.valid && dispatcher != null ? dispatcher.getCapability(capability, side) : LazyOptional.empty();
    }

    public static class AsField<B extends ICapabilityProviderImpl<B>> extends CapabilityProvider<B> {
        private final B owner;

        public AsField(Class<B> baseClass, B owner) {
            super(baseClass);
            this.owner = owner;
        }

        public AsField(Class<B> baseClass, B owner, boolean isLazy) {
            super(baseClass, isLazy);
            this.owner = owner;
        }

        public void initInternal() {
            this.gatherCapabilities();
        }

        public @Nullable NbtCompound serializeInternal() {
            return this.serializeCaps();
        }

        public void deserializeInternal(NbtCompound tag) {
            this.deserializeCaps(tag);
        }

        @Override
        @NotNull B getProvider() {
            return this.owner;
        }
    }
}
