package com.simibubi.create.content.schematics.client;

import java.util.Collections;
import java.util.List;
import com.simibubi.create.AllItems;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.GuiGameElement;
import com.simibubi.create.foundation.gui.widgets.IconButton;
import com.simibubi.create.foundation.gui.widgets.Label;
import com.simibubi.create.foundation.gui.widgets.ScrollInput;
import com.simibubi.create.foundation.gui.widgets.SelectionScrollInput;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class SchematicEditScreen extends AbstractSimiScreen {

	private TextFieldWidget xInput;
	private TextFieldWidget yInput;
	private TextFieldWidget zInput;
	private IconButton confirmButton;

	private final List<Text> rotationOptions =
		Lang.translatedOptions("schematic.rotation", "none", "cw90", "cw180", "cw270");
	private final List<Text> mirrorOptions =
		Lang.translatedOptions("schematic.mirror", "none", "leftRight", "frontBack");
	private final Text rotationLabel = Lang.translate("schematic.rotation");
	private final Text mirrorLabel = Lang.translate("schematic.mirror");

	private ScrollInput rotationArea;
	private ScrollInput mirrorArea;
	private SchematicHandler handler;

	@Override
	protected void init() {
		AllGuiTextures background = AllGuiTextures.SCHEMATIC;
		setWindowSize(background.width + 50, background.height);
		int x = guiLeft;
		int y = guiTop;
		handler = CreateClient.schematicHandler;

		xInput = new TextFieldWidget(textRenderer, x + 50, y + 26, 34, 10, LiteralText.EMPTY);
		yInput = new TextFieldWidget(textRenderer, x + 90, y + 26, 34, 10, LiteralText.EMPTY);
		zInput = new TextFieldWidget(textRenderer, x + 130, y + 26, 34, 10, LiteralText.EMPTY);

		BlockPos anchor = handler.getTransformation()
			.getAnchor();
		if (handler.isDeployed()) {
			xInput.setText("" + anchor.getX());
			yInput.setText("" + anchor.getY());
			zInput.setText("" + anchor.getZ());
		} else {
			BlockPos alt = client.player.getBlockPos();
			xInput.setText("" + alt.getX());
			yInput.setText("" + alt.getY());
			zInput.setText("" + alt.getZ());
		}

		for (TextFieldWidget widget : new TextFieldWidget[] { xInput, yInput, zInput }) {
			widget.setMaxLength(6);
			widget.setDrawsBackground(false);
			widget.setEditableColor(0xFFFFFF);
			widget.changeFocus(false);
			widget.mouseClicked(0, 0, 0);
			widget.setTextPredicate(s -> {
				if (s.isEmpty() || s.equals("-"))
					return true;
				try {
					Integer.parseInt(s);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			});
		}

		StructurePlacementData settings = handler.getTransformation()
			.toSettings();
		Label labelR = new Label(x + 50, y + 48, LiteralText.EMPTY).withShadow();
		rotationArea = new SelectionScrollInput(x + 45, y + 43, 118, 18).forOptions(rotationOptions)
			.titled(rotationLabel.copy())
			.setState(settings.getRotation()
				.ordinal())
			.writingTo(labelR);

		Label labelM = new Label(x + 50, y + 70, LiteralText.EMPTY).withShadow();
		mirrorArea = new SelectionScrollInput(x + 45, y + 65, 118, 18).forOptions(mirrorOptions)
			.titled(mirrorLabel.copy())
			.setState(settings.getMirror()
				.ordinal())
			.writingTo(labelM);

		Collections.addAll(widgets, xInput, yInput, zInput);
		Collections.addAll(widgets, labelR, labelM, rotationArea, mirrorArea);

		confirmButton =
			new IconButton(guiLeft + background.width - 33, guiTop + background.height - 24, AllIcons.I_CONFIRM);
		widgets.add(confirmButton);

		super.init();
	}

	@Override
	public boolean keyPressed(int code, int p_keyPressed_2_, int p_keyPressed_3_) {

		if (isPaste(code)) {
			String coords = client.keyboard.getClipboard();
			if (coords != null && !coords.isEmpty()) {
				coords.replaceAll(" ", "");
				String[] split = coords.split(",");
				if (split.length == 3) {
					boolean valid = true;
					for (String s : split) {
						try {
							Integer.parseInt(s);
						} catch (NumberFormatException e) {
							valid = false;
						}
					}
					if (valid) {
						xInput.setText(split[0]);
						yInput.setText(split[1]);
						zInput.setText(split[2]);
						return true;
					}
				}
			}
		}

		return super.keyPressed(code, p_keyPressed_2_, p_keyPressed_3_);
	}

	@Override
	protected void renderWindow(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		int x = guiLeft;
		int y = guiTop;
		AllGuiTextures.SCHEMATIC.draw(matrixStack, this, x, y);
		textRenderer.drawWithShadow(matrixStack, handler.getCurrentSchematicName(),
			x + 93 - textRenderer.getWidth(handler.getCurrentSchematicName()) / 2, y + 3, 0xffffff);

		matrixStack.push();
		matrixStack.translate(guiLeft + 200, guiTop + 130, 0);
		matrixStack.scale(3, 3, 3);
		GuiGameElement.of(AllItems.SCHEMATIC.asStack())
			.render(matrixStack);
		matrixStack.pop();
	}

	@Override
	public void removed() {
		boolean validCoords = true;
		BlockPos newLocation = null;
		try {
			newLocation = new BlockPos(Integer.parseInt(xInput.getText()), Integer.parseInt(yInput.getText()),
				Integer.parseInt(zInput.getText()));
		} catch (NumberFormatException e) {
			validCoords = false;
		}

		StructurePlacementData settings = new StructurePlacementData();
		settings.setRotation(BlockRotation.values()[rotationArea.getState()]);
		settings.setMirror(BlockMirror.values()[mirrorArea.getState()]);

		if (validCoords && newLocation != null) {
			ItemStack item = handler.getActiveSchematicItem();
			if (item != null) {
				item.getTag()
					.putBoolean("Deployed", true);
				item.getTag()
					.put("Anchor", NbtHelper.fromBlockPos(newLocation));
			}

			handler.getTransformation()
				.init(newLocation, settings, handler.getBounds());
			handler.markDirty();
			handler.deploy();
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if (confirmButton.isHovered()) {
			onClose();
			return true;
		}

		return super.mouseClicked(x, y, button);
	}

}
