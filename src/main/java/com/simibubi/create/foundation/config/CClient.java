package com.simibubi.create.foundation.config;

import com.simibubi.create.foundation.config.util.Validatable;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import net.minecraft.util.math.MathHelper;

public class CClient implements Validatable {
	@Tooltip
	boolean enableTooltips = true;

	@Tooltip
	boolean enableOverstressedTooltip = true;

	@Tooltip
	boolean explainRenderErrors = true;

	float fanParticleDensity = .5f;

	@Tooltip
	boolean enableRainbowDebug = true;

	@Tooltip
	boolean experimentalRendering = true;

	@Tooltip
	int overlayOffsetX = 20;

	@Tooltip
	int overlayOffsetY = 0;

	@Tooltip
	boolean smoothPlacementIndicator = false;

	@Override
	public void validate() throws ConfigData.ValidationException {
		fanParticleDensity = MathHelper.clamp(fanParticleDensity, 0f, 1f);
	}
}
