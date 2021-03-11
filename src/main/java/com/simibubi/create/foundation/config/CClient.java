package com.simibubi.create.foundation.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class CClient {
	// todo: lang
	// "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!"
	boolean enableTooltips = true; // "Show item descriptions on Shift and controls on Ctrl."
	boolean enableOverstressedTooltip = true; // "Display a tooltip when looking at overstressed components."
	boolean explainRenderErrors = true; // "Log a stack-trace when rendering issues happen within a moving contraption."
	float fanParticleDensity = .5f; // 0-1, "fanParticleDensity"
	boolean enableRainbowDebug = true; // "Show colourful debug information while the F3-Menu is open."
	boolean experimentalRendering = true; // "Use modern OpenGL features to drastically increase performance."
	int overlayOffsetX = 20; // > -2147483648, "Offset the overlay from goggle- and hover- information by this many pixels on the X axis; Use /create overlay"
	int overlayOffsetY = 0; // > -2147483648, "Offset the overlay from goggle- and hover- information by this many pixels on the Y axis; Use /create overlay"
	boolean smoothPlacementIndicator = false; // "Use an alternative indicator when showing where the assisted placement ends up relative to your crosshair"
}