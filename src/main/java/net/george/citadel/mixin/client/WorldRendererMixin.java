package net.george.citadel.mixin.client;

import net.george.citadel.CitadelConstants;
import net.george.citadel.ClientHandler;
import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.api.event.EventResult;
import net.george.citadel.client.event.EventGetOutlineColor;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Redirect(
            method = "render",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I")
    )
    private int citadel$getTeamColor(Entity entity) {
        EventGetOutlineColor event = new EventGetOutlineColor(entity, entity.getTeamColorValue());
        int color = entity.getTeamColorValue();
        if (CitadelEventManager.INSTANCE.send(event) == EventResult.ALLOW) {
            color = event.getColor();
        }
        return color;
    }

    @Redirect(
            method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
            remap = CitadelConstants.REMAPREFS,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getSkyAngle(F)F"),
            expect = 2
    )
    private float citadel$getTimeOfDay(ClientWorld instance, float delta) {
        //default implementation does not lerp the time of day
        float lerpBy = ClientHandler.HANDLER.isGamePaused() ? 0F : delta;
        float lerpedDayTime = (instance.getDimension().fixedTime().orElse(instance.getTimeOfDay()) + lerpBy) / 24000.0F;
        double d0 = MathHelper.fractionalPart((double)lerpedDayTime - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
        return (float)(d0 * 2.0D + d1) / 3.0F;
    }
}
