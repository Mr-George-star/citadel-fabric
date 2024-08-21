package net.george.citadel.server.entity.collision;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

@SuppressWarnings("unused")
public class MovementControllerCustomCollisions extends MoveControl {
    public MovementControllerCustomCollisions(MobEntity mob) {
        super(mob);
    }

    @Override
    public void tick() {
        float f9;
        if (this.state == State.STRAFE) {
            float f = (float)this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            float f1 = (float)this.speed * f;
            float f2 = this.forwardMovement;
            float f3 = this.sidewaysMovement;
            float f4 = MathHelper.sqrt(f2 * f2 + f3 * f3);
            if (f4 < 1.0F) {
                f4 = 1.0F;
            }

            f4 = f1 / f4;
            f2 *= f4;
            f3 *= f4;
            float f5 = MathHelper.sin(this.entity.getYaw() * 0.017453292F);
            float f6 = MathHelper.cos(this.entity.getYaw() * 0.017453292F);
            float f7 = f2 * f6 - f3 * f5;
            f9 = f3 * f6 + f2 * f5;
            if (!this.isWalkable(f7, f9)) {
                this.forwardMovement = 1.0F;
                this.sidewaysMovement = 0.0F;
            }

            this.entity.setMovementSpeed(f1);
            this.entity.setForwardSpeed(this.forwardMovement);
            this.entity.setSidewaysSpeed(this.sidewaysMovement);
            this.state = State.WAIT;
        } else if (this.state == State.MOVE_TO) {
            this.state = State.WAIT;
            double d0 = this.targetX - this.entity.getX();
            double d1 = this.targetZ - this.entity.getZ();
            double d2 = this.targetY - this.entity.getY();
            double d3 = d0 * d0 + d2 * d2 + d1 * d1;
            if (d3 < 2.500000277905201E-7) {
                this.entity.setForwardSpeed(0.0F);
                return;
            }

            f9 = (float)(MathHelper.atan2(d1, d0) * 57.2957763671875) - 90.0F;
            this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), f9, 90.0F));
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
            BlockPos pos = this.entity.getBlockPos();
            BlockState blockState = this.entity.world.getBlockState(pos);
            VoxelShape sidesShape = blockState.getSidesShape(this.entity.world, pos);
            if ((!(this.entity instanceof ICustomCollisions) || !((ICustomCollisions)this.entity).canPassThrough(pos, blockState, sidesShape)) && (d2 > (double)this.entity.stepHeight && d0 * d0 + d1 * d1 < (double)Math.max(1.0F, this.entity.getWidth()) || !sidesShape.isEmpty() && this.entity.getY() < sidesShape.getMax(Direction.Axis.Y) + (double)pos.getY() && !blockState.isIn(BlockTags.DOORS) && !blockState.isIn(BlockTags.FENCES))) {
                this.entity.getJumpControl().setActive();
                this.state = State.JUMPING;
            }
        } else if (this.state == State.JUMPING) {
            this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
            if (this.entity.isOnGround()) {
                this.state = State.WAIT;
            }
        } else {
            this.entity.setForwardSpeed(0.0F);
        }

    }

    private boolean isWalkable(float xMultiplier, float yMultiplier) {
        EntityNavigation navigation = this.entity.getNavigation();
        if (navigation == null) {
            return true;
        } else {
            PathNodeMaker nodeMaker = navigation.getNodeMaker();
            return nodeMaker == null || nodeMaker.getDefaultNodeType(this.entity.world, MathHelper.floor(this.entity.getX() + (double)xMultiplier), MathHelper.floor(this.entity.getY()), MathHelper.floor(this.entity.getZ() + (double)yMultiplier)) == PathNodeType.WALKABLE;
        }
    }
}
