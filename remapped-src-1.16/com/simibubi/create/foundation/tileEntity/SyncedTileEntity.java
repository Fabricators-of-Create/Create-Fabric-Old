package com.simibubi.create.foundation.tileEntity;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class SyncedTileEntity extends BlockEntity {

	public SyncedTileEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public CompoundTag getTileData() {
		return super.getTileData();
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return toTag(new CompoundTag());
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundTag tag) {
		fromTag(state, tag);
	}

	public void sendData() {
		if (world != null)
			world.updateListeners(getPos(), getCachedState(), getCachedState(), 2 | 4 | 16);
	}

	public void causeBlockUpdate() {
		if (world != null)
			world.updateListeners(getPos(), getCachedState(), getCachedState(), 1);
	}
	
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(getPos(), 1, writeToClient(new CompoundTag()));
	}

	@Override
	public void onDataPacket(ClientConnection net, BlockEntityUpdateS2CPacket pkt) {
		readClientUpdate(getCachedState(), pkt.getCompoundTag());
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
