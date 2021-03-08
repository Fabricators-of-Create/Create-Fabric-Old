package com.simibubi.create.foundation.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class KillTPSCommand {

	public static ArgumentBuilder<ServerCommandSource ,?> register() {
		return CommandManager.literal("killtps")
				.requires(cs -> cs.hasPermissionLevel(2)).executes(ctx -> {
					// killtps no arguments
					ctx.getSource().sendFeedback(
							Lang.createTranslationTextComponent("command.killTPSCommand.status.slowed_by.0",
									Create.lagger.isLagging() ? Create.lagger.getTickTime() : 0),
							true);
					if (Create.lagger.isLagging())
						ctx.getSource().sendFeedback(
								Lang.createTranslationTextComponent("command.killTPSCommand.status.usage.0"), true);
					else
						ctx.getSource().sendFeedback(
								Lang.createTranslationTextComponent("command.killTPSCommand.status.usage.1"), true);

					return 1;
				}).then(CommandManager.literal("start").executes(ctx -> {
					// killtps start no time
					int tickTime = Create.lagger.getTickTime();
					if (tickTime > 0) {
						Create.lagger.setLagging(true);
						ctx.getSource().sendFeedback((Lang
								.createTranslationTextComponent("command.killTPSCommand.status.slowed_by.1", tickTime)),
								true);
						ctx.getSource().sendFeedback(
								Lang.createTranslationTextComponent("command.killTPSCommand.status.usage.0"), true);
					} else {
						ctx.getSource().sendFeedback(
								Lang.createTranslationTextComponent("command.killTPSCommand.status.usage.1"), true);
					}

					return 1;
				}).then(CommandManager.argument(Lang.translate("command.killTPSCommand.argument.tickTime").asString(),
						IntegerArgumentType.integer(1)).executes(ctx -> {
							// killtps start tickTime
							int tickTime = IntegerArgumentType.getInteger(ctx,
									Lang.translate("command.killTPSCommand.argument.tickTime").asString());
							Create.lagger.setTickTime(tickTime);
							Create.lagger.setLagging(true);
							ctx.getSource().sendFeedback((Lang.createTranslationTextComponent(
									"command.killTPSCommand.status.slowed_by.1", tickTime)), true);
							ctx.getSource().sendFeedback(
									Lang.createTranslationTextComponent("command.killTPSCommand.status.usage.0"), true);

							return 1;
						})))
				.then(CommandManager.literal("stop").executes(ctx -> {
					// killtps stop
					Create.lagger.setLagging(false);
					ctx.getSource().sendFeedback(
							Lang.createTranslationTextComponent("command.killTPSCommand.status.slowed_by.2"), false);

					return 1;
				}));
	}
}
