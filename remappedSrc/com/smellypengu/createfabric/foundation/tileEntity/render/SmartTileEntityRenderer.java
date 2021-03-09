package com.smellypengu.createfabric.foundation.tileEntity.render;

import com.smellypengu.createfabric.foundation.tileEntity.SmartTileEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class SmartTileEntityRenderer<T extends SmartTileEntity> extends SafeTileEntityRenderer<T> {
	
	@Override
	protected void renderSafe(T tileEntityIn, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light,
			int overlay) {
		/**FilteringRenderer.renderOnTileEntity(tileEntityIn, partialTicks, ms, buffer, light, overlay);
		LinkRenderer.renderOnTileEntity(tileEntityIn, partialTicks, ms, buffer, light, overlay);*/
	}

}
