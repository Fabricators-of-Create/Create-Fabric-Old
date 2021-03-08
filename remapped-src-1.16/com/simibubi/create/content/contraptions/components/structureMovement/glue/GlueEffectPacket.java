package com.simibubi.create.content.contraptions.components.structureMovement.glue;

import java.util.function.Supplier;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class GlueEffectPacket extends SimplePacketBase {

	private BlockPos pos;
	private Direction direction;
	private boolean fullBlock;

	public GlueEffectPacket(BlockPos pos, Direction direction, boolean fullBlock) {
		this.pos = pos;
		this.direction = direction;
		this.fullBlock = fullBlock;
	}

	public GlueEffectPacket(PacketByteBuf buffer) {
		pos = buffer.readBlockPos();
		direction = Direction.byId(buffer.readByte());
		fullBlock = buffer.readBoolean();
	}

	public void write(PacketByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeByte(direction.getId());
		buffer.writeBoolean(fullBlock);
	}

	@Environment(EnvType.CLIENT)
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			MinecraftClient mc = MinecraftClient.getInstance();
			if (!mc.player.getBlockPos().isWithinDistance(pos, 100))
				return;
			SuperGlueItem.spawnParticles(mc.world, pos, direction, fullBlock);
		}));
		context.get().setPacketHandled(true);
	}

}
