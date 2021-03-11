package com.simibubi.create.foundation.block.entity.render;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;

public abstract class SafeBlockEntityRenderer<T extends BlockEntity> extends BlockEntityRenderer<T> {

	public SafeBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public final void render(T te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
		if (isInvalid(te))
			return;
		renderSafe(te, partialTicks, ms, buffer, light, overlay);
	}

	protected abstract void renderSafe(T te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light,
									   int overlay);

	public boolean isInvalid(T te) {
		return !te.hasWorld() || te.getCachedState()
			.getBlock() == Blocks.AIR;
	}
}
