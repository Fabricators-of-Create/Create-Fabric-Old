package com.simibubi.create.foundation;

import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.block.render.SpriteShifter;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;

public class ResourceReloadHandler extends SinglePreparationResourceReloadListener<Unit> {
	@Override
	protected Unit prepare(ResourceManager resourceManagerIn, Profiler profilerIn) {
		return Unit.INSTANCE;
	}

	@Override
	protected void apply(Unit $, ResourceManager resourceManagerIn, Profiler profilerIn) {
		SpriteShifter.reloadUVs();
		CreateClient.invalidateRenderers();
	}
}
