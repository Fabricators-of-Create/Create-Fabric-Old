package com.simibubi.create.compat.jei.category;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.gui.GuiGameElement;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;

public class FanBlastingCategory extends ProcessingViaFanCategory<AbstractCookingRecipe> {

	public FanBlastingCategory() {
		super(doubleItemIcon(AllItems.PROPELLER.get(), Items.LAVA_BUCKET));
	}

	@Override
	public Class<? extends AbstractCookingRecipe> getRecipeClass() {
		return AbstractCookingRecipe.class;
	}

	@Override
	public void renderAttachedBlock(MatrixStack matrixStack) {
		matrixStack.push();

		GuiGameElement.of(Fluids.LAVA)
			.scale(24)
			.atLocal(0, 0, 2)
			.render(matrixStack);

		matrixStack.pop();
	}

}
