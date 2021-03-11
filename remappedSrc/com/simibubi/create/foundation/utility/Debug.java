package com.simibubi.create.foundation.utility;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

/** Deprecated so simi doensn't forget to remove debug calls **/
@Environment(value = EnvType.CLIENT)
public class Debug {

	@Deprecated
	public static void debugChat(String message) {
		if (MinecraftClient.getInstance().player != null)
			MinecraftClient.getInstance().player.sendMessage(new LiteralText(message), false);
	}

	@Deprecated
	public static void debugChatAndShowStack(String message, int depth) {
		if (MinecraftClient.getInstance().player != null)
			MinecraftClient.getInstance().player
					.sendMessage(new LiteralText(message + " @" + debugStack(depth)), false);
	}

	@Deprecated
	public static void debugMessage(String message) {
		if (MinecraftClient.getInstance().player != null)
			MinecraftClient.getInstance().player.sendMessage(new LiteralText(message), true);
	}

	@Deprecated
	public static String getLogicalSide() {
		/**return EffectiveSide.get().isClient() ? "CL" : "SV";*/
		return "";
	}

	@Deprecated
	public static String debugStack(int depth) {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
		String text = "[" + Formatting.GOLD + getLogicalSide() + Formatting.WHITE + "] ";
		for (int i = 1; i < depth + 2 && i < stackTraceElements.length; i++) {
			StackTraceElement e = stackTraceElements[i];
			if (e.getClassName().equals(Debug.class.getName()))
				continue;
			text = text + Formatting.YELLOW + e.getMethodName() + Formatting.WHITE + ", ";
		}
		return text + Formatting.GRAY + " ...";
	}
	
	@Deprecated
	public static void markTemporary() {};

}
