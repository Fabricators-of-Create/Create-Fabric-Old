package com.smellypengu.createfabric.content.contraptions.relays.belt;

import com.smellypengu.createfabric.foundation.render.backend.gl.attrib.VertexFormat;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedModel;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedTileRenderer;
import net.minecraft.client.render.BufferBuilder;

public class BeltInstancedModel extends InstancedModel<BeltData> {
    public BeltInstancedModel(InstancedTileRenderer<?> renderer, BufferBuilder buf) {
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
