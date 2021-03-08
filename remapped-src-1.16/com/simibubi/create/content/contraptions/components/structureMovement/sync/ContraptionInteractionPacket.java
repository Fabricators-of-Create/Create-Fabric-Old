package com.simibubi.create.content.contraptions.components.structureMovement.sync;

import java.util.function.Supplier;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class ContraptionInteractionPacket extends SimplePacketBase {

	private Hand interactionHand;
	private int target;
	private BlockPos localPos;
	private Direction face;

	public ContraptionInteractionPacket(AbstractContraptionEntity target, Hand hand, BlockPos localPos, Direction side) {
		this.interactionHand = hand;
		this.localPos = localPos;
		this.target = target.getEntityId();
		this.face = side;
	}

	public ContraptionInteractionPacket(PacketByteBuf buffer) {
		target = buffer.readInt();
		int handId = buffer.readInt();
		interactionHand = handId == -1 ? null : Hand.values()[handId];
		localPos = buffer.readBlockPos();
		face = Direction.byId(buffer.readShort());
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt(target);
		buffer.writeInt(interactionHand == null ? -1 : interactionHand.ordinal());
		buffer.writeBlockPos(localPos);
		buffer.writeShort(face.getId());
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			ServerPlayerEntity sender = context.get().getSender();
			if (sender == null)
				return;
			Entity entityByID = sender.getServerWorld().getEntityById(target);
			if (!(entityByID instanceof AbstractContraptionEntity))
				return;
			AbstractContraptionEntity contraptionEntity = (AbstractContraptionEntity) entityByID;
			double d = sender.getAttributeInstance(ForgeMod.REACH_DISTANCE.get()).getValue();
			if (!sender.canSee(entityByID))
				d -= 3;
			d *= d;
			if (sender.squaredDistanceTo(entityByID) > d) {
				// TODO log?
				return;
			}
			if (contraptionEntity.handlePlayerInteraction(sender, localPos, face, interactionHand))
				sender.swingHand(interactionHand, true);
		});
		context.get().setPacketHandled(true);
	}

}
