package net.george.citadel.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.george.citadel.api.ClickableWeightExtensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.*;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Mixin(ClickableWidget.class)
public abstract class ClickableWeightMixin extends DrawableHelper implements ClickableWeightExtensions {
    @Shadow @Final public static Identifier WIDGETS_TEXTURE;
    @Shadow protected float alpha;
    @Shadow protected abstract int getYImage(boolean hovered);
    @Shadow public abstract boolean isHovered();
    @Shadow public int x;
    @Shadow public int y;
    @Shadow protected int width;
    @Shadow protected int height;
    @Shadow protected abstract void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY);
    @Shadow public abstract Text getMessage();

    @Unique
    private int packedFGColor = -1;

    /**
     * @author Mr.George
     * @reason for modify color
     */
    @Overwrite
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.drawTexture(matrices, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBackground(matrices, minecraftClient, mouseX, mouseY);
        int j = getFGColor();
        ClickableWidget.drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0f) << 24);
    }

    @Override
    public int getPackedFGColor() {
        return this.packedFGColor;
    }

    @Override
    public void setPackedFGColor(int packedFGColor) {
        this.packedFGColor = packedFGColor;
    }
}
