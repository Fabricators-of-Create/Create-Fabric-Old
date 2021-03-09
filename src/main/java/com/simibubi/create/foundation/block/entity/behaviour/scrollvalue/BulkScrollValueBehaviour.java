package com.simibubi.create.foundation.block.entity.behaviour.scrollvalue;

import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.block.entity.behaviour.ValueBoxTransform;

import java.util.List;
import java.util.function.Function;

public class BulkScrollValueBehaviour extends ScrollValueBehaviour {

	Function<SmartBlockEntity, List<? extends SmartBlockEntity>> groupGetter;

	public BulkScrollValueBehaviour(String label, SmartBlockEntity te, ValueBoxTransform slot,
									Function<SmartBlockEntity, List<? extends SmartBlockEntity>> groupGetter) {
		super(label, te, slot);
		this.groupGetter = groupGetter;
	}

	List<? extends SmartBlockEntity> getBulk() {
		return groupGetter.apply(blockEntity);
	}

}
