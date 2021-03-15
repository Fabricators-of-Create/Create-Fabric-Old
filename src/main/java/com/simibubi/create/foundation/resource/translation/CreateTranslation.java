package com.simibubi.create.foundation.resource.translation;

import com.simibubi.create.Create;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.simibubi.create.Create.ID;

/**
 * A translation to a Minecraft-supported language that can
 * be added to create through {@link com.simibubi.create.foundation.resource.TranslationsHolder}.
 */
public class CreateTranslation {
	public final String languageCode;
	public final Consumer<JLang> register;

	public CreateTranslation(String languageCode, Consumer<JLang> register) {
		this.languageCode = languageCode;
		this.register = register;
	}

	public final void register(RuntimeResourcePack pack) {
		JLang lang = JLang.lang();
		register.accept(lang);

		// This ID is here to avoid conflicting with actual Json so everything can be used together.
		pack.addLang(new Identifier(Create.ID + "_rrp", languageCode), lang);
	}

	protected static void text(JLang lang, String option, String name, String... parents) {
		ConfigHelper.text(lang, option, name, parents);
	}

	protected static void tooltip(JLang lang, String option, String tooltip, String... parents) {
		ConfigHelper.tooltip(lang, option, tooltip, parents);
	}

	protected static void prefixText(JLang lang, String option, String prefixText, String... parents) {
		ConfigHelper.prefixText(lang, option, prefixText, parents);
	}

	protected static final class ConfigHelper {
		public static String getOptionKey(String option, String... parents) {
			StringBuilder str = new StringBuilder("text.autoconfig." + ID + ".option");
			if (parents.length > 0) {
				Arrays.stream(parents).forEach(parent -> str.append(".").append(parent));
			}
			str.append(".").append(option);

			return str.toString();
		}

		public static void text(JLang lang, String option, String name, String... parents) {
			lang.entry(getOptionKey(option, parents), name);
		}

		public static void tooltip(JLang lang, String option, String tooltip, String... parents) {
			String key = getOptionKey(option, parents) + ".@Tooltip";

			String[] split = tooltip.split("\n");
			if (split.length == 1) {
				lang.entry(key, split[0]);
				return;
			}

			for (int i = 0; i < split.length; i++) {
				lang.entry(key + "[" + i + "]", split[i]);
			}
		}

		public static void prefixText(JLang lang, String option, String prefixText, String... parents) {
			lang.entry(getOptionKey(option, parents) + ".@PrefixText", prefixText);
		}
	}
}
