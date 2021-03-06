package com.smellypengu.createfabric.foundation.utility;

import com.smellypengu.createfabric.Create;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Lang {

	public static String translate(String key, Object... args) {
		return createTranslationTextComponent(key, args).getString();
	}

	public static TranslatableText createTranslationTextComponent(String key, Object... args) {
		return new TranslatableText(Create.ID + "." + key, args);
	}

	public static void sendStatus(PlayerEntity player, String key, Object... args) {
		player.sendMessage(createTranslationTextComponent(key, args), true); // TODO sendmessage INSTEAD OF sendStatusMessage??
	}

	public static List<String> translatedOptions(String prefix, String... keys) {
		List<String> result = new ArrayList<>(keys.length);
		for (String key : keys) {
			result.add(translate(prefix + "." + key));
		}
		return result;
	}

	public static String asId(String name) {
		return name.toLowerCase(Locale.ENGLISH);
	}

}
