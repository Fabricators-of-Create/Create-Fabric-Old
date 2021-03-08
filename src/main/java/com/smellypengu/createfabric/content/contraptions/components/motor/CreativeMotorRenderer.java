package com.smellypengu.createfabric.content.contraptions.components.motor;

import com.smellypengu.createfabric.AllBlockPartials;
import com.smellypengu.createfabric.content.contraptions.base.KineticTileEntity;
import com.smellypengu.createfabric.content.contraptions.base.KineticTileEntityRenderer;
import com.smellypengu.createfabric.foundation.render.SuperByteBuffer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public class CreativeMotorRenderer extends KineticTileEntityRenderer {

	public CreativeMotorRenderer(BlockEntityRendererFactory.Context ctx) {
	}

	@Override
	protected SuperByteBuffer getRotatedModel(KineticTileEntity te) {
		return AllBlockPartials.SHAFT_HALF.renderOnDirectionalSouth(te.getCachedState());
	}

}
