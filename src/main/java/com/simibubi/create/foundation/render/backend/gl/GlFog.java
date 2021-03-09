package com.simibubi.create.foundation.render.backend.gl;

import com.simibubi.create.foundation.mixin.accessor.GlStateManagerAccessor;
import org.lwjgl.opengl.GL11;

public class GlFog {
	public static float[] FOG_COLOR = new float[]{0, 0, 0, 0};

	public static boolean fogEnabled() {
		return true; // GlStateManagerAccessor.getFog().capState.state; TODO PLS FIX FOG
	}

	public static int getFogModeGlEnum() {
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

	public static GlFogMode getFogMode() {
		if (!fogEnabled()) {
			return GlFogMode.NONE;
		}

		int mode = getFogModeGlEnum();

		switch (mode) {
			case GL11.GL_EXP2:
			case GL11.GL_EXP:
				return GlFogMode.EXP2;
			case GL11.GL_LINEAR:
				return GlFogMode.LINEAR;
			default:
				throw new UnsupportedOperationException("Unknown fog mode: " + mode);
		}
	}
}
