package com.simibubi.create.foundation.config;

import com.simibubi.create.foundation.config.util.Validatable;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

public class CRecipes implements Validatable {
	@Tooltip
	boolean allowShapelessInMixer = true;
	@Tooltip
	boolean allowShapedSquareInPress = true;
	@Tooltip
	boolean allowRegularCraftingInCrafter = true;
	@Tooltip
	boolean allowStonecuttingOnSaw = true;
	@Tooltip
	boolean allowWoodcuttingOnSaw = true;
	@Tooltip
	int lightSourceCountForRefinedRadiance = 10; // min 1
	@Tooltip
	boolean enableRefinedRadianceRecipe = true;
	@Tooltip
	boolean enableShadowSteelRecipe = true;

	@Override
	public void validate() throws ConfigData.ValidationException {
		lightSourceCountForRefinedRadiance = Math.max(lightSourceCountForRefinedRadiance, 1);
	}
}
