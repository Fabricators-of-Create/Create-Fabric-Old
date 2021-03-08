package com.simibubi.create.foundation.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.simibubi.create.Create;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class Lang {

	public static TranslatableText translate(String key, Object... args) {
		return createTranslationTextComponent(key, args);
	}

	public static TranslatableText createTranslationTextComponent(String key, Object... args) {
		return new TranslatableText(Create.ID + "." + key, args);
	}

	public static void sendStatus(PlayerEntity player, String key, Object... args) {
		player.sendMessage(createTranslationTextComponent(key, args), true);
	}

	public static List<Text> translatedOptions(String prefix, String... keys) {
		List<Text> result = new ArrayList<>(keys.length);
		for (String key : keys) {
			result.add(translate(prefix + "." + key));
		}
		return result;
	}

	public static String asId(String name) {
		return name.toLowerCase(Locale.ENGLISH);
	}

}
