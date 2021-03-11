package com.simibubi.create.foundation.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class CCommon {
	@ConfigEntry.Gui.Tooltip
	boolean logBeErrors = false;
}

// TODO: public CWorldGen worldGen = nested(0, CWorldGen::new, Comments.worldGen); "Modify Create's impact on your terrain"
