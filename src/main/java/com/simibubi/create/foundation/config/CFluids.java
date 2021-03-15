package com.simibubi.create.foundation.config;

import com.simibubi.create.foundation.config.util.Validatable;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

public class CFluids implements Validatable {
	@Tooltip
	int fluidTankCapacity = 8; // min 1, "[in Buckets]" "The amount of liquid a tank can hold per block."

	@Tooltip
	int fluidTankMaxHeight = 32; // min 1, "[in Blocks]" "The maximum height a fluid tank can reach."

	@Tooltip
	int mechanicalPumpRange = 16; // min 1, "[in Blocks]" "The maximum distance a mechanical pump can push or pull liquids on either side."

	@Tooltip(count = 2)
	int hosePulleyBlockThreshold = 10000; // min -1, "[in Blocks]" "[-1 to disable this behaviour]" "The minimum amount of fluid blocks the hose pulley needs to find before deeming it an infinite source."

	@Tooltip
	int hosePulleyRange = 128; // min 1, "[in Blocks]" "The maximum distance a hose pulley can draw fluid blocks from."

	@Override
	public void validate() throws ConfigData.ValidationException {
		fluidTankCapacity = Math.max(fluidTankCapacity, 1);
		fluidTankMaxHeight = Math.max(fluidTankMaxHeight, 1);
		mechanicalPumpRange = Math.max(mechanicalPumpRange, 1);
		hosePulleyBlockThreshold = Math.max(hosePulleyBlockThreshold, -1);
		hosePulleyRange = Math.max(hosePulleyRange, 1);
	}
}
