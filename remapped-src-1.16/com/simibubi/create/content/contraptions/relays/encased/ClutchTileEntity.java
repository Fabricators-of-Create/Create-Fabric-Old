package com.simibubi.create.content.contraptions.relays.encased;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class ClutchTileEntity extends SplitShaftTileEntity {

	public ClutchTileEntity(BlockEntityType<? extends ClutchTileEntity> type) {
		super(type);
	}

	@Override
	public float getRotationSpeedModifier(Direction face) {
		if (hasSource()) {
			if (face != getSourceFacing() && getCachedState().get(Properties.POWERED))
				return 0;
		}
		return 1;
	}

}
