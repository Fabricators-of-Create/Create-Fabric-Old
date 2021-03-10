package com.simibubi.create.foundation.mixin;

import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3d.class)
public abstract class Vec3dMixin {
	@Shadow
	public abstract Vec3d multiply(double mult);

	// They are client-only, but not anymore!

	@Intrinsic
	public Vec3d negate() {
		return multiply(-1.0D);
	}

	@Intrinsic
	public Vec3d method_22882() {
		return negate();
	}
}
