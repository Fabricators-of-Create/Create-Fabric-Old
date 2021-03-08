package com.simibubi.create.content.logistics.block.redstone;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.ColorHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class AnalogLeverRenderer extends SafeTileEntityRenderer<AnalogLeverTileEntity> {

	public AnalogLeverRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(AnalogLeverTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
		int light, int overlay) {
		BlockState leverState = te.getCachedState();
		int lightCoords = WorldRenderer.getLightmapCoordinates(te.getWorld(), leverState, te.getPos());
		float state = te.clientState.get(partialTicks);

		VertexConsumer vb = buffer.getBuffer(RenderLayer.getSolid());

		// Handle
		SuperByteBuffer handle = AllBlockPartials.ANALOG_LEVER_HANDLE.renderOn(leverState);
		float angle = (float) ((state / 15) * 90 / 180 * Math.PI);
		transform(handle, leverState).translate(1 / 2f, 1 / 16f, 1 / 2f)
			.rotate(Direction.EAST, angle)
			.translate(-1 / 2f, -1 / 16f, -1 / 2f);
		handle.light(lightCoords)
			.renderInto(ms, vb);

		// Indicator
		int color = ColorHelper.mixColors(0x2C0300, 0xCD0000, state / 15f);
		SuperByteBuffer indicator = transform(AllBlockPartials.ANALOG_LEVER_INDICATOR.renderOn(leverState), leverState);
		indicator.light(lightCoords)
			.color(color)
			.renderInto(ms, vb);
	}

	private SuperByteBuffer transform(SuperByteBuffer buffer, BlockState leverState) {
		WallMountLocation face = leverState.get(AnalogLeverBlock.FACE);
		float rX = face == WallMountLocation.FLOOR ? 0 : face == WallMountLocation.WALL ? 90 : 180;
		float rY = AngleHelper.horizontalAngle(leverState.get(AnalogLeverBlock.FACING));
		buffer.rotateCentered(Direction.UP, (float) (rY / 180 * Math.PI));
		buffer.rotateCentered(Direction.EAST, (float) (rX / 180 * Math.PI));
		return buffer;
	}

}
