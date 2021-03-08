package com.simibubi.create.foundation.data.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.RecipesProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraftforge.common.Tags;

public abstract class CreateRecipeProvider extends RecipesProvider {

	final List<GeneratedRecipe> all = new ArrayList<>();

	public CreateRecipeProvider(DataGenerator p_i48262_1_) {
		super(p_i48262_1_);
	}

	@Override
	protected void generate(Consumer<RecipeJsonProvider> p_200404_1_) {
		all.forEach(c -> c.register(p_200404_1_));
		Create.logger.info(getName() + " registered " + all.size() + " recipe" + (all.size() == 1 ? "" : "s"));
	}

	@FunctionalInterface
	interface GeneratedRecipe {
		void register(Consumer<RecipeJsonProvider> consumer);
	}

	protected GeneratedRecipe register(GeneratedRecipe recipe) {
		all.add(recipe);
		return recipe;
	}

	protected static class Marker {
	}

	protected static class I {

		static Tag.Identified<Item> redstone() {
			return Tags.Items.DUSTS_REDSTONE;
		}
		
		static Tag.Identified<Item> planks() {
			return ItemTags.PLANKS;
		}

		static Tag.Identified<Item> gold() {
			return AllTags.forgeItemTag("ingots/gold");
		}

		static Tag.Identified<Item> goldSheet() {
			return AllTags.forgeItemTag("plates/gold");
		}

		static Tag.Identified<Item> stone() {
			return Tags.Items.STONE;
		}

		static ItemConvertible andesite() {
			return AllItems.ANDESITE_ALLOY.get();
		}

		static ItemConvertible shaft() {
			return AllBlocks.SHAFT.get();
		}

		static ItemConvertible cog() {
			return AllBlocks.COGWHEEL.get();
		}

		static ItemConvertible andesiteCasing() {
			return AllBlocks.ANDESITE_CASING.get();
		}

		static Tag.Identified<Item> brass() {
			return AllTags.forgeItemTag("ingots/brass");
		}

		static Tag.Identified<Item> brassSheet() {
			return AllTags.forgeItemTag("plates/brass");
		}

		static Tag.Identified<Item> iron() {
			return Tags.Items.INGOTS_IRON;
		}

		static Tag.Identified<Item> zinc() {
			return AllTags.forgeItemTag("ingots/zinc");
		}

		static Tag.Identified<Item> ironSheet() {
			return AllTags.forgeItemTag("plates/iron");
		}

		static ItemConvertible brassCasing() {
			return AllBlocks.BRASS_CASING.get();
		}

		static ItemConvertible electronTube() {
			return AllItems.ELECTRON_TUBE.get();
		}

		static ItemConvertible circuit() {
			return AllItems.INTEGRATED_CIRCUIT.get();
		}

		static Tag.Identified<Item> copperBlock() {
			return AllTags.forgeItemTag("storage_blocks/copper");
		}

		static Tag.Identified<Item> brassBlock() {
			return AllTags.forgeItemTag("storage_blocks/brass");
		}

		static Tag.Identified<Item> zincBlock() {
			return AllTags.forgeItemTag("storage_blocks/zinc");
		}

		static Tag.Identified<Item> copper() {
			return AllTags.forgeItemTag("ingots/copper");
		}

		static Tag.Identified<Item> copperSheet() {
			return AllTags.forgeItemTag("plates/copper");
		}

		static Tag.Identified<Item> copperNugget() {
			return AllTags.forgeItemTag("nuggets/copper");
		}

		static Tag.Identified<Item> brassNugget() {
			return AllTags.forgeItemTag("nuggets/brass");
		}

		static Tag.Identified<Item> zincNugget() {
			return AllTags.forgeItemTag("nuggets/zinc");
		}

		static ItemConvertible copperCasing() {
			return AllBlocks.COPPER_CASING.get();
		}

		static ItemConvertible refinedRadiance() {
			return AllItems.REFINED_RADIANCE.get();
		}

		static ItemConvertible shadowSteel() {
			return AllItems.SHADOW_STEEL.get();
		}

	}
}
