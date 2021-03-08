package com.simibubi.create.content.curiosities.zapper.blockzapper;

import com.google.gson.JsonObject;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.curiosities.zapper.blockzapper.BlockzapperItem.ComponentTier;
import com.simibubi.create.content.curiosities.zapper.blockzapper.BlockzapperItem.Components;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BlockzapperUpgradeRecipe implements CraftingRecipe {

	private ShapedRecipe recipe;
	private Components component;
	private ComponentTier tier;
	
	public BlockzapperUpgradeRecipe(ShapedRecipe recipe, Components component, ComponentTier tier) {
		this.recipe = recipe;
		this.component = component;
		this.tier = tier;
	}
	
	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		return getRecipe().matches(inv, worldIn);
	}
	
	@Override
	public DefaultedList<Ingredient> getPreviewInputs() {
		return recipe.getPreviewInputs();
	}
	
	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		for (int slot = 0; slot < inv.size(); slot++) {
			ItemStack handgun = inv.getStack(slot).copy();
			if (!AllItems.BLOCKZAPPER.isIn(handgun))
				continue;
			BlockzapperItem.setTier(getUpgradedComponent(), getTier(), handgun);
			return handgun;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getOutput() {
		ItemStack handgun = new ItemStack(AllItems.BLOCKZAPPER.get());
		BlockzapperItem.setTier(getUpgradedComponent(), getTier(), handgun);
		return handgun;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return true;
	}
	
	@Override
	public Identifier getId() {
		return getRecipe().getId();
	}

//	@Override
//	public IRecipeType<?> getType() {
//		return AllRecipes.Types.BLOCKZAPPER_UPGRADE;
//	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return AllRecipeTypes.BLOCKZAPPER_UPGRADE.serializer;
	}
	
	public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<BlockzapperUpgradeRecipe> {

		@Override
		public BlockzapperUpgradeRecipe read(Identifier recipeId, JsonObject json) {
			ShapedRecipe recipe = RecipeSerializer.SHAPED.read(recipeId, json);
			
			Components component = Components.valueOf(JsonHelper.getString(json, "component"));
			ComponentTier tier = ComponentTier.valueOf(JsonHelper.getString(json, "tier"));
			return new BlockzapperUpgradeRecipe(recipe, component, tier);
		}

		@Override
		public BlockzapperUpgradeRecipe read(Identifier recipeId, PacketByteBuf buffer) {
			ShapedRecipe recipe = RecipeSerializer.SHAPED.read(recipeId, buffer);
			
			Components component = Components.valueOf(buffer.readString(buffer.readInt()));
			ComponentTier tier = ComponentTier.valueOf(buffer.readString(buffer.readInt()));
			return new BlockzapperUpgradeRecipe(recipe, component, tier);
		}

		@Override
		public void write(PacketByteBuf buffer, BlockzapperUpgradeRecipe recipe) {
			RecipeSerializer.SHAPED.write(buffer, recipe.getRecipe());
			
			String name = recipe.getUpgradedComponent().name();
			String name2 = recipe.getTier().name();
			buffer.writeInt(name.length());
			buffer.writeString(name);
			buffer.writeInt(name2.length());
			buffer.writeString(name2);
		}
		
	}

	@Override
	public boolean fits(int width, int height) {
		return getRecipe().fits(width, height);
	}

	public ShapedRecipe getRecipe() {
		return recipe;
	}

	public Components getUpgradedComponent() {
		return component;
	}

	public ComponentTier getTier() {
		return tier;
	}

}
