package com.simibubi.create.content.contraptions.relays.encased;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import com.simibubi.create.content.contraptions.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.contraptions.base.Rotating;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.render.backend.FastRenderDispatcher;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SplitShaftRenderer extends KineticBlockEntityRenderer {

	public SplitShaftRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(KineticBlockEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
							  int light, int overlay) {
		if (FastRenderDispatcher.available(te.getWorld())) return;

		Block block = te.getCachedState().getBlock();
		final Direction.Axis boxAxis = ((Rotating) block).getRotationAxis(te.getCachedState());
		final BlockPos pos = te.getPos();
		float time = AnimationTickHolder.getRenderTick();

		for (Direction direction : Iterate.directions) {
			Direction.Axis axis = direction.getAxis();
			if (boxAxis != axis)
				continue;

			float offset = getRotationOffsetForPosition(te, pos, axis);
			float angle = (time * te.getSpeed() * 3f / 10) % 360;
			float modifier = 1;

			if (te instanceof SplitShaftBlockEntity)
				modifier = ((SplitShaftBlockEntity) te).getRotationSpeedModifier(direction);

			angle *= modifier;
			angle += offset;
			angle = angle / 180f * (float) Math.PI;

			SuperByteBuffer superByteBuffer =
				AllBlockPartials.SHAFT_HALF.renderOnDirectionalSouth(te.getCachedState(), direction);
			kineticRotationTransform(superByteBuffer, te, axis, angle, light);
			superByteBuffer.renderInto(ms, buffer.getBuffer(RenderLayer.getSolid()));
		}
	}

}
