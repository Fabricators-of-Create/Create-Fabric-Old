package com.smellypengu.createfabric.content.contraptions.components.structureMovement;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRendererFactory;

public class OrientedContraptionEntityRenderer extends ContraptionEntityRenderer<OrientedContraptionEntity> {

	public OrientedContraptionEntityRenderer(EntityRendererFactory.Context ctx) {
		super(ctx);
	}

	@Override
	public boolean shouldRender(OrientedContraptionEntity entity, Frustum frustum, double x, double y, double z) {
		if (!super.shouldRender(entity, frustum, x, y, z))
			return false;
		/**if (entity.getContraption()
				.getType() == ContraptionType.MOUNTED && entity.getRidingEntity() == null) TODO ContraptionType.MOUNTED CHECK
			return false;*/
		return true;
	}
}
