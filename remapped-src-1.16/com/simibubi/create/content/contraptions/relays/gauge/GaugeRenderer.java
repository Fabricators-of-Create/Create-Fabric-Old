package com.simibubi.create.content.contraptions.relays.gauge;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.content.contraptions.relays.gauge.GaugeBlock.Type;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class GaugeRenderer extends KineticTileEntityRenderer {

	protected GaugeBlock.Type type;

	public static GaugeRenderer speed(BlockEntityRenderDispatcher dispatcher) {
		return new GaugeRenderer(dispatcher, Type.SPEED);
	}
	
	public static GaugeRenderer stress(BlockEntityRenderDispatcher dispatcher) {
		return new GaugeRenderer(dispatcher, Type.STRESS);
	}
	
	protected GaugeRenderer(BlockEntityRenderDispatcher dispatcher, GaugeBlock.Type type) {
		super(dispatcher);
		this.type = type;
	}

	@Override
	protected void renderSafe(KineticTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
		int light, int overlay) {
		super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
		BlockState gaugeState = te.getCachedState();
		GaugeTileEntity gaugeTE = (GaugeTileEntity) te;
		int lightCoords = WorldRenderer.getLightmapCoordinates(te.getWorld(), gaugeState, te.getPos());

		SuperByteBuffer headBuffer =
			(type == Type.SPEED ? AllBlockPartials.GAUGE_HEAD_SPEED : AllBlockPartials.GAUGE_HEAD_STRESS)
				.renderOn(gaugeState);
		SuperByteBuffer dialBuffer = AllBlockPartials.GAUGE_DIAL.renderOn(gaugeState);

		for (Direction facing : Iterate.directions) {
			if (!((GaugeBlock) gaugeState.getBlock()).shouldRenderHeadOnFace(te.getWorld(), te.getPos(), gaugeState,
				facing))
				continue;

			float dialPivot = 5.75f / 16;
			float progress = MathHelper.lerp(partialTicks, gaugeTE.prevDialState, gaugeTE.dialState);

			VertexConsumer vb = buffer.getBuffer(RenderLayer.getSolid());
			rotateBufferTowards(dialBuffer, facing).translate(0, dialPivot, dialPivot)
				.rotate(Direction.EAST, (float) (Math.PI / 2 * -progress))
				.translate(0, -dialPivot, -dialPivot)
				.light(lightCoords)
				.renderInto(ms, vb);
			rotateBufferTowards(headBuffer, facing).light(lightCoords)
				.renderInto(ms, vb);
		}

	}

	@Override
	protected BlockState getRenderedBlockState(KineticTileEntity te) {
		return shaft(getRotationAxisOf(te));
	}

	protected SuperByteBuffer rotateBufferTowards(SuperByteBuffer buffer, Direction target) {
		return buffer.rotateCentered(Direction.UP, (float) ((-target.asRotation() - 90) / 180 * Math.PI));
	}

}
