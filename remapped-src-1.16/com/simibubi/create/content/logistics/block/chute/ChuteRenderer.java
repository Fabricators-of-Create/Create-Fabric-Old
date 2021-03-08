package com.simibubi.create.content.logistics.block.chute;

import com.simibubi.create.content.logistics.block.chute.ChuteBlock.Shape;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.MatrixStacker;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class ChuteRenderer extends SafeTileEntityRenderer<ChuteTileEntity> {

	public ChuteRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(ChuteTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
		int light, int overlay) {
		if (te.item.isEmpty())
			return;
		BlockState blockState = te.getCachedState();
		if (blockState.get(ChuteBlock.FACING) != Direction.DOWN)
			return;
		if (blockState.get(ChuteBlock.SHAPE) != Shape.WINDOW
			&& (te.bottomPullDistance == 0 || te.itemPosition.get(partialTicks) > .5f))
			return;

		renderItem(te, partialTicks, ms, buffer, light, overlay);
	}

	public static void renderItem(ChuteTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
		int light, int overlay) {
		ItemRenderer itemRenderer = MinecraftClient.getInstance()
			.getItemRenderer();
		MatrixStacker msr = MatrixStacker.of(ms);
		ms.push();
		msr.centre();
		float itemScale = .5f;
		float itemPosition = te.itemPosition.get(partialTicks);
		ms.translate(0, -.5 + itemPosition, 0);
		ms.scale(itemScale, itemScale, itemScale);
		msr.rotateX(itemPosition * 180);
		msr.rotateY(itemPosition * 180);
		itemRenderer.renderItem(te.item, Mode.FIXED, light, overlay, ms, buffer);
		ms.pop();
	}

}
