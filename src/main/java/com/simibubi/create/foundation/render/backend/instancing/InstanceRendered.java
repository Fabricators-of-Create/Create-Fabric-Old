package com.simibubi.create.foundation.render.backend.instancing;

import com.simibubi.create.foundation.render.backend.light.LightListener;

public interface InstanceRendered extends LightListener {
    default boolean shouldRenderAsBe() {
        return false;
    }
}
