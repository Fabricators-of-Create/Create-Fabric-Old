package com.simibubi.create.foundation.block.entity;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public abstract class SyncedBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

	public SyncedBlockEntity(BlockEntityType<?> blockEntityTypeIn) {
		super(blockEntityTypeIn);
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		readClientUpdate(getCachedState(), tag);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		return writeToClient(tag);
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return toTag(new CompoundTag());
	}

	public void sendData() {
		if (world != null)
			world.updateListeners(getPos(), getCachedState(), getCachedState(), 2 | 4 | 16);
	}

	public void causeBlockUpdate() {
		if (world != null)
			world.updateListeners(getPos(), getCachedState(), getCachedState(), 1);
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
