package com.smellypengu.createfabric.content.contraptions.components.actors;

import com.smellypengu.createfabric.foundation.render.backend.gl.attrib.VertexFormat;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedModel;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedTileRenderer;
import net.minecraft.client.render.BufferBuilder;

public class RotatingActorModel extends InstancedModel<ContraptionActorData> {
    public RotatingActorModel(InstancedTileRenderer<?> renderer, BufferBuilder buf) {
        super(renderer, buf);
    }

    @Override
    protected VertexFormat getInstanceFormat() {
        return ContraptionActorData.FORMAT;
    }

    @Override
    protected ContraptionActorData newInstance() {
        return new ContraptionActorData(this);
    }
}
