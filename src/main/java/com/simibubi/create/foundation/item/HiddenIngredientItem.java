package com.simibubi.create.foundation.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class HiddenIngredientItem extends Item {

	public HiddenIngredientItem(Settings p_i48487_1_) {
		super(p_i48487_1_);
	}
	
	@Override
	public void appendStacks(ItemGroup p_150895_1_, DefaultedList<ItemStack> p_150895_2_) {
		if (p_150895_1_ != ItemGroup.SEARCH)
			return;
		super.appendStacks(p_150895_1_, p_150895_2_);
	}

}
