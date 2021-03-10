package com.simibubi.create.content.contraptions.components.crank;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import com.simibubi.create.content.contraptions.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

import static net.minecraft.state.property.Properties.FACING;

public class HandCrankRenderer extends KineticBlockEntityRenderer {

	public HandCrankRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(KineticBlockEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
							  int light, int overlay) {
		super.renderSafe(te, partialTicks, ms, buffer, light, overlay);

		BlockState state = te.getCachedState();
		Block block = state.getBlock();
		AllBlockPartials renderedHandle = null;
		if (block instanceof HandCrankBlock)
			renderedHandle = ((HandCrankBlock) block).getRenderedHandle();
		if (renderedHandle == null)
			return;

		Direction facing = state.get(FACING);
		SuperByteBuffer handle = renderedHandle.renderOnDirectionalSouth(state, facing.getOpposite());
		HandCrankBlockEntity crank = (HandCrankBlockEntity) te;
		kineticRotationTransform(handle, te, facing.getAxis(),
			(crank.independentAngle + partialTicks * crank.chasingVelocity) / 360, light);
		handle.renderInto(ms, buffer.getBuffer(RenderLayer.getSolid()));
	}

}
