package com.simibubi.create.content.contraptions.components.structureMovement;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;

public class OrientedContraptionEntityRenderer extends ContraptionEntityRenderer<OrientedContraptionEntity> {

	public OrientedContraptionEntityRenderer(EntityRenderDispatcher dispatcher, EntityRendererRegistry.Context context) {
		super(dispatcher, context);
	}

	@Override
	public boolean shouldRender(OrientedContraptionEntity entity, Frustum frustum, double x, double y, double z) {
		if (!super.shouldRender(entity, frustum, x, y, z))
			return false;
		if (entity.getContraption().getType() == ContraptionType.MOUNTED && entity.getVehicle() == null)
			return false;
		return true;
	}

}
