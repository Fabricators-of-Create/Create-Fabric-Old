package com.simibubi.create.content.schematics;

import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemRequirement {

	public static ItemRequirement INVALID = new ItemRequirement();
	public static ItemRequirement NONE = new ItemRequirement();
	ItemUseType usage;
	List<ItemStack> requiredItems;

	private ItemRequirement() {
	}

	public ItemRequirement(ItemUseType usage, Item item) {
		this(usage, Arrays.asList(new ItemStack(item)));
	}

	public ItemRequirement(ItemUseType usage, List<ItemStack> requiredItems) {
		this.usage = usage;
		this.requiredItems = requiredItems;
	}

	public static ItemRequirement of(BlockState state) {
		Block block = state.getBlock();
		if (block == Blocks.AIR)
			return NONE;
		if (block instanceof SpecialBlockItemRequirement)
			return ((SpecialBlockItemRequirement) block).getRequiredItems(state);

		Item item = BlockItem.BLOCK_ITEMS.getOrDefault(state.getBlock(), Items.AIR);

		// double slab needs two items
		if (state.contains(Properties.SLAB_TYPE) && state.get(Properties.SLAB_TYPE) == SlabType.DOUBLE)
			return new ItemRequirement(ItemUseType.CONSUME, Arrays.asList(new ItemStack(item, 2)));
		if (block instanceof TurtleEggBlock)
			return new ItemRequirement(ItemUseType.CONSUME, Arrays.asList(new ItemStack(item, state.get(TurtleEggBlock.EGGS).intValue())));
		if (block instanceof SeaPickleBlock)
			return new ItemRequirement(ItemUseType.CONSUME, Arrays.asList(new ItemStack(item, state.get(SeaPickleBlock.PICKLES).intValue())));
		if (block instanceof SnowBlock)
			return new ItemRequirement(ItemUseType.CONSUME, Arrays.asList(new ItemStack(item, state.get(SnowBlock.LAYERS).intValue())));
		if (block instanceof GrassPathBlock) // TODO MIGHT BE WRONG
			return new ItemRequirement(ItemUseType.CONSUME, Arrays.asList(new ItemStack(Items.GRASS_BLOCK)));
		if (block instanceof FarmlandBlock)
			return new ItemRequirement(ItemUseType.CONSUME, Arrays.asList(new ItemStack(Items.DIRT)));

		return item == Items.AIR ? INVALID : new ItemRequirement(ItemUseType.CONSUME, item);
	}

	public static ItemRequirement of(Entity entity) {
		EntityType<?> type = entity.getType();

		if (entity instanceof SpecialEntityItemRequirement)
			return ((SpecialEntityItemRequirement) entity).getRequiredItems();

		if (type == EntityType.ITEM_FRAME) {
			ItemFrameEntity ife = (ItemFrameEntity) entity;
			ItemStack frame = new ItemStack(Items.ITEM_FRAME);
			ItemStack displayedItem = ife.getHeldItemStack();
			if (displayedItem.isEmpty())
				return new ItemRequirement(ItemUseType.CONSUME, Items.ITEM_FRAME);
			return new ItemRequirement(ItemUseType.CONSUME, Arrays.asList(frame, displayedItem));
		}

		if (type == EntityType.PAINTING)
			return new ItemRequirement(ItemUseType.CONSUME, Items.PAINTING);

		if (type == EntityType.ARMOR_STAND) {
			List<ItemStack> requirements = new ArrayList<>();
			ArmorStandEntity armorStandEntity = (ArmorStandEntity) entity;
			armorStandEntity.getItemsEquipped().forEach(requirements::add);
			requirements.add(new ItemStack(Items.ARMOR_STAND));
			return new ItemRequirement(ItemUseType.CONSUME, requirements);
		}

		if (entity instanceof AbstractMinecartEntity) {
			AbstractMinecartEntity minecartEntity = (AbstractMinecartEntity) entity;
			return new ItemRequirement(ItemUseType.CONSUME, (List<ItemStack>) minecartEntity.getItemsEquipped()); // TODO COULD BE WRONG minecartEntity CHECK
		}

		if (entity instanceof BoatEntity) {
			BoatEntity boatEntity = (BoatEntity) entity;
			return new ItemRequirement(ItemUseType.CONSUME, boatEntity.asItem());
		}

		if (type == EntityType.END_CRYSTAL)
			return new ItemRequirement(ItemUseType.CONSUME, Items.END_CRYSTAL);

		return INVALID;
	}

	public static boolean validate(ItemStack required, ItemStack present) {
		return required.isEmpty() || required.getItem() == present.getItem();
	}

	public boolean isEmpty() {
		return NONE == this;
	}

	public boolean isInvalid() {
		return INVALID == this;
	}

	public List<ItemStack> getRequiredItems() {
		return requiredItems;
	}

	public ItemUseType getUsage() {
		return usage;
	}

	public enum ItemUseType {
		CONSUME, DAMAGE
	}

}
