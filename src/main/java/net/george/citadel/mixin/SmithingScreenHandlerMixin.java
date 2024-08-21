package net.george.citadel.mixin;

import net.george.citadel.CitadelConstants;
import net.george.citadel.server.item.CitadelRecipes;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(SmithingScreenHandler.class)
public class SmithingScreenHandlerMixin {
    @Redirect(
            method = "updateResult",
            remap = CitadelConstants.REMAPREFS, at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeManager;getAllMatches(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/List;")
    )
    private List<SmithingRecipe> citadel$getAllMatches(RecipeManager instance, RecipeType<SmithingRecipe> type, Inventory inventory, World world) {
        List<SmithingRecipe> list = new ArrayList<>(instance.getAllMatches(type, inventory, world));
        if(type == RecipeType.SMITHING && inventory.size() >= 2 && !inventory.getStack(0).isEmpty()&& !inventory.getStack(1).isEmpty()){
            list.addAll(CitadelRecipes.getSmithingRecipes());
        }
        return list;
    }
}
