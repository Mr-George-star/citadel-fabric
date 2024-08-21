package net.george.citadel.server.capability;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@ApiStatus.Internal
public interface ICapabilityProviderImpl<V extends ICapabilityProviderImpl<V>> extends ICapabilityProvider {
    boolean areCapsCompatible(CapabilityProvider<V> provider);

    boolean areCapsCompatible(@Nullable CapabilityDispatcher dispatcher);

    void invalidateCaps();

    void reviveCaps();
}