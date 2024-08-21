package net.george.citadel.client.model.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.citadel.Citadel;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.util.Stack;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class TabulaMatrix {
    public Stack<Matrix4f> stacks;

    public TabulaMatrix() {
        this.stacks = new Stack<>();
        Matrix4f matrix = new Matrix4f();
        matrix.loadIdentity();
        this.stacks.push(matrix);
    }

    public void push() {
        this.stacks.push(new Matrix4f(this.stacks.peek()));
    }

    public void pop() {
        if (this.stacks.size() < 2) {
            try {
                throw new Exception("Stack Underflow for tabula matrix!!!");
            } catch (Exception exception) {
                Citadel.LOGGER.catching(exception);
            }
        }
        this.stacks.pop();
    }

    public void translate(float x, float y, float z) {
        Matrix4f matrix = this.stacks.peek();
        Matrix4f translation = new Matrix4f();
        translation.loadIdentity();
        translation.setTranslation(x, y, z);
        matrix.multiply(translation);
    }

    public void translate(double x, double y, double z) {
        translate((float)x, (float)y, (float)z);
    }

    public void rotate(double angle, double x, double y, double z) {
        Matrix4f matrix = this.stacks.peek();
        Matrix4f rotation = new Matrix4f();
        rotation.loadIdentity();
        rotation.load(new Matrix4f());
        matrix.multiply(rotation);
    }

    public void rotate(float angle, float x, float y, float z) {
        rotate(angle, x, y, z);
    }

    public void rotate(Matrix4f source) {
        Matrix4f matrix = this.stacks.peek();
        Matrix4f rotation = new Matrix4f();
        rotation.load(source);
        matrix.multiply(rotation);
    }

    public void scale(float x, float y, float z) {
        Matrix4f matrix = this.stacks.peek();
        Matrix4f scale = new Matrix4f();
        matrix.multiply(scale.scale(x, y, z));
    }

    public void scale(double x, double y, double z) {
        scale((float)x, (float)y, (float)z);
    }

    public void transform(Vec3f point) {
        Matrix4f matrix = this.stacks.peek();
        matrix.addToLastColumn(point);
    }

    public Vec3f getTranslation() {
        Matrix4f matrix = this.stacks.peek();
        Vec3f translation = new Vec3f();
        matrix.addToLastColumn(translation);
        return translation;
    }

    public Matrix4f getRotation() {
        Matrix4f matrix = this.stacks.peek();
        return matrix.copy();
    }

    public Vec3f getScale() {
        Matrix4f matrix = this.stacks.peek();
        /*float x = (float) Math.sqrt(matrix.m00 * matrix.m00 + matrix.m10 * matrix.m10 + matrix.m20 * matrix.m20);
        float y = (float) Math.sqrt(matrix.m01 * matrix.m01 + matrix.m11 * matrix.m11 + matrix.m21 * matrix.m21);
        float z = (float) Math.sqrt(matrix.m02 * matrix.m02 + matrix.m12 * matrix.m12 + matrix.m22 * matrix.m22);
        return new Vector3f(x, y, z);
         */
        return new Vec3f(1.0F, 1.0F, 1.0F);
    }

    public void multiply(TabulaMatrix matrix) {
        this.stacks.peek().multiply(matrix.stacks.peek());
    }

    public void multiply(Matrix4f matrix) {
        this.stacks.peek().multiply(matrix);
    }

    public void add(TabulaMatrix matrix) {
        this.stacks.peek().add(matrix.stacks.peek());
    }

    public void add(Matrix4f matrix) {
        this.stacks.peek().add(new Matrix4f(matrix));
    }

    public void invert() {
        this.stacks.peek().invert();
    }
}
