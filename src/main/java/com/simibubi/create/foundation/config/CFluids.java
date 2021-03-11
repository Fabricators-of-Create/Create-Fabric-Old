package com.simibubi.create.foundation.config;

public class CFluids {
	int fluidTankCapacity = 8; // min 1, "[in Buckets]" "The amount of liquid a tank can hold per block."
	int fluidTankMaxHeight = 32; // min 1, "[in Blocks]" "The maximum height a fluid tank can reach."
	int mechanicalPumpRange = 16; // min 1, "[in Blocks]" "The maximum distance a mechanical pump can push or pull liquids on either side."
	int hosePulleyBlockThreshold = 10000; // min -1, "[in Blocks]" "[-1 to disable this behaviour]" "The minimum amount of fluid blocks the hose pulley needs to find before deeming it an infinite source."
	int hosePulleyRange = 128; // min 1, "[in Blocks]" "The maximum distance a hose pulley can draw fluid blocks from."
}
