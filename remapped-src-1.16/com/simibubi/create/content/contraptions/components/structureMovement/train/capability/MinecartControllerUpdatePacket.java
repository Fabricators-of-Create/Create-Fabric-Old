package com.simibubi.create.content.contraptions.components.structureMovement.train.capability;

import java.util.function.Supplier;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class MinecartControllerUpdatePacket extends SimplePacketBase {

	int entityID;
	CompoundTag nbt;

	public MinecartControllerUpdatePacket(MinecartController controller) {
		entityID = controller.cart()
			.getEntityId();
		nbt = controller.serializeNBT();
	}

	public MinecartControllerUpdatePacket(PacketByteBuf buffer) {
		entityID = buffer.readInt();
		nbt = buffer.readCompoundTag();
	}

	@Override
	public void write(PacketByteBuf buffer) {
 		buffer.writeInt(entityID);
		buffer.writeCompoundTag(nbt);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get()
			.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleCL));
		context.get()
			.setPacketHandled(true);
	}

	@Environment(EnvType.CLIENT)
	private void handleCL() {
		ClientWorld world = MinecraftClient.getInstance().world;
		if (world == null)
			return;
		Entity entityByID = world.getEntityById(entityID);
		if (entityByID == null)
			return;
		entityByID.getCapability(CapabilityMinecartController.MINECART_CONTROLLER_CAPABILITY)
			.ifPresent(mc -> mc.deserializeNBT(nbt));
	}

}
