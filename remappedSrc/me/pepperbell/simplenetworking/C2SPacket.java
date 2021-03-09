package me.pepperbell.simplenetworking;

import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public interface C2SPacket extends Packet {
	public void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, ResponseTarget responseTarget);
}
