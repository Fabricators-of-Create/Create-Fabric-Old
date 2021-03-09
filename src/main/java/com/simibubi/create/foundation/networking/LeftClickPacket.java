package com.simibubi.create.foundation.networking;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class LeftClickPacket implements C2SPacket {

	public LeftClickPacket() {}

	@Override
	public void read(PacketByteBuf buffer) {
	}

	@Override
	public void write(PacketByteBuf buffer) {
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, ResponseTarget responseTarget) {
		//server.execute(() -> CommonEvents.leftClickEmpty(player));
	}

}
