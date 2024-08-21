package net.george.citadel.mixin;

import net.george.citadel.CitadelConstants;
import net.george.citadel.server.world.ModifiableTickRateServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"DefaultAnnotationParam", "AddedMixinMembersNamePattern"})
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ModifiableTickRateServer {
    @Unique
    private long modifiedMsPerTick = -1;
    @Unique
    private long masterMs;

    @Inject(
            method = {"runServer()V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;startTickMetrics()V",
                    shift = At.Shift.BEFORE
            )
    )
    protected void citadel_beforeServerTick(CallbackInfo ci) {
        masterTick();
    }

    @Unique
    private void masterTick() {
        this.masterMs += 50L;
    }

    @ModifyConstant(
            method = {"runServer()V"},
            constant = @Constant(longValue = 50L),
            expect = 4)
    private long citadel_serverMsPerTick(long value) {
        return this.modifiedMsPerTick == -1 ? value : this.modifiedMsPerTick;
    }

    @Override
    public void setGlobalTickLengthMs(long msPerTick) {
        this.modifiedMsPerTick = msPerTick;
    }

    @Override
    public long getMasterMs() {
        return this.masterMs;
    }
}
