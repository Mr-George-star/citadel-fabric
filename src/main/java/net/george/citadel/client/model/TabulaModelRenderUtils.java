package net.george.citadel.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class TabulaModelRenderUtils {
    @Environment(EnvType.CLIENT)
    static class PositionTextureVertex {
        public final Vec3f position;
        public final float textureU;
        public final float textureV;

        public PositionTextureVertex(float x, float y, float z, float u, float v) {
            this(new Vec3f(x, y, z), u, v);
        }

        public PositionTextureVertex setTextureUV(float u, float v) {
            return new PositionTextureVertex(this.position, u, v);
        }

        public PositionTextureVertex(Vec3f position, float u, float v) {
            this.position = position;
            this.textureU = u;
            this.textureV = v;
        }
    }

    @Environment(EnvType.CLIENT)
    static class TexturedQuad {
        public final PositionTextureVertex[] vertexPositions;
        public final Vec3f normal;

        public TexturedQuad(PositionTextureVertex[] vertices, float u1, float v1, float u2, float v2, float texWidth, float texHeight, boolean mirror, Direction direction) {
            this.vertexPositions = vertices;
            float width = 0.0F / texWidth;
            float height = 0.0F / texHeight;
            vertices[0] = vertices[0].setTextureUV(u2 / texWidth - width, v1 / texHeight + height);
            vertices[1] = vertices[1].setTextureUV(u1 / texWidth + width, v1 / texHeight + height);
            vertices[2] = vertices[2].setTextureUV(u1 / texWidth + width, v2 / texHeight - height);
            vertices[3] = vertices[3].setTextureUV(u2 / texWidth - width, v2 / texHeight - height);
            if (mirror) {
                int length = vertices.length;

                for(int i = 0; i < length / 2; ++i) {
                    PositionTextureVertex vertex = vertices[i];
                    vertices[i] = vertices[length - 1 - i];
                    vertices[length - 1 - i] = vertex;
                }
            }

            this.normal = direction.getUnitVector();
            if (mirror) {
                this.normal.multiplyComponentwise(-1.0F, 1.0F, 1.0F);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public static class ModelBox {
        @SuppressWarnings("ClassEscapesDefinedScope")
        public final TexturedQuad[] quads;
        public final float posX1;
        public final float posY1;
        public final float posZ1;
        public final float posX2;
        public final float posY2;
        public final float posZ2;

        public ModelBox(int texOffX, int texOffY, float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ, boolean mirror, float texWidth, float texHeight) {
            this.posX1 = x;
            this.posY1 = y;
            this.posZ1 = z;
            this.posX2 = x + width;
            this.posY2 = y + height;
            this.posZ2 = z + depth;
            this.quads = new TexturedQuad[6];
            float f = x + width;
            float f1 = y + height;
            float f2 = z + depth;
            x = x - deltaX;
            y = y - deltaY;
            z = z - deltaZ;
            f = f + deltaX;
            f1 = f1 + deltaY;
            f2 = f2 + deltaZ;
            if (mirror) {
                float f3 = f;
                f = x;
                x = f3;
            }

            PositionTextureVertex vertex7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
            PositionTextureVertex vertex = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
            PositionTextureVertex vertex1 = new PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
            PositionTextureVertex vertex2 = new PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
            PositionTextureVertex vertex3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
            PositionTextureVertex vertex4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
            PositionTextureVertex vertex5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
            PositionTextureVertex vertex6 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
            float f4 = (float)texOffX;
            float f5 = (float)texOffX + depth;
            float f6 = (float)texOffX + depth + width;
            float f7 = (float)texOffX + depth + width + width;
            float f8 = (float)texOffX + depth + width + depth;
            float f9 = (float)texOffX + depth + width + depth + width;
            float f10 = (float)texOffY;
            float f11 = (float)texOffY + depth;
            float f12 = (float)texOffY + depth + height;
            this.quads[2] = new TexturedQuad(new PositionTextureVertex[]{vertex4, vertex3, vertex7, vertex}, f5, f10, f6, f11, texWidth, texHeight, mirror, Direction.DOWN);
            this.quads[3] = new TexturedQuad(new PositionTextureVertex[]{vertex1, vertex2, vertex6, vertex5}, f6, f11, f7, f10, texWidth, texHeight, mirror, Direction.UP);
            this.quads[1] = new TexturedQuad(new PositionTextureVertex[]{vertex7, vertex3, vertex6, vertex2}, f4, f11, f5, f12, texWidth, texHeight, mirror, Direction.WEST);
            this.quads[4] = new TexturedQuad(new PositionTextureVertex[]{vertex, vertex7, vertex2, vertex1}, f5, f11, f6, f12, texWidth, texHeight, mirror, Direction.NORTH);
            this.quads[0] = new TexturedQuad(new PositionTextureVertex[]{vertex4, vertex, vertex1, vertex5}, f6, f11, f8, f12, texWidth, texHeight, mirror, Direction.EAST);
            this.quads[5] = new TexturedQuad(new PositionTextureVertex[]{vertex3, vertex4, vertex5, vertex6}, f8, f11, f9, f12, texWidth, texHeight, mirror, Direction.SOUTH);
        }
    }
}
