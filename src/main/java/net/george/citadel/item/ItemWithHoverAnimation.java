package net.george.citadel.item;

import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public interface ItemWithHoverAnimation {
    float getMaxHoverOverTime(ItemStack stack);

    boolean canHoverOver(ItemStack stack);
}
