package com.simibubi.create.content.contraptions.goggles;

import java.util.List;

/**
 * Implement this Interface in the {@link net.minecraft.block.entity.BlockEntity} class that wants to add info to the screen
 */
public interface HoveringInformationProvider {
	default boolean addToTooltip(List<String> tooltip, boolean isPlayerSneaking) {
		return false;
	}
}