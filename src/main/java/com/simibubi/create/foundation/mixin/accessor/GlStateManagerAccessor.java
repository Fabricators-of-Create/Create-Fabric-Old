package com.simibubi.create.foundation.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.platform.GlStateManager;

@Mixin(GlStateManager.class)
public interface GlStateManagerAccessor {
	@Accessor("FOG")
	static GlStateManager.FogState create$FOG() {
		throw new AssertionError("Mixin didn't merge, very funni");
	}
}
