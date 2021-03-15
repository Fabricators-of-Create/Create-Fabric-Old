package com.simibubi.create.content.contraptions.components.structureMovement.sync;

import com.simibubi.create.foundation.networking.AllPackets;
import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class ClientMotionPacket implements C2SPacket {

	private Vec3d motion;
	private boolean onGround;
	private float limbSwing;

	public ClientMotionPacket() {}

	public ClientMotionPacket(Vec3d motion, boolean onGround, float limbSwing) {
		this.motion = motion;
		this.onGround = onGround;
		this.limbSwing = limbSwing;
	}

	@Override
	public void read(PacketByteBuf buffer) {
		motion = new Vec3d(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
		onGround = buffer.readBoolean();
		limbSwing = buffer.readFloat();
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeFloat((float) motion.x);
		buffer.writeFloat((float) motion.y);
		buffer.writeFloat((float) motion.z);
		buffer.writeBoolean(onGround);
		buffer.writeFloat(limbSwing);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, SimpleChannel.ResponseTarget responseTarget) {
		server.execute(() -> {
			if (player == null)
				return;
			player.setVelocity(motion);
			player.setOnGround(onGround);
			if (onGround) {
				player.handleFallDamage(player.fallDistance, 1);
				player.fallDistance = 0;
				//player.networkHandler.floatingTicks = 0;
			}
			AllPackets.CHANNEL.sendToClientsTracking(new LimbSwingUpdatePacket(player.getEntityId(), player.getPos(), limbSwing), player);
		});
	}

}
