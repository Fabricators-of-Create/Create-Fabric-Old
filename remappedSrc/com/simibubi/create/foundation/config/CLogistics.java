package com.simibubi.create.foundation.config;

public class CLogistics {
	int defaultExtractionLimit = 64; // min 1, max 64, "The maximum amount of items a funnel pulls at a time without an applied filter."
	int defaultExtractionTimer = 8; // min 1, "The amount of ticks a funnel waits between item transferrals, when it is not re-activated by redstone."
	int psiTimeout = 20; // min 1, "The amount of ticks a portable storage interface waits for transfers until letting contraptions move along."
	int mechanicalArmRange = 5; // min 1, "Maximum distance in blocks a Mechanical Arm can reach across."
	int linkRange = 128; // min 1, "Maximum possible range in blocks of redstone link connections."
}
