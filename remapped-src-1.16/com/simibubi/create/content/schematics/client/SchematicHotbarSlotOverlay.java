package com.simibubi.create.content.schematics.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

public class SchematicHotbarSlotOverlay extends DrawableHelper {
	
	public void renderOn(MatrixStack matrixStack, int slot) {
		Window mainWindow = MinecraftClient.getInstance().getWindow();
		int x = mainWindow.getScaledWidth() / 2 - 88;
		int y = mainWindow.getScaledHeight() - 19;
		RenderSystem.enableAlphaTest();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		AllGuiTextures.SCHEMATIC_SLOT.draw(matrixStack, this, x + 20 * slot, y);
		RenderSystem.disableAlphaTest();
	}

}
