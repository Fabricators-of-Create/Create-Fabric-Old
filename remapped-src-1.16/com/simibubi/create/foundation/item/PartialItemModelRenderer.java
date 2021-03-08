package com.simibubi.create.foundation.item;

import java.util.Random;
import com.simibubi.create.foundation.renderState.RenderTypes;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

public class PartialItemModelRenderer {

	static PartialItemModelRenderer instance;

	ItemStack stack;
	int overlay;
	MatrixStack ms;
	ModelTransformation.Mode transformType;
	VertexConsumerProvider buffer;

	static PartialItemModelRenderer get() {
		if (instance == null)
			instance = new PartialItemModelRenderer();
		return instance;
	}

	public static PartialItemModelRenderer of(ItemStack stack, ModelTransformation.Mode transformType, MatrixStack ms, VertexConsumerProvider buffer, int overlay) {
		PartialItemModelRenderer instance = get();
		instance.stack = stack;
		instance.buffer = buffer;
		instance.ms = ms;
		instance.transformType = transformType;
		instance.overlay = overlay;
		return instance;
	}

	public void render(BakedModel model, int light) {
		render(model, RenderTypes.getItemPartialTranslucent(), light);
	}
	
	public void renderSolid(BakedModel model, int light) {
		render(model, RenderTypes.getItemPartialSolid(), light);
	}
	
	public void renderSolidGlowing(BakedModel model, int light) {
		render(model, RenderTypes.getGlowingSolid(), light);
	}
	
	public void renderGlowing(BakedModel model, int light) {
		render(model, RenderTypes.getGlowingTranslucent(), light);
	}

	public void render(BakedModel model, RenderLayer type, int light) {
		if (stack.isEmpty())
			return;

		ms.push();
		ms.translate(-0.5D, -0.5D, -0.5D);

		if (!model.isBuiltin())
			renderBakedItemModel(model, light, ms,
				ItemRenderer.getArmorGlintConsumer(buffer, type, true, stack.hasGlint()));
		else
			stack.getItem()
				.getItemStackTileEntityRenderer()
				.render(stack, transformType, ms, buffer, light, overlay);

		ms.pop();
	}

	private void renderBakedItemModel(BakedModel model, int light, MatrixStack ms, VertexConsumer p_229114_6_) {
		ItemRenderer ir = MinecraftClient.getInstance()
			.getItemRenderer();
		Random random = new Random();
		IModelData data = EmptyModelData.INSTANCE;

		for (Direction direction : Iterate.directions) {
			random.setSeed(42L);
			ir.renderBakedItemQuads(ms, p_229114_6_, model.getQuads(null, direction, random, data), stack,
				light, overlay);
		}

		random.setSeed(42L);
		ir.renderBakedItemQuads(ms, p_229114_6_, model.getQuads(null, null, random, data),
			stack, light, overlay);
	}

}
