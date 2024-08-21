package net.george.citadel.server.item;

import net.minecraft.recipe.SmithingRecipe;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CitadelRecipes {
    private static final List<SmithingRecipe> smithingRecipes = new ArrayList<>();

    public static void registerSmithingRecipe(SmithingRecipe recipe){
        smithingRecipes.add(recipe);
    }

    public static List<SmithingRecipe> getSmithingRecipes(){
        return smithingRecipes;
    }
}
