package com.smellypengu.createfabric.content.schematics;

public interface ISpecialEntityItemRequirement {

	default ItemRequirement getRequiredItems() {
		return ItemRequirement.INVALID;
	}
	
}
