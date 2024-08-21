package net.george.citadel.server.capability;

import net.george.citadel.util.LazyOptional;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface ICapabilityProvider {
    <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction);

    default <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability) {
        return this.getCapability(capability, null);
    }
}
