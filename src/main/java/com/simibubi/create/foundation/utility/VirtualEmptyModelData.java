package com.simibubi.create.foundation.utility;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

/**
 * This model data instance is passed whenever a model is rendered without
 * available in-world context. IBakedModel#getModelData can react accordingly
 * and avoid looking for model data itself
 **/
public enum VirtualEmptyModelData implements RenderAttachmentBlockEntity {

	INSTANCE;

	@Override
	public @Nullable Object getRenderAttachmentData() {
		return null;
	}
}
