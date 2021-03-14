package com.simibubi.create.content.contraptions.goggles;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.GuiGameElement;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GoggleConfigScreen extends AbstractSimiScreen {

	private final List<String> tooltip;
	private int offsetX;
	private int offsetY;

	public GoggleConfigScreen() {
		String spacing = "    ";
		tooltip = new ArrayList<>();
		tooltip.add(spacing + Lang.translate("gui.config.overlay1"));
		tooltip.add(spacing + Formatting.GRAY + Lang.translate("gui.config.overlay2"));
		tooltip.add("");
		tooltip.add(spacing + Lang.translate("gui.config.overlay3"));
		tooltip.add(spacing + Lang.translate("gui.config.overlay4"));
		tooltip.add("");
		tooltip.add(spacing + Formatting.GRAY + Lang.translate("gui.config.overlay5"));
		tooltip.add(spacing + Formatting.GRAY + Lang.translate("gui.config.overlay6"));
		tooltip.add("");
		tooltip.add(spacing + Lang.translate("gui.config.overlay7"));
		tooltip.add(spacing + Lang.translate("gui.config.overlay8"));
	}

	@Override
	protected void init() {
		MinecraftClient mc = MinecraftClient.getInstance();
		this.width = mc.getWindow().getScaledWidth();
		this.height = mc.getWindow().getScaledHeight();

		offsetX = 0;/**AllConfigs.CLIENT.overlayOffsetX.get(); TODO CONFIG*/
		offsetY = 0; /**AllConfigs.CLIENT.overlayOffsetY.get(); TODO CONFIG*/
	}

	@Override
	public void removed() {
		/**AllConfigs.CLIENT.overlayOffsetX.set(offsetX); TODO CONFIG
		 AllConfigs.CLIENT.overlayOffsetY.set(offsetY); TODO CONFIG */
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		updateOffset(x, y);

		return true;
	}

	@Override
	public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
		updateOffset(p_mouseDragged_1_, p_mouseDragged_3_);

		return true;
	}

	private void updateOffset(double windowX, double windowY) {
		offsetX = (int) (windowX - (this.width / 2));
		offsetY = (int) (windowY - (this.height / 2));
	}

	@Override
	public void renderWindow(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.pushMatrix();
		int posX = this.width / 2 + offsetX;
		int posY = this.height / 2 + offsetY;
		//tooltipScreen.renderTooltip(tooltip, tooltipScreen.width / 2, tooltipScreen.height / 2);
		renderTooltip(matrices, (Text) tooltip, posX, posY);

		ItemStack item = AllItems.GOGGLES.asItem().getDefaultStack();
		//GuiGameElement.of(item).at(tooltipScreen.width / 2 + 10, tooltipScreen.height / 2 - 16).render();
		GuiGameElement.of(item).at(posX + 10, posY - 16).render();
		RenderSystem.popMatrix();
	}
}
