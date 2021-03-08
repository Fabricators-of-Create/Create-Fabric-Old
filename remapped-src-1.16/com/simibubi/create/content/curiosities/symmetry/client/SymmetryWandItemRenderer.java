package com.simibubi.create.content.curiosities.symmetry.client;

import com.simibubi.create.foundation.block.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class SymmetryWandItemRenderer extends CustomRenderedItemModelRenderer<SymmetryWandModel> {

	@Override
	protected void render(ItemStack stack, SymmetryWandModel model, PartialItemModelRenderer renderer, MatrixStack ms,
		VertexConsumerProvider buffer, int light, int overlay) {
		float worldTime = AnimationTickHolder.getRenderTick() / 20;
		int maxLight = 0xF000F0;

		renderer.render(model.getBakedModel(), light);
		renderer.renderSolidGlowing(model.getPartial("core"), maxLight);
		renderer.renderGlowing(model.getPartial("core_glow"), maxLight);

		float floating = MathHelper.sin(worldTime) * .05f;
		float angle = worldTime * -10 % 360;
		
		ms.translate(0, floating, 0);
		ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(angle));
		
		renderer.renderGlowing(model.getPartial("bits"), maxLight);
	}

}
