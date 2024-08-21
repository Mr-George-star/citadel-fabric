package net.george.citadel.client.model.basic;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;

import java.util.Random;

/*
 * @since 1.9.0
 * Duplicate of ModelPart class which is not final
 */
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class BasicModelPart {
    public float textureWidth = 64.0F;
    public float textureHeight = 32.0F;
    public int textureOffsetX;
    public int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public boolean mirror;
    public boolean showModel = true;
    private final ObjectList<ModelBox> cubeList = new ObjectArrayList<>();
    private final ObjectList<BasicModelPart> childModels = new ObjectArrayList<>();

    public BasicModelPart(BasicEntityModel<?> model) {
        this.setTextureSize(model.textureWidth, model.textureHeight);
    }

    public BasicModelPart(BasicEntityModel<?> model, int texOffX, int texOffY) {
        this(model.textureWidth, model.textureHeight, texOffX, texOffY);
    }

    public BasicModelPart(int textureWidth, int textureHeight, int textureOffsetX, int textureOffsetY) {
        this.setTextureSize(textureWidth, textureHeight);
        this.setTextureOffset(textureOffsetX, textureOffsetY);
    }

    private BasicModelPart() {
    }

    public BasicModelPart getModelAngleCopy() {
        BasicModelPart BasicModelPart = new BasicModelPart();
        BasicModelPart.copyModelAngles(this);
        return BasicModelPart;
    }

    public void copyModelAngles(BasicModelPart part) {
        this.rotateAngleX = part.rotateAngleX;
        this.rotateAngleY = part.rotateAngleY;
        this.rotateAngleZ = part.rotateAngleZ;
        this.rotationPointX = part.rotationPointX;
        this.rotationPointY = part.rotationPointY;
        this.rotationPointZ = part.rotationPointZ;
    }

    /**
     * Sets the current box's rotation points and rotation angles to another box.
     */
    public void addChild(BasicModelPart renderer) {
        this.childModels.add(renderer);
    }

    public BasicModelPart setTextureOffset(int x, int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        return this;
    }

    public BasicModelPart addBox(String partName, float x, float y, float z, int width, int height, int depth, float delta, int texX, int texY) {
        this.setTextureOffset(texX, texY);
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, (float)width, (float)height, (float)depth, delta, delta, delta, this.mirror, false);
        return this;
    }

    public BasicModelPart addBox(float x, float y, float z, float width, float height, float depth) {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, this.mirror, false);
        return this;
    }

    public BasicModelPart addBox(float x, float y, float z, float width, float height, float depth, boolean mirror) {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, 0.0F, 0.0F, 0.0F, mirror, false);
        return this;
    }

    public void addBox(float x, float y, float z, float width, float height, float depth, float delta) {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, delta, delta, delta, this.mirror, false);
    }

    public void addBox(float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ) {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, this.mirror, false);
    }

    public void addBox(float x, float y, float z, float width, float height, float depth, float delta, boolean mirror) {
        this.addBox(this.textureOffsetX, this.textureOffsetY, x, y, z, width, height, depth, delta, delta, delta, mirror, false);
    }

    private void addBox(int texOffX, int texOffY, float x, float y, float z, float width, float height, float depth, float deltaX, float deltaY, float deltaZ, boolean mirror, boolean p_228305_13_) {
        this.cubeList.add(new ModelBox(texOffX, texOffY, x, y, z, width, height, depth, deltaX, deltaY, deltaZ, mirror, this.textureWidth, this.textureHeight));
    }

    public void setRotationPoint(float rotationPointX, float rotationPointY, float rotationPointZ) {
        this.rotationPointX = rotationPointX;
        this.rotationPointY = rotationPointY;
        this.rotationPointZ = rotationPointZ;
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.render(matrices, vertices, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (this.showModel) {
            if (!this.cubeList.isEmpty() || !this.childModels.isEmpty()) {
                matrices.push();
                this.translateRotate(matrices);
                this.doRender(matrices.peek(), vertices, light, overlay, red, green, blue, alpha);

                for(BasicModelPart BasicModelPart : this.childModels) {
                    BasicModelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
                }

                matrices.pop();
            }
        }
    }

    public void translateRotate(MatrixStack matrices) {
        matrices.translate(this.rotationPointX / 16.0F, this.rotationPointY / 16.0F, this.rotationPointZ / 16.0F);
        if (this.rotateAngleZ != 0.0F) {
            matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(this.rotateAngleZ));
        }

        if (this.rotateAngleY != 0.0F) {
            matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(this.rotateAngleY));
        }

        if (this.rotateAngleX != 0.0F) {
            matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(this.rotateAngleX));
        }
    }

    private void doRender(MatrixStack.Entry entry, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();

        for (ModelBox box : this.cubeList) {
            for (TexturedQuad quad : box.quads) {
                Vec3f vector3f = quad.normal.copy();
                vector3f.transform(matrix3f);
                float f = vector3f.getX();
                float f1 = vector3f.getY();
                float f2 = vector3f.getZ();

                for (int i = 0; i < 4; ++i) {
                    PositionTextureVertex vertex = quad.vertexPositions[i];
                    float f3 = vertex.position.getX() / 16.0F;
                    float f4 = vertex.position.getY() / 16.0F;
                    float f5 = vertex.position.getZ() / 16.0F;
                    Vector4f vector4f = new Vector4f(f3, f4, f5, 1.0F);
                    vector4f.transform(matrix4f);
                    vertices.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.textureU, vertex.textureV, overlay, light, f, f1, f2);
                }
            }
        }
    }

    /**
     * Returns the model renderer with the new texture parameters.
     */
    public BasicModelPart setTextureSize(int textureWidth, int textureHeight) {
        this.textureWidth = (float)textureWidth;
        this.textureHeight = (float)textureHeight;
        return this;
    }

    public ModelBox getRandomCube(Random random) {
        return this.cubeList.get(random.nextInt(this.cubeList.size()));
    }

    @Environment(EnvType.CLIENT)
    public static class ModelBox {
        private final TexturedQuad[] quads;
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

            PositionTextureVertex vertex1 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
            PositionTextureVertex vertex2 = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
            PositionTextureVertex vertex3 = new PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
            PositionTextureVertex vertex4 = new PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
            PositionTextureVertex vertex5 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
            PositionTextureVertex vertex6 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
            PositionTextureVertex vertex7 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
            PositionTextureVertex vertex8 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
            float f4 = (float)texOffX;
            float f5 = (float)texOffX + depth;
            float f6 = (float)texOffX + depth + width;
            float f7 = (float)texOffX + depth + width + width;
            float f8 = (float)texOffX + depth + width + depth;
            float f9 = (float)texOffX + depth + width + depth + width;
            float f10 = (float)texOffY;
            float f11 = (float)texOffY + depth;
            float f12 = (float)texOffY + depth + height;
            this.quads[2] = new TexturedQuad(new PositionTextureVertex[]{vertex6, vertex5, vertex1, vertex2}, f5, f10, f6, f11, texWidth, texHeight, mirror, Direction.DOWN);
            this.quads[3] = new TexturedQuad(new PositionTextureVertex[]{vertex3, vertex4, vertex8, vertex7}, f6, f11, f7, f10, texWidth, texHeight, mirror, Direction.UP);
            this.quads[1] = new TexturedQuad(new PositionTextureVertex[]{vertex1, vertex5, vertex8, vertex4}, f4, f11, f5, f12, texWidth, texHeight, mirror, Direction.WEST);
            this.quads[4] = new TexturedQuad(new PositionTextureVertex[]{vertex2, vertex1, vertex4, vertex3}, f5, f11, f6, f12, texWidth, texHeight, mirror, Direction.NORTH);
            this.quads[0] = new TexturedQuad(new PositionTextureVertex[]{vertex6, vertex2, vertex3, vertex7}, f6, f11, f8, f12, texWidth, texHeight, mirror, Direction.EAST);
            this.quads[5] = new TexturedQuad(new PositionTextureVertex[]{vertex5, vertex6, vertex7, vertex8}, f8, f11, f9, f12, texWidth, texHeight, mirror, Direction.SOUTH);
        }
    }

    @Environment(EnvType.CLIENT)
    static class PositionTextureVertex {
        public final Vec3f position;
        public final float textureU;
        public final float textureV;

        public PositionTextureVertex(float x, float y, float z, float texU, float texV) {
            this(new Vec3f(x, y, z), texU, texV);
        }

        public PositionTextureVertex setTextureUV(float texU, float texV) {
            return new PositionTextureVertex(this.position, texU, texV);
        }

        public PositionTextureVertex(Vec3f posIn, float texU, float texV) {
            this.position = posIn;
            this.textureU = texU;
            this.textureV = texV;
        }
    }

    @Environment(EnvType.CLIENT)
    static class TexturedQuad {
        public final PositionTextureVertex[] vertexPositions;
        public final Vec3f normal;

        public TexturedQuad(PositionTextureVertex[] positions, float u1, float v1, float u2, float v2, float texWidth, float texHeight, boolean mirror, Direction direction) {
            this.vertexPositions = positions;
            float f = 0.0F / texWidth;
            float f1 = 0.0F / texHeight;
            positions[0] = positions[0].setTextureUV(u2 / texWidth - f, v1 / texHeight + f1);
            positions[1] = positions[1].setTextureUV(u1 / texWidth + f, v1 / texHeight + f1);
            positions[2] = positions[2].setTextureUV(u1 / texWidth + f, v2 / texHeight - f1);
            positions[3] = positions[3].setTextureUV(u2 / texWidth - f, v2 / texHeight - f1);
            if (mirror) {
                int i = positions.length;

                for(int j = 0; j < i / 2; ++j) {
                    PositionTextureVertex vertex = positions[j];
                    positions[j] = positions[i - 1 - j];
                    positions[i - 1 - j] = vertex;
                }
            }

            this.normal = direction.getUnitVector();
            if (mirror) {
                this.normal.multiplyComponentwise(-1.0F, 1.0F, 1.0F);
            }
        }
    }
}
