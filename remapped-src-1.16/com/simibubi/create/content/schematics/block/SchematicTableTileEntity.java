package com.simibubi.create.content.schematics.block;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Tickable;
import net.minecraftforge.items.ItemStackHandler;

public class SchematicTableTileEntity extends SyncedTileEntity implements Tickable, NamedScreenHandlerFactory {

	public SchematicTableInventory inventory;
	public boolean isUploading;
	public String uploadingSchematic;
	public float uploadingProgress;
	public boolean sendUpdate;

	public class SchematicTableInventory extends ItemStackHandler {
		public SchematicTableInventory() {
			super(2);
		}

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			markDirty();
		}
	}

	public SchematicTableTileEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		inventory = new SchematicTableInventory();
		uploadingSchematic = null;
		uploadingProgress = 0;
	}

	public void sendToContainer(PacketByteBuf buffer) {
		buffer.writeBlockPos(getPos());
		buffer.writeCompoundTag(toInitialChunkDataTag());
	}

	@Override
	public void fromTag(BlockState state, CompoundTag compound) {
		inventory.deserializeNBT(compound.getCompound("Inventory"));
		readClientUpdate(state, compound);
		super.fromTag(state, compound);
	}

	@Override
	public void readClientUpdate(BlockState state, CompoundTag compound) {
		if (compound.contains("Uploading")) {
			isUploading = true;
			uploadingSchematic = compound.getString("Schematic");
			uploadingProgress = compound.getFloat("Progress");
		} else {
			isUploading = false;
			uploadingSchematic = null;
			uploadingProgress = 0;
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compound) {
		compound.put("Inventory", inventory.serializeNBT());
		writeToClient(compound);
		return super.toTag(compound);
	}

	@Override
	public CompoundTag writeToClient(CompoundTag compound) {
		if (isUploading) {
			compound.putBoolean("Uploading", true);
			compound.putString("Schematic", uploadingSchematic);
			compound.putFloat("Progress", uploadingProgress);
		}
		return compound;
	}

	@Override
	public void tick() {
		// Update Client Tile
		if (sendUpdate) {
			sendUpdate = false;
			world.updateListeners(pos, getCachedState(), getCachedState(), 6);
		}
	}
	
	public void startUpload(String schematic) {
		isUploading = true;
		uploadingProgress = 0;
		uploadingSchematic = schematic;
		sendUpdate = true;
		inventory.setStackInSlot(0, ItemStack.EMPTY);
	}
	
	public void finishUpload() {
		isUploading = false;
		uploadingProgress = 0;
		uploadingSchematic = null;
		sendUpdate = true;
	}

	@Override
	public ScreenHandler createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		return new SchematicTableContainer(p_createMenu_1_, p_createMenu_2_, this);
	}

	@Override
	public Text getDisplayName() {
		return new LiteralText(getType().getRegistryName().toString());
	}

}
