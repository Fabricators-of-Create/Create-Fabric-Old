package com.smellypengu.createfabric.foundation.render.backend.gl;

import com.smellypengu.createfabric.foundation.mixins.GlStateManagerAccessor;

public class GlFog {
    public static float[] FOG_COLOR = new float[] {0, 0, 0, 0};

    public static boolean fogEnabled() {
        return true; // GlStateManagerAccessor.getFog().capState.state; TODO PLS FIX FOG
    }

    public static int getFogMode() {
        return GlStateManagerAccessor.getFog().mode;
    }

    public static float getFogDensity() {
        return GlStateManagerAccessor.getFog().density;
    }

    public static float getFogEnd() {
        return GlStateManagerAccessor.getFog().end;
    }

    public static float getFogStart() {
        return GlStateManagerAccessor.getFog().start;
    }
}
