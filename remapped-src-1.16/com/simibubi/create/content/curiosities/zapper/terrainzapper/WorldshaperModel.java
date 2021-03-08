package com.simibubi.create.content.curiosities.zapper.terrainzapper;

import com.simibubi.create.foundation.block.render.CustomRenderedItemModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.BakedModel;

public class WorldshaperModel extends CustomRenderedItemModel {

	public WorldshaperModel(BakedModel template) {
		super(template, "handheld_worldshaper");
		addPartials("core", "core_glow", "accelerator");
	}

	@Override
	public BuiltinModelItemRenderer createRenderer() {
		return new WorldshaperItemRenderer();
	}

}
