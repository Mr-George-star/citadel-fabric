package net.george.citadel.mixin.client;

import net.george.citadel.CitadelConstants;
import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.api.event.EventResult;
import net.george.citadel.client.event.EventGetFluidRenderType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.fluid.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(RenderLayers.class)
public class RenderLayersMixin {
    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, cancellable = true,
            method = "getFluidLayer")
    private static void citadel$getFluidLayer(FluidState fluidState, CallbackInfoReturnable<RenderLayer> cir) {
        EventGetFluidRenderType event = new EventGetFluidRenderType(fluidState, cir.getReturnValue());
        if (CitadelEventManager.INSTANCE.send(event) == EventResult.ALLOW) {
            cir.setReturnValue(event.getRenderLayer());
        }
    }
}
