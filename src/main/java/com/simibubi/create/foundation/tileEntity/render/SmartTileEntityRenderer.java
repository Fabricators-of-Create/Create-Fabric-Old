package com.simibubi.create.foundation.tileEntity.render;

import com.simibubi.create.foundation.tileEntity.SmartTileEntity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;

public class SmartTileEntityRenderer<T extends SmartTileEntity> extends SafeTileEntityRenderer<T> {
	
	public SmartTileEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(T tileEntityIn, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light,
			int overlay) {
		/**FilteringRenderer.renderOnTileEntity(tileEntityIn, partialTicks, ms, buffer, light, overlay);
		LinkRenderer.renderOnTileEntity(tileEntityIn, partialTicks, ms, buffer, light, overlay);*/
	}

}
