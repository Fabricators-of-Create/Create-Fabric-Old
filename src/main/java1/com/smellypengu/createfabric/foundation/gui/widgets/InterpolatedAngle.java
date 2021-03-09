package com.smellypengu.createfabric.foundation.gui.widgets;

import com.smellypengu.createfabric.foundation.utility.AngleHelper;

public class InterpolatedAngle extends InterpolatedValue {
	
	public float get(float partialTicks) {
		return AngleHelper.angleLerp(partialTicks, lastValue, value);
	}

}
