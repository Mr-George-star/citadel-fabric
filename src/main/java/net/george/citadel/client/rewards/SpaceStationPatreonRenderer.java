package net.george.citadel.client.rewards;

import net.george.citadel.ClientHandler;
import net.george.citadel.client.texture.CitadelTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class SpaceStationPatreonRenderer extends CitadelPatreonRenderer {
    private static final Identifier CITADEL_TEXTURE = new Identifier("citadel", "textures/patreon/citadel_model.png");
    private static final Identifier CITADEL_LIGHTS_TEXTURE = new Identifier("citadel", "textures/patreon/citadel_model_glow.png");
    private final Identifier id;
    private final int[] colors;

    public SpaceStationPatreonRenderer(Identifier id, int[] colors) {
        this.id = id;
        this.colors = colors;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider consumerProvider, int light, float delta, LivingEntity entity, float distance, float rotateSpeed, float rotateHeight) {
        float tick = entity.age + delta;
        float bob = (float) (Math.sin(tick * 0.1F) * 1 * 0.05F - 1 * 0.05F);
        float scale = 0.4F;

        float rotation = MathHelper.wrapDegrees((tick * rotateSpeed) % 360);
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        matrices.translate(0, entity.getHeight() + bob + (rotateHeight - 1F), entity.getWidth() * distance);
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(75));
        matrices.scale(scale, scale, scale);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation * 10));
        ClientHandler.CITADEL_MODEL.resetToDefaultPose();
        ClientHandler.CITADEL_MODEL.render(matrices, consumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(CitadelTextureManager.getColorMappedTexture(this.id, CITADEL_TEXTURE, this.colors))), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        ClientHandler.CITADEL_MODEL.render(matrices, consumerProvider.getBuffer(RenderLayer.getEyes(CITADEL_LIGHTS_TEXTURE)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
        matrices.pop();
    }
}
