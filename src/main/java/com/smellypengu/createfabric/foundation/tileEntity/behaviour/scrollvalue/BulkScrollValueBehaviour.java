package com.smellypengu.createfabric.foundation.tileEntity.behaviour.scrollvalue;

import com.smellypengu.createfabric.foundation.tileEntity.SmartTileEntity;
import com.smellypengu.createfabric.foundation.tileEntity.behaviour.ValueBoxTransform;

import java.util.List;
import java.util.function.Function;

public class BulkScrollValueBehaviour extends ScrollValueBehaviour {

	Function<SmartTileEntity, List<? extends SmartTileEntity>> groupGetter;

	public BulkScrollValueBehaviour(String label, SmartTileEntity te, ValueBoxTransform slot,
			Function<SmartTileEntity, List<? extends SmartTileEntity>> groupGetter) {
		super(label, te, slot);
		this.groupGetter = groupGetter;
	}

	List<? extends SmartTileEntity> getBulk() {
		return groupGetter.apply(tileEntity);
	}

}
