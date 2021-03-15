package com.simibubi.create.content.logistics.block.diodes;

import com.simibubi.create.AllBlockEntities;

import static com.simibubi.create.content.logistics.block.diodes.AdjustableRepeaterBlock.POWERING;

public class AdjustablePulseRepeaterBlockEntity extends AdjustableRepeaterBlockEntity {

	public AdjustablePulseRepeaterBlockEntity() {
		super(AllBlockEntities.ADJUSTABLE_PULSE_REPEATER);
	}

	@Override
	protected void updateState(boolean powered, boolean powering, boolean atMax, boolean atMin) {
		if (!charging && powered && !atMax)
			charging = true;

		if (charging && atMax) {
			if (powering) {
				world.setBlockState(pos, getCachedState().with(POWERING, false));
				charging = false;
				return;
			}
			if (!powering && !world.isClient)
				world.setBlockState(pos, getCachedState().with(POWERING, true));
			return;
		}
		
		if (!charging && powered)
			return;

		if (!charging && !atMin) {
			if (!world.isClient)
				world.setBlockState(pos, getCachedState().with(POWERING, false));
			state = 0;
			return;
		}

		state += charging ? 1 : 0;
	}
	
}
