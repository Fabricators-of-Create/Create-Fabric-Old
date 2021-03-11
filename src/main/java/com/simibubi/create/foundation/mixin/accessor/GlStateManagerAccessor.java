package com.simibubi.create.foundation.mixin.accessor;

import com.mojang.blaze3d.platform.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GlStateManager.class)
public interface GlStateManagerAccessor {
	/*@Accessor("FOG")
	static GlStateManager.FogState create$FOG() {
		throw new AssertionError("Mixin didn't merge, very funni");
	}*/
}
