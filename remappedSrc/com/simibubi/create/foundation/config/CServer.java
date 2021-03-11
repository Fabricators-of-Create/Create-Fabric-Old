package com.simibubi.create.foundation.config;

public class CServer {
	// infrastructure           public ConfigGroup infrastructure = group(0, "infrastructure", Comments.infrastructure);
	int tickrateSyncTimer = 20; // min 5, "[in Ticks]" "The amount of time a server waits before sending out tickrate synchronization packets." "These packets help animations to be more accurate when tps is below 20."
}
