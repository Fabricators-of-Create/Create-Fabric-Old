package com.smellypengu.createfabric.foundation.block.entity.behaviour;

import com.smellypengu.createfabric.foundation.block.entity.BlockEntityBehaviour;

public class BehaviourType<T extends BlockEntityBehaviour> {
	private String name;

	public BehaviourType(String name) {
		this.name = name;
	}

	public BehaviourType() {
		this("");
	}

	public String getName() {
		return name;
	}
}
