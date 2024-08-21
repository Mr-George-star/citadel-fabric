package net.george.citadel.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;

@SuppressWarnings("unused")
public interface SpecialRecipeInGuideBook {
    DefaultedList<Ingredient> getDisplayIngredients();

    ItemStack getDisplayResultFor(DefaultedList<ItemStack> stacks);
}
