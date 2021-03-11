package com.simibubi.create.foundation.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

public class CClient {
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
}
