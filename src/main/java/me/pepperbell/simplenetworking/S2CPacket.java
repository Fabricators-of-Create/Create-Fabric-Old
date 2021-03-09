package me.pepperbell.simplenetworking;

import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public interface S2CPacket extends Packet {
    void handle(MinecraftClient client, ClientPlayNetworkHandler handler, ResponseTarget responseTarget);
}
