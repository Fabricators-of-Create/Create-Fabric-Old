package com.simibubi.create.foundation.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.foundation.networking.AllPackets;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.PacketDistributor;

public class OverlayConfigCommand {

	public static ArgumentBuilder<ServerCommandSource, ?> register() {
		return CommandManager.literal("overlay")
				.requires(cs -> cs.hasPermissionLevel(0))
				.then(CommandManager.literal("reset")
					.executes(ctx -> {
						DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ConfigureConfigPacket.Actions.overlayReset.performAction(""));

						DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () ->
								AllPackets.channel.send(
										PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) ctx.getSource().getEntity()),
										new ConfigureConfigPacket(ConfigureConfigPacket.Actions.overlayReset.name(), "")));

						ctx.getSource().sendFeedback(new LiteralText("reset overlay offset"), true);

						return 1;
					})
				)
				.executes(ctx -> {
					DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ConfigureConfigPacket.Actions.overlayScreen.performAction(""));

					DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () ->
							AllPackets.channel.send(
									PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) ctx.getSource().getEntity()),
									new ConfigureConfigPacket(ConfigureConfigPacket.Actions.overlayScreen.name(), "")));

					ctx.getSource().sendFeedback(new LiteralText("window opened"), true);

					return 1;
				});

	}
}
