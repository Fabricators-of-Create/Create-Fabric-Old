package com.simibubi.create.foundation.tileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

public abstract class SyncedTileEntity extends BlockEntity {

	public SyncedTileEntity(BlockEntityType<?> type) {
		super(type);
	}

	public void sendData() {
		world.updateListeners(getPos(), getCachedState(), getCachedState(), 2 | 4 | 16);
	}

	public void causeBlockUpdate() {
		world.updateListeners(getPos(), getCachedState(), getCachedState(), 1);
	}

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(getPos(), 1, writeToClient(new CompoundTag()));
	}

	// Special handling for client update packets
	public void readClientUpdate(BlockState state, CompoundTag tag) {
		fromTag(state, tag);
	}

	// Special handling for client update packets
	public CompoundTag writeToClient(CompoundTag tag) {
		return toTag(tag);
	}
	
	public void notifyUpdate() {
		markDirty();
		sendData();
	}

}
