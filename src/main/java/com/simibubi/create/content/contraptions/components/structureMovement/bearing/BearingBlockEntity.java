package com.simibubi.create.content.contraptions.components.structureMovement.bearing;

import com.simibubi.create.content.contraptions.components.structureMovement.ControlContraption;
import com.simibubi.create.content.contraptions.components.structureMovement.DirectionalExtenderScrollOptionSlot;
import com.simibubi.create.foundation.block.entity.behaviour.ValueBoxTransform;

import net.minecraft.util.math.Direction;

public interface BearingBlockEntity extends ControlContraption {

	float getInterpolatedAngle(float partialTicks);

	boolean isWoodenTop();

	default ValueBoxTransform getMovementModeSlot() {
		return new DirectionalExtenderScrollOptionSlot((state, d) -> {
			Direction.Axis axis = d.getAxis();
			Direction.Axis bearingAxis = state.get(BearingBlock.FACING)
				.getAxis();
			return bearingAxis != axis;
		});
	}

}
