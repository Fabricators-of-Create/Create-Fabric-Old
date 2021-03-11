package com.simibubi.create.content.schematics;

public interface SpecialEntityItemRequirement {
	default ItemRequirement getRequiredItems() {
		return ItemRequirement.INVALID;
	}
}
