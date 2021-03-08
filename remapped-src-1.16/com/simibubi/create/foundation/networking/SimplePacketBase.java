package com.simibubi.create.foundation.networking;

import java.util.function.Supplier;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public abstract class SimplePacketBase {

	public abstract void write(PacketByteBuf buffer);
	public abstract void handle(Supplier<Context> context);
	
}
