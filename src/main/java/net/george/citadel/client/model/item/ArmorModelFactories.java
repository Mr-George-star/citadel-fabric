package net.george.citadel.client.model.item;

import io.github.fabricators_of_create.porting_lib.item.ArmorTextureItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class ArmorModelFactories {
    @SuppressWarnings("unchecked")
    public static <T extends Item & ArmorTextureItem> void register(T item, ArmorModelFactory factory) {
        ArmorRenderer renderer = (matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
            T armor = (T) stack.getItem();
            BipedEntityModel<LivingEntity> model = factory.getModel(matrices, vertexConsumers, stack, entity, slot, light, contextModel);
            String texture = armor.getArmorTexture(stack, entity, slot, "");

            contextModel.copyStateTo(model);
            ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, new Identifier(texture));
        };

        ArmorRenderer.register(renderer, item);
    }

    public static <T extends Item & ArmorTextureItem> void register(T[] items, ArmorModelFactory factory) {
        for (T item : items) {
            register(item, factory);
        }
    }

    /**
     * A function interface that uses known information to obtain the model of a certain armor.
     */
    @FunctionalInterface
    @Environment(EnvType.CLIENT)
    public interface ArmorModelFactory {
        @NotNull
        BipedEntityModel<LivingEntity> getModel(MatrixStack matrices, VertexConsumerProvider vertices, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel);
    }
}
