package com.simibubi.create.content.logistics.block.chute;

import com.simibubi.create.foundation.tileEntity.renderer.SmartTileEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;

public class SmartChuteRenderer extends SmartTileEntityRenderer<SmartChuteTileEntity> {

	public SmartChuteRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	protected void renderSafe(SmartChuteTileEntity tileEntityIn, float partialTicks, MatrixStack ms,
		VertexConsumerProvider buffer, int light, int overlay) {
		super.renderSafe(tileEntityIn, partialTicks, ms, buffer, light, overlay);
		if (tileEntityIn.item.isEmpty())
			return;
		if (tileEntityIn.itemPosition.get(partialTicks) > 0)
			return;
		ChuteRenderer.renderItem(tileEntityIn, partialTicks, ms, buffer, light, overlay);
	}

}
