package com.simibubi.create.content.contraptions.components.structureMovement;

import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ContraptionEntityRenderer<C extends AbstractContraptionEntity> extends EntityRenderer<C> {

	public ContraptionEntityRenderer(EntityRenderDispatcher p_i46179_1_) {
		super(p_i46179_1_);
	}

	@Override
	public Identifier getEntityTexture(C entity) {
		return null;
	}

	@Override
	public boolean shouldRender(C entity, Frustum clippingHelper, double p_225626_3_, double p_225626_5_,
		double p_225626_7_) {
		if (entity.getContraption() == null)
			return false;
		if (!entity.isAlive())
			return false;

		return super.shouldRender(entity, clippingHelper, p_225626_3_, p_225626_5_, p_225626_7_);
	}
	
	@Override
	public void render(C entity, float yaw, float partialTicks, MatrixStack ms, VertexConsumerProvider buffers,
		int overlay) {
		super.render(entity, yaw, partialTicks, ms, buffers, overlay);

		// Keep a copy of the transforms in order to determine correct lighting
		MatrixStack msLocal = translateTo(entity, AnimationTickHolder.getRenderTick());
		MatrixStack[] matrixStacks = new MatrixStack[] { ms, msLocal };

		ms.push();
		entity.doLocalTransforms(partialTicks, matrixStacks);
		Contraption contraption = entity.getContraption();
		if (contraption != null) {
			ContraptionRenderDispatcher.render(entity, ms, buffers, msLocal, contraption);
		}
		ms.pop();

	}

	protected MatrixStack translateTo(AbstractContraptionEntity entity, float pt) {
		MatrixStack matrixStack = new MatrixStack();
		double x = MathHelper.lerp(pt, entity.lastRenderX, entity.getX());
		double y = MathHelper.lerp(pt, entity.lastRenderY, entity.getY());
		double z = MathHelper.lerp(pt, entity.lastRenderZ, entity.getZ());
		matrixStack.translate(x, y, z);
		return matrixStack;
	}

}
