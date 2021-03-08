package com.simibubi.create.content.curiosities.zapper.terrainzapper;

import static java.lang.Math.max;
import static net.minecraft.util.math.MathHelper.clamp;

import com.simibubi.create.content.curiosities.zapper.ZapperItemRenderer;
import com.simibubi.create.foundation.item.PartialItemModelRenderer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

public class WorldshaperItemRenderer extends ZapperItemRenderer<WorldshaperModel> {

	@Override
	protected void render(ItemStack stack, WorldshaperModel model, PartialItemModelRenderer renderer, MatrixStack ms,
		VertexConsumerProvider buffer, int light, int overlay) {
		super.render(stack, model, renderer, ms, buffer, light, overlay);

		float pt = AnimationTickHolder.getPartialTicks();
		float worldTime = AnimationTickHolder.getRenderTick() / 20;

		renderer.renderSolid(model.getBakedModel(), light);

		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		boolean leftHanded = player.getMainArm() == Arm.LEFT;
		boolean mainHand = player.getMainHandStack() == stack;
		boolean offHand = player.getOffHandStack() == stack;
		float animation = getAnimationProgress(pt, leftHanded, mainHand);

		// Core glows
		float multiplier = MathHelper.sin(worldTime * 5);
		if (mainHand || offHand) 
			multiplier = animation;

		int lightItensity = (int) (15 * clamp(multiplier, 0, 1));
		int glowLight = LightmapTextureManager.pack(lightItensity, max(lightItensity, 4));
		renderer.renderSolidGlowing(model.getPartial("core"), glowLight);
		renderer.renderGlowing(model.getPartial("core_glow"), glowLight);

		// Accelerator spins
		float angle = worldTime * -25;
		if (mainHand || offHand)
			angle += 360 * animation;

		angle %= 360;
		float offset = -.155f;
		ms.translate(0, offset, 0);
		ms.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(angle));
		ms.translate(0, -offset, 0);
		renderer.render(model.getPartial("accelerator"), light);
	}

}
