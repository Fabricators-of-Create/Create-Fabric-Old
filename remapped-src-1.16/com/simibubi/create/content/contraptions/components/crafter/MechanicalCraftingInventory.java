package com.simibubi.create.content.contraptions.components.crafter;

import org.apache.commons.lang3.tuple.Pair;

import com.simibubi.create.content.contraptions.components.crafter.RecipeGridHandler.GroupedItems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class MechanicalCraftingInventory extends CraftingInventory {

	private static ScreenHandler dummyContainer = new ScreenHandler(null, -1) {
		public boolean canUse(PlayerEntity playerIn) {
			return false;
		}
	};

	public MechanicalCraftingInventory(GroupedItems items) {
		super(dummyContainer, items.width, items.height);
		for (int y = 0; y < items.height; y++) {
			for (int x = 0; x < items.width; x++) {
				ItemStack stack = items.grid.get(Pair.of(x + items.minX, y + items.minY));
				setStack(x + (items.height - y - 1) * items.width,
						stack == null ? ItemStack.EMPTY : stack.copy());
			}
		}
	}

}
