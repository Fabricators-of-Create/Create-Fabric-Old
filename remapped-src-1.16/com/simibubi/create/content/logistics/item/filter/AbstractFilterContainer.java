package com.simibubi.create.content.logistics.item.filter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public abstract class AbstractFilterContainer extends ScreenHandler {

	public PlayerEntity player;
	protected PlayerInventory playerInventory;
	public ItemStack filterItem;
	public ItemStackHandler filterInventory;

	protected AbstractFilterContainer(ScreenHandlerType<?> type, int id, PlayerInventory inv, PacketByteBuf extraData) {
		this(type, id, inv, extraData.readItemStack());
	}

	protected AbstractFilterContainer(ScreenHandlerType<?> type, int id, PlayerInventory inv, ItemStack filterItem) {
		super(type, id);
		player = inv.player;
		playerInventory = inv;
		this.filterItem = filterItem;
		init();
	}

	protected void init() {
		this.filterInventory = createFilterInventory();
		readData(filterItem);
		addPlayerSlots();
		addFilterSlots();
		sendContentUpdates();
	}

	protected void clearContents() {
		for (int i = 0; i < filterInventory.getSlots(); i++)
			filterInventory.setStackInSlot(i, ItemStack.EMPTY);
	}

	protected abstract int getInventoryOffset();

	protected abstract void addFilterSlots();

	protected abstract ItemStackHandler createFilterInventory();

	protected abstract void readData(ItemStack filterItem);

	protected abstract void saveData(ItemStack filterItem);

	protected void addPlayerSlots() {
		int x = 58;
		int y = 28 + getInventoryOffset();

		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot)
			this.addSlot(new Slot(playerInventory, hotbarSlot, x + hotbarSlot * 18, y + 58));
		for (int row = 0; row < 3; ++row)
			for (int col = 0; col < 9; ++col)
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, x + col * 18, y + row * 18));
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slotIn) {
		return canInsertIntoSlot(slotIn);
	}

	@Override
	public boolean canInsertIntoSlot(Slot slotIn) {
		return slotIn.inventory == playerInventory;
	}

	@Override
	public boolean canUse(PlayerEntity playerIn) {
		return true;
	}

	@Override
	public ItemStack onSlotClick(int slotId, int dragType, SlotActionType clickTypeIn, PlayerEntity player) {
		if (slotId == playerInventory.selectedSlot && clickTypeIn != SlotActionType.THROW)
			return ItemStack.EMPTY;

		ItemStack held = playerInventory.getCursorStack();
		if (slotId < 36)
			return super.onSlotClick(slotId, dragType, clickTypeIn, player);
		if (clickTypeIn == SlotActionType.THROW)
			return ItemStack.EMPTY;

		int slot = slotId - 36;
		if (clickTypeIn == SlotActionType.CLONE) {
			if (player.isCreative() && held.isEmpty()) {
				ItemStack stackInSlot = filterInventory.getStackInSlot(slot).copy();
				stackInSlot.setCount(64);
				playerInventory.setCursorStack(stackInSlot);
				return ItemStack.EMPTY;
			}
			return ItemStack.EMPTY;
		}

		if (held.isEmpty()) {
			filterInventory.setStackInSlot(slot, ItemStack.EMPTY);
			return ItemStack.EMPTY;
		}

		ItemStack insert = held.copy();
		insert.setCount(1);
		filterInventory.setStackInSlot(slot, insert);
		return held;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerIn, int index) {
		if (index < 36) {
			ItemStack stackToInsert = playerInventory.getStack(index);
			for (int i = 0; i < filterInventory.getSlots(); i++) {
				ItemStack stack = filterInventory.getStackInSlot(i);
				if (ItemHandlerHelper.canItemStacksStack(stack, stackToInsert))
					break;
				if (stack.isEmpty()) {
					ItemStack copy = stackToInsert.copy();
					copy.setCount(1);
					filterInventory.insertItem(i, copy, false);
					break;
				}
			}
		} else
			filterInventory.extractItem(index - 36, 1, false);
		return ItemStack.EMPTY;
	}

	@Override
	public void close(PlayerEntity playerIn) {
		super.close(playerIn);
		filterItem.getOrCreateTag().put("Items", filterInventory.serializeNBT());
		saveData(filterItem);
	}

}
