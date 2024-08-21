package net.george.citadel.mixin;

import net.george.citadel.CitadelConstants;
import net.george.citadel.server.capability.data.WorldCapabilityData;
import net.george.citadel.server.capability.impl.WorldCapabilityManager;
import net.george.citadel.server.tick.ServerTickRateTracker;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.Executor;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements WorldCapabilityManager {
    @Shadow
    @Final
    private MinecraftServer server;
    @Unique
    private WorldCapabilityData capabilityData;

    @Shadow
    public abstract PersistentStateManager getPersistentStateManager();

    @Inject(method = "<init>", at = @At("TAIL"))
    private void citadel$initCapabilities(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<Spawner> spawners, boolean shouldTickTime, CallbackInfo ci) {
        this.initCapabilities();
    }

    @ModifyConstant(
            method = "tickTime",
            remap = CitadelConstants.REMAPREFS,
            constant = @Constant(longValue = 1L),
            expect = 2)
    private long citadel$clientSetDayTime(long time) {
        return ServerTickRateTracker.getForServer(this.server).getDayTimeIncrement(time);
    }

    @Unique
    protected void initCapabilities() {
        this.gatherCapabilities();
        this.capabilityData = this.getPersistentStateManager().getOrCreate(
                nbt -> WorldCapabilityData.load(nbt, this.getCapabilities()),
                () -> new WorldCapabilityData(this.getCapabilities()),
                WorldCapabilityData.ID);
        this.capabilityData.setCapabilities(this.getCapabilities());
    }

//    @Inject(method = "addPlayer", at = @At("HEAD"), cancellable = true)
//    private void iaf$addPlayerCallEvent(ServerPlayerEntity player, CallbackInfo ci) {
//        EntityJoinLevelEvent event = new EntityJoinLevelEvent(player, (ServerWorld)(Object)this);
//        event.interact();
//        if (event.isCanceled()) {
//            ci.cancel();
//        }
//    }

    @Inject(method = "addPlayer", at = @At("TAIL"))
    private void iaf$addPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        player.onAddedToWorld();
    }

    @Inject(method = "addEntity", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void iaf$addEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            entity.onAddedToWorld();
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }
}
