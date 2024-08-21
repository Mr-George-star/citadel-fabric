package net.george.citadel.mixin.client;

import net.george.citadel.CitadelConstants;
import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.api.event.EventResult;
import net.george.citadel.client.event.EventPosePlayerHand;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@SuppressWarnings({"DefaultAnnotationParam", "rawtypes", "unchecked"})
@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin extends Model {
    public BipedEntityModelMixin(Function<Identifier, RenderLayer> layerFactory) {
        super(layerFactory);
    }

    @Inject(at = @At("HEAD"), remap = CitadelConstants.REMAPREFS, method = "positionRightArm", cancellable = true)
    private void citadel$positionRightArm(LivingEntity entity, CallbackInfo ci) {
        EventPosePlayerHand event = new EventPosePlayerHand(entity, (BipedEntityModel)((Model) this), false);
        if (CitadelEventManager.INSTANCE.send(event) == EventResult.ALLOW) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), remap = CitadelConstants.REMAPREFS, method = "positionLeftArm", cancellable = true)
    private void citadel$positionLeftArm(LivingEntity entity, CallbackInfo ci) {
        EventPosePlayerHand event = new EventPosePlayerHand(entity, (BipedEntityModel)((Model) this), true);
        if (CitadelEventManager.INSTANCE.send(event) == EventResult.ALLOW) {
            ci.cancel();
        }
    }
}
