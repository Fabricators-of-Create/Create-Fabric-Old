package com.simibubi.create.foundation.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.foundation.networking.AllPackets;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.PacketDistributor;

public class ToggleExperimentalRenderingCommand {

	static ArgumentBuilder<ServerCommandSource, ?> register() {
		return CommandManager.literal("experimentalRendering")
				.requires(cs -> cs.hasPermissionLevel(0))
				.then(CommandManager.argument("value", BoolArgumentType.bool())
						.executes(ctx -> {
							boolean value = BoolArgumentType.getBool(ctx, "value");
							//DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> AllConfigs.CLIENT.rainbowDebug.set(value));
							DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ConfigureConfigPacket.Actions.experimentalRendering.performAction(String.valueOf(value)));

							DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () ->
									AllPackets.channel.send(
											PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) ctx.getSource().getEntity()),
											new ConfigureConfigPacket(ConfigureConfigPacket.Actions.experimentalRendering.name(), String.valueOf(value))));

							ctx.getSource().sendFeedback(new LiteralText((value ? "enabled" : "disabled") + " experimental rendering"), true);

							return 1;
						}));
	}
}
