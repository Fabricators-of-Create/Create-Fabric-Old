package com.simibubi.create.content.contraptions.components.flywheel.engine;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;

import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class EngineRenderer<T extends EngineTileEntity> extends SafeTileEntityRenderer<T> {

	public EngineRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(T te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light,
		int overlay) {
		Block block = te.getCachedState()
			.getBlock();
		if (block instanceof EngineBlock) {
			EngineBlock engineBlock = (EngineBlock) block;
			AllBlockPartials frame = engineBlock.getFrameModel();
			if (frame != null) {
				Direction facing = te.getCachedState()
					.get(EngineBlock.FACING);
				float angle = AngleHelper.rad(AngleHelper.horizontalAngle(facing));
				frame.renderOn(te.getCachedState())
					.rotateCentered(Direction.UP, angle)
					.translate(0, 0, -1)
					.light(WorldRenderer.getLightmapCoordinates(te.getWorld(), te.getCachedState(), te.getPos()))
					.renderInto(ms, buffer.getBuffer(RenderLayer.getSolid()));
			}
		}
	}

}
