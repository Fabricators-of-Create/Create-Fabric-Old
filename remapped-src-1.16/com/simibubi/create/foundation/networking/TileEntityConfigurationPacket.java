package com.simibubi.create.foundation.networking;

import java.util.function.Supplier;

import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public abstract class TileEntityConfigurationPacket<TE extends SyncedTileEntity> extends SimplePacketBase {

	protected BlockPos pos;

	public TileEntityConfigurationPacket(PacketByteBuf buffer) {
		pos = buffer.readBlockPos();
		readSettings(buffer);
	}
	
	public TileEntityConfigurationPacket(BlockPos pos) {
		this.pos = pos;
	}
	
	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeBlockPos(pos);
		writeSettings(buffer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			ServerPlayerEntity player = context.get().getSender();
			if (player == null)
				return;
			World world = player.world;

			if (world == null || !world.canSetBlock(pos))
				return;
			BlockEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof SyncedTileEntity) {
				applySettings((TE) tileEntity);
				((SyncedTileEntity) tileEntity).sendData();
				tileEntity.markDirty();
			}
		});
		context.get().setPacketHandled(true);
		
	}
	
	protected abstract void writeSettings(PacketByteBuf buffer);
	protected abstract void readSettings(PacketByteBuf buffer);
	protected abstract void applySettings(TE te);

}
