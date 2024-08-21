package net.george.citadel.server.entity.collision;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class CitadelVoxelShapeSpliterator extends Spliterators.AbstractSpliterator<VoxelShape> {
    @Nullable
    private final Entity entity;
    private final Box box;
    private final ShapeContext context;
    private final CuboidBlockIterator cubeCoordinateIterator;
    private final BlockPos.Mutable mutablePos;
    private final VoxelShape shape;
    private final CollisionView reader;
    private final BiPredicate<BlockState, BlockPos> statePositionPredicate;
    private boolean needsBorderCheck;

    public CitadelVoxelShapeSpliterator(CollisionView reader, @Nullable Entity entity, Box box) {
        this(reader, entity, box, (state, pos) -> true);
    }

    public CitadelVoxelShapeSpliterator(CollisionView reader, @Nullable Entity entity, Box box, BiPredicate<BlockState, BlockPos> statePositionPredicate) {
        super(Long.MAX_VALUE, Spliterator.NONNULL | Spliterator.IMMUTABLE);
        this.context = entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
        this.mutablePos = new BlockPos.Mutable();
        this.shape = VoxelShapes.cuboid(box);
        this.reader = reader;
        this.needsBorderCheck = entity != null;
        this.entity = entity;
        this.box = box;
        this.statePositionPredicate = statePositionPredicate;
        int i = MathHelper.floor(box.minX - 1.0E-7D) - 1;
        int j = MathHelper.floor(box.maxX + 1.0E-7D) + 1;
        int k = MathHelper.floor(box.minY - 1.0E-7D) - 1;
        int l = MathHelper.floor(box.maxY + 1.0E-7D) + 1;
        int i1 = MathHelper.floor(box.minZ - 1.0E-7D) - 1;
        int j1 = MathHelper.floor(box.maxZ + 1.0E-7D) + 1;
        this.cubeCoordinateIterator = new CuboidBlockIterator(i, k, i1, j, l, j1);
    }

    private static boolean isCloseToBorder(VoxelShape shape, Box box) {
        return VoxelShapes.matchesAnywhere(shape, VoxelShapes.cuboid(box.expand(1.0E-7D)), BooleanBiFunction.AND);
    }

    private static boolean isOutsideBorder(VoxelShape shape, Box box) {
        return VoxelShapes.matchesAnywhere(shape, VoxelShapes.cuboid(box.contract(1.0E-7D)), BooleanBiFunction.AND);
    }

    public static boolean isBoxFullyWithinWorldBorder(WorldBorder border, Box box) {
        double d0 = MathHelper.floor(border.getBoundWest());
        double d1 = MathHelper.floor(border.getBoundNorth());
        double d2 = MathHelper.ceil(border.getBoundEast());
        double d3 = MathHelper.ceil(border.getBoundSouth());
        return box.minX > d0 && box.minX < d2 && box.minZ > d1 && box.minZ < d3 && box.maxX > d0 && box.maxX < d2 && box.maxZ > d1 && box.maxZ < d3;
    }

    @Override
    public boolean tryAdvance(Consumer<? super VoxelShape> action) {
        return this.needsBorderCheck && this.worldBorderCheck(action) || this.collisionCheck(action);
    }

    boolean collisionCheck(Consumer<? super VoxelShape> consumer) {
        while (true) {
            if (this.cubeCoordinateIterator.step()) {
                int i = this.cubeCoordinateIterator.getX();
                int j = this.cubeCoordinateIterator.getY();
                int k = this.cubeCoordinateIterator.getZ();
                int l = this.cubeCoordinateIterator.getEdgeCoordinatesCount();
                if (l == 3) {
                    continue;
                }

                BlockView world = this.getChunk(i, k);
                if (world == null) {
                    continue;
                }

                this.mutablePos.set(i, j, k);
                BlockState state = world.getBlockState(this.mutablePos);
                if (!this.statePositionPredicate.test(state, this.mutablePos) || l == 1 && !state.exceedsCube() || l == 2 && state.getBlock() != Blocks.MOVING_PISTON) {
                    continue;
                }
                VoxelShape collisionShape = state.getCollisionShape(this.reader, this.mutablePos, this.context);
                if (this.entity instanceof ICustomCollisions && ((ICustomCollisions) this.entity).canPassThrough(this.mutablePos, state, collisionShape)) {
                    continue;
                }
                if (collisionShape == VoxelShapes.fullCube()) {
                    if (!this.box.intersects(i, j, k, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D)) {
                        continue;
                    }

                    consumer.accept(collisionShape.offset(i, j, k));
                    return true;
                }

                VoxelShape offset = collisionShape.offset(i, j, k);
                if (!VoxelShapes.matchesAnywhere(offset, this.shape, BooleanBiFunction.AND)) {
                    continue;
                }

                consumer.accept(offset);
                return true;
            }

            return false;
        }
    }

    @Nullable
    private BlockView getChunk(int chunkX, int chunkZ) {
        int i = chunkX >> 4;
        int j = chunkZ >> 4;
        return this.reader.getChunkAsView(i, j);
    }

    boolean worldBorderCheck(Consumer<? super VoxelShape> shape) {
        Objects.requireNonNull(this.entity);
        this.needsBorderCheck = false;
        WorldBorder worldBorder = this.reader.getWorldBorder();
        Box box = this.entity.getBoundingBox();
        if (!isBoxFullyWithinWorldBorder(worldBorder, box)) {
            VoxelShape border = worldBorder.asVoxelShape();
            if (!isOutsideBorder(border, box) && isCloseToBorder(border, box)) {
                shape.accept(border);
                return true;
            }
        }

        return false;
    }
}
