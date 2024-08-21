package net.george.citadel.server.entity.collision;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class CustomCollisionsNodeProcessor extends LandPathNodeMaker {
    public CustomCollisionsNodeProcessor() {
    }

    public static PathNodeType getBlockPathTypeStatic(BlockView world, BlockPos.Mutable mutablePos) {
        int i = mutablePos.getX();
        int j = mutablePos.getY();
        int k = mutablePos.getZ();
        PathNodeType nodes = getNodes(world, mutablePos);
        if (nodes == PathNodeType.OPEN && j >= 1) {
            PathNodeType nodeType = getNodes(world, mutablePos.set(i, j - 1, k));
            nodes = nodeType != PathNodeType.WALKABLE && nodeType != PathNodeType.OPEN && nodeType != PathNodeType.WATER && nodeType != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;
            if (nodeType == PathNodeType.DAMAGE_FIRE) {
                nodes = PathNodeType.DAMAGE_FIRE;
            }

            if (nodeType == PathNodeType.DAMAGE_CACTUS) {
                nodes = PathNodeType.DAMAGE_CACTUS;
            }

            if (nodeType == PathNodeType.DAMAGE_OTHER) {
                nodes = PathNodeType.DAMAGE_OTHER;
            }

            if (nodeType == PathNodeType.STICKY_HONEY) {
                nodes = PathNodeType.STICKY_HONEY;
            }
        }

        if (nodes == PathNodeType.WALKABLE) {
            nodes = getNodeTypeFromNeighbors(world, mutablePos.set(i, j, k), nodes);
        }

        return nodes;
    }

    @SuppressWarnings("unused")
    protected static PathNodeType getNodes(BlockView world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        PathNodeType type = LandPathNodeMaker.getCommonNodeType(world, pos);
        if (type != null) {
            return type;
        } else {
            Block block = state.getBlock();
            Material material = state.getMaterial();
            if (state.isAir()) {
                return PathNodeType.OPEN;
            } else {
                return state.getBlock() == Blocks.BAMBOO ? PathNodeType.OPEN : getCommonNodeType(world, pos);
            }
        }
    }

    public PathNodeType getDefaultNodeType(BlockView world, int x, int y, int z) {
        return getBlockPathTypeStatic(world, new BlockPos.Mutable(x, y, z));
    }

    protected PathNodeType adjustNodeType(BlockView world, boolean canOpenDoors, boolean canEnterOpenDoors, BlockPos pos, PathNodeType nodeType) {
        BlockState state = world.getBlockState(pos);
        return ((ICustomCollisions)this.entity).canPassThrough(pos, state, state.getSidesShape(world, pos)) ? PathNodeType.OPEN : super.adjustNodeType(world, canOpenDoors, canEnterOpenDoors, pos, nodeType);
    }
}
