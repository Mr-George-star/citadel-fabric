package net.george.citadel.mixin;

import net.george.citadel.CitadelConstants;
import net.george.citadel.ClientHandler;
import net.george.citadel.ServerHandler;
import net.george.citadel.server.capability.CapabilityDispatcher;
import net.george.citadel.server.capability.ICapabilityProvider;
import net.george.citadel.server.capability.impl.WorldCapabilityManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"DefaultAnnotationParam", "AddedMixinMembersNamePattern"})
@Mixin(World.class)
public class WorldMixin implements WorldCapabilityManager {
    @Shadow
    @Final
    public boolean isClient;

    @Unique
    private @NotNull Class<World> baseClass = World.class;
    @Unique
    private @Nullable CapabilityDispatcher capabilities;
    @Unique
    private boolean valid;
    @Unique
    private boolean isLazy;
    @Unique
    private Supplier<ICapabilityProvider> lazyParentSupplier;
    @Unique
    private NbtCompound lazyData;
    @Unique
    private boolean initialized;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void citadel$initCapabilityManagerBuiltinVars(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates, CallbackInfo ci) {
        this.initBuiltinVars(World.class);
    }

    @Inject(at = @At("HEAD"), remap = CitadelConstants.REMAPREFS, cancellable = true, method = "tickEntity")
    private void citadel$tickEntity(Consumer<Entity> tickConsumer, Entity entity, CallbackInfo ci) {
        if (!this.isClient){
            if (!ServerHandler.HANDLER.canEntityTickServer((World)(Object) this, entity)) {
                ci.cancel();
            }
        } else {
            if (!ClientHandler.HANDLER.canEntityTickClient((World)(Object) this, entity)) {
                ci.cancel();
            }
        }
    }

    @Override
    public void setBaseClass(@NotNull Class<World> baseClass) {
        this.baseClass = baseClass;
    }

    @NotNull
    @Override
    public Class<World> getBaseClass() {
        return this.baseClass;
    }

    @Override
    public void setDispatcher(@Nullable CapabilityDispatcher dispatcher) {
        this.capabilities = dispatcher;
    }

    @Override
    public @Nullable CapabilityDispatcher getDispatcher() {
        return this.capabilities;
    }

    @Override
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public void setLazy(boolean lazy) {
        this.isLazy = lazy;
    }

    @Override
    public boolean isLazy() {
        return this.isLazy;
    }

    @Override
    public void setLazyParentSupplier(Supplier<ICapabilityProvider> lazyParentSupplier) {
        this.lazyParentSupplier = lazyParentSupplier;
    }

    @Override
    public Supplier<ICapabilityProvider> getLazyParentSupplier() {
        return this.lazyParentSupplier;
    }

    @Override
    public void setLazyData(NbtCompound lazyData) {
        this.lazyData = lazyData;
    }

    @Override
    public NbtCompound getLazyData() {
        return this.lazyData;
    }

    @Override
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }
}
