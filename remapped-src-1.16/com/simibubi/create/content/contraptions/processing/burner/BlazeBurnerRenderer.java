package com.simibubi.create.content.contraptions.processing.burner;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class BlazeBurnerRenderer extends SafeTileEntityRenderer<BlazeBurnerTileEntity> {

	public BlazeBurnerRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(BlazeBurnerTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
		int light, int overlay) {
		HeatLevel heatLevel = te.getHeatLevelFromBlock();
		if (heatLevel == HeatLevel.NONE)
			return;

		float renderTick = AnimationTickHolder.getRenderTick() + (te.hashCode() % 13) * 16f;
		float offset = (MathHelper.sin((float) ((renderTick / 16f) % (2 * Math.PI))) + .5f) / 16f;

		AllBlockPartials blazeModel = AllBlockPartials.BLAZES.get(heatLevel);
		SuperByteBuffer blazeBuffer = blazeModel.renderOn(te.getCachedState());
		blazeBuffer.rotateCentered(Direction.UP, AngleHelper.rad(te.headAngle.getValue(partialTicks)));
		blazeBuffer.translate(0, offset, 0);
		blazeBuffer.light(0xF000F0)
			.renderInto(ms, buffer.getBuffer(RenderLayer.getSolid()));
	}
}
