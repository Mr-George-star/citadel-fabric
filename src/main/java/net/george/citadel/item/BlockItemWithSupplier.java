package net.george.citadel.item;

import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

@SuppressWarnings("unused")
public class BlockItemWithSupplier extends BlockItem {
    private final RegistryObject<Block> blockSupplier;

    public BlockItemWithSupplier(RegistryObject<Block> blockSupplier, Settings settings) {
        super(null, settings);
        this.blockSupplier = blockSupplier;
    }

    @Override
    public Block getBlock() {
        return this.blockSupplier.get();
    }

    @Override
    public boolean canBeNested() {
        return !(this.blockSupplier.get() instanceof ShulkerBoxBlock);
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity itemEntity) {
        if (this.blockSupplier.get() instanceof ShulkerBoxBlock) {
            ItemStack stack = itemEntity.getStack();
            NbtCompound nbt = getBlockEntityNbt(stack);
            if (nbt != null && nbt.contains("Items", 9)) {
                NbtList items = nbt.getList("Items", 10);
                ItemUsage.spawnItemContents(itemEntity, items.stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt));
            }
        }
    }
}
