package com.simibubi.create.content.contraptions.components.crafter;

import com.google.gson.JsonObject;
import com.simibubi.create.AllRecipeTypes;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class MechanicalCraftingRecipe extends ShapedRecipe {

	public MechanicalCraftingRecipe(Identifier idIn, String groupIn, int recipeWidthIn, int recipeHeightIn,
			DefaultedList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn) {
		super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
	}

	private static MechanicalCraftingRecipe fromShaped(ShapedRecipe recipe) {
		return new MechanicalCraftingRecipe(recipe.getId(), recipe.getGroup(), recipe.getWidth(), recipe.getHeight(),
				recipe.getPreviewInputs(), recipe.getOutput());
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		return inv instanceof MechanicalCraftingInventory && super.matches(inv, worldIn);
	}

	@Override
	public RecipeType<?> getType() {
		return AllRecipeTypes.MECHANICAL_CRAFTING.type;
	}
	
	@Override
	public RecipeSerializer<?> getSerializer() {
		return AllRecipeTypes.MECHANICAL_CRAFTING.serializer;
	}

	public static class Serializer extends ShapedRecipe.Serializer {

		@Override
		public ShapedRecipe read(Identifier recipeId, JsonObject json) {
			return fromShaped(super.read(recipeId, json));
		}
		
		@Override
		public ShapedRecipe read(Identifier recipeId, PacketByteBuf buffer) {
			return fromShaped(super.read(recipeId, buffer));
		}

	}

}
