package com.simibubi.create.foundation.command;

import java.util.Collections;
import java.util.function.Predicate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class AllCommands {

	public static Predicate<ServerCommandSource> sourceIsPlayer = (cs) -> cs.getEntity() instanceof PlayerEntity;

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

		LiteralCommandNode<ServerCommandSource> createRoot = dispatcher.register(CommandManager.literal("create")
						//general purpose
						.then(ToggleDebugCommand.register())
						.then(OverlayConfigCommand.register())
						.then(FixLightingCommand.register())
						.then(ReplaceInCommandBlocksCommand.register())
						.then(HighlightCommand.register())
					    .then(ToggleExperimentalRenderingCommand.register())

						//dev-util
						//Comment out for release
						.then(ClearBufferCacheCommand.register())
						.then(ChunkUtilCommand.register())
						//.then(KillTPSCommand.register())
		);

		CommandNode<ServerCommandSource> c = dispatcher.findNode(Collections.singleton("c"));
		if (c != null)
			return;

		dispatcher.register(CommandManager.literal("c")
				.redirect(createRoot)
		);
	}
}
