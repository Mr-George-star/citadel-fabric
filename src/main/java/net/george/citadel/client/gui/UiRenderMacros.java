package net.george.citadel.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

/**
 * Our replacement for DrawableHelper.
 */
@SuppressWarnings("unused")
public class UiRenderMacros {
    public static final double HALF_BIAS = 0.5;

    public static void drawLineRectGradient(final MatrixStack matrices,
                                            final int x,
                                            final int y,
                                            final int w,
                                            final int h,
                                            final int argbColorStart,
                                            final int argbColorEnd) {
        drawLineRectGradient(matrices, x, y, w, h, argbColorStart, argbColorEnd, 1);
    }

    public static void drawLineRectGradient(final MatrixStack matrices,
                                            final int x,
                                            final int y,
                                            final int w,
                                            final int h,
                                            final int argbColorStart,
                                            final int argbColorEnd,
                                            final int lineWidth) {
        drawLineRectGradient(matrices,
                x,
                y,
                w,
                h,
                (argbColorStart >> 16) & 0xff,
                (argbColorEnd >> 16) & 0xff,
                (argbColorStart >> 8) & 0xff,
                (argbColorEnd >> 8) & 0xff,
                argbColorStart & 0xff,
                argbColorEnd & 0xff,
                (argbColorStart >> 24) & 0xff,
                (argbColorEnd >> 24) & 0xff,
                lineWidth);
    }

    public static void drawLineRectGradient(final MatrixStack matrices,
                                            final int x,
                                            final int y,
                                            final int w,
                                            final int h,
                                            final int redStart,
                                            final int redEnd,
                                            final int greenStart,
                                            final int greenEnd,
                                            final int blueStart,
                                            final int blueEnd,
                                            final int alphaStart,
                                            final int alphaEnd,
                                            final int lineWidth) {
        if (lineWidth < 1 || (alphaStart == 0 && alphaEnd == 0)) {
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        if (alphaStart != 255 || alphaEnd != 255) {
            RenderSystem.enableBlend();
        } else {
            RenderSystem.disableBlend();
        }

        final Matrix4f matrix = matrices.peek().getPositionMatrix();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y, 0).color(redStart, greenStart, blueStart, alphaStart).next();
        buffer.vertex(matrix, x, y + h, 0).color(redEnd, greenEnd, blueEnd, alphaEnd).next();
        buffer.vertex(matrix, x + lineWidth, y + h - lineWidth, 0).color(redEnd, greenEnd, blueEnd, alphaEnd).next();
        buffer.vertex(matrix, x + lineWidth, y + lineWidth, 0).color(redStart, greenStart, blueStart, alphaStart).next();
        buffer.vertex(matrix, x + w - lineWidth, y + lineWidth, 0).color(redStart, greenStart, blueStart, alphaStart).next();
        buffer.vertex(matrix, x + w, y, 0).color(redStart, greenStart, blueStart, alphaStart).next();
        Tessellator.getInstance().draw();

        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x + w, y + h, 0).color(redEnd, greenEnd, blueEnd, alphaEnd).next();
        buffer.vertex(matrix, x + w, y, 0).color(redStart, greenStart, blueStart, alphaStart).next();
        buffer.vertex(matrix, x + w - lineWidth, y + lineWidth, 0).color(redStart, greenStart, blueStart, alphaStart).next();
        buffer.vertex(matrix, x + w - lineWidth, y + h - lineWidth, 0).color(redEnd, greenEnd, blueEnd, alphaEnd).next();
        buffer.vertex(matrix, x + lineWidth, y + h - lineWidth, 0).color(redEnd, greenEnd, blueEnd, alphaEnd).next();
        buffer.vertex(matrix, x, y + h, 0).color(redEnd, greenEnd, blueEnd, alphaEnd).next();
        Tessellator.getInstance().draw();

        RenderSystem.disableBlend();
    }

    public static void drawLineRect(final MatrixStack matrices, final int x, final int y, final int w, final int h, final int argbColor) {
        drawLineRect(matrices, x, y, w, h, argbColor, 1);
    }

    public static void drawLineRect(final MatrixStack matrices,
                                    final int x,
                                    final int y,
                                    final int w,
                                    final int h,
                                    final int argbColor,
                                    final int lineWidth) {
        drawLineRect(matrices,
                x,
                y,
                w,
                h,
                (argbColor >> 16) & 0xff,
                (argbColor >> 8) & 0xff,
                argbColor & 0xff,
                (argbColor >> 24) & 0xff,
                lineWidth);
    }

    public static void drawLineRect(final MatrixStack matrices,
                                    final int x,
                                    final int y,
                                    final int w,
                                    final int h,
                                    final int red,
                                    final int green,
                                    final int blue,
                                    final int alpha,
                                    final int lineWidth) {
        if (lineWidth < 1 || alpha == 0) {
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        if (alpha != 255) {
            RenderSystem.enableBlend();
        } else {
            RenderSystem.disableBlend();
        }

        final Matrix4f matrix = matrices.peek().getPositionMatrix();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x, y + h, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + lineWidth, y + h - lineWidth, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + lineWidth, y + lineWidth, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + w - lineWidth, y + lineWidth, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + w, y, 0).color(red, green, blue, alpha).next();
        Tessellator.getInstance().draw();

        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x + w, y + h, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + w, y, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + w - lineWidth, y + lineWidth, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + w - lineWidth, y + h - lineWidth, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + lineWidth, y + h - lineWidth, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x, y + h, 0).color(red, green, blue, alpha).next();
        Tessellator.getInstance().draw();

        RenderSystem.disableBlend();
    }

    public static void fill(final MatrixStack matrices, final int x, final int y, final int w, final int h, final int argbColor) {
        fill(matrices, x, y, w, h, (argbColor >> 16) & 0xff, (argbColor >> 8) & 0xff, argbColor & 0xff, (argbColor >> 24) & 0xff);
    }

    public static void fill(final MatrixStack matrices,
                            final int x,
                            final int y,
                            final int w,
                            final int h,
                            final int red,
                            final int green,
                            final int blue,
                            final int alpha) {
        if (alpha == 0) {
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        if (alpha != 255) {
            RenderSystem.enableBlend();
        } else {
            RenderSystem.disableBlend();
        }

        final Matrix4f matrix = matrices.peek().getPositionMatrix();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x, y + h, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + w, y + h, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + w, y, 0).color(red, green, blue, alpha).next();
        Tessellator.getInstance().draw();

        RenderSystem.disableBlend();
    }


    public static void fillGradient(final MatrixStack matrices,
                                    final int x,
                                    final int y,
                                    final int w,
                                    final int h,
                                    final int argbColorStart,
                                    final int argbColorEnd) {
        fillGradient(matrices,
                x,
                y,
                w,
                h,
                (argbColorStart >> 16) & 0xff,
                (argbColorEnd >> 16) & 0xff,
                (argbColorStart >> 8) & 0xff,
                (argbColorEnd >> 8) & 0xff,
                argbColorStart & 0xff,
                argbColorEnd & 0xff,
                (argbColorStart >> 24) & 0xff,
                (argbColorEnd >> 24) & 0xff);
    }

    public static void fillGradient(final MatrixStack matrices,
                                    final int x,
                                    final int y,
                                    final int w,
                                    final int h,
                                    final int redStart,
                                    final int redEnd,
                                    final int greenStart,
                                    final int greenEnd,
                                    final int blueStart,
                                    final int blueEnd,
                                    final int alphaStart,
                                    final int alphaEnd) {
        if (alphaStart == 0 && alphaEnd == 0) {
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        if (alphaStart != 255 || alphaEnd != 255) {
            RenderSystem.enableBlend();
        } else {
            RenderSystem.disableBlend();
        }

        final Matrix4f matrix = matrices.peek().getPositionMatrix();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y, 0).color(redStart, greenStart, blueStart, alphaStart).next();
        buffer.vertex(matrix, x, y + h, 0).color(redEnd, greenEnd, blueEnd, alphaEnd).next();
        buffer.vertex(matrix, x + w, y + h, 0).color(redEnd, greenEnd, blueEnd, alphaEnd).next();
        buffer.vertex(matrix, x + w, y, 0).color(redStart, greenStart, blueStart, alphaStart).next();
        Tessellator.getInstance().draw();

        RenderSystem.disableBlend();
    }

    public static void hLine(final MatrixStack matrices, final int x, final int xEnd, final int y, final int argbColor) {
        line(matrices, x, y, xEnd, y, (argbColor >> 16) & 0xff, (argbColor >> 8) & 0xff, argbColor & 0xff, (argbColor >> 24) & 0xff);
    }

    public static void hLine(final MatrixStack matrices,
                             final int x,
                             final int xEnd,
                             final int y,
                             final int red,
                             final int green,
                             final int blue,
                             final int alpha) {
        line(matrices, x, y, xEnd, y, red, green, blue, alpha);
    }

    public static void vLine(final MatrixStack matrices, final int x, final int y, final int yEnd, final int argbColor) {
        line(matrices, x, y, x, yEnd, (argbColor >> 16) & 0xff, (argbColor >> 8) & 0xff, argbColor & 0xff, (argbColor >> 24) & 0xff);
    }

    public static void vLine(final MatrixStack matrices,
                             final int x,
                             final int y,
                             final int yEnd,
                             final int red,
                             final int green,
                             final int blue,
                             final int alpha) {
        line(matrices, x, y, x, yEnd, red, green, blue, alpha);
    }

    public static void line(final MatrixStack matrices, final int x, final int y, final int xEnd, final int yEnd, final int argbColor) {
        line(matrices, x, y, xEnd, yEnd, (argbColor >> 16) & 0xff, (argbColor >> 8) & 0xff, argbColor & 0xff, (argbColor >> 24) & 0xff);
    }

    public static void line(final MatrixStack matrices,
                            final int x,
                            final int y,
                            final int xEnd,
                            final int yEnd,
                            final int red,
                            final int green,
                            final int blue,
                            final int alpha) {
        if (alpha == 0) {
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        if (alpha != 255) {
            RenderSystem.enableBlend();
        } else {
            RenderSystem.disableBlend();
        }

        final Matrix4f matrix = matrices.peek().getPositionMatrix();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, x, y, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, xEnd, yEnd, 0).color(red, green, blue, alpha).next();
        Tessellator.getInstance().draw();

        RenderSystem.disableBlend();
    }

    public static void blit(final MatrixStack matrices, final Identifier id, final int x, final int y, final int w, final int h) {
        blit(matrices, id, x, y, w, h, 0.0f, 0.0f, 1.0f, 1.0f);
    }

    public static void blit(final MatrixStack matrices,
                            final Identifier id,
                            final int x,
                            final int y,
                            final int w,
                            final int h,
                            final int u,
                            final int v,
                            final int mapW,
                            final int mapH) {
        blit(matrices, id, x, y, w, h, (float) u / mapW, (float) v / mapH, (float) (u + w) / mapW, (float) (v + h) / mapH);
    }

    public static void blit(final MatrixStack matrices,
                            final Identifier id,
                            final int x,
                            final int y,
                            final int w,
                            final int h,
                            final int u,
                            final int v,
                            final int uW,
                            final int vH,
                            final int mapW,
                            final int mapH) {
        blit(matrices, id, x, y, w, h, (float) u / mapW, (float) v / mapH, (float) (u + uW) / mapW, (float) (v + vH) / mapH);
    }

    public static void blit(final MatrixStack matrices,
                            final Identifier rl,
                            final int x,
                            final int y,
                            final int w,
                            final int h,
                            final float uMin,
                            final float vMin,
                            final float uMax,
                            final float vMax) {
        MinecraftClient.getInstance().getTextureManager().bindTexture(rl);
        RenderSystem.setShaderTexture(0, rl);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        final Matrix4f matrix = matrices.peek().getPositionMatrix();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(matrix, x, y, 0).texture(uMin, vMin).next();
        buffer.vertex(matrix, x, y + h, 0).texture(uMin, vMax).next();
        buffer.vertex(matrix, x + w, y + h, 0).texture(uMax, vMax).next();
        buffer.vertex(matrix, x + w, y, 0).texture(uMax, vMin).next();
        Tessellator.getInstance().draw();
    }

    /**
     * Draws texture without scaling so one texel is one pixel, using repeatable texture center.
     * TODO: Nightenom - rework to better algorithm from pgr, also texture extensions?
     *
     * @param matrices            MatrixStack
     * @param id            image ResLoc
     * @param x             start target coords [pixels]
     * @param y             start target coords [pixels]
     * @param width         target rendering box [pixels]
     * @param height        target rendering box [pixels]
     * @param u             texture start offset [texels]
     * @param v             texture start offset [texels]
     * @param uWidth        texture rendering box [texels]
     * @param vHeight       texture rendering box [texels]
     * @param textureWidth  texture file size [texels]
     * @param textureHeight texture file size [texels]
     * @param uRepeat       offset relative to u, v [texels], smaller than uWidth
     * @param vRepeat       offset relative to u, v [texels], smaller than vHeight
     * @param repeatWidth   size of repeatable box in texture [texels], smaller than or equal uWidth - uRepeat
     * @param repeatHeight  size of repeatable box in texture [texels], smaller than or equal vHeight - vRepeat
     */
    protected static void blitRepeatable(final MatrixStack matrices,
                                         final Identifier id,
                                         final int x, final int y,
                                         final int width, final int height,
                                         final int u, final int v,
                                         final int uWidth, final int vHeight,
                                         final int textureWidth, final int textureHeight,
                                         final int uRepeat, final int vRepeat,
                                         final int repeatWidth, final int repeatHeight) {
        if (uRepeat < 0 || vRepeat < 0 || uRepeat >= uWidth || vRepeat >= vHeight || repeatWidth < 1 || repeatHeight < 1
                || repeatWidth > uWidth - uRepeat || repeatHeight > vHeight - vRepeat) {
            throw new IllegalArgumentException("Repeatable box is outside of texture box");
        }

        final int repeatCountX = Math.max(1, Math.max(0, width - (uWidth - repeatWidth)) / repeatWidth);
        final int repeatCountY = Math.max(1, Math.max(0, height - (vHeight - repeatHeight)) / repeatHeight);

        final Matrix4f mat = matrices.peek().getPositionMatrix();
        final BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_TEXTURE);

        // main
        for (int i = 0; i < repeatCountX; i++) {
            final int uAdjust = i == 0 ? 0 : uRepeat;
            final int xStart = x + uAdjust + i * repeatWidth;
            final int w = Math.min(repeatWidth + uRepeat - uAdjust, width - (uWidth - uRepeat - repeatWidth));
            final float minU = (float) (u + uAdjust) / textureWidth;
            final float maxU = (float) (u + uAdjust + w) / textureWidth;

            for (int j = 0; j < repeatCountY; j++) {
                final int vAdjust = j == 0 ? 0 : vRepeat;
                final int yStart = y + vAdjust + j * repeatHeight;
                final int h = Math.min(repeatHeight + vRepeat - vAdjust, height - (vHeight - vRepeat - repeatHeight));
                final float minV = (float) (v + vAdjust) / textureHeight;
                final float maxV = (float) (v + vAdjust + h) / textureHeight;

                populateBlitTriangles(buffer, mat, xStart, xStart + w, yStart, yStart + h, minU, maxU, minV, maxV);
            }
        }

        final int xEnd = x + Math.min(uRepeat + repeatCountX * repeatWidth, width - (uWidth - uRepeat - repeatWidth));
        final int yEnd = y + Math.min(vRepeat + repeatCountY * repeatHeight, height - (vHeight - vRepeat - repeatHeight));
        final int uLeft = width - (xEnd - x);
        final int vLeft = height - (yEnd - y);
        final float restMinU = (float) (u + uWidth - uLeft) / textureWidth;
        final float restMaxU = (float) (u + uWidth) / textureWidth;
        final float restMinV = (float) (v + vHeight - vLeft) / textureHeight;
        final float restMaxV = (float) (v + vHeight) / textureHeight;

        // bot border
        for (int i = 0; i < repeatCountX; i++) {
            final int uAdjust = i == 0 ? 0 : uRepeat;
            final int xStart = x + uAdjust + i * repeatWidth;
            final int w = Math.min(repeatWidth + uRepeat - uAdjust, width - uLeft);
            final float minU = (float) (u + uAdjust) / textureWidth;
            final float maxU = (float) (u + uAdjust + w) / textureWidth;

            populateBlitTriangles(buffer, mat, xStart, xStart + w, yEnd, yEnd + vLeft, minU, maxU, restMinV, restMaxV);
        }

        // left border
        for (int j = 0; j < repeatCountY; j++) {
            final int vAdjust = j == 0 ? 0 : vRepeat;
            final int yStart = y + vAdjust + j * repeatHeight;
            final int h = Math.min(repeatHeight + vRepeat - vAdjust, height - vLeft);
            float minV = (float) (v + vAdjust) / textureHeight;
            float maxV = (float) (v + vAdjust + h) / textureHeight;

            populateBlitTriangles(buffer, mat, xEnd, xEnd + uLeft, yStart, yStart + h, restMinU, restMaxU, minV, maxV);
        }

        // bot left corner
        populateBlitTriangles(buffer, mat, xEnd, xEnd + uLeft, yEnd, yEnd + vLeft, restMinU, restMaxU, restMinV, restMaxV);

        MinecraftClient.getInstance().getTextureManager().bindTexture(id);
        RenderSystem.setShaderTexture(0, id);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Tessellator.getInstance().draw();
    }

    public static void populateFillTriangles(final Matrix4f matrix,
                                             final BufferBuilder buffer,
                                             final int x,
                                             final int y,
                                             final int w,
                                             final int h,
                                             final int red,
                                             final int green,
                                             final int blue,
                                             final int alpha) {
        buffer.vertex(matrix, x, y, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x, y + h, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + w, y, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + w, y, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x, y + h, 0).color(red, green, blue, alpha).next();
        buffer.vertex(matrix, x + w, y + h, 0).color(red, green, blue, alpha).next();
    }

    public static void populateFillGradientTriangles(final Matrix4f matrix,
                                                     final BufferBuilder buffer,
                                                     final int x,
                                                     final int y,
                                                     final int w,
                                                     final int h,
                                                     final int redStart,
                                                     final int redEnd,
                                                     final int greenStart,
                                                     final int greenEnd,
                                                     final int blueStart,
                                                     final int blueEnd,
                                                     final int alphaStart,
                                                     final int alphaEnd) {
        buffer.vertex(matrix, x, y, 0).color(redStart, greenStart, blueStart, alphaStart).next();
        buffer.vertex(matrix, x, y + h, 0).color(redEnd, greenEnd, blueEnd, alphaEnd).next();
        buffer.vertex(matrix, x + w, y, 0).color(redStart, greenStart, blueStart, alphaStart).next();
        buffer.vertex(matrix, x + w, y, 0).color(redStart, greenStart, blueStart, alphaStart).next();
        buffer.vertex(matrix, x, y + h, 0).color(redEnd, greenEnd, blueEnd, alphaEnd).next();
        buffer.vertex(matrix, x + w, y + h, 0).color(redEnd, greenEnd, blueEnd, alphaEnd).next();
    }

    public static void populateBlitTriangles(final BufferBuilder buffer,
                                             final Matrix4f matrix,
                                             final float xStart,
                                             final float xEnd,
                                             final float yStart,
                                             final float yEnd,
                                             final float uMin,
                                             final float uMax,
                                             final float vMin,
                                             final float vMax) {
        buffer.vertex(matrix, xStart, yStart, 0).texture(uMin, vMin).next();
        buffer.vertex(matrix, xStart, yEnd, 0).texture(uMin, vMax).next();
        buffer.vertex(matrix, xEnd, yStart, 0).texture(uMax, vMin).next();
        buffer.vertex(matrix, xEnd, yStart, 0).texture(uMax, vMin).next();
        buffer.vertex(matrix, xStart, yEnd, 0).texture(uMin, vMax).next();
        buffer.vertex(matrix, xEnd, yEnd, 0).texture(uMax, vMax).next();
    }
}
