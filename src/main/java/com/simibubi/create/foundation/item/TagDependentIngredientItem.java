package com.simibubi.create.foundation.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class TagDependentIngredientItem extends Item {

	private Identifier tag;

	public TagDependentIngredientItem(Settings p_i48487_1_, Identifier tag) {
		super(p_i48487_1_);
		this.tag = tag;
	}

	@Override
	public void appendStacks(ItemGroup p_150895_1_, DefaultedList<ItemStack> p_150895_2_) {
		if (!shouldHide())
			super.appendStacks(p_150895_1_, p_150895_2_);
	}

	public boolean shouldHide() {
		Tag<?> tag = ItemTags.getTagGroup()
			.getTag(this.tag);
		return tag == null || tag.values()
			.isEmpty();
	}

}
