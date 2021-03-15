package com.simibubi.create.foundation.render.backend.gl;

import org.lwjgl.opengl.GL11;

import com.simibubi.create.foundation.mixin.accessor.GlStateManagerAccessor;

public class GlFog {
	public static float[] FOG_COLOR = new float[]{0, 0, 0, 0};

	public static boolean fogEnabled() {
		return true; // GlStateManagerAccessor.getFog().capState.state; TODO PLS FIX FOG
	}

	public static int getFogModeGlEnum() {
		return GlStateManagerAccessor.create$FOG().mode;
	}

	public static float getFogDensity() {
		return GlStateManagerAccessor.create$FOG().density;
	}

	public static float getFogEnd() {
		return GlStateManagerAccessor.create$FOG().end;
	}

	public static float getFogStart() {
		return GlStateManagerAccessor.create$FOG().start;
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
