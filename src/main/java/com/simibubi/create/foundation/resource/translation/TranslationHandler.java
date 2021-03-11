package com.simibubi.create.foundation.resource.translation;

import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.lang.JLang;

import java.util.Arrays;

import static com.simibubi.create.Create.ID;
import static com.simibubi.create.Create.id;
import static net.devtech.arrp.json.lang.JLang.lang;

public final class TranslationHandler {
	public static void addLang(RuntimeResourcePack rp) {
		rp.addLang(id("en_us"), addLang(lang()));
	}

	private static JLang addLang(JLang lang) {
		ConfigLang.addAll(lang);
		BlockLang.addAll(lang);

		return lang;
	}

	public static final class ConfigLang {
		public static void addAll(JLang lang) {
			addClient(lang);
			addCommon(lang);
		}

		private static void addClient(JLang lang) {
			// Option Names
			text(lang, "client", "Client");
			text(lang, "enableTooltips", "Enable Tooltips", "client");
			text(lang, "enableOverstressedTooltip", "Enable Overstressed Tooltip", "client");
			text(lang, "explainRenderErrors", "Explain Render Errors", "client");

			// Tooltips
			tooltip(lang, "client", "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!");
			tooltip(lang, "enableTooltips", "Show item descriptions on Shift and controls on Ctrl.", "client");
			tooltip(lang, "enableOverstressedTooltip", "Display a tooltip when looking at overstressed components.", "client");
			tooltip(lang, "explainRenderErrors", "Log a stack-trace when rendering issues happen within a moving contraption.", "client");
		}

		private static void addCommon(JLang lang) {
			text(lang, "common", "Common");
		}

		private static String getOptionKey(String option, String... parents) {
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

	// Consider: The following? Will not be able to copy Forge Create lang when it updates

	private static final class BlockLang {
		public static void addAll(JLang lang) {
			lang.block(id("acacia_window"), "Acacia Window");
			lang.block(id("acacia_window_pane"), "Acacia Window Pane");

			// TODO: All other blocks
		}
	}

	// TODO: Item lang
	// TODO: Advancement lang
	// TODO: Item group lang
	// TODO: Death lang

	// TODO: Create recipe lang
	// TODO: Create generic lang
	// TODO: Create action lang
	// TODO: Create keyinfo lang
	// TODO: Create GUI lang
	// TODO: Create symmetry lang
	// TODO: Create orientation lang
	// TODO: Create terrainzapper lang
	// TODO: Create blockzapper lang
	// TODO: Create minecart coupling lang
	// TODO: Create contraptions lang
	// TODO: Create logistics lang
	// TODO: Create schematic and quill lang
	// TODO: Create schematic lang
	// TODO: Create material checklist lang
	// TODO: Create item attributes lang
	// TODO: Create tooltip lang
	// TODO: Create mechanical arm lang
	// TODO: Create tunnel lang
	// TODO: Create hint lang
	// TODO: Create command lang
	// TODO: Create subtitle lang
}
