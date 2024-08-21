package net.george.citadel.item;

import net.george.citadel.ClientHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ItemCitadelBook extends Item {
    public ItemCitadelBook(Settings properties) {
        super(properties);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stackInHand = user.getStackInHand(hand);
        if (world.isClient) {
            ClientHandler.HANDLER.openBookGUI(stackInHand);
        }
        return new TypedActionResult<>(ActionResult.PASS, stackInHand);
    }
}
