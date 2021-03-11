package com.simibubi.create.foundation.resource.translation;

import net.devtech.arrp.json.lang.JLang;

import static com.simibubi.create.Create.id;

public final class EnUsTranslation extends CreateTranslation {
	public static final EnUsTranslation INSTANCE = new EnUsTranslation();
	private EnUsTranslation() {
		super("en_us", lang -> {
			ConfigLang.addAll(lang);
			BlockLang.addAll(lang);
		});
	}

	private static final class ConfigLang {
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
			text(lang, "fanParticleDensity", "Fan Particle Density", "client");
			text(lang, "enableRainbowDebug", "Enable Rainbow Debug", "client");
			text(lang, "experimentalRendering", "Experimental Rendering", "client");
			text(lang, "overlayOffsetX", "Overlay Offset X", "client");
			text(lang, "overlayOffsetY", "Overlay Offset Y", "client");
			text(lang, "smoothPlacementIndicator", "Smooth Placement Indicator", "client");

			// Tooltips
			tooltip(lang, "client", "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!");
			tooltip(lang, "enableTooltips", "Show item descriptions on Shift and controls on Ctrl.", "client");
			tooltip(lang, "enableOverstressedTooltip", "Display a tooltip when looking at overstressed components.", "client");
			tooltip(lang, "explainRenderErrors", "Log a stack-trace when rendering issues happen within a moving contraption.", "client");
			tooltip(lang, "enableRainbowDebug", "Show colourful debug information while the F3-Menu is open.", "client");
			tooltip(lang, "experimentalRendering", "Use modern OpenGL features to drastically increase performance.", "client");
			tooltip(lang, "overlayOffsetX", "Offset the overlay from goggle- and hover- information by this many pixels on the X axis; Use /create overlay", "client");
			tooltip(lang, "overlayOffsetY", "Offset the overlay from goggle- and hover- information by this many pixels on the Y axis; Use /create overlay", "client");
			tooltip(lang, "smoothPlacementIndicator", "Use an alternative indicator when showing where the assisted placement ends up relative to your crosshair", "client");
		}

		private static void addCommon(JLang lang) {
			text(lang, "common", "Common");
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
