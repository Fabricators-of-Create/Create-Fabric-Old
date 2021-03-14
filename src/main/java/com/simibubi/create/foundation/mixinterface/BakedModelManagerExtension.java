package com.simibubi.create.foundation.mixinterface;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.Identifier;

public interface BakedModelManagerExtension {
	BakedModel getModel(Identifier id);
}
