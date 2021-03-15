package com.simibubi.create.content.contraptions.components.structureMovement.sync;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class LimbSwingUpdatePacket implements S2CPacket {

	private int entityId;
	private Vec3d position;
	private float limbSwing;

	public LimbSwingUpdatePacket() {}

	public LimbSwingUpdatePacket(int entityId, Vec3d position, float limbSwing) {
		this.entityId = entityId;
		this.position = position;
		this.limbSwing = limbSwing;
	}

	@Override
	public void read(PacketByteBuf buffer) {
		entityId = buffer.readInt();
		position = new Vec3d(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
		limbSwing = buffer.readFloat();
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt(entityId);
		buffer.writeFloat((float) position.x);
		buffer.writeFloat((float) position.y);
		buffer.writeFloat((float) position.z);
		buffer.writeFloat(limbSwing);
	}

	@Override
	public void handle(MinecraftClient client, ClientPlayNetworkHandler handler, SimpleChannel.ResponseTarget responseTarget) {
		client.execute(() -> {
			ClientWorld world = MinecraftClient.getInstance().world;
			if (world == null)
				return;
			Entity entity = world.getEntityById(entityId);
			if (entity == null)
				return;
			/*CompoundTag data = entity.getPersistentData();
			data.putInt("LastOverrideLimbSwingUpdate", 0);
			data.putFloat("OverrideLimbSwing", limbSwing);*/
			entity.updateTrackedPositionAndAngles(position.x, position.y, position.z, entity.yaw,
				entity.pitch, 2, false);
		});
	}

}
