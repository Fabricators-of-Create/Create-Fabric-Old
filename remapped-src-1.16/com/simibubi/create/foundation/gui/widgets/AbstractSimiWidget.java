package com.simibubi.create.foundation.gui.widgets;

import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public abstract class AbstractSimiWidget extends AbstractButtonWidget {

	protected List<Text> toolTip;
	
	public AbstractSimiWidget(int xIn, int yIn, int widthIn, int heightIn) {
		super(xIn, yIn, widthIn, heightIn, LiteralText.EMPTY);
		toolTip = new LinkedList<>();
	}
	
	public List<Text> getToolTip() {
		return toolTip;
	}
	
	@Override
	public void renderButton(MatrixStack matrixStack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
	}

}
