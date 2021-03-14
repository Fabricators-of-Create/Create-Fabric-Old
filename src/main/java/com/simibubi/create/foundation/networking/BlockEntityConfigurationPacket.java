package com.simibubi.create.foundation.networking;

import com.simibubi.create.foundation.block.entity.SyncedBlockEntity;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BlockEntityConfigurationPacket<BE extends SyncedBlockEntity> implements C2SPacket {
	protected BlockPos pos;

	protected BlockEntityConfigurationPacket() {}

	public BlockEntityConfigurationPacket(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void read(PacketByteBuf buffer) {
		pos = buffer.readBlockPos();
		readSettings(buffer);
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeBlockPos(pos);
		writeSettings(buffer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, ResponseTarget responseTarget) {
		server.execute(() -> {
			if (player == null)
				return;
			World world = player.world;

			if (world == null || !world.canSetBlock(pos))
				return;
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof SyncedBlockEntity) {
				applySettings((BE) blockEntity);
				((SyncedBlockEntity) blockEntity).sendData();
				blockEntity.markDirty();
			}
		});
		
	}

	protected abstract void writeSettings(PacketByteBuf buffer);
	protected abstract void readSettings(PacketByteBuf buffer);
	protected abstract void applySettings(BE te);
}
