package com.smellypengu.createfabric.foundation.render.backend.instancing;

import com.smellypengu.createfabric.foundation.render.backend.light.ILightListener;

public interface InstanceRendered extends ILightListener {
    default boolean shouldRenderAsBe() {
        return false;
    }
}
