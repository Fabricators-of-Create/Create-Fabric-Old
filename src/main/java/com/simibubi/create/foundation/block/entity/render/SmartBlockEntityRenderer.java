package com.simibubi.create.foundation.block.entity.render;

import com.simibubi.create.foundation.block.entity.SmartBlockEntity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;

public class SmartBlockEntityRenderer<T extends SmartBlockEntity> extends SafeBlockEntityRenderer<T> {
	
	public SmartBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(T blockEntityIn, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light,
							  int overlay) {
//		FilteringRenderer.renderOnBlockEntity(blockEntityIn, partialTicks, ms, buffer, light, overlay);
//		LinkRenderer.renderOnBlockEntity(blockEntityIn, partialTicks, ms, buffer, light, overlay);
	}

}
