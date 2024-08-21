package net.george.citadel.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class LinkButton extends ButtonWidget {
    public ItemStack previewStack;
    public GuiBasicBook book;

    public LinkButton(GuiBasicBook book, int x, int y, int width, int height, Text text, ItemStack previewStack, PressAction action) {
        super(x, y, width + (previewStack.isEmpty() ? 0 : 6), height, text, action, ButtonWidget.EMPTY);
        this.previewStack = previewStack;
        this.book = book;
    }

    public LinkButton(GuiBasicBook book, int x, int y, int width, int height, Text text, PressAction action) {
        this(book, x, y, width, height, text, ItemStack.EMPTY, action);
    }

    @Override
    public void renderButton(MatrixStack matrices, int guiX, int guiY, float partialTicks) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer font = minecraft.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, book.getBookButtonsTexture());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        this.drawTexture(matrices, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.drawTexture(matrices, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        if (this.hovered) {
            int color = this.book.getWidgetColor();
            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0xFF00) >> 8;
            int b = (color & 0xFF);
            BookBlit.setRGB(r, g, b, 255);
            i = 3;
            BookBlit.blit(matrices, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height, 256, 256);
            BookBlit.blit(matrices, this.x + this.width / 2, this.y, 200 - (float) this.width / 2, 46 + i * 20, this.width / 2, this.height, 256, 256);
        }

        this.renderBackground(matrices, minecraft, guiX, guiY);
        int j = this.active ? 16777215 : 10526880;
        int itemTextOffset = this.previewStack.isEmpty() ? 0 : 8;
        if (!this.previewStack.isEmpty()) {
            ItemRenderer itemRenderer =  MinecraftClient.getInstance().getItemRenderer();
            itemRenderer.zOffset = 100.0F;
            itemRenderer.renderInGuiWithOverrides(this.previewStack, this.x + 2, this.y + 1);
            itemRenderer.zOffset = 0.0F;
        }
        drawTextOf(matrices, font, this.getMessage(), this.x + itemTextOffset + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    public static void drawTextOf(MatrixStack matrices, TextRenderer renderer, Text text, int x, int y, int color) {
        OrderedText orderedText = text.asOrderedText();
        renderer.draw(matrices, orderedText, (float)(x - renderer.getWidth(orderedText) / 2), (float)y, color);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
    }
}
