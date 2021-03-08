package com.simibubi.create.content.curiosities.zapper;

import java.util.function.Supplier;

import com.simibubi.create.content.curiosities.zapper.ZapperRenderHandler.LaserBeam;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ZapperBeamPacket extends SimplePacketBase {

	public Vec3d start;
	public Vec3d target;
	public Hand hand;
	public boolean self;

	public ZapperBeamPacket(Vec3d start, Vec3d target, Hand hand, boolean self) {
		this.start = start;
		this.target = target;
		this.hand = hand;
		this.self = self;
	}
	
	public ZapperBeamPacket(PacketByteBuf buffer) {
		start = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		target = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		hand = buffer.readBoolean()? Hand.MAIN_HAND : Hand.OFF_HAND;
		self = buffer.readBoolean();
	}

	public void write(PacketByteBuf buffer) {
		buffer.writeDouble(start.x);
		buffer.writeDouble(start.y);
		buffer.writeDouble(start.z);
		buffer.writeDouble(target.x);
		buffer.writeDouble(target.y);
		buffer.writeDouble(target.z);
		
		buffer.writeBoolean(hand == Hand.MAIN_HAND);
		buffer.writeBoolean(self);
	}

	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			if (MinecraftClient.getInstance().player.getPos().distanceTo(start) > 100)
				return;
			ZapperRenderHandler.addBeam(new LaserBeam(start, target).followPlayer(self, hand == Hand.MAIN_HAND));
			
			if (self)
				ZapperRenderHandler.shoot(hand);
			else
				ZapperRenderHandler.playSound(hand, new BlockPos(start));
		}));
		context.get().setPacketHandled(true);
	}

}
