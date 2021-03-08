package com.simibubi.create.compat.jei.category;

import static com.simibubi.create.foundation.gui.AllGuiTextures.BLOCKZAPPER_UPGRADE_RECIPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.ScreenResourceWrapper;
import com.simibubi.create.content.curiosities.zapper.blockzapper.BlockzapperUpgradeRecipe;
import com.simibubi.create.foundation.gui.GuiGameElement;
import com.simibubi.create.foundation.utility.Lang;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

public class BlockzapperUpgradeCategory extends CreateRecipeCategory<BlockzapperUpgradeRecipe> {

	public BlockzapperUpgradeCategory() {
		super(itemIcon(AllItems.BLOCKZAPPER.get()), new ScreenResourceWrapper(BLOCKZAPPER_UPGRADE_RECIPE));
	}

	@Override
	public Class<? extends BlockzapperUpgradeRecipe> getRecipeClass() {
		return BlockzapperUpgradeRecipe.class;
	}

	@Override
	public void setIngredients(BlockzapperUpgradeRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getPreviewInputs());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BlockzapperUpgradeRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		ShapedRecipe shape = recipe.getRecipe();
		DefaultedList<Ingredient> shapedIngredients = shape.getPreviewInputs();

		int top = 0;
		int left = 0;

		int i = 0;
		for (int y = 0; y < shape.getRecipeHeight(); y++) {
			for (int x = 0; x < shape.getRecipeWidth(); x++) {
				itemStacks.init(i, true, left + x * 18, top + y * 18);
				itemStacks.set(i, Arrays.asList(shapedIngredients.get(i)
					.getMatchingStacksClient()));
				i++;
			}
		}
	}

	@Override
	public List<Text> getTooltipStrings(BlockzapperUpgradeRecipe recipe, double mouseX, double mouseY) {
		List<Text> list = new ArrayList<>();
		if (mouseX < 91 || mouseX > 91 + 52 || mouseY < 1 || mouseY > 53)
			return list;
		list.addAll(recipe.getOutput()
			.getTooltip(MinecraftClient.getInstance().player,
				MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipContext.Default.ADVANCED
					: TooltipContext.Default.NORMAL));
		return list;
	}

	@Override
	public void draw(BlockzapperUpgradeRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		TextRenderer font = MinecraftClient.getInstance().textRenderer;

		MutableText textComponent =
				new LiteralText("+ ")
				.append(Lang.translate("blockzapper.component." + Lang.asId(recipe.getUpgradedComponent().name())))
				.formatted(recipe.getTier().color);

		font.drawWithShadow(matrixStack, textComponent, (BLOCKZAPPER_UPGRADE_RECIPE.width - font.getWidth(textComponent.getString())) / 2f, 57, 0x8B8B8B);

		GuiGameElement.of(recipe.getOutput())
				.at(90, 55)
				.scale(3.5)
				.render(matrixStack);
	}
}