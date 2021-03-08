package com.simibubi.create.content.contraptions.fluids.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

public class FluidTransferRecipes {

	public static List<ItemStack> POTION_ITEMS = new ArrayList<>();
	public static List<Item> FILLED_BUCKETS = new ArrayList<>();

	
	
	public static final SinglePreparationResourceReloadListener<Object> LISTENER = new SinglePreparationResourceReloadListener<Object>() {

		@Override
		protected Object prepare(ResourceManager p_212854_1_, Profiler p_212854_2_) {
			return new Object();
		}

		@Override
		protected void apply(Object p_212853_1_, ResourceManager p_212853_2_, Profiler p_212853_3_) {
			POTION_ITEMS.clear();
			FILLED_BUCKETS.clear();
		}

	};
}
