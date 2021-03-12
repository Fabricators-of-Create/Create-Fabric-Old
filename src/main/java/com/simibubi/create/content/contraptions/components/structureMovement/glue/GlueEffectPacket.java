package com.simibubi.create.content.contraptions.components.structureMovement.glue;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class GlueEffectPacket implements S2CPacket {

	private BlockPos pos;
	private Direction direction;
	private boolean fullBlock;

	public GlueEffectPacket() {}

	public GlueEffectPacket(BlockPos pos, Direction direction, boolean fullBlock) {
		this.pos = pos;
		this.direction = direction;
		this.fullBlock = fullBlock;
	}

	@Override
	public void read(PacketByteBuf buffer) {
		pos = buffer.readBlockPos();
		direction = Direction.byId(buffer.readByte());
		fullBlock = buffer.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeByte(direction.getId());
		buffer.writeBoolean(fullBlock);
	}

	@Override
	public void handle(MinecraftClient client, ClientPlayNetworkHandler handler, SimpleChannel.ResponseTarget responseTarget) {
		client.execute(() -> {
			if (!client.player.getBlockPos().isWithinDistance(pos, 100))
				return;
			SuperGlueItem.spawnParticles(client.world, pos, direction, fullBlock);
		});
	}
}