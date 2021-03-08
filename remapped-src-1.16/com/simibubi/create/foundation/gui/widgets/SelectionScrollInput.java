package com.simibubi.create.foundation.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.simibubi.create.foundation.utility.Lang;

public class SelectionScrollInput extends ScrollInput {

	private final MutableText scrollToSelect = Lang.translate("gui.scrollInput.scrollToSelect");
	protected List<Text> options;

	public SelectionScrollInput(int xIn, int yIn, int widthIn, int heightIn) {
		super(xIn, yIn, widthIn, heightIn);
		options = new ArrayList<>();
	}

	public ScrollInput forOptions(List<Text> options) {
		this.options = options;
		this.max = options.size();
		updateTooltip();
		return this;
	}

	@Override
	protected void writeToLabel() {
		displayLabel.text = options.get(state);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		return super.mouseScrolled(mouseX, mouseY, -delta);
	}

	@Override
	protected void updateTooltip() {
		toolTip.clear();
		toolTip.add(title.copy().formatted(Formatting.BLUE));
		for (int i = min; i < max; i++) {
			if (i == state)
				toolTip.add(LiteralText.EMPTY.copy().append("-> ").append(options.get(i)).formatted(Formatting.WHITE));
			else
				toolTip.add(LiteralText.EMPTY.copy().append("> ").append(options.get(i)).formatted(Formatting.GRAY));
		}
		toolTip.add( LiteralText.EMPTY.copy().append(scrollToSelect).formatted(Formatting.ITALIC, Formatting.DARK_GRAY));
	}

}
