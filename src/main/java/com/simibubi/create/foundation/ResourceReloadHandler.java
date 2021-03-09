package com.simibubi.create.foundation;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.block.render.SpriteShifter;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

public class ResourceReloadHandler extends SinglePreparationResourceReloadListener<Object> {

	@Override
	protected Object prepare(ResourceManager resourceManagerIn, Profiler profilerIn) {
		return new Object();
	}

	@Override
	protected void apply(Object $, ResourceManager resourceManagerIn, Profiler profilerIn) {
		SpriteShifter.reloadUVs();
		CreateClient.invalidateRenderers();
	}

}
