package com.simibubi.create.content.logistics.item.filter;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllContainerTypes;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class AttributeFilterContainer extends AbstractFilterContainer {

	public enum WhitelistMode {
		WHITELIST_DISJ, WHITELIST_CONJ, BLACKLIST;
	}

	WhitelistMode whitelistMode;
	List<Pair<ItemAttribute, Boolean>> selectedAttributes;

	public AttributeFilterContainer(int id, PlayerInventory inv, PacketByteBuf extraData) {
		super(AllContainerTypes.ATTRIBUTE_FILTER.type, id, inv, extraData);
	}

	public AttributeFilterContainer(int id, PlayerInventory inv, ItemStack stack) {
		super(AllContainerTypes.ATTRIBUTE_FILTER.type, id, inv, stack);
	}

	public void appendSelectedAttribute(ItemAttribute itemAttribute, boolean inverted) {
		selectedAttributes.add(Pair.of(itemAttribute, inverted));
	}

	@Override
	protected void clearContents() {
		selectedAttributes.clear();
	}

	@Override
	protected void init() {
		super.init();
		ItemStack stack = new ItemStack(Items.NAME_TAG);
		stack.setCustomName(
			new LiteralText("Selected Tags").formatted(Formatting.RESET, Formatting.BLUE));
		filterInventory.setStackInSlot(1, stack);
	}

	@Override
	protected ItemStackHandler createFilterInventory() {
		return new ItemStackHandler(2);
	}

	protected void addFilterSlots() {
		this.addSlot(new SlotItemHandler(filterInventory, 0, 16, 22));
		this.addSlot(new SlotItemHandler(filterInventory, 1, 22, 57) {
			@Override
			public boolean canTakeItems(PlayerEntity playerIn) {
				return false;
			}
		});
	}

	@Override
	public ItemStack onSlotClick(int slotId, int dragType, SlotActionType clickTypeIn, PlayerEntity player) {
		if (slotId == 37)
			return ItemStack.EMPTY;
		return super.onSlotClick(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public boolean canInsertIntoSlot(Slot slotIn) {
		if (slotIn.id == 37)
			return false;
		return super.canInsertIntoSlot(slotIn);
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slotIn) {
		if (slotIn.id == 37)
			return false;
		return super.canInsertIntoSlot(stack, slotIn);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerIn, int index) {
		if (index == 37)
			return ItemStack.EMPTY;
		if (index == 36) {
			filterInventory.setStackInSlot(37, ItemStack.EMPTY);
			return ItemStack.EMPTY;
		}
		if (index < 36) {
			ItemStack stackToInsert = playerInventory.getStack(index);
			ItemStack copy = stackToInsert.copy();
			copy.setCount(1);
			filterInventory.setStackInSlot(0, copy);
		}
		return ItemStack.EMPTY;
	}

	@Override
	protected int getInventoryOffset() {
		return 83;
	}

	@Override
	protected void readData(ItemStack filterItem) {
		selectedAttributes = new ArrayList<>();
		whitelistMode = WhitelistMode.values()[filterItem.getOrCreateTag()
			.getInt("WhitelistMode")];
		ListTag attributes = filterItem.getOrCreateTag()
			.getList("MatchedAttributes", NBT.TAG_COMPOUND);
		attributes.forEach(inbt -> {
			CompoundTag compound = (CompoundTag) inbt;
			selectedAttributes.add(Pair.of(ItemAttribute.fromNBT(compound), compound.getBoolean("Inverted")));
		});
	}

	@Override
	protected void saveData(ItemStack filterItem) {
		filterItem.getOrCreateTag()
			.putInt("WhitelistMode", whitelistMode.ordinal());
		ListTag attributes = new ListTag();
		selectedAttributes.forEach(at -> {
			if (at == null)
				return;
			CompoundTag compoundNBT = new CompoundTag();
			at.getFirst().serializeNBT(compoundNBT);
			compoundNBT.putBoolean("Inverted", at.getSecond());
			attributes.add(compoundNBT);
		});
		filterItem.getOrCreateTag()
			.put("MatchedAttributes", attributes);
	}

}
