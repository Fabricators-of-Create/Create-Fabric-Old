package com.simibubi.create.foundation.render.backend.instancing;

import com.simibubi.create.foundation.render.backend.light.ILightListener;

public interface InstanceRendered extends ILightListener {
    default boolean shouldRenderAsBe() {
        return false;
    }
}
