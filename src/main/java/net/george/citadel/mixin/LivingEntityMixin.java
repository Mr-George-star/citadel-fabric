package net.george.citadel.mixin;

import net.george.citadel.CitadelConstants;
import net.george.citadel.server.entity.ICitadelDataEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"WrongEntityDataParameterClass", "DefaultAnnotationParam"})
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ICitadelDataEntity {
    @Unique
    private static final TrackedData<NbtCompound> CITADEL_DATA = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

//    @Inject(method = "tick", at = @At("HEAD"))
//    private void citadel$tick(CallbackInfo ci) {
//        LivingTickEvent.EVENT.invoker().interact((LivingEntity)(Object)this);
//    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, method = "initDataTracker")
    private void citadel$registerData(CallbackInfo ci) {
        this.dataTracker.startTracking(CITADEL_DATA, new NbtCompound());
    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, method = "writeCustomDataToNbt")
    private void citadel$writeNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound citadelDat = getCitadelEntityData();
        if (citadelDat != null) {
            nbt.put("CitadelData", citadelDat);
        }
    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, method = "readCustomDataFromNbt")
    private void citadel$readNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("CitadelData")) {
            setCitadelEntityData(nbt.getCompound("CitadelData"));
        }
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public NbtCompound getCitadelEntityData() {
        return this.dataTracker.get(CITADEL_DATA);
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setCitadelEntityData(NbtCompound nbt) {
        this.dataTracker.set(CITADEL_DATA, nbt);
    }

//    @Inject(method = "onDeath", at = @At("HEAD"), cancellable = true)
//    private void citadel$death(DamageSource damageSource, CallbackInfo ci) {
//        LivingDeathEvent event = new LivingDeathEvent((LivingEntity)(Object)this, damageSource);
//        event.interact();
//        if (event.isCanceled()) {
//            ci.cancel();
//        }
//    }
//
//    @Inject(method = "tickActiveItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER, ordinal = 1))
//    private void citadel$tickUsingItem(CallbackInfo ci) {
//        this.itemUseTimeLeft = LivingEntityUseItemEvent.onItemUseTick((LivingEntity)(Object)this, this.activeItemStack, this.itemUseTimeLeft);
//    }
}
