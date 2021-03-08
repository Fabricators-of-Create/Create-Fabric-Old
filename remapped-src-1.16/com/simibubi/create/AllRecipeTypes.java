package com.simibubi.create;

import java.util.Optional;
import java.util.function.Supplier;

import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.content.contraptions.components.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.contraptions.components.crusher.CrushingRecipe;
import com.simibubi.create.content.contraptions.components.fan.SplashingRecipe;
import com.simibubi.create.content.contraptions.components.millstone.MillingRecipe;
import com.simibubi.create.content.contraptions.components.mixer.CompactingRecipe;
import com.simibubi.create.content.contraptions.components.mixer.MixingRecipe;
import com.simibubi.create.content.contraptions.components.press.PressingRecipe;
import com.simibubi.create.content.contraptions.components.saw.CuttingRecipe;
import com.simibubi.create.content.contraptions.fluids.actors.FillingRecipe;
import com.simibubi.create.content.contraptions.processing.BasinRecipe;
import com.simibubi.create.content.contraptions.processing.EmptyingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder.ProcessingRecipeFactory;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer;
import com.simibubi.create.content.curiosities.tools.SandPaperPolishingRecipe;
import com.simibubi.create.content.curiosities.zapper.blockzapper.BlockzapperUpgradeRecipe;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;

public enum AllRecipeTypes {

	BLOCKZAPPER_UPGRADE(BlockzapperUpgradeRecipe.Serializer::new, RecipeType.CRAFTING),
	MECHANICAL_CRAFTING(MechanicalCraftingRecipe.Serializer::new),

	CONVERSION(processingSerializer(ConversionRecipe::new)),
	CRUSHING(processingSerializer(CrushingRecipe::new)),
	CUTTING(processingSerializer(CuttingRecipe::new)),
	MILLING(processingSerializer(MillingRecipe::new)),
	BASIN(processingSerializer(BasinRecipe::new)),
	MIXING(processingSerializer(MixingRecipe::new)),
	COMPACTING(processingSerializer(CompactingRecipe::new)),
	PRESSING(processingSerializer(PressingRecipe::new)),
	SANDPAPER_POLISHING(processingSerializer(SandPaperPolishingRecipe::new)),
	SPLASHING(processingSerializer(SplashingRecipe::new)),
	FILLING(processingSerializer(FillingRecipe::new)),
	EMPTYING(processingSerializer(EmptyingRecipe::new)),

	;

	public RecipeSerializer<?> serializer;
	public Supplier<RecipeSerializer<?>> supplier;
	public RecipeType<? extends Recipe<? extends Inventory>> type;

	AllRecipeTypes(Supplier<RecipeSerializer<?>> supplier) {
		this(supplier, null);
	}

	AllRecipeTypes(Supplier<RecipeSerializer<?>> supplier,
		RecipeType<? extends Recipe<? extends Inventory>> existingType) {
		this.supplier = supplier;
		this.type = existingType;
	}

	public static void register(RegistryEvent.Register<RecipeSerializer<?>> event) {
		ShapedRecipe.setCraftingSize(9, 9);

		for (AllRecipeTypes r : AllRecipeTypes.values()) {
			if (r.type == null)
				r.type = customType(Lang.asId(r.name()));

			r.serializer = r.supplier.get();
			Identifier location = new Identifier(Create.ID, Lang.asId(r.name()));
			event.getRegistry()
				.register(r.serializer.setRegistryName(location));
		}
	}

	private static <T extends Recipe<?>> RecipeType<T> customType(String id) {
		return Registry.register(Registry.RECIPE_TYPE, new Identifier(Create.ID, id), new RecipeType<T>() {
			public String toString() {
				return Create.ID + ":" + id;
			}
		});
	}

	private static Supplier<RecipeSerializer<?>> processingSerializer(
		ProcessingRecipeFactory<? extends ProcessingRecipe<?>> factory) {
		return () -> new ProcessingRecipeSerializer<>(factory);
	}

	@SuppressWarnings("unchecked")
	public <T extends RecipeType<?>> T getType() {
		return (T) type;
	}

	public <C extends Inventory, T extends Recipe<C>> Optional<T> find(C inv, World world) {
		return world.getRecipeManager()
			.getFirstMatch(getType(), inv, world);
	}
}
