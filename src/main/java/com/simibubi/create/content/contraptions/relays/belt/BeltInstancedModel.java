package com.simibubi.create.content.contraptions.relays.belt;

import com.simibubi.create.foundation.render.backend.gl.attrib.VertexFormat;
import com.simibubi.create.foundation.render.backend.instancing.InstancedBlockRenderer;
import com.simibubi.create.foundation.render.backend.instancing.InstancedModel;

import net.minecraft.client.render.BufferBuilder;

public class BeltInstancedModel extends InstancedModel<BeltData> {
	public BeltInstancedModel(InstancedBlockRenderer<?> renderer, BufferBuilder buf) {
		super(renderer, buf);
	}

	@Override
	protected BeltData newInstance() {
		return new BeltData(this);
	}

	@Override
	protected VertexFormat getInstanceFormat() {
		return BeltData.FORMAT;
	}

}
