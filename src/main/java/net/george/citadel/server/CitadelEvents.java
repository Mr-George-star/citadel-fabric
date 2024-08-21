package net.george.citadel.server;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.george.citadel.Citadel;
import net.george.citadel.CitadelConstants;
import net.george.citadel.server.block.CitadelLecternBlock;
import net.george.citadel.server.block.CitadelLecternBlockEntity;
import net.george.citadel.server.block.LecternBooks;
import net.george.citadel.server.entity.CitadelEntityData;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class CitadelEvents {
    public static void register() {
        LivingEntityEvents.TICK.register(entity -> {
            if (CitadelConstants.DEBUG) {
                if ((entity instanceof PlayerEntity)) {
                    NbtCompound nbt = CitadelEntityData.getCitadelTag(entity);
                    nbt.putInt("CitadelInt", nbt.getInt("CitadelInt") + 1);
                    Citadel.LOGGER.debug("Citadel Data Tag tracker example: " + nbt.getInt("CitadelInt"));
                }
            }
        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            ItemStack stack = player.getStackInHand(hand);
            if (world.getBlockState(pos).isOf(Blocks.LECTERN) && LecternBooks.isLecternBook(stack)) {
                player.getItemCooldownManager().set(stack.getItem(), 1);
                BlockState oldLectern = world.getBlockState(pos);
                if (world.getBlockEntity(pos) instanceof LecternBlockEntity oldBe && !oldBe.hasBook()) {
                    BlockState newLectern = Citadel.LECTERN.get().getDefaultState().with(CitadelLecternBlock.FACING, oldLectern.get(LecternBlock.FACING)).with(CitadelLecternBlock.POWERED, oldLectern.get(LecternBlock.POWERED)).with(CitadelLecternBlock.HAS_BOOK, true);
                    world.setBlockState(pos, newLectern);
                    CitadelLecternBlockEntity entity = new CitadelLecternBlockEntity(pos, newLectern);
                    ItemStack bookCopy = stack.copy();
                    bookCopy.setCount(1);
                    entity.setBook(bookCopy);
                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                    world.addBlockEntity(entity);
                    player.swingHand(hand, true);
                    world.playSound(null, pos, SoundEvents.ITEM_BOOK_PUT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
            return ActionResult.PASS;
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (oldPlayer != null && CitadelEntityData.getCitadelTag(oldPlayer) != null) {
                CitadelEntityData.setCitadelTag(newPlayer, CitadelEntityData.getCitadelTag(oldPlayer));
            }
        });
    }
}
