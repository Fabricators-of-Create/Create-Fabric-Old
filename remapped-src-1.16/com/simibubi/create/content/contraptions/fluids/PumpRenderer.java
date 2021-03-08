package com.simibubi.create.content.contraptions.fluids;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.MatrixStacker;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PumpRenderer extends KineticTileEntityRenderer {

	public PumpRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(KineticTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
		int light, int overlay) {
		super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
		if (!(te instanceof PumpTileEntity))
			return;
		PumpTileEntity pump = (PumpTileEntity) te;
		Vec3d rotationOffset = new Vec3d(.5, 14 / 16f, .5);
		BlockState blockState = te.getCachedState();
		float angle = MathHelper.lerp(pump.arrowDirection.getValue(partialTicks), 0, 90) - 90;
		for (float yRot : new float[] { 0, 90 }) {
			ms.push();
			SuperByteBuffer arrow = AllBlockPartials.MECHANICAL_PUMP_ARROW.renderOn(blockState);
			Direction direction = blockState.get(PumpBlock.FACING);
			MatrixStacker.of(ms)
				.centre()
				.rotateY(AngleHelper.horizontalAngle(direction) + 180)
				.rotateX(-AngleHelper.verticalAngle(direction) - 90)
				.unCentre()
				.translate(rotationOffset)
				.rotateY(yRot)
				.rotateZ(angle)
				.translateBack(rotationOffset);
			arrow.light(light).renderInto(ms, buffer.getBuffer(RenderLayer.getSolid()));
			ms.pop();
		}
	}

	@Override
	protected SuperByteBuffer getRotatedModel(KineticTileEntity te) {
		return AllBlockPartials.MECHANICAL_PUMP_COG.renderOnDirectionalSouth(te.getCachedState());
	}

}
