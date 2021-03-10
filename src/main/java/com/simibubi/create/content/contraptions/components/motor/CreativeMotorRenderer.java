package com.simibubi.create.content.contraptions.components.motor;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import com.simibubi.create.content.contraptions.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;

public class CreativeMotorRenderer extends KineticBlockEntityRenderer {

	public CreativeMotorRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected SuperByteBuffer getRotatedModel(KineticBlockEntity te) {
		return AllBlockPartials.SHAFT_HALF.renderOnDirectionalSouth(te.getCachedState());
	}

}
