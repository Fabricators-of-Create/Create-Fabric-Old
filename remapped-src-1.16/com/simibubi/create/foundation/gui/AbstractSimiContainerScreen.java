package com.simibubi.create.foundation.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.foundation.gui.widgets.AbstractSimiWidget;

import mcp.MethodsReturnNonnullByDefault;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.Rect2i;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Environment(EnvType.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractSimiContainerScreen<T extends ScreenHandler> extends HandledScreen<T> {

	protected List<AbstractButtonWidget> widgets;

	public AbstractSimiContainerScreen(T container, PlayerInventory inv, Text title) {
		super(container, inv, title);
		widgets = new ArrayList<>();
	}

	protected void setWindowSize(int width, int height) {
		this.backgroundWidth = width;
		this.backgroundHeight = height;
	}

	@Override
	protected void drawForeground(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrixStack);
		renderWindow(matrixStack, mouseX, mouseY, partialTicks);
		
		for (AbstractButtonWidget widget : widgets)
			widget.render(matrixStack, mouseX, mouseY, partialTicks);
		
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		
		RenderSystem.enableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.disableRescaleNormal();
		DiffuseLighting.disable();
		RenderSystem.disableLighting();
		RenderSystem.disableDepthTest();
		renderWindowForeground(matrixStack, mouseX, mouseY, partialTicks);
		for (AbstractButtonWidget widget : widgets)
			widget.renderToolTip(matrixStack, mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		boolean result = false;
		for (AbstractButtonWidget widget : widgets) {
			if (widget.mouseClicked(x, y, button))
				result = true;
		}
		return result || super.mouseClicked(x, y, button);
	}

	@Override
	public boolean keyPressed(int code, int p_keyPressed_2_, int p_keyPressed_3_) {
		for (AbstractButtonWidget widget : widgets) {
			if (widget.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_))
				return true;
		}
		return super.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	public boolean charTyped(char character, int code) {
		for (AbstractButtonWidget widget : widgets) {
			if (widget.charTyped(character, code))
				return true;
		}
		if (character == 'e')
			onClose();
		return super.charTyped(character, code);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		for (AbstractButtonWidget widget : widgets) {
			if (widget.mouseScrolled(mouseX, mouseY, delta))
				return true;
		}
		return super.mouseScrolled(mouseX, mouseY, delta);
	}
	
	@Override
	public boolean mouseReleased(double x, double y, int button) {
		boolean result = false;
		for (AbstractButtonWidget widget : widgets) {
			if (widget.mouseReleased(x, y, button))
				result = true;
		}
		return result | super.mouseReleased(x, y, button);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	protected abstract void renderWindow(MatrixStack ms, int mouseX, int mouseY, float partialTicks);

	@Override
	protected void drawBackground(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {

	}

	protected void renderWindowForeground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		drawMouseoverTooltip(matrixStack, mouseX, mouseY);
		for (AbstractButtonWidget widget : widgets) {
			if (!widget.isHovered())
				continue;

			if (widget instanceof AbstractSimiWidget && !((AbstractSimiWidget) widget).getToolTip().isEmpty()) {
				renderTooltip(matrixStack, ((AbstractSimiWidget) widget).getToolTip(), mouseX, mouseY);
			}
		}
	}
	
	protected void renderItemOverlayIntoGUI(MatrixStack matrixStack, TextRenderer fr, ItemStack stack, int xPosition, int yPosition,
			@Nullable String text, int textColor) {
		if (!stack.isEmpty()) {
			if (stack.getItem().showDurabilityBar(stack)) {
				RenderSystem.disableLighting();
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				RenderSystem.disableAlphaTest();
				RenderSystem.disableBlend();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				double health = stack.getItem().getDurabilityForDisplay(stack);
				int i = Math.round(13.0F - (float) health * 13.0F);
				int j = stack.getItem().getRGBDurabilityForDisplay(stack);
				this.draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
				this.draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255,
						255);
				RenderSystem.enableBlend();
				RenderSystem.enableAlphaTest();
				RenderSystem.enableTexture();
				RenderSystem.enableLighting();
				RenderSystem.enableDepthTest();
			}

			if (stack.getCount() != 1 || text != null) {
				String s = text == null ? String.valueOf(stack.getCount()) : text;
				RenderSystem.disableLighting();
				RenderSystem.disableDepthTest();
				RenderSystem.disableBlend();
				matrixStack.push();

				int guiScaleFactor = (int) client.getWindow().getScaleFactor();
				matrixStack.translate(xPosition + 16.5f, yPosition + 16.5f, 0);
				double scale = getItemCountTextScale();

				matrixStack.scale((float) scale, (float) scale, 0);
				matrixStack.translate(-fr.getWidth(s) - (guiScaleFactor > 1 ? 0 : -.5f),
						-textRenderer.fontHeight + (guiScaleFactor > 1 ? 1 : 1.75f), 0);
				fr.drawWithShadow(matrixStack, s, 0, 0, textColor);

				matrixStack.pop();
				RenderSystem.enableBlend();
				RenderSystem.enableLighting();
				RenderSystem.enableDepthTest();
				RenderSystem.enableBlend();
			}
		}
	}

	public double getItemCountTextScale() {
		int guiScaleFactor = (int) client.getWindow().getScaleFactor();
		double scale = 1;
		switch (guiScaleFactor) {
		case 1:
			scale = 2060 / 2048d;
			break;
		case 2:
			scale = .5;
			break;
		case 3:
			scale = .675;
			break;
		case 4:
			scale = .75;
			break;
		default:
			scale = ((float) guiScaleFactor - 1) / guiScaleFactor;
		}
		return scale;
	}

	private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue,
			int alpha) {
		renderer.begin(7, VertexFormats.POSITION_COLOR);
		renderer.vertex((double) (x + 0), (double) (y + 0), 0.0D).color(red, green, blue, alpha).next();
		renderer.vertex((double) (x + 0), (double) (y + height), 0.0D).color(red, green, blue, alpha).next();
		renderer.vertex((double) (x + width), (double) (y + height), 0.0D).color(red, green, blue, alpha).next();
		renderer.vertex((double) (x + width), (double) (y + 0), 0.0D).color(red, green, blue, alpha).next();
		Tessellator.getInstance().draw();
	}

	/**
	 * Used for moving JEI out of the way of extra things like Flexcrate renders
	 *
	 * @return the space that the gui takes up besides the normal rectangle defined by {@link HandledScreen}.
	 */
	public List<Rect2i> getExtraAreas() {
		return Collections.emptyList();
	}
}
