package com.simibubi.create.foundation.block.entity.behaviour;

import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;

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
