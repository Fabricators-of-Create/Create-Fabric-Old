package com.simibubi.create.foundation.networking;

import java.util.function.Supplier;

import com.simibubi.create.events.CommonEvents;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class LeftClickPacket extends SimplePacketBase {

	public LeftClickPacket() {
	}

	LeftClickPacket(PacketByteBuf buffer) {
	}

	@Override
	public void write(PacketByteBuf buffer) {
	}

	@Override
	public void handle(Supplier<Context> context) {
		Context ctx = context.get();
		if (ctx.getDirection() != NetworkDirection.PLAY_TO_SERVER)
			return;
		ctx.enqueueWork(() -> CommonEvents.leftClickEmpty(ctx.getSender()));
		ctx.setPacketHandled(true);
	}

}
