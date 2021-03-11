package com.simibubi.create.foundation.config;

import com.simibubi.create.foundation.config.util.Validatable;
import me.shedaniel.autoconfig.ConfigData;

public class CRecipes implements Validatable {
	boolean allowShapelessInMixer = true; // "When true, allows any shapeless crafting recipes to be processed by a Mechanical Mixer + Basin."
	boolean allowShapedSquareInPress = true; // "When true, allows any single-ingredient 2x2 or 3x3 crafting recipes to be processed by a Mechanical Press + Basin."
	boolean allowRegularCraftingInCrafter = true; // "When true, allows any standard crafting recipes to be processed by Mechanical Crafters."
	boolean allowStonecuttingOnSaw = true; // "When true, allows any stonecutting recipes to be processed by a Mechanical Saw."
	boolean allowWoodcuttingOnSaw = true; // "When true, allows any Druidcraft woodcutter recipes to be processed by a Mechanical Saw."
	int lightSourceCountForRefinedRadiance = 10; // min 1, "The amount of Light sources destroyed before Chromatic Compound turns into Refined Radiance."
	boolean enableRefinedRadianceRecipe = true; // "Allow the standard in-world Refined Radiance recipes."
	boolean enableShadowSteelRecipe = true; // "Allow the standard in-world Shadow Steel recipe."

	@Override
	public void validate() throws ConfigData.ValidationException {
		lightSourceCountForRefinedRadiance = Math.max(lightSourceCountForRefinedRadiance, 1);
	}
}
