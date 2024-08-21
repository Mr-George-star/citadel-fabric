package net.george.citadel.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.citadel.client.gui.data.EntityLinkData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class EntityLinkButton extends ButtonWidget {
    private static final Map<String, Entity> renderedEntities = new HashMap<>();
    private final EntityLinkData data;
    private final GuiBasicBook bookGui;
    private final EntityRenderWindow window = new EntityRenderWindow();

    public EntityLinkButton(GuiBasicBook bookGui, EntityLinkData linkData, int k, int l, PressAction onPress) {
        super(k + linkData.getX() - 12, l + linkData.getY(), (int) (24 * linkData.getScale()), (int) (24 * linkData.getScale()), Text.empty(), onPress);
        this.data = linkData;
        this.bookGui = bookGui;
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int u = 0;
        int v = 30;
        float f = (float) data.getScale();
        RenderSystem.setShaderTexture(0, this.bookGui.getBookWidgetTexture());
        matrices.push();
        matrices.translate(this.x, this.y, 0);
        matrices.scale(f, f, 1);
        this.drawButton(false, matrices, 0, 0, u, v, 24, 24);
        Entity model;
        EntityType<?> type = Registry.ENTITY_TYPE.get(new Identifier(this.data.getEntity()));
        model = renderedEntities.putIfAbsent(this.data.getEntity(), type.create(MinecraftClient.getInstance().world));

        matrices.push();
        if (model != null) {
            this.window.renderEntityWindow(matrices, x, y, model, (float) this.data.getEntityScale() * f, this.data.getOffset_x() * f, this.data.getOffset_y() * f, 2, 2, 22, 22);
        }
        matrices.pop();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        if (this.hovered) {
            this.bookGui.setEntityTooltip(this.data.getHoverText());
            u = 48;
        } else {
            u = 24;
        }
        int color = bookGui.getWidgetColor();
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF);
        BookBlit.setRGB(r, g, b, 255);
        RenderSystem.setShaderTexture(0, bookGui.getBookWidgetTexture());
        this.drawButton(!this.hovered, matrices, 0, 0, u, v, 24, 24);
        matrices.pop();
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    }

    public void drawButton(boolean color, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        if (color) {
            BookBlit.blit(matrices, x, y, this.getZOffset(), (float) u, (float) v, width, height, 256, 256);
        } else {
            drawTexture(matrices, x, y, this.getZOffset(), (float) u, (float) v, width, height, 256, 256);
        }
    }

    private class EntityRenderWindow extends DrawableHelper {
        public void renderEntityWindow(MatrixStack matrices, float x, float y, Entity toRender, float renderScale, float offsetX, float offsetY, int minX, int minY, int maxX, int maxY) {
            matrices.push();
            matrices.translate(0, 0, -1F);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            matrices.push();
            RenderSystem.enableDepthTest();
            matrices.translate(0.0F, 0.0F, 950.0F);
            RenderSystem.colorMask(false, false, false, false);
            fill(matrices, 4680, 2260, -4680, -2260, -16777216);
            RenderSystem.colorMask(true, true, true, true);
            matrices.translate(0.0F, 0.0F, -950.0F);
            RenderSystem.depthFunc(518);
            fill(matrices, 22, 22, 2, 2, -16777216);
            RenderSystem.depthFunc(515);
            RenderSystem.setShaderTexture(0, bookGui.getBookWidgetTexture());
            drawTexture(matrices, 0, 0, 0, 30, 24, 24, 256, 256);
            if (toRender != null) {
                toRender.age = Objects.requireNonNull(MinecraftClient.getInstance().player).age;
                float transitional = Math.max(0.0F, renderScale - 1.0F) * 8;
                bookGui.drawEntityOnScreen(matrices, (int) (12 * renderScale + transitional + (x + offsetX)), (int) (24 * renderScale - transitional + y + offsetY), 10 * renderScale, false, 30, -130, 0, 0, 0, toRender);
            }
            RenderSystem.depthFunc(518);
            matrices.translate(0.0F, 0.0F, -950.0F);
            RenderSystem.colorMask(false, false, false, false);
            fill(matrices, 4680, 2260, -4680, -2260, -16777216);
            RenderSystem.colorMask(true, true, true, true);
            matrices.translate(0.0F, 0.0F, 950.0F);
            RenderSystem.depthFunc(515);
            matrices.pop();
            matrices.pop();
        }
    }
}
