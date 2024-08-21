package net.george.citadel.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.george.citadel.CitadelConstants;
import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.client.event.EventLivingRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"DefaultAnnotationParam", "rawtypes"})
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
    @Shadow
    protected EntityModel model;

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(
            method = {"setupTransforms"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "RETURN")
    )
    protected void citadel$setupTransforms(LivingEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo ci) {
        EventLivingRenderer.SetupRotations event = new EventLivingRenderer.SetupRotations(entity, this.model, matrices, bodyYaw, tickDelta);
        CitadelEventManager.INSTANCE.send(event);
    }

    @Inject(
            method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V",
                    shift = At.Shift.BEFORE
            )
    )
    protected void citadel$render_setAngles_before(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        EventLivingRenderer.PreSetupAnimations event = new EventLivingRenderer.PreSetupAnimations(livingEntity, this.model, matrixStack, f, g, vertexConsumerProvider, i);
        CitadelEventManager.INSTANCE.send(event);
    }

    @Inject(
            method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V",
                    shift = At.Shift.AFTER
            )
    )
    protected void citadel$render_setAngles_after(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        EventLivingRenderer.PostSetupAnimations event = new EventLivingRenderer.PostSetupAnimations(livingEntity, this.model, matrixStack, f, g, vertexConsumerProvider, i);
        CitadelEventManager.INSTANCE.send(event);
    }

    @Inject(
            method = {"render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "RETURN")
    )
    protected void citadel$render(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        EventLivingRenderer.PostRenderModel event = new EventLivingRenderer.PostRenderModel(livingEntity, this.model, matrixStack, f, g, vertexConsumerProvider, i);
        CitadelEventManager.INSTANCE.send(event);
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/model/EntityModel;riding:Z"))
    private void citadel$render(M instance, boolean value, @Local(argsOnly = true) T entity) {
        instance.riding = value && entity.getVehicle() != null && entity.shouldRiderSit();
    }

//    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
//    private void citadel$render_head(T entity, float yaw, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
//        RenderLivingEvent.Pre event = new RenderLivingEvent.Pre<>(entity, (LivingEntityRenderer<T, M>)(Object)this, delta, matrixStack, vertexConsumerProvider, i);
//        event.interact();
//        if (event.isCanceled()) {
//            ci.cancel();
//        }
//    }
//
//    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
//    private void citadel$render_tail(T entity, float yaw, float delta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
//        RenderLivingEvent.Post event = new RenderLivingEvent.Post<>(entity, (LivingEntityRenderer<T, M>)(Object)this, delta, matrixStack, vertexConsumerProvider, i);
//        event.interact();
//    }
}
