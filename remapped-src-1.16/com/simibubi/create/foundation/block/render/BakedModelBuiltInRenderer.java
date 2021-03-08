package com.simibubi.create.foundation.block.render;

import net.minecraft.client.render.model.BakedModel;

public class BakedModelBuiltInRenderer extends WrappedBakedModel {

	public BakedModelBuiltInRenderer(BakedModel template) {
		super(template);
	}
	
	@Override
	public boolean isBuiltin() {
		return true;
	}

}
