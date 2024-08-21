package net.george.citadel.client.game;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;

import java.awt.*;
import java.util.Arrays;

@SuppressWarnings({"SameParameterValue", "deprecation", "BooleanMethodIsAlwaysInverted", "ConstantValue", "unused"})
public class Tetris {
    protected final Random random = Random.create();

    private boolean started = false;
    private int score;
    private int renderTime = 0;
    private int keyCooldown;
    private static final int HEIGHT = 20;
    private TetrominoShape fallingShape;
    private BlockState fallingBlock;
    private float fallingX;
    private float prevFallingY;
    private float fallingY;
    private BlockRotation fallingRotation;
    private final BlockState[][] settledBlocks = new BlockState[10][HEIGHT];
    private boolean gameOver = false;

    private TetrominoShape nextShape;
    private BlockState nextBlock;

    private final boolean[] flashingLayer = new boolean[HEIGHT];
    private int flashFor = 0;

    private final Block[] allRegisteredBlocks = Registry.BLOCK.stream().toList().toArray(Block[]::new);

    public Tetris() {
        reset();
    }

    public void tick() {
        this.renderTime++;
        this.prevFallingY = this.fallingY;
        if (this.keyCooldown > 0) {
            this.keyCooldown--;
        }
        if (this.started && !this.gameOver) {
            if (this.fallingShape == null) {
                generateTetromino();
                generateNextTetromino();
            } else if (groundedTetromino()) {
                groundTetromino();
                this.fallingShape = null;
            } else {
                float f = 0.15F;
                if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), InputUtil.GLFW_KEY_DOWN)) {
                    f = 1F;
                }
                this.fallingY += f;
                if (keyPressed(InputUtil.GLFW_KEY_LEFT) && !isBlocksInOffset(-1, 0)) {
                    this.fallingX = restrictTetrominoX((int) (Math.floor(this.fallingX) - 1));
                }
                if (keyPressed(InputUtil.GLFW_KEY_RIGHT) && !isBlocksInOffset(1, 0)) {
                    this.fallingX = restrictTetrominoX((int) (Math.ceil(this.fallingX) + 1));
                }
                if (keyPressed(InputUtil.GLFW_KEY_UP) && this.fallingRotation != null && this.fallingShape != TetrominoShape.SQUARE) {
                    this.fallingRotation = this.fallingRotation.rotate(BlockRotation.CLOCKWISE_90);
                    this.fallingX = restrictTetrominoX((int) (Math.floor(this.fallingX)));
                }
            }
        }
        if (this.flashFor > 0) {
            this.flashFor--;
            if (this.flashFor == 0) {
                for (int j = 0; j < HEIGHT; j++) {
                    if (this.flashingLayer[j]) {
                        for (int k = j; k < HEIGHT; k++) {
                            for (int i = 0; i < 10; i++) {
                                this.settledBlocks[i][k] = k < HEIGHT - 1 ? this.settledBlocks[i][k + 1] : null;
                            }
                        }
                    }
                }
                int cleared = 0;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F));
                for (int i = 0; i < this.flashingLayer.length; i++) {
                    if (this.flashingLayer[i]) {
                        cleared++;
                    }
                    this.flashingLayer[i] = false;
                }
                if (cleared == 1) {
                    this.score += 40;
                } else if (cleared == 2) {
                    this.score += 100;
                } else if (cleared == 3) {
                    this.score += 300;
                } else if (cleared >= 4) {
                    this.score += 1200 * (cleared - 3);
                }
            }
        }
        if (keyPressed(InputUtil.GLFW_KEY_T)) {
            this.started = true;
            reset();
        }
    }

    private boolean groundedTetromino() {
        for (Vec3i position : this.fallingShape.getRelativePositions()) {
            Vec3i transformed = transform(position, this.fallingRotation, Vec3i.ZERO);
            int x = Math.round(this.fallingX) + transformed.getX();
            int y = HEIGHT - (int) Math.ceil(this.fallingY) - transformed.getY();
            if (y < 0) {
                return true;
            }
            if (x >= 0 && x < 10 && y >= 0 && y < HEIGHT) {
                if (y == 0 || this.settledBlocks[x][y - 1] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void groundTetromino() {
        for (Vec3i position : this.fallingShape.getRelativePositions()) {
            Vec3i transformed = transform(position, this.fallingRotation, Vec3i.ZERO);
            int x = Math.round(this.fallingX) + transformed.getX();
            int y = HEIGHT - (int) Math.ceil(this.fallingY) - transformed.getY();
            if (x >= 0 && x < 10 && y >= 0 && y < HEIGHT) {
                if (y == HEIGHT - 1) {
                    this.gameOver = true;
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_PLAYER_DEATH, 1.0F));
                }
                if (this.settledBlocks[x][y] == null) {
                    this.settledBlocks[x][y] = fallingBlock;
                }
            }
        }
        boolean flag = false;
        for (int j = 0; j < HEIGHT; j++) {
            for (int i = 0; i < 10; i++) {
                if (this.settledBlocks[i][j] == null) {
                    break;
                }
                if (i == 9) {
                    this.flashingLayer[j] = true;
                    flag = true;
                    break;
                }
            }
        }
        if (flag) {
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F));
            this.flashFor = 20;
        }
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(this.fallingBlock.getSoundGroup().getPlaceSound(), 1.0F));
    }

    private boolean isBlocksInOffset(int xOffset, int yOffset) {
        for (Vec3i vec : this.fallingShape.getRelativePositions()) {
            Vec3i vec2 = transform(vec, this.fallingRotation, Vec3i.ZERO);
            int x = Math.round(this.fallingX) + vec2.getX() + xOffset;
            int y = HEIGHT - (int) Math.ceil(this.fallingY) - vec2.getY() + yOffset;
            if (x >= 0 && x < 10 && y >= 0 && y < HEIGHT) {
                if (y == 0 || this.settledBlocks[x][y] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isStarted() {
        return this.started;
    }

    private boolean keyPressed(int keyId) {
        if (this.keyCooldown == 0 && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyId)) {
            this.keyCooldown = 4;
            return true;
        }
        return false;
    }

    private void generateNextTetromino() {
        BlockState randomState = Blocks.DIRT.getDefaultState();
        for (int tries = 0; tries < 5; tries++) {
            if (this.allRegisteredBlocks.length > 1) {
                BlockState block = this.allRegisteredBlocks[this.random.nextInt(this.allRegisteredBlocks.length - 1)].getDefaultState();
                BakedModel blockModel = MinecraftClient.getInstance().getBlockRenderManager().getModel(block);
                //TODO: Just can't do this :: && blockModel.getRenderTypes(block, this.random, ModelData.EMPTY).contains(RenderType.solid())
                if (!block.isOf(Blocks.GLOWSTONE) && !blockModel.isBuiltin()) {
                    randomState = block;
                    break;
                }
            }
        }
        this.nextShape = TetrominoShape.getRandom(this.random);
        this.nextBlock = randomState;
    }

    private void generateTetromino() {
        this.fallingShape = this.nextShape;
        this.fallingBlock = this.nextBlock;
        this.fallingRotation = BlockRotation.random(this.random);
        this.fallingX = restrictTetrominoX(this.random.nextInt(10));
        this.prevFallingY = 0;
        this.fallingY = -2;
    }

    private int restrictTetrominoX(int x) {
        int minShapeX = 0;
        int maxShapeX = 0;
        for (Vec3i position : this.fallingShape.getRelativePositions()) {
            Vec3i transformed = transform(position, this.fallingRotation, Vec3i.ZERO);
            if (transformed.getX() < minShapeX) {
                minShapeX = transformed.getX();
            }
            if (transformed.getX() > maxShapeX) {
                maxShapeX = transformed.getX();
            }
        }
        if (x + minShapeX < 0) {
            x = Math.max(x - minShapeX, minShapeX);
        }
        if (x + maxShapeX > 9) {
            x = Math.min(x - maxShapeX, 9 - maxShapeX);
        }
        return x;
    }

    private void renderTetromino(TetrominoShape shape, BlockState blockState, BlockRotation fallingRotation, float x, float y, float scale, float offsetX, float offsetY) {
        for (Vec3i position : shape.getRelativePositions()) {
            Vec3i transformed = transform(position, fallingRotation, Vec3i.ZERO);
            renderBlockState(blockState, offsetX + (x + transformed.getX()) * scale, offsetY + (y + transformed.getY()) * scale, scale);
        }
    }

    private void renderBlockState(BlockState state, float offsetX, float offsetY, float size) {
        Sprite sprite = MinecraftClient.getInstance().getBlockRenderManager().getModel(state).getParticleSprite();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        float f = size * 0.5F;
        buffer.vertex(-f + offsetX, f + offsetY, 80.0D).texture(sprite.getMinU(), sprite.getMaxV()).next();
        buffer.vertex(f + offsetX, f + offsetY, 80.0D).texture(sprite.getMaxU(), sprite.getMaxV()).next();
        buffer.vertex(f + offsetX, -f + offsetY, 80.0D).texture(sprite.getMaxU(), sprite.getMinV()).next();
        buffer.vertex(-f + offsetX, -f + offsetY, 80.0D).texture(sprite.getMinU(), sprite.getMinV()).next();
        tessellator.draw();
    }

    public void render(TitleScreen screen, MatrixStack matrices, float delta) {
        float scale = Math.min(screen.width / 15F, screen.height / (float) HEIGHT);
        float offsetX = screen.width / 2F - scale * 5F;
        float offsetY = scale * 0.5F;
        if (this.started) {
            DrawableHelper.fill(matrices, (int) (screen.width * 0.05F), (int) (screen.height * 0.3F), (int) (screen.width * 0.05F) + 70, (int) (screen.height * 0.5F), -1873784752);
            DrawableHelper.fill(matrices, (int) (screen.width * 0.7F), (int) (screen.height * 0.3F), (int) (screen.width * 0.7F) + 130, (int) (screen.height * 0.84F), -1873784752);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
            for (int i = 0; i < this.settledBlocks.length; i++) {
                int max = this.settledBlocks[i].length;
                for (int j = 0; j < max; j++) {
                    BlockState state = this.settledBlocks[i][j];
                    if (this.flashingLayer[j] && this.renderTime % 4 < 2) {
                        state = Blocks.GLOWSTONE.getDefaultState();
                    }
                    if (state != null) {
                        renderBlockState(state, offsetX + i * scale, offsetY + (max - j - 1) * scale, scale);
                    }
                }
            }
            if (this.fallingShape != null) {
                float lerpedFallingY = this.prevFallingY + (this.fallingY - this.prevFallingY) * delta;
                renderTetromino(this.fallingShape, this.fallingBlock, this.fallingRotation, this.fallingX, lerpedFallingY, scale, offsetX, offsetY);
            }
            if (this.nextShape != null) {
                renderTetromino(this.nextShape, this.nextBlock, BlockRotation.NONE, 0, 0, scale, screen.width * 0.85F, screen.height * 0.4F);
            }
            float hue = (System.currentTimeMillis() % 6000) / 6000f;
            int rainbow = Color.HSBtoRGB(hue, 0.6f, 1);
            matrices.push();
            matrices.scale(2, 2, 2);
            DrawableHelper.drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, "SCORE", (int) (screen.width * 0.065F), (int) (screen.height * 0.175F), rainbow);
            DrawableHelper.drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, "" + this.score, (int) (screen.width * 0.065F), (int) (screen.height * 0.175F) + 10, rainbow);
            matrices.pop();
            DrawableHelper.drawStringWithShadow(matrices, MinecraftClient.getInstance().textRenderer, "[LEFT ARROW] move left", (int) (screen.width * 0.71F), (int) (screen.height * 0.55F), rainbow);
            DrawableHelper.drawStringWithShadow(matrices, MinecraftClient.getInstance().textRenderer, "[RIGHT ARROW] move right", (int) (screen.width * 0.71F), (int) (screen.height * 0.55F) + 10, rainbow);
            DrawableHelper.drawStringWithShadow(matrices, MinecraftClient.getInstance().textRenderer, "[UP ARROW] rotate", (int) (screen.width * 0.71F), (int) (screen.height * 0.55F) + 20, rainbow);
            DrawableHelper.drawStringWithShadow(matrices, MinecraftClient.getInstance().textRenderer, "[DOWN ARROW] quick drop", (int) (screen.width * 0.71F), (int) (screen.height * 0.55F) + 30, rainbow);
            DrawableHelper.drawStringWithShadow(matrices, MinecraftClient.getInstance().textRenderer, "[T] start over", (int) (screen.width * 0.71F), (int) (screen.height * 0.55F) + 50, rainbow);
            DrawableHelper.drawStringWithShadow(matrices, MinecraftClient.getInstance().textRenderer, "Happy april fools from Citadel", 5, 5, rainbow);
            if (this.gameOver) {
                matrices.push();
                matrices.translate((int) (screen.width * 0.5F), (int) (screen.height * 0.5F), 150);
                matrices.scale(3 + (float) Math.sin(hue * Math.PI) * 0.4F, 3 + (float) Math.sin(hue * Math.PI) * 0.4F, 3 + (float) Math.sin(hue * Math.PI) * 0.4F);
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) Math.sin(hue * Math.PI) * 10));
                DrawableHelper.drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, "GAME OVER", 0, 0, rainbow);
                matrices.pop();
            }
        }
    }

    public void reset() {
        this.score = 0;
        for (BlockState[] settledBlock : this.settledBlocks) {
            Arrays.fill(settledBlock, null);
        }
        this.gameOver = false;
        Arrays.fill(this.flashingLayer, false);
        generateNextTetromino();
        generateTetromino();
        generateNextTetromino();
    }

    private static Vec3i transform(Vec3i vec3i, BlockRotation rotation, Vec3i relativeTo) {
        int i = vec3i.getX();
        int k = vec3i.getY();
        int j = vec3i.getZ();

        int l = relativeTo.getX();
        int i1 = relativeTo.getY();
        return switch (rotation) {
            case COUNTERCLOCKWISE_90 -> new Vec3i(l - i1 + k, l + i1 - i, j);
            case CLOCKWISE_90 -> new Vec3i(l + i1 - k, i1 - l + i, j);
            case CLOCKWISE_180 -> new Vec3i(l + l - i, i1 + i1 - k, j);
            default -> new Vec3i(i, k, j);
        };
    }
}
