package net.george.citadel.server.entity.collision;

import com.google.common.collect.AbstractIterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.CollisionView;
import org.jetbrains.annotations.Nullable;

public class CustomCollisionsBlockCollisions extends AbstractIterator<VoxelShape> {
    private final Box box;
    private final ShapeContext context;
    private final CuboidBlockIterator cursor;
    private final BlockPos.Mutable pos;
    private final VoxelShape entityShape;
    private final CollisionView collisionView;
    private final boolean onlySuffocatingBlocks;
    @Nullable
    private BlockView cachedBlockGetter;
    private long cachedBlockGetterPos;

    public CustomCollisionsBlockCollisions(CollisionView collisionView, @Nullable Entity entity, Box box) {
        this(collisionView, entity, box, false);
    }

    public CustomCollisionsBlockCollisions(CollisionView collisionView, @Nullable Entity entity, Box box, boolean onlySuffocatingBlocks) {
        this.context = entity == null ? ShapeContext.absent() : ShapeContext.of(entity);
        this.pos = new BlockPos.Mutable();
        this.entityShape = VoxelShapes.cuboid(box);
        this.collisionView = collisionView;
        this.box = box;
        this.onlySuffocatingBlocks = onlySuffocatingBlocks;
        int i = MathHelper.floor(box.minX - 1.0E-7D) - 1;
        int j = MathHelper.floor(box.maxX + 1.0E-7D) + 1;
        int k = MathHelper.floor(box.minY - 1.0E-7D) - 1;
        int l = MathHelper.floor(box.maxY + 1.0E-7D) + 1;
        int i1 = MathHelper.floor(box.minZ - 1.0E-7D) - 1;
        int j1 = MathHelper.floor(box.maxZ + 1.0E-7D) + 1;
        this.cursor = new CuboidBlockIterator(i, k, i1, j, l, j1);
    }

    @Nullable
    private BlockView getChunk(int xCoord, int zCoord) {
        int i = ChunkSectionPos.getSectionCoord(xCoord);
        int j = ChunkSectionPos.getSectionCoord(zCoord);
        long k = ChunkPos.toLong(i, j);
        if (this.cachedBlockGetter != null && this.cachedBlockGetterPos == k) {
            return this.cachedBlockGetter;
        } else {
            BlockView chunk = this.collisionView.getChunkAsView(i, j);
            this.cachedBlockGetter = chunk;
            this.cachedBlockGetterPos = k;
            return chunk;
        }
    }

    @Override
    protected VoxelShape computeNext() {
        while (true) {
            if (this.cursor.step()) {
                int i = this.cursor.getX();
                int j = this.cursor.getY();
                int k = this.cursor.getZ();
                int l = this.cursor.getEdgeCoordinatesCount();
                if (l == 3) {
                    continue;
                }

                BlockView world = this.getChunk(i, k);
                if (world == null) {
                    continue;
                }

                this.pos.set(i, j, k);
                BlockState state = world.getBlockState(this.pos);

                if (this.onlySuffocatingBlocks && !state.shouldSuffocate(world, this.pos) || l == 1 && !state.exceedsCube() || l == 2 && !state.isOf(Blocks.MOVING_PISTON)) {
                    continue;
                }

                VoxelShape shape = state.getCollisionShape(this.collisionView, this.pos, this.context);
                if (this.context instanceof EntityShapeContext){
                    Entity entity = ((EntityShapeContext)this.context).getEntity();
                    if (entity instanceof ICustomCollisions) {
                        if (((ICustomCollisions) entity).canPassThrough(this.pos, state, shape)) {
                            continue;
                        }
                    }
                }
                if (shape == VoxelShapes.fullCube()) {
                    if (!this.box.intersects(i, j, k, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D)) {
                        continue;
                    }

                    return shape.offset(i, j, k);
                }

                VoxelShape offset = shape.offset(i, j, k);
                if (!VoxelShapes.matchesAnywhere(offset, this.entityShape, BooleanBiFunction.AND)) {
                    continue;
                }

                return offset;
            }

            return this.endOfData();
        }
    }
}
