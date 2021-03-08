package com.simibubi.create.compat.jei;

import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.gui.drawable.IDrawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class ScreenResourceWrapper implements IDrawable {

	private AllGuiTextures resource;

	public ScreenResourceWrapper(AllGuiTextures resource) {
		this.resource = resource;
	}

	@Override
	public int getWidth() {
		return resource.width;
	}

	@Override
	public int getHeight() {
		return resource.height;
	}

	@Override
	public void draw(MatrixStack matrixStack, int xOffset, int yOffset) {
		resource.bind();
		DrawableHelper.drawTexture(matrixStack, xOffset, yOffset, 0, resource.startX, resource.startY, resource.width, resource.height, 256,
				256);
	}

}
