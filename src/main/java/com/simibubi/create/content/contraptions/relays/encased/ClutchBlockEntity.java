package com.simibubi.create.content.contraptions.relays.encased;

import com.simibubi.create.AllBlockEntities;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class ClutchBlockEntity extends SplitShaftBlockEntity {

	public ClutchBlockEntity() {
		super(AllBlockEntities.CLUTCH);
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
