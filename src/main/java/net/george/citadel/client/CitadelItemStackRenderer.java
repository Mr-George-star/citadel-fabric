package net.george.citadel.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.george.citadel.Citadel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CitadelItemStackRenderer extends BuiltinModelItemRenderer {
    private static final Identifier DEFAULT_ICON_TEXTURE = new Identifier("citadel:textures/gui/book/icon_default.png");
    private static final Map<String, Identifier> LOADED_ICONS = new HashMap<>();

    public CitadelItemStackRenderer() {
        super(null, null);
    }

    @Override
    public void render(ItemStack stack, ModelTransformation.Mode transformType, MatrixStack matrices, VertexConsumerProvider buffer, int light, int overlay) {
        float delta = MinecraftClient.getInstance().getTickDelta();
        float ticksExisted = Util.getMeasuringTimeMs() / 50F + delta;
        int id = MinecraftClient.getInstance().player == null ? 0 : MinecraftClient.getInstance().player.getId();
        if (stack.getItem() == Citadel.FANCY_ITEM.get()) {
            Random random = new Random();
            boolean animateAnyways = false;
            ItemStack toRender = null;
            if (stack.getNbt() != null && stack.getNbt().contains("DisplayItem")) {
                String displayID = stack.getNbt().getString("DisplayItem");
                toRender = new ItemStack(Registry.ITEM.get(new Identifier(displayID)));
                if (stack.getNbt().contains("DisplayItemNBT")) {
                    try {
                        toRender.setNbt(stack.getNbt().getCompound("DisplayItemNBT"));
                    } catch (Exception exception) {
                        toRender = new ItemStack(Items.BARRIER);
                    }
                }
            }
            if (toRender == null) {
                animateAnyways = true;
                toRender = new ItemStack(Items.BARRIER);
            }
            matrices.push();
            matrices.translate(0.5F, 0.5f, 0.5f);
            if (stack.getNbt() != null && stack.getNbt().contains("DisplayShake") && stack.getNbt().getBoolean("DisplayShake")) {
                matrices.translate((random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.1F);
            }
            if (animateAnyways || stack.getNbt() != null && stack.getNbt().contains("DisplayBob") && stack.getNbt().getBoolean("DisplayBob")) {
                matrices.translate(0, 0.05F + 0.1F * MathHelper.sin(0.3F * ticksExisted), 0);
            }
            if (stack.getNbt() != null && stack.getNbt().contains("DisplaySpin") && stack.getNbt().getBoolean("DisplaySpin")) {
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(6 * ticksExisted));
            }
            if (animateAnyways || stack.getNbt() != null && stack.getNbt().contains("DisplayZoom") && stack.getNbt().getBoolean("DisplayZoom")) {
                float scale = (float) (1F + 0.15F * (Math.sin(ticksExisted * 0.3F) + 1F));
                matrices.scale(scale, scale, scale);
            }
            if (stack.getNbt() != null && stack.getNbt().contains("DisplayScale") && stack.getNbt().getFloat("DisplayScale") != 1.0F) {
                float scale = stack.getNbt().getFloat("DisplayScale");
                matrices.scale(scale, scale, scale);
            }
            MinecraftClient.getInstance().getItemRenderer().renderItem(toRender, transformType, light, overlay, matrices, buffer, id);
            matrices.pop();
        }
        if (stack.getItem() == Citadel.EFFECT_ITEM.get()) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            // RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();
            StatusEffect effect;
            if (stack.getNbt() != null && stack.getNbt().contains("DisplayEffect")) {
                String displayID = stack.getNbt().getString("DisplayEffect");
                effect = Registry.STATUS_EFFECT.get(new Identifier(displayID));
            } else {
                int size = Registry.STATUS_EFFECT.getIds().size();
                int time = (int) (Util.getMeasuringTimeMs() / 500);
                effect = Registry.STATUS_EFFECT.get(time % size);
                if (effect == null) {
                    effect = StatusEffects.SPEED;
                }
            }
            if (effect == null) {
                effect = StatusEffects.SPEED;
            }
            StatusEffectSpriteManager manager = MinecraftClient.getInstance().getStatusEffectSpriteManager();
            matrices.push();
            matrices.translate(0, 0, 0.5F);
            Sprite sprite = manager.getSprite(effect);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
            Matrix4f mx = matrices.peek().getPositionMatrix();
            int br = 255;
            builder.vertex(mx, (float) 1, (float) 1, (float) 0).texture(sprite.getMaxU(), sprite.getMinV()).color(br, br, br, 255).light(light).next();
            builder.vertex(mx, (float) 0, (float) 1, (float) 0).texture(sprite.getMinU(), sprite.getMinV()).color(br, br, br, 255).light(light).next();
            builder.vertex(mx, (float) 0, (float) 0, (float) 0).texture(sprite.getMinU(), sprite.getMaxV()).color(br, br, br, 255).light(light).next();
            builder.vertex(mx, (float) 1, (float) 0, (float) 0).texture(sprite.getMaxU(), sprite.getMaxV()).color(br, br, br, 255).light(light).next();
            tessellator.draw();
            matrices.pop();
        }
        if (stack.getItem() == Citadel.ICON_ITEM.get()) {
            Identifier texture = DEFAULT_ICON_TEXTURE;
            if (stack.getNbt() != null && stack.getNbt().contains("IconLocation")) {
                String iconLocationStr = stack.getNbt().getString("IconLocation");
                if (LOADED_ICONS.containsKey(iconLocationStr)) {
                    texture = LOADED_ICONS.get(iconLocationStr);
                } else {
                    texture = new Identifier(iconLocationStr);
                    LOADED_ICONS.put(iconLocationStr, texture);
                }
            }
            matrices.push();
            matrices.translate(0, 0, 0.5F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, texture);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();
            builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
            Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
            int br = 255;
            builder.vertex(positionMatrix, (float) 1, (float) 1, (float) 0).texture(1, 0).color(br, br, br, 255).light(light).next();
            builder.vertex(positionMatrix, (float) 0, (float) 1, (float) 0).texture(0, 0).color(br, br, br, 255).light(light).next();
            builder.vertex(positionMatrix, (float) 0, (float) 0, (float) 0).texture(0, 1).color(br, br, br, 255).light(light).next();
            builder.vertex(positionMatrix, (float) 1, (float) 0, (float) 0).texture(1, 1).color(br, br, br, 255).light(light).next();
            tessellator.draw();
            matrices.pop();
        }
    }
}
