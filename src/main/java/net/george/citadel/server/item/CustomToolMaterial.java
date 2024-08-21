package net.george.citadel.server.item;

import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

@SuppressWarnings("unused")
public class CustomToolMaterial implements ToolMaterial {
    private final String name;
    private final int miningLevel;
    private final int durability;
    private final float damage;
    private final float speed;
    private final int enchantability;
    private Ingredient ingredient = null;

    public CustomToolMaterial(String name, int miningLevel, int durability, float damage, float speed, int enchantability) {
        this.name = name;
        this.miningLevel = miningLevel;
        this.durability = durability;
        this.damage = damage;
        this.speed = speed;
        this.enchantability = enchantability;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int getDurability() {
        return this.durability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.speed;
    }

    @Override
    public float getAttackDamage() {
        return this.damage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.ingredient == null ? Ingredient.EMPTY : this.ingredient;
    }

    public void setRepairMaterial(Ingredient ingredient){
        this.ingredient = ingredient;
    }
}
