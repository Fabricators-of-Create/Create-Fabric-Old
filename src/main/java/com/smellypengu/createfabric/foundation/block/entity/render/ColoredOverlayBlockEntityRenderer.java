package com.smellypengu.createfabric.foundation.block.entity.render;

import com.smellypengu.createfabric.foundation.render.SuperByteBuffer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class ColoredOverlayBlockEntityRenderer<T extends BlockEntity> extends SafeBlockEntityRenderer<T> {

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
