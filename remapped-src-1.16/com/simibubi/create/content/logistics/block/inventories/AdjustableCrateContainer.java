package com.simibubi.create.content.logistics.block.inventories;

import com.simibubi.create.AllContainerTypes;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraftforge.items.SlotItemHandler;

public class AdjustableCrateContainer extends ScreenHandler {

	public AdjustableCrateTileEntity te;
	public PlayerInventory playerInventory;
	public boolean doubleCrate;

	public AdjustableCrateContainer(int id, PlayerInventory inv, PacketByteBuf extraData) {
		super(AllContainerTypes.FLEXCRATE.type, id);
		ClientWorld world = MinecraftClient.getInstance().world;
		BlockEntity tileEntity = world.getBlockEntity(extraData.readBlockPos());
		this.playerInventory = inv;
		if (tileEntity instanceof AdjustableCrateTileEntity) {
			this.te = (AdjustableCrateTileEntity) tileEntity;
			this.te.handleUpdateTag(te.getCachedState(), extraData.readCompoundTag());
			init();
		}
	}

	public AdjustableCrateContainer(int id, PlayerInventory inv, AdjustableCrateTileEntity te) {
		super(AllContainerTypes.FLEXCRATE.type, id);
		this.te = te;
		this.playerInventory = inv;
		init();
	}

	private void init() {
		doubleCrate = te.isDoubleCrate();
		int x = doubleCrate ? 51 : 123;
		int maxCol = doubleCrate ? 8 : 4;
		for (int row = 0; row < 4; ++row) {
			for (int col = 0; col < maxCol; ++col) {
				this.addSlot(new SlotItemHandler(te.inventory, col + row * maxCol, x + col * 18, 20 + row * 18));
			}
		}

		// player Slots
		int xOffset = 58;
		int yOffset = 155;
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, xOffset + col * 18, yOffset + row * 18));
			}
		}

		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
			this.addSlot(new Slot(playerInventory, hotbarSlot, xOffset + hotbarSlot * 18, yOffset + 58));
		}

		sendContentUpdates();
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerIn, int index) {
		Slot clickedSlot = getSlot(index);
		if (!clickedSlot.hasStack())
			return ItemStack.EMPTY;

		ItemStack stack = clickedSlot.getStack();
		int crateSize = doubleCrate ? 32 : 16;
		if (index < crateSize) {
			insertItem(stack, crateSize, slots.size(), false);
			te.inventory.onContentsChanged(index);
		} else
			insertItem(stack, 0, crateSize - 1, false);

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canUse(PlayerEntity playerIn) {
		return true;
	}

}
