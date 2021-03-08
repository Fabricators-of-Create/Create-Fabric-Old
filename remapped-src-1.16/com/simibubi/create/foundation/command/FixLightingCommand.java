package com.simibubi.create.foundation.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.foundation.networking.AllPackets;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraftforge.fml.network.PacketDistributor;

public class FixLightingCommand {

	static ArgumentBuilder<ServerCommandSource, ?> register() {
		return CommandManager.literal("fixLighting")
			.requires(cs -> cs.hasPermissionLevel(0))
			.executes(ctx -> {
				AllPackets.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) ctx.getSource()
					.getEntity()),
					new ConfigureConfigPacket(ConfigureConfigPacket.Actions.fixLighting.name(), String.valueOf(true)));

				ctx.getSource()
					.sendFeedback(
						new LiteralText("Forge's experimental block rendering pipeline is now enabled."), true);

				return 1;
			});
	}
}
