package com.smellypengu.createfabric.foundation.render.backend.instancing;

import com.smellypengu.createfabric.foundation.render.backend.light.ILightListener;

public interface IInstanceRendered extends ILightListener {
    default boolean shouldRenderAsTE() {
        return false;
    }
}
