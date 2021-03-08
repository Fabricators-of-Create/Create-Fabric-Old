package com.simibubi.create.foundation.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.Create;
import net.minecraft.command.argument.ColumnPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ColumnPos;

public class ChunkUtilCommand {

	public static ArgumentBuilder<ServerCommandSource, ?> register() {
		return CommandManager.literal("chunk")
				.requires(cs -> cs.hasPermissionLevel(2))
					.then(CommandManager.literal("reload").then(CommandManager.argument("pos", ColumnPosArgumentType.columnPos())
						.executes(ctx -> {
							//chunk reload <pos>
							ColumnPos columnPos = ColumnPosArgumentType.getColumnPos(ctx, "pos");
							ChunkPos chunkPos = new ChunkPos(columnPos.x >> 4, columnPos.z >> 4);
							ServerChunkManager chunkProvider = ctx.getSource().getWorld().getChunkManager();

							boolean success = Create.chunkUtil.reloadChunk(chunkProvider, chunkPos);

							if (success) {
								ctx.getSource().sendFeedback(new LiteralText("scheduled unload for chunk " + chunkPos.toString() + ", might need to repeat command"), true);
								return 1;
							} else {
								ctx.getSource().sendFeedback(new LiteralText("unable to schedule unload, is chunk " + chunkPos.toString() + " loaded?"), true);
								return 0;
							}
						})
					))
					.then(CommandManager.literal("unload").then(CommandManager.argument("pos", ColumnPosArgumentType.columnPos())
						.executes(ctx -> {
							//chunk unload <pos>
							ColumnPos columnPos = ColumnPosArgumentType.getColumnPos(ctx, "pos");
							ChunkPos chunkPos = new ChunkPos(columnPos.x >> 4, columnPos.z >> 4);
							ServerChunkManager chunkProvider = ctx.getSource().getWorld().getChunkManager();

							boolean success  = Create.chunkUtil.unloadChunk(chunkProvider, chunkPos);
							ctx.getSource().sendFeedback(new LiteralText("added chunk " + chunkPos.toString() + " to unload list"), true);

							if (success) {
								ctx.getSource().sendFeedback(new LiteralText("scheduled unload for chunk " + chunkPos.toString() + ", might need to repeat command"), true);
								return 1;
							} else {
								ctx.getSource().sendFeedback(new LiteralText("unable to schedule unload, is chunk " + chunkPos.toString() + " loaded?"), true);
								return 0;
							}
						})
					))
					.then(CommandManager.literal("clear")
						.executes(ctx -> {
							//chunk clear
							int count = Create.chunkUtil.clear(ctx.getSource().getWorld().getChunkManager());
							ctx.getSource().sendFeedback(new LiteralText("removed " + count + " entries from unload list"), false);

							return 1;
						})
					);

	}
}
