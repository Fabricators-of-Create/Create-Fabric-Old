package com.simibubi.create.foundation.resource;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;

import java.util.Arrays;

import static com.simibubi.create.Create.ID;
import static com.simibubi.create.Create.id;
import static net.devtech.arrp.json.lang.JLang.lang;

@SuppressWarnings("SameParameterValue")
public final class AllResources {
	public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(id(ID).toString());

	private static JLang addTexts(JLang lang) {
		ConfigLang.addAll(lang);

		return lang;
	}

	private static final class ConfigLang {
		public static void addAll(JLang lang) {
			addClient(lang);
			addCommon(lang);
		}

		private static void addClient(JLang lang) {
			// Option Names
			configText(lang, "client", "Client");
			configText(lang, "enableTooltips", "Enable Tooltips", "client");

			// Tooltips
			configTooltip(lang, "client", "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!");
			configTooltip(lang, "enableTooltips", "Show item descriptions on Shift and controls on Ctrl.", "client");
		}

		private static void addCommon(JLang lang) {
			configText(lang, "common", "Common");
		}

		private static String getOptionKey(String option, String... parents) {
			StringBuilder str = new StringBuilder("text.autoconfig." + ID + ".option");
			if (parents.length > 0) {
				Arrays.stream(parents).forEach(parent -> str.append(".").append(parent));
			}
			str.append(".").append(option);

			return str.toString();
		}

		private static void configText(JLang lang, String option, String name, String... parents) {
			lang.entry(getOptionKey(option, parents), name);
		}

		private static void configTooltip(JLang lang, String option, String tooltip, String... parents) {
			String key = getOptionKey(option, parents) + ".@Tooltip";

			String[] split = key.split("\n");
			if (split.length == 1) {
				lang.entry(key, split[0]);
				return;
			}

			for (int i = 0; i < split.length; i++) {
				lang.entry(key + "[" + i + "]", split[i]);
			}
		}
	}

	private static void addResources(RuntimeResourcePack pack) {
		pack.addLang(id("en_us"), addTexts(lang()));
	}

	public static void initialize() {
		addResources(RESOURCE_PACK);

		RRPCallback.EVENT.register(a -> a.add(RESOURCE_PACK));
	}
}
