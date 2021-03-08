package com.simibubi.create.content.curiosities.zapper;

import com.simibubi.create.foundation.block.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.block.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.PartialItemModelRenderer;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.MathHelper;

public abstract class ZapperItemRenderer<M extends CustomRenderedItemModel> extends CustomRenderedItemModelRenderer<M> {

	@Override
	protected void render(ItemStack stack, M model, PartialItemModelRenderer renderer, MatrixStack ms,
		VertexConsumerProvider buffer, int light, int overlay) {
		// Block indicator
		if (model.getCurrentPerspective() == Mode.GUI && stack.hasTag() && stack.getTag()
			.contains("BlockUsed"))
			renderBlockUsed(stack, ms, buffer, light, overlay);
	}

	private void renderBlockUsed(ItemStack stack, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
		BlockState state = NbtHelper.toBlockState(stack.getTag()
			.getCompound("BlockUsed"));

		ms.push();
		ms.translate(-0.3F, -0.45F, -0.0F);
		ms.scale(0.25F, 0.25F, 0.25F);
		BakedModel modelForState = MinecraftClient.getInstance()
			.getBlockRenderManager()
			.getModel(state);

		if (state.getBlock() instanceof HorizontalConnectingBlock)
			modelForState = MinecraftClient.getInstance()
				.getItemRenderer()
				.getHeldItemModel(new ItemStack(state.getBlock()), MinecraftClient.getInstance().world, null);

		MinecraftClient.getInstance()
			.getItemRenderer()
			.renderItem(new ItemStack(state.getBlock()), Mode.NONE, false, ms, buffer, light, overlay,
				modelForState);
		ms.pop();
	}

	protected float getAnimationProgress(float pt, boolean leftHanded, boolean mainHand) {
		float last = mainHand ^ leftHanded ? ZapperRenderHandler.lastRightHandAnimation
			: ZapperRenderHandler.lastLeftHandAnimation;
		float current =
			mainHand ^ leftHanded ? ZapperRenderHandler.rightHandAnimation : ZapperRenderHandler.leftHandAnimation;
		float animation = MathHelper.clamp(MathHelper.lerp(pt, last, current) * 5, 0, 1);
		return animation;
	}

}
