package net.george.citadel.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class BookPageButton extends ButtonWidget {
    private final boolean isForward;
    private final boolean playTurnSound;
    private final GuiBasicBook bookGui;

    public BookPageButton(GuiBasicBook bookGui, int x, int y, boolean isForward, PressAction onPress, boolean playTurnSound) {
        super(x, y, 23, 13, Text.empty(), onPress);
        this.isForward = isForward;
        this.playTurnSound = playTurnSound;
        this.bookGui = bookGui;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.bookGui.getBookWidgetTexture());
        int u = 0;
        int v = 0;
        if (this.hovered) {
            u += 23;
        }
        if (!this.isForward) {
            v += 13;
        }
        int color = bookGui.getWidgetColor();
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF);
        BookBlit.setRGB(r, g, b, 255);
        drawNextArrow(matrices, this.x, this.y, u, v, 18, 12);
    }

    public void drawNextArrow(MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        if (this.hovered) {
            BookBlit.blit(matrices, x, y, this.getZOffset(), (float)u, (float)v, width, height, 256, 256);
        } else {
            drawTexture(matrices, x, y, this.getZOffset(), (float)u, (float)v, width, height, 256, 256);
        }
    }

    public void playDownSound(SoundManager manager) {
        if (this.playTurnSound) {
            manager.play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
        }
    }
}
