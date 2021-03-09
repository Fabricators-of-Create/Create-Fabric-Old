package com.smellypengu.createfabric.foundation.gui.widgets;

import com.smellypengu.createfabric.foundation.utility.AngleHelper;

public class InterpolatedChasingAngle extends InterpolatedChasingValue {

	public float get(float partialTicks) {
		return AngleHelper.angleLerp(partialTicks, lastValue, value);
	}
	
	@Override
	protected float getCurrentDiff() {
		return AngleHelper.getShortestAngleDiff(value, getTarget());
	}

}
