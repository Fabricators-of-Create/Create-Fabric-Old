package com.simibubi.create.foundation.command;

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.contraptions.components.structureMovement.IDisplayAssemblyExceptions;
import com.simibubi.create.foundation.networking.AllPackets;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.network.PacketDistributor;

public class HighlightCommand {

	public static ArgumentBuilder<ServerCommandSource, ?> register() {
		return CommandManager.literal("highlight")
				.requires(cs -> cs.hasPermissionLevel(0))
				.then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
						.then(CommandManager.argument("players", EntityArgumentType.players())
								.executes(ctx -> {
									Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "players");
									BlockPos pos = BlockPosArgumentType.getBlockPos(ctx, "pos");

									for (ServerPlayerEntity p : players) {
										AllPackets.channel.send(
												PacketDistributor.PLAYER.with(() -> p),
												new HighlightPacket(pos)
										);
									}

									return players.size();
								})
						)
						//.requires(AllCommands.sourceIsPlayer)
						.executes(ctx -> {
							BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "pos");

							AllPackets.channel.send(
									PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) ctx.getSource().getEntity()),
									new HighlightPacket(pos)
							);

							return Command.SINGLE_SUCCESS;
						})
				)
				//.requires(AllCommands.sourceIsPlayer)
				.executes(ctx -> {
					ServerPlayerEntity player = ctx.getSource().getPlayer();
					return highlightAssemblyExceptionFor(player, ctx.getSource());
				});

	}

	private static void sendMissMessage(ServerCommandSource source) {
		source.sendFeedback(new LiteralText("Try looking at a Block that has failed to assemble a Contraption and try again."), true);
	}

	private static int highlightAssemblyExceptionFor(ServerPlayerEntity player, ServerCommandSource source) {
		double distance = player.getAttributeInstance(ForgeMod.REACH_DISTANCE.get()).getValue();
		Vec3d start = player.getCameraPosVec(1);
		Vec3d look = player.getRotationVec(1);
		Vec3d end = start.add(look.x * distance, look.y * distance, look.z * distance);
		World world = player.world;

		BlockHitResult ray = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));
		if (ray.getType() == HitResult.Type.MISS) {
			sendMissMessage(source);
			return 0;
		}

		BlockPos pos = ray.getBlockPos();
		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof IDisplayAssemblyExceptions)) {
			sendMissMessage(source);
			return 0;
		}

		IDisplayAssemblyExceptions display = (IDisplayAssemblyExceptions) te;
		AssemblyException exception = display.getLastAssemblyException();
		if (exception == null) {
			sendMissMessage(source);
			return 0;
		}

		if (!exception.hasPosition()) {
			source.sendFeedback(new LiteralText("Can't highlight a specific position for this issue"), true);
			return Command.SINGLE_SUCCESS;
		}

		BlockPos p = exception.getPosition();
		String command = "/create highlight " + p.getX() + " " + p.getY() + " " + p.getZ();
		return player.server.getCommandManager().execute(source, command);
	}
}
