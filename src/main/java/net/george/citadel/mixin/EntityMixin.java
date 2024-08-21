package net.george.citadel.mixin;

import net.george.citadel.api.EntityExtensions;
import net.george.citadel.server.capability.CapabilityDispatcher;
import net.george.citadel.server.capability.ICapabilityProvider;
import net.george.citadel.server.capability.impl.EntityCapabilityManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(Entity.class)
public class EntityMixin implements EntityCapabilityManager, EntityExtensions {
    @Unique
    private @NotNull Class<Entity> baseClass = Entity.class;
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
    @Unique
    private boolean isAddedToWorld;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void citadel$initCapabilityManagerBuiltinVars(EntityType<?> type, World world, CallbackInfo ci) {
        this.initBuiltinVars(Entity.class);
        this.gatherCapabilities();
    }

    @Inject(method = "remove", at = @At("TAIL"))
    private void citadel$remove(Entity.RemovalReason reason, CallbackInfo ci) {
        this.invalidateCaps();
    }

    @Inject(method = "writeNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", shift = At.Shift.BEFORE))
    private void citadel$writeCapability(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        NbtCompound capabilities = this.serializeCaps();
        if (capabilities != null) {
            nbt.put(CAPABILITY_STORAGE_KEY, capabilities);
        }
    }

    @Inject(method = "readNbt", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;hasVisualFire:Z", shift = At.Shift.AFTER))
    private void citadel$readCapability(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains(CAPABILITY_STORAGE_KEY, 10)) {
            this.deserializeCaps(nbt.getCompound(CAPABILITY_STORAGE_KEY));
        }
    }

    @Inject(method = "unsetRemoved", at = @At("TAIL"))
    private void citadel$reviveCapabilities(CallbackInfo ci) {
        this.reviveCaps();
    }

    @Override
    public void setBaseClass(@NotNull Class<Entity> baseClass) {
        this.baseClass = baseClass;
    }

    @NotNull
    @Override
    public Class<Entity> getBaseClass() {
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

    @Override
    public boolean isAddedToWorld() {
        return this.isAddedToWorld;
    }

    @Override
    public void onAddedToWorld() {
        this.isAddedToWorld = true;
    }

    @Override
    public void onRemovedFromWorld() {
        this.isAddedToWorld = false;
    }
}
