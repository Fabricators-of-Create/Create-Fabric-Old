package com.simibubi.create.content.schematics.block;

import com.simibubi.create.AllContainerTypes;
import com.simibubi.create.AllItems;
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

public class SchematicTableContainer extends ScreenHandler {

	private SchematicTableTileEntity te;
	private Slot inputSlot;
	private Slot outputSlot;
	private PlayerEntity player;

	public SchematicTableContainer(int id, PlayerInventory inv, PacketByteBuf extraData) {
		super(AllContainerTypes.SCHEMATIC_TABLE.type, id);
		player = inv.player;
		ClientWorld world = MinecraftClient.getInstance().world;
		BlockEntity tileEntity = world.getBlockEntity(extraData.readBlockPos());
		if (tileEntity instanceof SchematicTableTileEntity) {
			this.te = (SchematicTableTileEntity) tileEntity;
			this.te.handleUpdateTag(te.getCachedState(), extraData.readCompoundTag());
			init();
		}
	}

	public SchematicTableContainer(int id, PlayerInventory inv, SchematicTableTileEntity te) {
		super(AllContainerTypes.SCHEMATIC_TABLE.type, id);
		this.player = inv.player;
		this.te = te;
		init();
	}

	protected void init() {
		inputSlot = new SlotItemHandler(te.inventory, 0, -35, 41) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return AllItems.EMPTY_SCHEMATIC.isIn(stack) || AllItems.SCHEMATIC_AND_QUILL.isIn(stack)
					|| AllItems.SCHEMATIC.isIn(stack);
			}
		};

		outputSlot = new SlotItemHandler(te.inventory, 1, 110, 41) {
			@Override
			public boolean canInsert(ItemStack stack) {
				return false;
			}
		};

		addSlot(inputSlot);
		addSlot(outputSlot);

		// player Slots
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				this.addSlot(new Slot(player.inventory, col + row * 9 + 9, 12 + col * 18, 102 + row * 18));
			}
		}

		for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
			this.addSlot(new Slot(player.inventory, hotbarSlot, 12 + hotbarSlot * 18, 160));
		}

		sendContentUpdates();
	}

	public boolean canWrite() {
		return inputSlot.hasStack() && !outputSlot.hasStack();
	}

	@Override
	public boolean canUse(PlayerEntity playerIn) {
		return true;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerIn, int index) {
		Slot clickedSlot = getSlot(index);
		if (!clickedSlot.hasStack())
			return ItemStack.EMPTY;

		ItemStack stack = clickedSlot.getStack();
		if (index < 2)
			insertItem(stack, 2, slots.size(), false);
		else
			insertItem(stack, 0, 1, false);

		return ItemStack.EMPTY;
	}

	public SchematicTableTileEntity getTileEntity() {
		return te;
	}

}
