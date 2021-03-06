package com.smellypengu.createfabric.foundation.tileEntity.behaviour;

import com.smellypengu.createfabric.foundation.tileEntity.TileEntityBehaviour;

public class BehaviourType<T extends TileEntityBehaviour> {

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
