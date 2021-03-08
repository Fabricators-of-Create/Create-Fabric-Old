package com.simibubi.create.foundation.render.backend.gl;

import com.mojang.blaze3d.platform.GlStateManager;

public class GlFog {
    public static float[] FOG_COLOR = new float[] {0, 0, 0, 0};

    public static boolean fogEnabled() {
        return GlStateManager.FOG.capState.state;
    }

    public static int getFogMode() {
        return GlStateManager.FOG.mode;
    }

    public static float getFogDensity() {
        return GlStateManager.FOG.density;
    }

    public static float getFogEnd() {
        return GlStateManager.FOG.end;
    }

    public static float getFogStart() {
        return GlStateManager.FOG.start;
    }
}
