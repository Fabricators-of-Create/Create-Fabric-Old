package com.simibubi.create.content.contraptions.components.structureMovement;

import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ContraptionEntityRenderer<C extends AbstractContraptionEntity> extends EntityRenderer<C> {

	public ContraptionEntityRenderer(EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) {
		super(dispatcher);
	}

	@Override
	public Identifier getTexture(C entity) {
		return null;
	}

	@Override
	public boolean shouldRender(C entity, Frustum frustum, double x, double y, double z) {
		if (entity.getContraption() == null)
			return false;
		if (!entity.isAlive())
			return false;

		return super.shouldRender(entity, frustum, x, y, z);
	}

	@Override
	public void render(C entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

		// Keep a copy of the transforms in order to determine correct lighting
		MatrixStack msLocal = translateTo(entity, AnimationTickHolder.getRenderTick());
		MatrixStack[] matrixStacks = new MatrixStack[]{matrices, msLocal};

		matrices.push();
		entity.doLocalTransforms(tickDelta, matrixStacks);
		Contraption contraption = entity.getContraption();
		if (contraption != null) {
			ContraptionRenderDispatcher.render(entity, matrices, vertexConsumers, msLocal, contraption);
		}
		matrices.pop();
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
