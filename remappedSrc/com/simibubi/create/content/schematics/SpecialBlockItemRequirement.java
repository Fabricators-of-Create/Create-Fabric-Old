package com.simibubi.create.content.schematics;

import net.minecraft.block.BlockState;

public interface SpecialBlockItemRequirement {
	default ItemRequirement getRequiredItems(BlockState state) {
		return ItemRequirement.INVALID;
	}
}
