package com.simibubi.create.foundation.utility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.thread.EffectiveSide;

/** Deprecated so simi doensn't forget to remove debug calls **/
@Environment(EnvType.CLIENT)
public class Debug {

	@Deprecated
	public static void debugChat(Text message) {
		if (MinecraftClient.getInstance().player != null)
			MinecraftClient.getInstance().player.sendMessage(message, false);
	}

	@Deprecated
	public static void debugChatAndShowStack(Text message, int depth) {
		if (MinecraftClient.getInstance().player != null)
			MinecraftClient.getInstance().player
					.sendMessage(message.copy().append("@").append(debugStack(depth)), false);
	}

	@Deprecated
	public static void debugMessage(Text message) {
		if (MinecraftClient.getInstance().player != null)
			MinecraftClient.getInstance().player.sendMessage(message, true);
	}

	@Deprecated
	public static String getLogicalSide() {
		return EffectiveSide.get().isClient() ? "CL" : "SV";
	}

	@Deprecated
	public static Text debugStack(int depth) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		MutableText text = new LiteralText("[").append(new LiteralText(getLogicalSide()).formatted(Formatting.GOLD)).append("] ");
		for (int i = 1; i < depth + 2 && i < stackTraceElements.length; i++) {
			StackTraceElement e = stackTraceElements[i];
			if (e.getClassName().equals(Debug.class.getName()))
				continue;
			text.append(new LiteralText(e.getMethodName()).formatted(Formatting.YELLOW)).append(", ");
		}
		return text.append(new LiteralText(" ...").formatted(Formatting.GRAY));
	}
	
	@Deprecated
	public static void markTemporary() {}

}
