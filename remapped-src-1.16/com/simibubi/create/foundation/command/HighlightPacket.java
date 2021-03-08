package com.simibubi.create.foundation.command;

import java.util.function.Supplier;

import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class HighlightPacket extends SimplePacketBase {

	private final BlockPos pos;

	public HighlightPacket(BlockPos pos) {
		this.pos = pos;
	}

	public HighlightPacket(PacketByteBuf buffer) {
		this.pos = BlockPos.fromLong(buffer.readLong());
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeLong(pos.asLong());
	}

	@Override
	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			performHighlight(pos);
		}));

		ctx.get().setPacketHandled(true);
	}

	@Environment(EnvType.CLIENT)
	public static void performHighlight(BlockPos pos) {
		if (MinecraftClient.getInstance().world == null || !MinecraftClient.getInstance().world.canSetBlock(pos))
			return;

		CreateClient.outliner.showAABB("highlightCommand", VoxelShapes.fullCube().getBoundingBox().offset(pos), 200)
				.lineWidth(1 / 32f)
				.colored(0xEeEeEe)
				//.colored(0x243B50)
				.withFaceTexture(AllSpecialTextures.SELECTION);

	}
}
