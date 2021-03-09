package com.simibubi.create.content.curiosities.tools;

import com.simibubi.create.AllItems;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Lazy;

import java.util.function.Supplier;

public enum AllToolTiers implements ToolMaterial {

	RADIANT(4, 1024, 16.0F, 3.5F, 10, () -> {
		return Ingredient.ofItems(AllItems.REFINED_RADIANCE);
	}),

	;

	private final int harvestLevel;
	private final int maxUses;
	private final float efficiency;
	private final float attackDamage;
	private final int enchantability;
	private final Lazy<Ingredient> repairMaterial;

	AllToolTiers(int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn,
				 int enchantabilityIn, Supplier<Ingredient> repairMaterialIn) {
		this.harvestLevel = harvestLevelIn;
		this.maxUses = maxUsesIn;
		this.efficiency = efficiencyIn;
		this.attackDamage = attackDamageIn;
		this.enchantability = enchantabilityIn;
		this.repairMaterial = new Lazy<>(repairMaterialIn);
	}

	@Override
	public int getDurability() {
		return this.maxUses;
	}

	@Override
	public float getMiningSpeedMultiplier() {
		return this.efficiency;
	}

	public float getAttackDamage() {
		return this.attackDamage;
	}

	@Override
	public int getMiningLevel() {
		return this.harvestLevel;
	}

	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return this.repairMaterial.get();
	}
}
