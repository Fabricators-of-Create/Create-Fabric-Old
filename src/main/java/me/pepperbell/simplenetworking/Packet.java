package me.pepperbell.simplenetworking;

import net.minecraft.network.PacketByteBuf;

public interface Packet {
	void read(PacketByteBuf buf);

	void write(PacketByteBuf buf);
}
