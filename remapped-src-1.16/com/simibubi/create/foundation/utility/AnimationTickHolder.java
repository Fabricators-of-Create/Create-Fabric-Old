package com.simibubi.create.foundation.utility;

import net.minecraft.client.MinecraftClient;

public class AnimationTickHolder {

	private static int ticks;

	public static void reset() {
		ticks = 0;
	}

	public static void tick() {
		if (!MinecraftClient.getInstance().isPaused()) {
			ticks = (ticks + 1) % 1_728_000; // wrap around every 24 hours so we maintain enough floating point precision
		}
	}

	public static float getRenderTick() {
		return getTicks() + getPartialTicks();
	}

	public static float getPartialTicks() {
		MinecraftClient mc = MinecraftClient.getInstance();
		return (mc.isPaused() ? mc.pausedTickDelta : mc.getTickDelta());
	}

	public static int getTicks() {
		return ticks;
	}
}
