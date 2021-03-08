package com.simibubi.create.content.logistics.block.diodes;

import static com.simibubi.create.content.logistics.block.diodes.AdjustableRepeaterBlock.POWERING;

import net.minecraft.block.entity.BlockEntityType;

public class AdjustablePulseRepeaterTileEntity extends AdjustableRepeaterTileEntity {

	public AdjustablePulseRepeaterTileEntity(BlockEntityType<? extends AdjustablePulseRepeaterTileEntity> type) {
		super(type);
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
