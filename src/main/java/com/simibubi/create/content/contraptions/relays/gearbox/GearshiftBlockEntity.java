package com.simibubi.create.content.contraptions.relays.gearbox;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.content.contraptions.relays.encased.SplitShaftBlockEntity;

import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class GearshiftBlockEntity extends SplitShaftBlockEntity {

	public GearshiftBlockEntity() {
		super(AllBlockEntities.GEARSHIFT);
	}

	@Override
	public float getRotationSpeedModifier(Direction face) {
		if (hasSource()) {
			if (face != getSourceFacing() && getCachedState().get(Properties.POWERED))
				return -1;
		}
		return 1;
	}
	
}
