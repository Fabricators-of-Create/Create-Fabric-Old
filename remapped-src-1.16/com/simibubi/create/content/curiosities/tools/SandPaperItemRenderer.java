package com.simibubi.create.content.curiosities.tools;

import com.simibubi.create.foundation.block.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;

public class SandPaperItemRenderer extends BuiltinModelItemRenderer {

	@Override
	public void render(ItemStack stack, ModelTransformation.Mode p_239207_2_, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
		ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		SandPaperModel mainModel = (SandPaperModel) itemRenderer.getHeldItemModel(stack, MinecraftClient.getInstance().world, null);
		Mode perspective = mainModel.getCurrentPerspective();
		float partialTicks = AnimationTickHolder.getPartialTicks();

		boolean leftHand = perspective == Mode.FIRST_PERSON_LEFT_HAND;
		boolean firstPerson = leftHand || perspective == Mode.FIRST_PERSON_RIGHT_HAND;

		ms.push();
		ms.translate(.5f, .5f, .5f);

		CompoundTag tag = stack.getOrCreateTag();
		boolean jeiMode = tag.contains("JEI");

		if (tag.contains("Polishing")) {
			ms.push();

			if (perspective == Mode.GUI) {
				ms.translate(0.0F, .2f, 1.0F);
				ms.scale(.75f, .75f, .75f);
			} else {
				int modifier = leftHand ? -1 : 1;
				ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(modifier * 40));
			}

			// Reverse bobbing
			float time = (float) (!jeiMode ? player.getItemUseTimeLeft()
					: (-AnimationTickHolder.getTicks()) % stack.getMaxUseTime()) - partialTicks + 1.0F;
			if (time / (float) stack.getMaxUseTime() < 0.8F) {
				float bobbing = -MathHelper.abs(MathHelper.cos(time / 4.0F * (float) Math.PI) * 0.1F);

				if (perspective == Mode.GUI)
					ms.translate(bobbing, bobbing, 0.0F);
				else
					ms.translate(0.0f, bobbing, 0.0F);
			}

			ItemStack toPolish = ItemStack.fromTag(tag.getCompound("Polishing"));
			itemRenderer.renderItem(toPolish, Mode.NONE, light, overlay, ms, buffer);

			ms.pop();
		}

		if (firstPerson) {
			int itemInUseCount = player.getItemUseTimeLeft();
			if (itemInUseCount > 0) {
				int modifier = leftHand ? -1 : 1;
				ms.translate(modifier * .5f, 0, -.25f);
				ms.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(modifier * 40));
				ms.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(modifier * 10));
				ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(modifier * 90));
			}
		}

		itemRenderer.renderItem(stack, Mode.NONE, false, ms, buffer, light, overlay, mainModel.getBakedModel());

		ms.pop();
	}

	public static class SandPaperModel extends CustomRenderedItemModel {

		public SandPaperModel(BakedModel template) {
			super(template, "");
		}

		@Override
		public BuiltinModelItemRenderer createRenderer() {
			return new SandPaperItemRenderer();
		}

	}

}
