package com.simibubi.create.content.contraptions.components.actors;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class DrillTileEntity extends BlockBreakingKineticTileEntity {

	public DrillTileEntity(BlockEntityType<? extends DrillTileEntity> type) {
		super(type);
	}

	@Override
	protected BlockPos getBreakingPos() {
		return getPos().offset(getCachedState().get(DrillBlock.FACING));
	}

}
