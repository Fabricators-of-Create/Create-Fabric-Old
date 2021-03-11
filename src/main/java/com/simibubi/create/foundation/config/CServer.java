package com.simibubi.create.foundation.config;

import com.simibubi.create.foundation.config.util.Validatable;
import me.shedaniel.autoconfig.ConfigData;

public class CServer implements Validatable {
	// infrastructure           public ConfigGroup infrastructure = group(0, "infrastructure", Comments.infrastructure);
	int tickrateSyncTimer = 20; // min 5, "[in Ticks]" "The amount of time a server waits before sending out tickrate synchronization packets." "These packets help animations to be more accurate when tps is below 20."

	@Override
	public void validate() throws ConfigData.ValidationException {
		tickrateSyncTimer = Math.max(tickrateSyncTimer, 5);
	}
}
