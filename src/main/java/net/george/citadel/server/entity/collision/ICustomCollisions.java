package net.george.citadel.server.entity.collision;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public interface ICustomCollisions {
    /*
        Override Entity#getAllowedMovement with entity method
     */
    static Vec3d getAllowedMovementForEntity(Entity entity, Vec3d vec3d) {
        Box box = entity.getBoundingBox();
        List<VoxelShape> list = entity.world.getEntityCollisions(entity, box.stretch(vec3d));
        Vec3d vec3 = vec3d.lengthSquared() == 0.0D ? vec3d : collideBoundingBox2(entity, vec3d, box, entity.world, list);
        boolean flag = vec3d.x != vec3.x;
        boolean flag1 = vec3d.y != vec3.y;
        boolean flag2 = vec3d.z != vec3.z;
        boolean flag3 = entity.isOnGround() || flag1 && vec3d.y < 0.0D;
        if (entity.stepHeight > 0.0F && flag3 && (flag || flag2)) {
            Vec3d vec3d1 = collideBoundingBox2(entity, new Vec3d(vec3d.x, entity.stepHeight, vec3d.z), box, entity.world, list);
            Vec3d vec3d2 = collideBoundingBox2(entity, new Vec3d(0.0D, entity.stepHeight, 0.0D), box.stretch(vec3d.x, 0.0D, vec3d.z), entity.world, list);
            if (vec3d2.y < (double)entity.stepHeight) {
                Vec3d vec3d3 = collideBoundingBox2(entity, new Vec3d(vec3d.x, 0.0D, vec3d.z), box.offset(vec3d2), entity.world, list).add(vec3d2);
                if (vec3d3.horizontalLengthSquared() > vec3d1.horizontalLengthSquared()) {
                    vec3d1 = vec3d3;
                }
            }

            if (vec3d1.horizontalLengthSquared() > vec3.horizontalLengthSquared()) {
                return vec3d1.add(collideBoundingBox2(entity, new Vec3d(0.0D, -vec3d1.y + vec3d.y, 0.0D), box.offset(vec3d1), entity.world, list));
            }
        }

        return vec3;
    }

    boolean canPassThrough(BlockPos mutablePos, BlockState state, VoxelShape shape);

    //1.18 logic
    private static Vec3d collideBoundingBox2(@Nullable Entity entity, Vec3d vec3d, Box box, World world, List<VoxelShape> voxelShapes) {
        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(voxelShapes.size() + 1);
        if (!voxelShapes.isEmpty()) {
            builder.addAll(voxelShapes);
        }

        WorldBorder worldBorder = world.getWorldBorder();
        boolean flag = entity != null && worldBorder.canCollide(entity, box.stretch(vec3d));
        if (flag) {
            builder.add(worldBorder.asVoxelShape());
        }

        builder.addAll(new CustomCollisionsBlockCollisions(world, entity, box.stretch(vec3d)));
        return collideWithShapes2(vec3d, box, builder.build());
    }

    private static Vec3d collideWithShapes2(Vec3d pos, Box box, List<VoxelShape> voxelShapes) {
        if (voxelShapes.isEmpty()) {
            return pos;
        } else {
            double d0 = pos.x;
            double d1 = pos.y;
            double d2 = pos.z;
            if (d1 != 0.0D) {
                d1 = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, box, voxelShapes, d1);
                if (d1 != 0.0D) {
                    box = box.offset(0.0D, d1, 0.0D);
                }
            }

            boolean flag = Math.abs(d0) < Math.abs(d2);
            if (flag && d2 != 0.0D) {
                d2 = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, box, voxelShapes, d2);
                if (d2 != 0.0D) {
                    box = box.offset(0.0D, 0.0D, d2);
                }
            }

            if (d0 != 0.0D) {
                d0 = VoxelShapes.calculateMaxOffset(Direction.Axis.X, box, voxelShapes, d0);
                if (!flag && d0 != 0.0D) {
                    box = box.offset(d0, 0.0D, 0.0D);
                }
            }

            if (!flag && d2 != 0.0D) {
                d2 = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, box, voxelShapes, d2);
            }

            return new Vec3d(d0, d1, d2);
        }
    }

}
