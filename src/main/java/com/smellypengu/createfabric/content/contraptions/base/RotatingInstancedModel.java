package com.smellypengu.createfabric.content.contraptions.base;

import com.smellypengu.createfabric.foundation.render.backend.gl.attrib.VertexFormat;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedModel;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedTileRenderer;
import net.minecraft.client.render.BufferBuilder;

public class RotatingInstancedModel extends InstancedModel<RotatingData> {
    public RotatingInstancedModel(InstancedTileRenderer<?> renderer, BufferBuilder buf) {
        super(renderer, buf);
    }

    @Override
    protected RotatingData newInstance() {
        return new RotatingData(this);
    }

    @Override
    protected VertexFormat getInstanceFormat() {
        return RotatingData.FORMAT;
    }

}
