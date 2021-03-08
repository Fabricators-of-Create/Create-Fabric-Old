package com.simibubi.create.foundation.data.recipe;

import static com.simibubi.create.foundation.data.recipe.Mods.EID;
import static com.simibubi.create.foundation.data.recipe.Mods.IE;
import static com.simibubi.create.foundation.data.recipe.Mods.INF;
import static com.simibubi.create.foundation.data.recipe.Mods.MEK;
import static com.simibubi.create.foundation.data.recipe.Mods.MW;
import static com.simibubi.create.foundation.data.recipe.Mods.SM;
import static com.simibubi.create.foundation.data.recipe.Mods.TH;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.palettes.AllPaletteBlocks;
import com.simibubi.create.foundation.data.recipe.StandardRecipeGen.GeneratedRecipeBuilder;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.recipe.CookingRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;

@SuppressWarnings("unused")
public class StandardRecipeGen extends CreateRecipeProvider {

	/*
	 * Recipes are added through fields, so one can navigate to the right one easily
	 * 
	 * (Ctrl-o) in Eclipse
	 */

	private Marker MATERIALS = enterSection(AllSections.MATERIALS);

	GeneratedRecipe

	COPPER_COMPACTING =
		metalCompacting(ImmutableList.of(AllItems.COPPER_NUGGET, AllItems.COPPER_INGOT, AllBlocks.COPPER_BLOCK),
			ImmutableList.of(I::copperNugget, I::copper, I::copperBlock)),

		BRASS_COMPACTING =
			metalCompacting(ImmutableList.of(AllItems.BRASS_NUGGET, AllItems.BRASS_INGOT, AllBlocks.BRASS_BLOCK),
				ImmutableList.of(I::brassNugget, I::brass, I::brassBlock)),

		ZINC_COMPACTING =
			metalCompacting(ImmutableList.of(AllItems.ZINC_NUGGET, AllItems.ZINC_INGOT, AllBlocks.ZINC_BLOCK),
				ImmutableList.of(I::zincNugget, I::zinc, I::zincBlock)),

		ANDESITE_ALLOY = create(AllItems.ANDESITE_ALLOY).unlockedByTag(I::iron)
			.viaShaped(b -> b.input('A', Blocks.ANDESITE)
				.input('B', Tags.Items.NUGGETS_IRON)
				.pattern("BA")
				.pattern("AB")),

		ANDESITE_ALLOY_FROM_ZINC = create(AllItems.ANDESITE_ALLOY).withSuffix("_from_zinc")
			.unlockedByTag(I::zinc)
			.viaShaped(b -> b.input('A', Blocks.ANDESITE)
				.input('B', I.zincNugget())
				.pattern("BA")
				.pattern("AB")),

		ANDESITE_CASING = create(AllBlocks.ANDESITE_CASING).returns(4)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('A', ItemTags.PLANKS)
				.input('C', I.andesite())
				.input('S', ItemTags.LOGS)
				.pattern("AAA")
				.pattern("CSC")
				.pattern("AAA")),

		BRASS_CASING = create(AllBlocks.BRASS_CASING).returns(4)
			.unlockedByTag(I::brass)
			.viaShaped(b -> b.input('A', ItemTags.PLANKS)
				.input('C', I.brassSheet())
				.input('S', ItemTags.LOGS)
				.pattern("AAA")
				.pattern("CSC")
				.pattern("AAA")),

		COPPER_CASING = create(AllBlocks.COPPER_CASING).returns(4)
			.unlockedByTag(I::copper)
			.viaShaped(b -> b.input('A', ItemTags.PLANKS)
				.input('C', I.copperSheet())
				.input('S', ItemTags.LOGS)
				.pattern("AAA")
				.pattern("CSC")
				.pattern("AAA")),

		RADIANT_CASING = create(AllBlocks.REFINED_RADIANCE_CASING).returns(4)
			.unlockedBy(I::refinedRadiance)
			.viaShaped(b -> b.input('A', ItemTags.PLANKS)
				.input('C', I.refinedRadiance())
				.input('S', Tags.Items.GLASS_COLORLESS)
				.pattern("AAA")
				.pattern("CSC")
				.pattern("AAA")),

		SHADOW_CASING = create(AllBlocks.SHADOW_STEEL_CASING).returns(4)
			.unlockedBy(I::shadowSteel)
			.viaShaped(b -> b.input('A', ItemTags.PLANKS)
				.input('C', I.shadowSteel())
				.input('S', Tags.Items.OBSIDIAN)
				.pattern("AAA")
				.pattern("CSC")
				.pattern("AAA")),

		ELECTRON_TUBE = create(AllItems.ELECTRON_TUBE).unlockedBy(AllItems.ROSE_QUARTZ::get)
			.viaShaped(b -> b.input('L', AllItems.POLISHED_ROSE_QUARTZ.get())
				.input('R', Items.REDSTONE_TORCH)
				.input('N', Tags.Items.NUGGETS_IRON)
				.pattern("L")
				.pattern("R")
				.pattern("N")),

		ROSE_QUARTZ = create(AllItems.ROSE_QUARTZ).unlockedBy(() -> Items.REDSTONE)
			.viaShapeless(b -> b.input(Tags.Items.GEMS_QUARTZ)
				.input(Ingredient.fromTag(I.redstone()), 8)),

		SAND_PAPER = create(AllItems.SAND_PAPER).unlockedBy(() -> Items.PAPER)
			.viaShapeless(b -> b.input(Items.PAPER)
				.input(Tags.Items.SAND_COLORLESS)),

		RED_SAND_PAPER = create(AllItems.RED_SAND_PAPER).unlockedBy(() -> Items.PAPER)
			.viaShapeless(b -> b.input(Items.PAPER)
				.input(Tags.Items.SAND_RED))

	;

	private Marker CURIOSITIES = enterSection(AllSections.CURIOSITIES);

	GeneratedRecipe DEFORESTER = create(AllItems.DEFORESTER).unlockedBy(I::refinedRadiance)
		.viaShaped(b -> b.input('E', I.refinedRadiance())
			.input('G', I.cog())
			.input('O', Tags.Items.OBSIDIAN)
			.pattern("EG")
			.pattern("EO")
			.pattern(" O")),

		WAND_OF_SYMMETRY = create(AllItems.WAND_OF_SYMMETRY).unlockedBy(I::refinedRadiance)
			.viaShaped(b -> b.input('E', I.refinedRadiance())
				.input('G', Tags.Items.GLASS_PANES_WHITE)
				.input('O', Tags.Items.OBSIDIAN)
				.input('L', I.brass())
				.pattern(" GE")
				.pattern("LEG")
				.pattern("OL ")),

		MINECART_COUPLING = create(AllItems.MINECART_COUPLING).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('E', I.andesite())
				.input('O', I.ironSheet())
				.pattern("  E")
				.pattern(" O ")
				.pattern("E  ")),

		BLOCKZAPPER = create(AllItems.BLOCKZAPPER).unlockedBy(I::refinedRadiance)
			.viaShaped(b -> b.input('E', I.refinedRadiance())
				.input('A', I.andesite())
				.input('O', Tags.Items.OBSIDIAN)
				.pattern("  E")
				.pattern(" O ")
				.pattern("OA "))

	;

	private Marker KINETICS = enterSection(AllSections.KINETICS);

	GeneratedRecipe BASIN = create(AllBlocks.BASIN).unlockedBy(I::andesite)
		.viaShaped(b -> b.input('A', I.andesite())
			.pattern("A A")
			.pattern("AAA")),

		GOGGLES = create(AllItems.GOGGLES).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('G', Tags.Items.GLASS)
				.input('P', I.goldSheet())
				.input('S', Tags.Items.STRING)
				.pattern(" S ")
				.pattern("GPG")),

		WRENCH = create(AllItems.WRENCH).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('G', I.goldSheet())
				.input('P', I.cog())
				.input('S', Tags.Items.RODS_WOODEN)
				.pattern("GG")
				.pattern("GP")
				.pattern(" S")),

		FILTER = create(AllItems.FILTER).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', ItemTags.WOOL)
				.input('A', Tags.Items.NUGGETS_IRON)
				.pattern("ASA")),

		ATTRIBUTE_FILTER = create(AllItems.ATTRIBUTE_FILTER).unlockedByTag(I::brass)
			.viaShaped(b -> b.input('S', ItemTags.WOOL)
				.input('A', I.brassNugget())
				.pattern("ASA")),

		BRASS_HAND = create(AllItems.BRASS_HAND).unlockedByTag(I::brass)
			.viaShaped(b -> b.input('A', I.andesite())
				.input('B', I.brassSheet())
				.pattern(" A ")
				.pattern("BBB")
				.pattern(" B ")),

		SUPER_GLUE = create(AllItems.SUPER_GLUE).unlockedByTag(I::ironSheet)
			.viaShaped(b -> b.input('A', Tags.Items.SLIMEBALLS)
				.input('S', I.ironSheet())
				.input('N', Tags.Items.NUGGETS_IRON)
				.pattern("AS")
				.pattern("NA")),

		CRAFTER_SLOT_COVER = create(AllItems.CRAFTER_SLOT_COVER).unlockedBy(AllBlocks.MECHANICAL_CRAFTER::get)
			.viaShaped(b -> b.input('A', I.brassNugget())
				.pattern("AAA")),

		COGWHEEL = create(AllBlocks.COGWHEEL).returns(8)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', ItemTags.WOODEN_BUTTONS)
				.input('C', I.andesite())
				.pattern("SSS")
				.pattern("SCS")
				.pattern("SSS")),

		LARGE_COGWHEEL = create(AllBlocks.LARGE_COGWHEEL).returns(2)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', ItemTags.WOODEN_BUTTONS)
				.input('C', I.andesite())
				.input('D', ItemTags.PLANKS)
				.pattern("SDS")
				.pattern("DCD")
				.pattern("SDS")),

		WATER_WHEEL = create(AllBlocks.WATER_WHEEL).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', ItemTags.WOODEN_SLABS)
				.input('C', AllBlocks.LARGE_COGWHEEL.get())
				.pattern("SSS")
				.pattern("SCS")
				.pattern("SSS")),

		SHAFT = create(AllBlocks.SHAFT).returns(8)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('A', I.andesite())
				.pattern("A")
				.pattern("A")),

		MECHANICAL_PRESS = create(AllBlocks.MECHANICAL_PRESS).unlockedBy(I::andesiteCasing)
			.viaShaped(b -> b.input('B', I.andesite())
				.input('S', I.cog())
				.input('C', I.andesiteCasing())
				.input('I', AllTags.forgeItemTag("storage_blocks/iron"))
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" I ")),

		MILLSTONE = create(AllBlocks.MILLSTONE).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('B', ItemTags.PLANKS)
				.input('S', I.andesite())
				.input('C', I.cog())
				.input('I', I.stone())
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" I ")),

		MECHANICAL_PISTON = create(AllBlocks.MECHANICAL_PISTON).unlockedBy(I::andesiteCasing)
			.viaShaped(b -> b.input('B', ItemTags.PLANKS)
				.input('S', I.cog())
				.input('C', I.andesiteCasing())
				.input('I', AllBlocks.PISTON_EXTENSION_POLE.get())
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" I ")),

		STICKY_MECHANICAL_PISTON = create(AllBlocks.STICKY_MECHANICAL_PISTON).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', Tags.Items.SLIMEBALLS)
				.input('P', AllBlocks.MECHANICAL_PISTON.get())
				.pattern("S")
				.pattern("P")),

		TURNTABLE = create(AllBlocks.TURNTABLE).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', I.shaft())
				.input('P', ItemTags.WOODEN_SLABS)
				.pattern("P")
				.pattern("S")),

		PISTON_EXTENSION_POLE = create(AllBlocks.PISTON_EXTENSION_POLE).returns(8)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('A', I.andesite())
				.input('P', ItemTags.PLANKS)
				.pattern("P")
				.pattern("A")
				.pattern("P")),
			
		GANTRY_PINION = create(AllBlocks.GANTRY_PINION).unlockedBy(I::andesiteCasing)
			.viaShaped(b -> b.input('B', ItemTags.PLANKS)
				.input('S', I.cog())
				.input('C', I.andesiteCasing())
				.input('I', I.shaft())
				.pattern(" B ")
				.pattern("ICI")
				.pattern(" S ")),

		GANTRY_SHAFT = create(AllBlocks.GANTRY_SHAFT).returns(8)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('A', I.andesite())
				.input('R', I.redstone())
				.pattern("A")
				.pattern("R")
				.pattern("A")),

		ANALOG_LEVER = create(AllBlocks.ANALOG_LEVER).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', I.andesiteCasing())
				.input('P', Tags.Items.RODS_WOODEN)
				.pattern("P")
				.pattern("S")),

		BELT_CONNECTOR = create(AllItems.BELT_CONNECTOR).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('D', Items.DRIED_KELP)
				.pattern("DDD")
				.pattern("DDD")),

		ADJUSTABLE_PULLEY = create(AllBlocks.ADJUSTABLE_CHAIN_GEARSHIFT).unlockedBy(I::brassCasing)
			.viaShaped(b -> b.input('A', I.electronTube())
				.input('B', AllBlocks.ENCASED_CHAIN_DRIVE.get())
				.input('C', AllBlocks.LARGE_COGWHEEL.get())
				.pattern("A")
				.pattern("B")
				.pattern("C")),

		CART_ASSEMBLER = create(AllBlocks.CART_ASSEMBLER).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('L', ItemTags.LOGS)
				.input('R', I.redstone())
				.input('C', I.andesite())
				.pattern(" L ")
				.pattern("CRC")
				.pattern("L L")),

		CONTROLLER_RAIL = create(AllBlocks.CONTROLLER_RAIL).returns(6)
			.unlockedBy(() -> Items.POWERED_RAIL)
			.viaShaped(b -> b.input('A', I.gold())
				.input('E', I.electronTube())
				.input('S', Tags.Items.RODS_WOODEN)
				.pattern("A A")
				.pattern("ASA")
				.pattern("AEA")),

		HAND_CRANK = create(AllBlocks.HAND_CRANK).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('A', I.andesite())
				.input('C', ItemTags.PLANKS)
				.input('S', I.shaft())
				.pattern(" S ")
				.pattern("CCC")
				.pattern("  A")),

		COPPER_VALVE_HANDLE = create(AllBlocks.COPPER_VALVE_HANDLE).unlockedByTag(I::copper)
			.viaShaped(b -> b.input('S', I.andesite())
				.input('C', I.copperSheet())
				.pattern("CCC")
				.pattern(" S ")),

		COPPER_VALVE_HANDLE_FROM_OTHER_HANDLES = create(AllBlocks.COPPER_VALVE_HANDLE).withSuffix("_from_others")
			.unlockedByTag(I::copper)
			.viaShapeless(b -> b.input(AllItemTags.VALVE_HANDLES.tag)),

		NOZZLE = create(AllBlocks.NOZZLE).unlockedBy(AllBlocks.ENCASED_FAN::get)
			.viaShaped(b -> b.input('S', I.andesite())
				.input('C', ItemTags.WOOL)
				.pattern(" S ")
				.pattern(" C ")
				.pattern("SSS")),

		PROPELLER = create(AllItems.PROPELLER).unlockedByTag(I::ironSheet)
			.viaShaped(b -> b.input('S', I.ironSheet())
				.input('C', I.andesite())
				.pattern(" S ")
				.pattern("SCS")
				.pattern(" S ")),

		WHISK = create(AllItems.WHISK).unlockedByTag(I::ironSheet)
			.viaShaped(b -> b.input('S', I.ironSheet())
				.input('C', I.andesite())
				.pattern(" C ")
				.pattern("SCS")
				.pattern("SSS")),

		ENCASED_FAN = create(AllBlocks.ENCASED_FAN).unlockedByTag(I::ironSheet)
			.viaShaped(b -> b.input('S', I.shaft())
				.input('A', I.andesiteCasing())
				.input('R', I.cog())
				.input('P', AllItems.PROPELLER.get())
				.pattern(" S ")
				.pattern("RAR")
				.pattern(" P ")),

		CUCKOO_CLOCK = create(AllBlocks.CUCKOO_CLOCK).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', ItemTags.PLANKS)
				.input('A', Items.CLOCK)
				.input('B', ItemTags.LOGS)
				.input('P', I.cog())
				.pattern(" S ")
				.pattern("SAS")
				.pattern("BPB")),

		MECHANICAL_CRAFTER = create(AllBlocks.MECHANICAL_CRAFTER).returns(3)
			.unlockedBy(I::brassCasing)
			.viaShaped(b -> b.input('B', I.electronTube())
				.input('R', Blocks.CRAFTING_TABLE)
				.input('C', I.brassCasing())
				.input('S', I.cog())
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" R ")),

		WINDMILL_BEARING = create(AllBlocks.WINDMILL_BEARING).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('I', I.shaft())
				.input('B', AllBlocks.TURNTABLE.get())
				.input('C', I.stone())
				.pattern(" B ")
				.pattern(" C ")
				.pattern(" I ")),

		MECHANICAL_BEARING = create(AllBlocks.MECHANICAL_BEARING).unlockedBy(I::andesiteCasing)
			.viaShaped(b -> b.input('I', I.shaft())
				.input('S', I.andesite())
				.input('B', AllBlocks.TURNTABLE.get())
				.input('C', I.andesiteCasing())
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" I ")),

		CLOCKWORK_BEARING = create(AllBlocks.CLOCKWORK_BEARING).unlockedBy(I::brassCasing)
			.viaShaped(b -> b.input('I', I.shaft())
				.input('S', I.electronTube())
				.input('B', AllBlocks.TURNTABLE.get())
				.input('C', I.brassCasing())
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" I ")),

		WOODEN_BRACKET = create(AllBlocks.WOODEN_BRACKET).returns(4)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', Tags.Items.RODS_WOODEN)
				.input('P', I.planks())
				.input('C', I.andesite())
				.pattern("SSS")
				.pattern("PCP")),

		METAL_BRACKET = create(AllBlocks.METAL_BRACKET).returns(4)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', Tags.Items.NUGGETS_IRON)
				.input('P', I.iron())
				.input('C', I.andesite())
				.pattern("SSS")
				.pattern("PCP")),

		FLUID_PIPE = create(AllBlocks.FLUID_PIPE).returns(8)
			.unlockedByTag(I::copper)
			.viaShaped(b -> b.input('S', I.copperSheet())
				.input('C', I.copper())
				.pattern("SCS")),

		MECHANICAL_PUMP = create(AllBlocks.MECHANICAL_PUMP).unlockedByTag(I::copper)
			.viaShaped(b -> b.input('P', I.cog())
				.input('S', AllBlocks.FLUID_PIPE.get())
				.pattern("P")
				.pattern("S")),

		SMART_FLUID_PIPE = create(AllBlocks.SMART_FLUID_PIPE).unlockedByTag(I::copper)
			.viaShaped(b -> b.input('P', I.electronTube())
				.input('S', AllBlocks.FLUID_PIPE.get())
				.input('I', I.brassSheet())
				.pattern("I")
				.pattern("S")
				.pattern("P")),

		FLUID_VALVE = create(AllBlocks.FLUID_VALVE).unlockedByTag(I::copper)
			.viaShaped(b -> b.input('P', I.shaft())
				.input('S', AllBlocks.FLUID_PIPE.get())
				.input('I', I.ironSheet())
				.pattern("I")
				.pattern("S")
				.pattern("P")),

		SPOUT = create(AllBlocks.SPOUT).unlockedBy(I::copperCasing)
			.viaShaped(b -> b.input('T', AllBlocks.FLUID_TANK.get())
				.input('P', Items.DRIED_KELP)
				.input('S', I.copperNugget())
				.pattern("T")
				.pattern("P")
				.pattern("S")),

		ITEM_DRAIN = create(AllBlocks.ITEM_DRAIN).unlockedBy(I::copperCasing)
			.viaShaped(b -> b.input('P', Blocks.IRON_BARS)
				.input('S', I.copperCasing())
				.pattern("P")
				.pattern("S")),

		FLUID_TANK = create(AllBlocks.FLUID_TANK).returns(2)
			.unlockedBy(I::copperCasing)
			.viaShaped(b -> b.input('B', I.copperCasing())
				.input('S', I.copperNugget())
				.input('C', Tags.Items.GLASS)
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" B ")),

		DEPLOYER = create(AllBlocks.DEPLOYER).unlockedBy(I::electronTube)
			.viaShaped(b -> b.input('I', AllItems.BRASS_HAND.get())
				.input('B', I.electronTube())
				.input('S', I.cog())
				.input('C', I.andesiteCasing())
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" I ")),

		PORTABLE_STORAGE_INTERFACE = create(AllBlocks.PORTABLE_STORAGE_INTERFACE).unlockedBy(I::brassCasing)
			.viaShaped(b -> b.input('I', I.brassCasing())
				.input('B', AllBlocks.ANDESITE_FUNNEL.get())
				.pattern(" B ")
				.pattern(" I ")),

		PORTABLE_FLUID_INTERFACE = create(AllBlocks.PORTABLE_FLUID_INTERFACE).unlockedBy(I::copperCasing)
			.viaShaped(b -> b.input('I', I.copperCasing())
				.input('B', AllBlocks.ANDESITE_FUNNEL.get())
				.pattern(" B ")
				.pattern(" I ")),

		ROPE_PULLEY = create(AllBlocks.ROPE_PULLEY).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', I.shaft())
				.input('B', I.andesiteCasing())
				.input('C', ItemTags.WOOL)
				.input('I', I.ironSheet())
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" I ")),

		HOSE_PULLEY = create(AllBlocks.HOSE_PULLEY).unlockedByTag(I::copper)
			.viaShaped(b -> b.input('S', I.shaft())
				.input('P', AllBlocks.FLUID_PIPE.get())
				.input('B', I.copperCasing())
				.input('C', Items.DRIED_KELP)
				.input('I', I.copperSheet())
				.pattern(" B ")
				.pattern("SCP")
				.pattern(" I ")),

		EMPTY_BLAZE_BURNER = create(AllItems.EMPTY_BLAZE_BURNER).unlockedByTag(I::iron)
			.viaShaped(b -> b.input('A', Blocks.IRON_BARS)
				.input('I', I.ironSheet())
				.pattern("II")
				.pattern("AA")),

		CHUTE = create(AllBlocks.CHUTE).unlockedBy(I::andesite)
			.returns(4)
			.viaShaped(b -> b.input('A', I.ironSheet())
				.input('I', I.andesite())
				.pattern("II")
				.pattern("AA")),

		SMART_CHUTE = create(AllBlocks.SMART_CHUTE).unlockedBy(AllBlocks.CHUTE::get)
			.viaShaped(b -> b.input('P', I.electronTube())
				.input('S', AllBlocks.CHUTE.get())
				.input('I', I.brassSheet())
				.pattern("I")
				.pattern("S")
				.pattern("P")),

		DEPOT = create(AllBlocks.DEPOT).unlockedBy(I::andesiteCasing)
			.viaShaped(b -> b.input('A', I.andesite())
				.input('I', I.andesiteCasing())
				.pattern("A")
				.pattern("I")),

		MECHANICAL_ARM = create(AllBlocks.MECHANICAL_ARM::get).unlockedBy(I::brassCasing)
			.returns(1)
			.viaShaped(b -> b.input('L', I.brassSheet())
				.input('R', I.cog())
				.input('I', I.electronTube())
				.input('A', I.andesite())
				.input('C', I.brassCasing())
				.pattern("LLA")
				.pattern("LR ")
				.pattern("ICI")),

		MECHANICAL_MIXER = create(AllBlocks.MECHANICAL_MIXER).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', I.cog())
				.input('B', I.andesite())
				.input('C', I.andesiteCasing())
				.input('I', AllItems.WHISK.get())
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" I ")),

		CLUTCH = create(AllBlocks.CLUTCH).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', I.shaft())
				.input('B', I.redstone())
				.input('C', I.andesiteCasing())
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" B ")),

		GEARSHIFT = create(AllBlocks.GEARSHIFT).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('S', I.cog())
				.input('B', I.redstone())
				.input('C', I.andesiteCasing())
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" B ")),

		SAIL_FRAME = create(AllBlocks.SAIL_FRAME).returns(8)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('A', I.andesite())
				.input('S', Tags.Items.RODS_WOODEN)
				.pattern("SSS")
				.pattern("SAS")
				.pattern("SSS")),

		SAIL = create(AllBlocks.SAIL).returns(8)
			.unlockedBy(AllBlocks.SAIL_FRAME::get)
			.viaShaped(b -> b.input('F', AllBlocks.SAIL_FRAME.get())
				.input('W', ItemTags.WOOL)
				.pattern("FFF")
				.pattern("FWF")
				.pattern("FFF")),

		RADIAL_CHASIS = create(AllBlocks.RADIAL_CHASSIS).returns(3)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('P', I.andesite())
				.input('L', ItemTags.LOGS)
				.pattern(" L ")
				.pattern("PLP")
				.pattern(" L ")),

		LINEAR_CHASIS = create(AllBlocks.LINEAR_CHASSIS).returns(3)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('P', I.andesite())
				.input('L', ItemTags.LOGS)
				.pattern(" P ")
				.pattern("LLL")
				.pattern(" P ")),

		LINEAR_CHASSIS_CYCLE =
			conversionCycle(ImmutableList.of(AllBlocks.LINEAR_CHASSIS, AllBlocks.SECONDARY_LINEAR_CHASSIS)),

		MINECART = create(() -> Items.MINECART).withSuffix("_from_contraption_cart")
			.unlockedBy(AllBlocks.CART_ASSEMBLER::get)
			.viaShapeless(b -> b.input(AllItems.MINECART_CONTRAPTION.get())),

		FURNACE_MINECART = create(() -> Items.FURNACE_MINECART).withSuffix("_from_contraption_cart")
			.unlockedBy(AllBlocks.CART_ASSEMBLER::get)
			.viaShapeless(b -> b.input(AllItems.FURNACE_MINECART_CONTRAPTION.get())),

		GEARBOX = create(AllBlocks.GEARBOX).unlockedBy(I::cog)
			.viaShaped(b -> b.input('C', I.cog())
				.input('B', I.andesiteCasing())
				.pattern(" C ")
				.pattern("CBC")
				.pattern(" C ")),

		GEARBOX_CYCLE = conversionCycle(ImmutableList.of(AllBlocks.GEARBOX, AllItems.VERTICAL_GEARBOX)),

		MYSTERIOUS_CUCKOO_CLOCK = create(AllBlocks.MYSTERIOUS_CUCKOO_CLOCK).unlockedBy(AllBlocks.CUCKOO_CLOCK::get)
			.viaShaped(b -> b.input('C', Tags.Items.GUNPOWDER)
				.input('B', AllBlocks.CUCKOO_CLOCK.get())
				.pattern(" C ")
				.pattern("CBC")
				.pattern(" C ")),

		ENCASED_CHAIN_DRIVE = create(AllBlocks.ENCASED_CHAIN_DRIVE).returns(2)
			.unlockedBy(I::andesiteCasing)
			.viaShaped(b -> b.input('S', I.shaft())
				.input('B', Tags.Items.NUGGETS_IRON)
				.input('C', I.andesiteCasing())
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" B ")),

		SPEEDOMETER = create(AllBlocks.SPEEDOMETER).unlockedBy(I::andesite)
			.viaShaped(b -> b.input('C', Items.COMPASS)
				.input('A', I.andesiteCasing())
				.input('S', I.shaft())
				.pattern(" C ")
				.pattern("SAS")),

		GAUGE_CYCLE = conversionCycle(ImmutableList.of(AllBlocks.SPEEDOMETER, AllBlocks.STRESSOMETER)),

		ROTATION_SPEED_CONTROLLER = create(AllBlocks.ROTATION_SPEED_CONTROLLER).unlockedBy(I::brassCasing)
			.viaShaped(b -> b.input('B', I.circuit())
				.input('C', I.brassCasing())
				.input('S', I.shaft())
				.pattern(" B ")
				.pattern("SCS")),

		NIXIE_TUBE = create(AllBlocks.NIXIE_TUBE).unlockedBy(I::brassCasing)
			.viaShaped(b -> b.input('E', I.electronTube())
				.input('B', I.brassCasing())
				.pattern("EBE")),

		MECHANICAL_SAW = create(AllBlocks.MECHANICAL_SAW).unlockedBy(I::andesiteCasing)
			.viaShaped(b -> b.input('C', I.andesiteCasing())
				.input('A', I.ironSheet())
				.input('I', I.iron())
				.pattern(" A ")
				.pattern("AIA")
				.pattern(" C ")),

		MECHANICAL_HARVESTER = create(AllBlocks.MECHANICAL_HARVESTER).unlockedBy(I::andesiteCasing)
			.viaShaped(b -> b.input('C', I.andesiteCasing())
				.input('A', I.andesite())
				.input('I', I.ironSheet())
				.pattern("AIA")
				.pattern("AIA")
				.pattern(" C ")),

		MECHANICAL_PLOUGH = create(AllBlocks.MECHANICAL_PLOUGH).unlockedBy(I::andesiteCasing)
			.viaShaped(b -> b.input('C', I.andesiteCasing())
				.input('A', I.andesite())
				.input('I', I.ironSheet())
				.pattern("III")
				.pattern("AAA")
				.pattern(" C ")),

		MECHANICAL_DRILL = create(AllBlocks.MECHANICAL_DRILL).unlockedBy(I::andesiteCasing)
			.viaShaped(b -> b.input('C', I.andesiteCasing())
				.input('A', I.andesite())
				.input('I', I.iron())
				.pattern(" A ")
				.pattern("AIA")
				.pattern(" C ")),

		SEQUENCED_GEARSHIFT = create(AllBlocks.SEQUENCED_GEARSHIFT).unlockedBy(I::brassCasing)
			.viaShaped(b -> b.input('B', I.electronTube())
				.input('S', I.cog())
				.input('C', I.brassCasing())
				.input('I', Items.CLOCK)
				.pattern(" B ")
				.pattern("SCS")
				.pattern(" I "))

	;

	private Marker LOGISTICS = enterSection(AllSections.LOGISTICS);

	GeneratedRecipe

	REDSTONE_CONTACT = create(AllBlocks.REDSTONE_CONTACT).returns(2)
		.unlockedBy(I::brassCasing)
		.viaShaped(b -> b.input('W', I.redstone())
			.input('D', I.brassCasing())
			.input('S', I.iron())
			.pattern("WDW")
			.pattern(" S ")
			.pattern("WDW")),

		ANDESITE_FUNNEL = create(AllBlocks.ANDESITE_FUNNEL).returns(2)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('A', I.andesite())
				.input('K', Items.DRIED_KELP)
				.pattern("AKA")
				.pattern(" K ")),

		BRASS_FUNNEL = create(AllBlocks.BRASS_FUNNEL).returns(2)
			.unlockedByTag(I::brass)
			.viaShaped(b -> b.input('A', I.brass())
				.input('K', Items.DRIED_KELP)
				.input('E', I.electronTube())
				.pattern("AEA")
				.pattern(" K ")),

		ANDESITE_TUNNEL = create(AllBlocks.ANDESITE_TUNNEL).returns(2)
			.unlockedBy(I::andesite)
			.viaShaped(b -> b.input('A', I.andesite())
				.input('K', Items.DRIED_KELP)
				.pattern("AA")
				.pattern("KK")),

		BRASS_TUNNEL = create(AllBlocks.BRASS_TUNNEL).returns(2)
			.unlockedByTag(I::brass)
			.viaShaped(b -> b.input('A', I.brass())
				.input('K', Items.DRIED_KELP)
				.input('E', I.electronTube())
				.pattern("E ")
				.pattern("AA")
				.pattern("KK")),

		ADJUSTABLE_CRATE = create(AllBlocks.ADJUSTABLE_CRATE).returns(4)
			.unlockedBy(I::brassCasing)
			.viaShaped(b -> b.input('B', I.brassCasing())
				.pattern("BBB")
				.pattern("B B")
				.pattern("BBB")),

		BELT_OBSERVER = create(AllBlocks.CONTENT_OBSERVER).unlockedBy(AllItems.BELT_CONNECTOR::get)
			.viaShaped(b -> b.input('B', I.brassCasing())
				.input('R', I.redstone())
				.input('I', I.iron())
				.input('C', Blocks.OBSERVER)
				.pattern("RCI")
				.pattern(" B ")),

		STOCKPILE_SWITCH = create(AllBlocks.STOCKPILE_SWITCH).unlockedBy(I::brassCasing)
			.viaShaped(b -> b.input('B', I.brassCasing())
				.input('R', I.redstone())
				.input('I', I.iron())
				.input('C', Blocks.COMPARATOR)
				.pattern("RCI")
				.pattern(" B ")),

		ADJUSTABLE_REPEATER = create(AllBlocks.ADJUSTABLE_REPEATER).unlockedByTag(I::redstone)
			.viaShaped(b -> b.input('T', Blocks.REDSTONE_TORCH)
				.input('C', Items.CLOCK)
				.input('R', I.redstone())
				.input('S', I.stone())
				.pattern("RCT")
				.pattern("SSS")),

		ADJUSTABLE_PULSE_REPEATER = create(AllBlocks.ADJUSTABLE_PULSE_REPEATER).unlockedByTag(I::redstone)
			.viaShaped(b -> b.input('S', AllBlocks.PULSE_REPEATER.get())
				.input('P', AllBlocks.ADJUSTABLE_REPEATER.get())
				.pattern("SP")),

		PULSE_REPEATER = create(AllBlocks.PULSE_REPEATER).unlockedByTag(I::redstone)
			.viaShaped(b -> b.input('T', Blocks.REDSTONE_TORCH)
				.input('R', I.redstone())
				.input('S', I.stone())
				.pattern("RRT")
				.pattern("SSS")),

		POWERED_TOGGLE_LATCH = create(AllBlocks.POWERED_TOGGLE_LATCH).unlockedByTag(I::redstone)
			.viaShaped(b -> b.input('T', Blocks.REDSTONE_TORCH)
				.input('C', Blocks.LEVER)
				.input('S', I.stone())
				.pattern(" T ")
				.pattern(" C ")
				.pattern("SSS")),

		POWERED_LATCH = create(AllBlocks.POWERED_LATCH).unlockedByTag(I::redstone)
			.viaShaped(b -> b.input('T', Blocks.REDSTONE_TORCH)
				.input('C', Blocks.LEVER)
				.input('R', I.redstone())
				.input('S', I.stone())
				.pattern(" T ")
				.pattern("RCR")
				.pattern("SSS")),

		REDSTONE_LINK = create(AllBlocks.REDSTONE_LINK).returns(2)
			.unlockedByTag(I::brass)
			.viaShaped(b -> b.input('C', Blocks.REDSTONE_TORCH)
				.input('S', I.brassSheet())
				.input('I', ItemTags.PLANKS)
				.pattern("  C")
				.pattern("SIS"))

	;

	private Marker SCHEMATICS = enterSection(AllSections.SCHEMATICS);

	GeneratedRecipe

	SCHEMATIC_TABLE = create(AllBlocks.SCHEMATIC_TABLE).unlockedBy(AllItems.EMPTY_SCHEMATIC::get)
		.viaShaped(b -> b.input('W', ItemTags.WOODEN_SLABS)
			.input('S', Blocks.SMOOTH_STONE)
			.pattern("WWW")
			.pattern(" S ")
			.pattern(" S ")),

		SCHEMATICANNON = create(AllBlocks.SCHEMATICANNON).unlockedBy(AllItems.EMPTY_SCHEMATIC::get)
			.viaShaped(b -> b.input('L', ItemTags.LOGS)
				.input('D', Blocks.DISPENSER)
				.input('C', Blocks.CAULDRON)
				.input('S', Blocks.SMOOTH_STONE)
				.input('I', Blocks.IRON_BLOCK)
				.pattern(" C ")
				.pattern("LDL")
				.pattern("SIS")),

		EMPTY_SCHEMATIC = create(AllItems.EMPTY_SCHEMATIC).unlockedBy(() -> Items.PAPER)
			.viaShapeless(b -> b.input(Items.PAPER)
				.input(Tags.Items.DYES_LIGHT_BLUE)),

		SCHEMATIC_AND_QUILL = create(AllItems.SCHEMATIC_AND_QUILL).unlockedBy(() -> Items.PAPER)
			.viaShapeless(b -> b.input(AllItems.EMPTY_SCHEMATIC.get())
				.input(Tags.Items.FEATHERS))

	;

	private Marker PALETTES = enterSection(AllSections.PALETTES);

	GeneratedRecipe

	DARK_SCORIA = create(AllPaletteBlocks.DARK_SCORIA).returns(8)
		.unlockedBy(() -> AllPaletteBlocks.SCORIA.get())
		.viaShaped(b -> b.input('#', AllPaletteBlocks.SCORIA.get())
			.input('D', Tags.Items.DYES_BLACK)
			.pattern("###")
			.pattern("#D#")
			.pattern("###")),

		COPPER_SHINGLES = create(AllBlocks.COPPER_SHINGLES).returns(16)
			.unlockedByTag(I::copperSheet)
			.viaShaped(b -> b.input('#', I.copperSheet())
				.pattern("##")
				.pattern("##")),

		COPPER_SHINGLES_FROM_TILES = create(AllBlocks.COPPER_SHINGLES).withSuffix("_from_tiles")
			.unlockedByTag(I::copperSheet)
			.viaShapeless(b -> b.input(AllBlocks.COPPER_TILES.get())),

		COPPER_TILES = create(AllBlocks.COPPER_TILES).unlockedByTag(I::copperSheet)
			.viaShapeless(b -> b.input(AllBlocks.COPPER_SHINGLES.get()))

	;

	private Marker APPLIANCES = enterFolder("appliances");

	GeneratedRecipe

	DOUGH = create(AllItems.DOUGH).unlockedBy(AllItems.WHEAT_FLOUR::get)
		.viaShapeless(b -> b.input(AllItems.WHEAT_FLOUR.get())
			.input(Items.WATER_BUCKET)),

		SLIME_BALL = create(() -> Items.SLIME_BALL).unlockedBy(AllItems.DOUGH::get)
			.viaShapeless(b -> b.input(AllItems.DOUGH.get())
				.input(Tags.Items.DYES_LIME)),

		TREE_FERTILIZER = create(AllItems.TREE_FERTILIZER).returns(2)
			.unlockedBy(() -> Items.BONE_MEAL)
			.viaShapeless(b -> b.input(Ingredient.fromTag(ItemTags.SMALL_FLOWERS), 2)
				.input(Ingredient.ofItems(Items.HORN_CORAL, Items.BRAIN_CORAL, Items.TUBE_CORAL,
					Items.BUBBLE_CORAL, Items.FIRE_CORAL))
				.input(Items.BONE_MEAL))

	;

	private Marker COOKING = enterFolder("/");

	GeneratedRecipe

	DOUGH_TO_BREAD = create(() -> Items.BREAD).viaCooking(AllItems.DOUGH::get)
		.inSmoker(),

		LIMESAND = create(AllPaletteBlocks.LIMESTONE::get).viaCooking(AllPaletteBlocks.LIMESAND::get)
			.inFurnace(),
		SOUL_SAND = create(AllPaletteBlocks.SCORIA::get).viaCooking(() -> Blocks.SOUL_SAND)
			.inFurnace(),
		DIORITE = create(AllPaletteBlocks.DOLOMITE::get).viaCooking(() -> Blocks.DIORITE)
			.inFurnace(),
		GRANITE = create(AllPaletteBlocks.GABBRO::get).viaCooking(() -> Blocks.GRANITE)
			.inFurnace(),
		NAT_SCORIA = create(AllPaletteBlocks.SCORIA::get).withSuffix("_from_natural")
			.viaCooking(AllPaletteBlocks.NATURAL_SCORIA::get)
			.inFurnace(),

		FRAMED_GLASS = recycleGlass(AllPaletteBlocks.FRAMED_GLASS),
		TILED_GLASS = recycleGlass(AllPaletteBlocks.TILED_GLASS),
		VERTICAL_FRAMED_GLASS = recycleGlass(AllPaletteBlocks.VERTICAL_FRAMED_GLASS),
		HORIZONTAL_FRAMED_GLASS = recycleGlass(AllPaletteBlocks.HORIZONTAL_FRAMED_GLASS),
		FRAMED_GLASS_PANE = recycleGlassPane(AllPaletteBlocks.FRAMED_GLASS_PANE),
		TILED_GLASS_PANE = recycleGlassPane(AllPaletteBlocks.TILED_GLASS_PANE),
		VERTICAL_FRAMED_GLASS_PANE = recycleGlassPane(AllPaletteBlocks.VERTICAL_FRAMED_GLASS_PANE),
		HORIZONTAL_FRAMED_GLASS_PANE = recycleGlassPane(AllPaletteBlocks.HORIZONTAL_FRAMED_GLASS_PANE),

		COPPER_ORE = blastMetalOre(AllItems.COPPER_INGOT::get, AllTags.forgeItemTag("ores/copper")),
		ZINC_ORE = blastMetalOre(AllItems.ZINC_INGOT::get, AllTags.forgeItemTag("ores/zinc")),
		CRUSHED_IRON = blastCrushedMetal(() -> Items.IRON_INGOT, AllItems.CRUSHED_IRON::get),
		CRUSHED_GOLD = blastCrushedMetal(() -> Items.GOLD_INGOT, AllItems.CRUSHED_GOLD::get),
		CRUSHED_COPPER = blastCrushedMetal(AllItems.COPPER_INGOT::get, AllItems.CRUSHED_COPPER::get),
		CRUSHED_ZINC = blastCrushedMetal(AllItems.ZINC_INGOT::get, AllItems.CRUSHED_ZINC::get),
		CRUSHED_BRASS = blastCrushedMetal(AllItems.BRASS_INGOT::get, AllItems.CRUSHED_BRASS::get),

		CRUSHED_OSMIUM = blastModdedCrushedMetal(AllItems.CRUSHED_OSMIUM, "osmium", MEK),
		CRUSHED_PLATINUM = blastModdedCrushedMetal(AllItems.CRUSHED_PLATINUM, "platinum", SM),
		CRUSHED_SILVER = blastModdedCrushedMetal(AllItems.CRUSHED_SILVER, "silver", MW, TH, IE, SM, INF),
		CRUSHED_TIN = blastModdedCrushedMetal(AllItems.CRUSHED_TIN, "tin", MEK, TH, MW, SM),
		CRUSHED_LEAD = blastModdedCrushedMetal(AllItems.CRUSHED_LEAD, "lead", MEK, MW, TH, IE, SM, EID),
		CRUSHED_QUICKSILVER = blastModdedCrushedMetal(AllItems.CRUSHED_QUICKSILVER, "quicksilver", MW),
		CRUSHED_BAUXITE = blastModdedCrushedMetal(AllItems.CRUSHED_BAUXITE, "aluminum", IE, SM),
		CRUSHED_URANIUM = blastModdedCrushedMetal(AllItems.CRUSHED_URANIUM, "uranium", MEK, IE, SM),
		CRUSHED_NICKEL = blastModdedCrushedMetal(AllItems.CRUSHED_NICKEL, "nickel", TH, IE, SM)

	;

	/*
	 * End of recipe list
	 */

	String currentFolder = "";

	Marker enterSection(AllSections section) {
		currentFolder = Lang.asId(section.name());
		return new Marker();
	}

	Marker enterFolder(String folder) {
		currentFolder = folder;
		return new Marker();
	}

	GeneratedRecipeBuilder create(Supplier<ItemConvertible> result) {
		return new GeneratedRecipeBuilder(currentFolder, result);
	}

	GeneratedRecipeBuilder create(Identifier result) {
		return new GeneratedRecipeBuilder(currentFolder, result);
	}

	GeneratedRecipeBuilder create(ItemProviderEntry<? extends ItemConvertible> result) {
		return create(result::get);
	}

	GeneratedRecipe blastCrushedMetal(Supplier<? extends ItemConvertible> result,
		Supplier<? extends ItemConvertible> ingredient) {
		return create(result::get).withSuffix("_from_crushed")
			.viaCooking(ingredient::get)
			.rewardXP(.1f)
			.inBlastFurnace();
	}

	GeneratedRecipe blastModdedCrushedMetal(ItemEntry<? extends Item> ingredient, String metalName, Mods... mods) {
		for (Mods mod : mods) {
			Identifier ingot = mod.ingotOf(metalName);
			String modId = mod.getId();
			create(ingot).withSuffix("_compat_" + modId)
				.whenModLoaded(modId)
				.viaCooking(ingredient::get)
				.rewardXP(.1f)
				.inBlastFurnace();
		}
		return null;
	}

	GeneratedRecipe blastMetalOre(Supplier<? extends ItemConvertible> result, Tag.Identified<Item> ore) {
		return create(result::get).withSuffix("_from_ore")
			.viaCookingTag(() -> ore)
			.rewardXP(.1f)
			.inBlastFurnace();
	}

	GeneratedRecipe recycleGlass(BlockEntry<? extends Block> ingredient) {
		return create(() -> Blocks.GLASS).withSuffix("_from_" + ingredient.getId()
			.getPath())
			.viaCooking(ingredient::get)
			.forDuration(50)
			.inFurnace();
	}

	GeneratedRecipe recycleGlassPane(BlockEntry<? extends Block> ingredient) {
		return create(() -> Blocks.GLASS_PANE).withSuffix("_from_" + ingredient.getId()
			.getPath())
			.viaCooking(ingredient::get)
			.forDuration(50)
			.inFurnace();
	}

	GeneratedRecipe metalCompacting(List<ItemProviderEntry<? extends ItemConvertible>> variants,
		List<Supplier<Tag<Item>>> ingredients) {
		GeneratedRecipe result = null;
		for (int i = 0; i + 1 < variants.size(); i++) {
			ItemProviderEntry<? extends ItemConvertible> currentEntry = variants.get(i);
			ItemProviderEntry<? extends ItemConvertible> nextEntry = variants.get(i + 1);
			Supplier<Tag<Item>> currentIngredient = ingredients.get(i);
			Supplier<Tag<Item>> nextIngredient = ingredients.get(i + 1);

			result = create(nextEntry).withSuffix("_from_compacting")
				.unlockedBy(currentEntry::get)
				.viaShaped(b -> b.pattern("###")
					.pattern("###")
					.pattern("###")
					.input('#', currentIngredient.get()));

			result = create(currentEntry).returns(9)
				.withSuffix("_from_decompacting")
				.unlockedBy(nextEntry::get)
				.viaShapeless(b -> b.input(nextIngredient.get()));
		}
		return result;
	}

	GeneratedRecipe conversionCycle(List<ItemProviderEntry<? extends ItemConvertible>> cycle) {
		GeneratedRecipe result = null;
		for (int i = 0; i < cycle.size(); i++) {
			ItemProviderEntry<? extends ItemConvertible> currentEntry = cycle.get(i);
			ItemProviderEntry<? extends ItemConvertible> nextEntry = cycle.get((i + 1) % cycle.size());
			result = create(nextEntry).withSuffix("from_conversion")
				.unlockedBy(currentEntry::get)
				.viaShapeless(b -> b.input(currentEntry.get()));
		}
		return result;
	}

	class GeneratedRecipeBuilder {

		private String path;
		private String suffix;
		private Supplier<? extends ItemConvertible> result;
		private Identifier compatDatagenOutput;
		List<ICondition> recipeConditions;

		private Supplier<ItemPredicate> unlockedBy;
		private int amount;

		private GeneratedRecipeBuilder(String path) {
			this.path = path;
			this.recipeConditions = new ArrayList<>();
			this.suffix = "";
			this.amount = 1;
		}

		public GeneratedRecipeBuilder(String path, Supplier<? extends ItemConvertible> result) {
			this(path);
			this.result = result;
		}

		public GeneratedRecipeBuilder(String path, Identifier result) {
			this(path);
			this.compatDatagenOutput = result;
		}

		GeneratedRecipeBuilder returns(int amount) {
			this.amount = amount;
			return this;
		}

		GeneratedRecipeBuilder unlockedBy(Supplier<? extends ItemConvertible> item) {
			this.unlockedBy = () -> ItemPredicate.Builder.create()
				.item(item.get())
				.build();
			return this;
		}

		GeneratedRecipeBuilder unlockedByTag(Supplier<Tag<Item>> tag) {
			this.unlockedBy = () -> ItemPredicate.Builder.create()
				.tag(tag.get())
				.build();
			return this;
		}

		GeneratedRecipeBuilder whenModLoaded(String modid) {
			return withCondition(new ModLoadedCondition(modid));
		}

		GeneratedRecipeBuilder whenModMissing(String modid) {
			return withCondition(new NotCondition(new ModLoadedCondition(modid)));
		}

		GeneratedRecipeBuilder withCondition(ICondition condition) {
			recipeConditions.add(condition);
			return this;
		}

		GeneratedRecipeBuilder withSuffix(String suffix) {
			this.suffix = suffix;
			return this;
		}

		GeneratedRecipe viaShaped(UnaryOperator<ShapedRecipeJsonFactory> builder) {
			return register(consumer -> {
				ShapedRecipeJsonFactory b = builder.apply(ShapedRecipeJsonFactory.create(result.get(), amount));
				if (unlockedBy != null)
					b.criterion("has_item", conditionsFromItemPredicates(unlockedBy.get()));
				b.offerTo(consumer, createLocation("crafting"));
			});
		}

		GeneratedRecipe viaShapeless(UnaryOperator<ShapelessRecipeJsonFactory> builder) {
			return register(consumer -> {
				ShapelessRecipeJsonFactory b = builder.apply(ShapelessRecipeJsonFactory.create(result.get(), amount));
				if (unlockedBy != null)
					b.criterion("has_item", conditionsFromItemPredicates(unlockedBy.get()));
				b.offerTo(consumer, createLocation("crafting"));
			});
		}

		private Identifier createSimpleLocation(String recipeType) {
			return Create.asResource(recipeType + "/" + getRegistryName().getPath() + suffix);
		}

		private Identifier createLocation(String recipeType) {
			return Create.asResource(recipeType + "/" + path + "/" + getRegistryName().getPath() + suffix);
		}

		private Identifier getRegistryName() {
			return compatDatagenOutput == null ? result.get()
				.asItem()
				.getRegistryName() : compatDatagenOutput;
		}

		GeneratedCookingRecipeBuilder viaCooking(Supplier<? extends ItemConvertible> item) {
			return unlockedBy(item).viaCookingIngredient(() -> Ingredient.ofItems(item.get()));
		}

		GeneratedCookingRecipeBuilder viaCookingTag(Supplier<Tag<Item>> tag) {
			return unlockedByTag(tag).viaCookingIngredient(() -> Ingredient.fromTag(tag.get()));
		}

		GeneratedCookingRecipeBuilder viaCookingIngredient(Supplier<Ingredient> ingredient) {
			return new GeneratedCookingRecipeBuilder(ingredient);
		}

		class GeneratedCookingRecipeBuilder {

			private Supplier<Ingredient> ingredient;
			private float exp;
			private int cookingTime;

			private final CookingRecipeSerializer<?> FURNACE = RecipeSerializer.SMELTING,
				SMOKER = RecipeSerializer.SMOKING, BLAST = RecipeSerializer.BLASTING,
				CAMPFIRE = RecipeSerializer.CAMPFIRE_COOKING;

			GeneratedCookingRecipeBuilder(Supplier<Ingredient> ingredient) {
				this.ingredient = ingredient;
				cookingTime = 200;
				exp = 0;
			}

			GeneratedCookingRecipeBuilder forDuration(int duration) {
				cookingTime = duration;
				return this;
			}

			GeneratedCookingRecipeBuilder rewardXP(float xp) {
				exp = xp;
				return this;
			}

			GeneratedRecipe inFurnace() {
				return inFurnace(b -> b);
			}

			GeneratedRecipe inFurnace(UnaryOperator<CookingRecipeJsonFactory> builder) {
				return create(FURNACE, builder, 1);
			}

			GeneratedRecipe inSmoker() {
				return inSmoker(b -> b);
			}

			GeneratedRecipe inSmoker(UnaryOperator<CookingRecipeJsonFactory> builder) {
				create(FURNACE, builder, 1);
				create(CAMPFIRE, builder, 3);
				return create(SMOKER, builder, .5f);
			}

			GeneratedRecipe inBlastFurnace() {
				return inBlastFurnace(b -> b);
			}

			GeneratedRecipe inBlastFurnace(UnaryOperator<CookingRecipeJsonFactory> builder) {
				create(FURNACE, builder, 1);
				return create(BLAST, builder, .5f);
			}

			private GeneratedRecipe create(CookingRecipeSerializer<?> serializer,
				UnaryOperator<CookingRecipeJsonFactory> builder, float cookingTimeModifier) {
				return register(consumer -> {
					boolean isOtherMod = compatDatagenOutput != null;

					CookingRecipeJsonFactory b = builder.apply(
						CookingRecipeJsonFactory.create(ingredient.get(), isOtherMod ? Items.DIRT : result.get(),
							exp, (int) (cookingTime * cookingTimeModifier), serializer));
					if (unlockedBy != null)
						b.criterion("has_item", conditionsFromItemPredicates(unlockedBy.get()));
					b.offerTo(result -> {
						consumer.accept(
							isOtherMod ? new ModdedCookingRecipeResult(result, compatDatagenOutput, recipeConditions)
								: result);
					}, createSimpleLocation(serializer.getRegistryName()
						.getPath()));
				});
			}
		}
	}

	@Override
	public String getName() {
		return "Create's Standard Recipes";
	}

	public StandardRecipeGen(DataGenerator p_i48262_1_) {
		super(p_i48262_1_);
	}

	private static class ModdedCookingRecipeResult implements RecipeJsonProvider {

		private RecipeJsonProvider wrapped;
		private Identifier outputOverride;
		private List<ICondition> conditions;

		public ModdedCookingRecipeResult(RecipeJsonProvider wrapped, Identifier outputOverride,
			List<ICondition> conditions) {
			this.wrapped = wrapped;
			this.outputOverride = outputOverride;
			this.conditions = conditions;
		}

		@Override
		public Identifier getRecipeId() {
			return wrapped.getRecipeId();
		}

		@Override
		public RecipeSerializer<?> getSerializer() {
			return wrapped.getSerializer();
		}

		@Override
		public JsonObject toAdvancementJson() {
			return wrapped.toAdvancementJson();
		}

		@Override
		public Identifier getAdvancementId() {
			return wrapped.getAdvancementId();
		}

		@Override
		public void serialize(JsonObject object) {
			wrapped.serialize(object);
			object.addProperty("result", outputOverride.toString());

			JsonArray conds = new JsonArray();
			conditions.forEach(c -> conds.add(CraftingHelper.serialize(c)));
			object.add("conditions", conds);
		}

	}

}
