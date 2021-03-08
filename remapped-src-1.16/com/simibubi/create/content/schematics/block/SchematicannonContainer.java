package com.simibubi.create.content.schematics.block;

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

public class SchematicannonContainer extends ScreenHandler {

	private SchematicannonTileEntity te;
	private PlayerEntity player;

	public SchematicannonContainer(int id, PlayerInventory inv, PacketByteBuf buffer) {
		super(AllContainerTypes.SCHEMATICANNON.type, id);
		player = inv.player;
		ClientWorld world = MinecraftClient.getInstance().world;
		BlockEntity tileEntity = world.getBlockEntity(buffer.readBlockPos());
		if (tileEntity instanceof SchematicannonTileEntity) {
			this.te = (SchematicannonTileEntity) tileEntity;
			this.te.handleUpdateTag(te.getCachedState(), buffer.readCompoundTag());
			init();
		}
	}

	public SchematicannonContainer(int id, PlayerInventory inv, SchematicannonTileEntity te) {
		super(AllContainerTypes.SCHEMATICANNON.type, id);
		player = inv.player;
		this.te = te;
		init();
	}

	protected void init() {
		int x = 20;
		int y = 0;

		addSlot(new SlotItemHandler(te.inventory, 0, x + 15, y + 65));
		addSlot(new SlotItemHandler(te.inventory, 1, x + 171, y + 65));
		addSlot(new SlotItemHandler(te.inventory, 2, x + 134, y + 19));
		addSlot(new SlotItemHandler(te.inventory, 3, x + 174, y + 19));
		addSlot(new SlotItemHandler(te.inventory, 4, x + 15, y + 19));

		// player Slots
		for (int row = 0; row < 3; ++row) 
			for (int col = 0; col < 9; ++col) 
				addSlot(new Slot(player.inventory, col + row * 9 + 9, -2 + col * 18, 163 + row * 18));
		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) 
			addSlot(new Slot(player.inventory, hotbarSlot, -2 + hotbarSlot * 18, 221));

		sendContentUpdates();
	}

	@Override
	public boolean canUse(PlayerEntity playerIn) {
		return true;
	}

	@Override
	public void close(PlayerEntity playerIn) {
		super.close(playerIn);
	}

	public SchematicannonTileEntity getTileEntity() {
		return te;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerIn, int index) {
		Slot clickedSlot = getSlot(index);
		if (!clickedSlot.hasStack())
			return ItemStack.EMPTY;
		ItemStack stack = clickedSlot.getStack();

		if (index < 5) {
			insertItem(stack, 5, slots.size(), false);
		} else {
			if (insertItem(stack, 0, 1, false) || insertItem(stack, 2, 3, false)
					|| insertItem(stack, 4, 5, false))
				;
		}

		return ItemStack.EMPTY;
	}

}
