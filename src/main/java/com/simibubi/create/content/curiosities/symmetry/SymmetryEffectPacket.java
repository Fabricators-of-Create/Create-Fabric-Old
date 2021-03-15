package com.simibubi.create.content.curiosities.symmetry;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class SymmetryEffectPacket implements S2CPacket {

	private BlockPos mirror;
	private List<BlockPos> positions;

	public SymmetryEffectPacket() {}

	public SymmetryEffectPacket(BlockPos mirror, List<BlockPos> positions) {
		this.mirror = mirror;
		this.positions = positions;
	}

	@Override
	public void read(PacketByteBuf buffer) {
		mirror = buffer.readBlockPos();
		int amt = buffer.readInt();
		positions = new ArrayList<>(amt);
		for (int i = 0; i < amt; i++) {
			positions.add(buffer.readBlockPos());
		}
	}

	public void write(PacketByteBuf buffer) {
		buffer.writeBlockPos(mirror);
		buffer.writeInt(positions.size());
		for (BlockPos blockPos : positions) {
			buffer.writeBlockPos(blockPos);
		}
	}

	@Override
	public void handle(MinecraftClient client, ClientPlayNetworkHandler handler, SimpleChannel.ResponseTarget responseTarget) {
		client.execute(() -> {
			if (MinecraftClient.getInstance().player.getPos().distanceTo(Vec3d.of(mirror)) > 100)
				return;
			for (BlockPos to : positions)
				SymmetryHandler.drawEffect(mirror, to);
		});
	}

}