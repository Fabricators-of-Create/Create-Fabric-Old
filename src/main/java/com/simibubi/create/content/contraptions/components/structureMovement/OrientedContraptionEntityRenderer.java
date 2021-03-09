package com.simibubi.create.content.contraptions.components.structureMovement;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;

public class OrientedContraptionEntityRenderer extends ContraptionEntityRenderer<OrientedContraptionEntity> {

	public OrientedContraptionEntityRenderer(EntityRenderDispatcher manager, EntityRendererRegistry.Context context) {
		super(manager, context);
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
