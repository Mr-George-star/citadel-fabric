package net.george.citadel.mixin.client;

import net.george.citadel.CitadelConstants;
import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.api.event.EventResult;
import net.george.citadel.client.event.EventGetStarBrightness;
import net.george.citadel.client.tick.ClientTickRateTracker;
import net.george.citadel.server.capability.impl.WorldCapabilityManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World implements WorldCapabilityManager {
    protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void citadel$gatherCapabilities(ClientPlayNetworkHandler networkHandler, ClientWorld.Properties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimensionTypeEntry, int loadDistance, int simulationDistance, Supplier<Profiler> profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
        this.gatherCapabilities();
    }

    @Inject(at = @At("RETURN"), remap = CitadelConstants.REMAPREFS, method = "method_23787", cancellable = true)
    private void citadel$getStarBrightness(float delta, CallbackInfoReturnable<Float> cir) {
        EventGetStarBrightness event = new EventGetStarBrightness(((ClientWorld)(Object)this), cir.getReturnValue(), delta);
        if (CitadelEventManager.INSTANCE.send(event) == EventResult.ALLOW) {
            cir.setReturnValue(event.getBrightness());
        }
    }

//    @Inject(method = "addPlayer", at = @At("HEAD"), cancellable = true)
//    private void iaf$addPlayerCallEvent(int id, AbstractClientPlayerEntity player, CallbackInfo ci) {
//        EntityJoinLevelEvent event = new EntityJoinLevelEvent(player, (ClientWorld)(Object)this);
//        event.interact();
//        if (event.isCanceled()) {
//            ci.cancel();
//        }
//    }

    @ModifyConstant(
            method = "tickTime",
            remap = CitadelConstants.REMAPREFS,
            constant = @Constant(longValue = 1L),
            expect = 2)
    private long citadel$tickTime(long time) {
        return ClientTickRateTracker.getForClient(MinecraftClient.getInstance()).getDayTimeIncrement(time);
    }

    @Inject(method = "addEntityPrivate", at = @At("TAIL"))
    private void iaf$addEntityPrivate(int id, Entity entity, CallbackInfo ci) {
        entity.onAddedToWorld();
    }
}
