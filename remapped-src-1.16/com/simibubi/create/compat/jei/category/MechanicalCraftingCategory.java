package com.simibubi.create.compat.jei.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedCrafter;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

public class MechanicalCraftingCategory extends CreateRecipeCategory<CraftingRecipe> {

	private final AnimatedCrafter crafter = new AnimatedCrafter();

	public MechanicalCraftingCategory() {
		super(itemIcon(AllBlocks.MECHANICAL_CRAFTER.get()), emptyBackground(177, 107));
	}

	@Override
	public void setIngredients(CraftingRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getPreviewInputs());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, CraftingRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		DefaultedList<Ingredient> recipeIngredients = recipe.getPreviewInputs();

		itemStacks.init(0, false, 133, 80);
		itemStacks.set(0, recipe.getOutput()
			.getStack());

		int x = getXPadding(recipe);
		int y = getYPadding(recipe);
		float scale = getScale(recipe);
		int size = recipeIngredients.size();
		IIngredientRenderer<ItemStack> renderer = new CrafterIngredientRenderer(recipe);

		for (int i = 0; i < size; i++) {
			float f = 19 * scale;
			int slotSize = (int) (16 * scale);
			int xPosition = (int) (x + 1 + (i % getWidth(recipe)) * f);
			int yPosition = (int) (y + 1 + (i / getWidth(recipe)) * f);
			itemStacks.init(i + 1, true, renderer, xPosition, yPosition, slotSize, slotSize, 0, 0);
			itemStacks.set(i + 1, Arrays.asList(recipeIngredients.get(i)
				.getMatchingStacksClient()));
		}

	}

	static int maxSize = 100;

	public static float getScale(CraftingRecipe recipe) {
		int w = getWidth(recipe);
		int h = getHeight(recipe);
		return Math.min(1, maxSize / (19f * Math.max(w, h)));
	}

	public static int getYPadding(CraftingRecipe recipe) {
		return 3 + 50 - (int) (getScale(recipe) * getHeight(recipe) * 19 * .5);
	}

	public static int getXPadding(CraftingRecipe recipe) {
		return 3 + 50 - (int) (getScale(recipe) * getWidth(recipe) * 19 * .5);
	}

	private static int getWidth(CraftingRecipe recipe) {
		return recipe instanceof ShapedRecipe ? ((ShapedRecipe) recipe).getWidth() : 1;
	}

	private static int getHeight(CraftingRecipe recipe) {
		return recipe instanceof ShapedRecipe ? ((ShapedRecipe) recipe).getHeight() : 1;
	}

	@Override
	public void draw(CraftingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		matrixStack.push();
		float scale = getScale(recipe);
		matrixStack.translate(getXPadding(recipe), getYPadding(recipe), 0);

		for (int row = 0; row < getHeight(recipe); row++)
			for (int col = 0; col < getWidth(recipe); col++)
				if (!recipe.getPreviewInputs()
					.get(row * getWidth(recipe) + col)
					.isEmpty()) {
					matrixStack.push();
					matrixStack.translate(col * 19 * scale, row * 19 * scale, 0);
					matrixStack.scale(scale, scale, scale);
					AllGuiTextures.JEI_SLOT.draw(matrixStack, 0, 0);
					matrixStack.pop();
				}

		matrixStack.pop();

		AllGuiTextures.JEI_SLOT.draw(matrixStack, 133, 80);
		AllGuiTextures.JEI_DOWN_ARROW.draw(matrixStack, 128, 59);
		crafter.draw(matrixStack, 129, 25);

		matrixStack.push();
		matrixStack.translate(0, 0, 300);

		DiffuseLighting.disable();
		int amount = 0;
		for (Ingredient ingredient : recipe.getPreviewInputs()) {
			if (Ingredient.EMPTY == ingredient)
				continue;
			amount++;
		}

		MinecraftClient.getInstance().textRenderer.drawWithShadow(matrixStack, amount + "", 142, 39, 0xFFFFFF);
		matrixStack.pop();
	}

	@Override
	public Class<? extends CraftingRecipe> getRecipeClass() {
		return CraftingRecipe.class;
	}

	private static final class CrafterIngredientRenderer implements IIngredientRenderer<ItemStack> {

		private final CraftingRecipe recipe;

		public CrafterIngredientRenderer(CraftingRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void render(MatrixStack matrixStack, int xPosition, int yPosition, ItemStack ingredient) {
			matrixStack.push();
			matrixStack.translate(xPosition, yPosition, 0);
			float scale = getScale(recipe);
			matrixStack.scale(scale, scale, scale);

			if (ingredient != null) {
				RenderSystem.pushMatrix();
				RenderSystem.multMatrix(matrixStack.peek().getModel());
				RenderSystem.enableDepthTest();
				DiffuseLighting.enable();
				MinecraftClient minecraft = MinecraftClient.getInstance();
				TextRenderer font = getFontRenderer(minecraft, ingredient);
				ItemRenderer itemRenderer = minecraft.getItemRenderer();
				itemRenderer.renderInGuiWithOverrides(null, ingredient, 0, 0);
				itemRenderer.renderGuiItemOverlay(font, ingredient, 0, 0, null);
				RenderSystem.disableBlend();
				DiffuseLighting.disable();
				RenderSystem.popMatrix();
			}

			matrixStack.pop();
		}

		@Override
		public List<Text> getTooltip(ItemStack ingredient, TooltipContext tooltipFlag) {
			MinecraftClient minecraft = MinecraftClient.getInstance();
			PlayerEntity player = minecraft.player;
			try {
				return ingredient.getTooltip(player, tooltipFlag);
			} catch (RuntimeException | LinkageError e) {
				List<Text> list = new ArrayList<>();
				TranslatableText crash = new TranslatableText("jei.tooltip.error.crash");
				list.add(crash.formatted(Formatting.RED));
				return list;
			}
		}
	}

}
