package com.simibubi.create.foundation.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry;

public class CWorldGen {
	boolean disable = false; // "Prevents all worldgen added by Create from taking effect"

	@ConfigEntry.Gui.PrefixText()
	int copperOreMinHeight = 40; // min 0,

	int copperOreMaxHeight = 85; // min 0,
	int copperOreClusterSize = 18; // min 0,
	float copperOreFrequency = 2.0f; // 0.0 ~ 512.0, Amount of clusters generated per Chunk. >1 to spawn multiple. <1 to make it a chance. #  0 to disable.

	@ConfigEntry.Gui.PrefixText()
	int weatheredLimestoneMinHeight = 10; // min 0,

	int weatheredLimestoneMaxHeight = 30; // min 0,
	int weatheredLimestoneClusterSize = 128; // min 0,
	float weatheredLimestoneFrequency = 0.015625f; // 0.0 ~ 512.0, Amount of clusters generated per Chunk. >1 to spawn multiple. <1 to make it a chance. #  0 to disable.

	@ConfigEntry.Gui.PrefixText()
	int zincOreMinHeight = 15; // min 0,

	int zincOreMaxHeight = 70; // min 0,
	int zincOreClusterSize = 14; // min 0,
	float zincOreFrequency = 4.0f; // 0.0 ~ 512.0, Amount of clusters generated per Chunk. >1 to spawn multiple. <1 to make it a chance. #  0 to disable.

	@ConfigEntry.Gui.PrefixText()
	int limestoneMinHeight = 30; // min 0,

	int limestoneMaxHeight = 70; // min 0,
	int limestoneClusterSize = 128; // min 0,
	float limestoneFrequency = 0.015625f; // 0.0 ~ 512.0, Amount of clusters generated per Chunk. >1 to spawn multiple. <1 to make it a chance. #  0 to disable.

	@ConfigEntry.Gui.PrefixText()
	int dolomiteMinHeight = 20; // min 0,

	int dolomiteMaxHeight = 70; // min 0,
	int dolomiteClusterSize = 128; // min 0,
	float dolomiteFrequency = 0.015625f; // 0.0 ~ 512.0, Amount of clusters generated per Chunk. >1 to spawn multiple. <1 to make it a chance. #  0 to disable.

	@ConfigEntry.Gui.PrefixText()
	int gabbroMinHeight = 20; // min 0,

	int gabbroMaxHeight = 70; // min 0,
	int gabbroClusterSize = 128; // min 0,
	float gabbroFrequency = 0.015625f; // 0.0 ~ 512.0, Amount of clusters generated per Chunk. >1 to spawn multiple. <1 to make it a chance. #  0 to disable.

	@ConfigEntry.Gui.PrefixText()
	int scoriaMinHeight = 0; // min 0,

	int scoriaMaxHeight = 10; // min 0,
	int scoriaClusterSize = 128; // min 0,
	float scoriaFrequency = 0.03125f; // 0.0 ~ 512.0, Amount of clusters generated per Chunk. >1 to spawn multiple. <1 to make it a chance. #  0 to disable.
}
