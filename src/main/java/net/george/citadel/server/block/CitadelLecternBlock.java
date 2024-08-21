package net.george.citadel.server.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class CitadelLecternBlock extends LecternBlock {
    public CitadelLecternBlock(Settings properties) {
        super(properties);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState blockState) {
        return new CitadelLecternBlockEntity(pos, blockState);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (world.isClient && blockEntity instanceof CitadelLecternBlockEntity lecternBlockEntity && lecternBlockEntity.hasBook()) {
            ItemStack book = lecternBlockEntity.getBook();
            if (!book.isEmpty() && !player.getItemCooldownManager().isCoolingDown(book.getItem())) {
                book.use(world, player, hand);
            }
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (state.get(HAS_BOOK)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CitadelLecternBlockEntity) {
                return ((CitadelLecternBlockEntity) blockEntity).getRedstoneSignal();
            }
        }

        return 0;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState replaceState, boolean moved) {
        if (!state.isOf(replaceState.getBlock())) {
            if (state.get(HAS_BOOK)) {
                this.popCitadelBook(state, world, pos);
            }

            if (state.get(POWERED)) {
                world.updateNeighborsAlways(pos.down(), this);
            }

            super.onStateReplaced(state, world, pos, replaceState, moved);
        }
    }

    private void popCitadelBook(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CitadelLecternBlockEntity lectern) {
            Direction direction = state.get(FACING);
            ItemStack book = lectern.getBook().copy();
            float f = 0.25F * (float) direction.getOffsetX();
            float f1 = 0.25F * (float) direction.getOffsetZ();
            ItemEntity pick = new ItemEntity(world, (double) pos.getX() + 0.5D + (double) f, pos.getY() + 1, (double) pos.getZ() + 0.5D + (double) f1, book);
            pick.setToDefaultPickupDelay();
            world.spawnEntity(pick);
            lectern.clear();
        }
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockView level, BlockPos pos, PlayerEntity player) {
        return new ItemStack(Items.LECTERN);
    }
}
