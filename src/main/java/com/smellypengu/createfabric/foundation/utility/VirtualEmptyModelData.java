package com.smellypengu.createfabric.foundation.utility;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import org.jetbrains.annotations.Nullable;

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
