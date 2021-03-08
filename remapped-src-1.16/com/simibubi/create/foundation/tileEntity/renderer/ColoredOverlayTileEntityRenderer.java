package com.simibubi.create.foundation.tileEntity.renderer;

import com.simibubi.create.foundation.render.SuperByteBuffer;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ColoredOverlayTileEntityRenderer<T extends BlockEntity> extends SafeTileEntityRenderer<T> {

	public ColoredOverlayTileEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(T te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
			int light, int overlay) {
		SuperByteBuffer render = render(te.getWorld(), te.getPos(), te.getCachedState(), getOverlayBuffer(te),
				getColor(te, partialTicks));
		render.renderInto(ms, buffer.getBuffer(RenderLayer.getSolid()));
	}

	protected abstract int getColor(T te, float partialTicks);

	protected abstract SuperByteBuffer getOverlayBuffer(T te);

	public static SuperByteBuffer render(World world, BlockPos pos, BlockState state, SuperByteBuffer buffer,
			int color) {
		int packedLightmapCoords = WorldRenderer.getLightmapCoordinates(world, state, pos);
		return buffer.color(color).light(packedLightmapCoords);
	}

}
