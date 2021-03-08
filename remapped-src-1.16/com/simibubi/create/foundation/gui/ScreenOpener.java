package com.simibubi.create.foundation.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class ScreenOpener {

	@Environment(EnvType.CLIENT)
	private static Screen openedGuiNextTick;

	public static void tick() {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			if (openedGuiNextTick != null) {
				MinecraftClient.getInstance().openScreen(openedGuiNextTick);
				openedGuiNextTick = null;
			}
		});
	}

	public static void open(Screen gui) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			openedGuiNextTick = gui;
		});
	}

}
