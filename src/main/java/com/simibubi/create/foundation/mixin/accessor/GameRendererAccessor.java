package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
	@Invoker("bobView")
	void callBobView(MatrixStack matrixStack, float f);

	@Invoker("bobViewWhenHurt")
	void callBobViewWhenHurt(MatrixStack matrixStack, float f);

	@Accessor("ticks")
	int getTicks();
}
