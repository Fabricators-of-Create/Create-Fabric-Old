package com.smellypengu.createfabric.foundation.block.entity.render;

import com.smellypengu.createfabric.foundation.block.entity.SmartBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class SmartBlockEntityRenderer<T extends SmartBlockEntity> extends SafeBlockEntityRenderer<T> {
	
	@Override
	protected void renderSafe(T tileEntityIn, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light,
			int overlay) {
		/**FilteringRenderer.renderOnTileEntity(tileEntityIn, partialTicks, ms, buffer, light, overlay);
		LinkRenderer.renderOnTileEntity(tileEntityIn, partialTicks, ms, buffer, light, overlay);*/
	}

}
