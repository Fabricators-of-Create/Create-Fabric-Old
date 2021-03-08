package com.simibubi.create.foundation.item;

import java.util.Collection;
import java.util.EnumSet;
import java.util.stream.Collectors;

import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class CreateItemGroupBase extends ItemGroup {

	public CreateItemGroupBase(String id) {
		super(getGroupCountSafe(), Create.ID + "." + id);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendStacks(DefaultedList<ItemStack> items) {
		addItems(items, true);
		addBlocks(items);
		addItems(items, false);
	}

	@Environment(EnvType.CLIENT)
	public void addBlocks(DefaultedList<ItemStack> items) {
		for (RegistryEntry<? extends Block> entry : getBlocks()) {
			Block def = entry.get();
			Item item = def.asItem();
			if (item != Items.AIR)
				def.addStacksForDisplay(this, items);
		}
	}
	
	@Environment(EnvType.CLIENT)
	public void addItems(DefaultedList<ItemStack> items, boolean specialItems) {
		MinecraftClient mc = MinecraftClient.getInstance();
		ItemRenderer itemRenderer = mc.getItemRenderer();
		ClientWorld world = mc.world;
		
		for (RegistryEntry<? extends Item> entry : getItems()) {
			Item item = entry.get();
			if (item instanceof BlockItem)
				continue;
			ItemStack stack = new ItemStack(item);
			BakedModel model = itemRenderer.getHeldItemModel(stack, world, null);
			if ((model.hasDepth() && AllSections.of(stack) != AllSections.CURIOSITIES) != specialItems)
				continue;
			item.appendStacks(this, items);
		}
	}

	protected Collection<RegistryEntry<Block>> getBlocks() {
		return getSections().stream()
			.flatMap(s -> Create.registrate()
				.getAll(s, Block.class)
				.stream())
			.collect(Collectors.toList());
	}

	protected Collection<RegistryEntry<Item>> getItems() {
		return getSections().stream()
			.flatMap(s -> Create.registrate()
				.getAll(s, Item.class)
				.stream())
			.collect(Collectors.toList());
	}

	protected EnumSet<AllSections> getSections() {
		return EnumSet.allOf(AllSections.class);
	}
}
