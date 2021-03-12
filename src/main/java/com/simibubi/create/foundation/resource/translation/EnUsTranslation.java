package com.simibubi.create.foundation.resource.translation;

import net.devtech.arrp.json.lang.JLang;

@SuppressWarnings("CommentedOutCode")
public final class EnUsTranslation extends CreateTranslation {
	public static final EnUsTranslation INSTANCE = new EnUsTranslation();
	private EnUsTranslation() {
		super("en_us", ConfigLang::addAll);
	}

	private static final class ConfigLang {
		public static void addAll(JLang lang) {
			addClient(lang);
			addCommon(lang);
			addCuriosities(lang);
			addFluids(lang);
			addKinetics(lang);
		}

		private static void addClient(JLang lang) {
			final String CLIENT = "client";
			text(lang, CLIENT, "Client");

			// Option Names
			text(lang, "enableTooltips", "Enable Tooltips", CLIENT);
			text(lang, "enableOverstressedTooltip", "Enable Overstressed Tooltip", CLIENT);
			text(lang, "explainRenderErrors", "Explain Render Errors", CLIENT);
			text(lang, "fanParticleDensity", "Fan Particle Density", CLIENT);
			text(lang, "enableRainbowDebug", "Enable Rainbow Debug", CLIENT);
			text(lang, "experimentalRendering", "Experimental Rendering", CLIENT);
			text(lang, "overlayOffsetX", "Overlay Offset X", CLIENT);
			text(lang, "overlayOffsetY", "Overlay Offset Y", CLIENT);
			text(lang, "smoothPlacementIndicator", "Smooth Placement Indicator", CLIENT);

			// Tooltips
			tooltip(lang, CLIENT, "Client-only settings - If you're looking for general settings, look inside your worlds serverconfig folder!");
			tooltip(lang, "enableTooltips", "Show item descriptions on Shift and controls on Ctrl.", CLIENT);
			tooltip(lang, "enableOverstressedTooltip", "Display a tooltip when looking at overstressed components.", CLIENT);
			tooltip(lang, "explainRenderErrors", "Log a stack-trace when rendering issues happen within a moving contraption.", CLIENT);
			tooltip(lang, "enableRainbowDebug", "Show colourful debug information while the F3-Menu is open.", CLIENT);
			tooltip(lang, "experimentalRendering", "Use modern OpenGL features to drastically increase performance.", CLIENT);
			tooltip(lang, "overlayOffsetX", "Offset the overlay from goggle- and hover- information by this many pixels on the X axis; Use /create overlay", CLIENT);
			tooltip(lang, "overlayOffsetY", "Offset the overlay from goggle- and hover- information by this many pixels on the Y axis; Use /create overlay", CLIENT);
			tooltip(lang, "smoothPlacementIndicator", "Use an alternative indicator when showing where the assisted placement ends up relative to your crosshair", CLIENT);
		}

		private static void addCommon(JLang lang) {
			final String COMMON = "common";
			text(lang, COMMON, "Common");

			text(lang, "logBeErrors", "Log BE Errors", COMMON);
			tooltip(lang, "logBeErrors", "Forward caught BlockEntityExceptions to the log at debug level.", COMMON);
		}

		private static void addCuriosities(JLang lang) {
			final String CURIOSITIES = "curiosities";
			text(lang, CURIOSITIES, "Curiosities");

			text(lang, "maxSymmetryWandRange", "Max Symmetry Wand Range", CURIOSITIES);
			tooltip(lang, "maxSymmetryWandRange", "The Maximum Distance to an active mirror for the symmetry wand to trigger.", CURIOSITIES);
		}

		private static void addFluids(JLang lang) {
			final String FLUIDS = "fluids";
			text(lang, FLUIDS, "Fluids");

			// Option Names
			text(lang, "fluidTankCapacity", "Fluid Tank Capacity", FLUIDS);
			text(lang, "fluidTankMaxHeight", "Fluid Tank Max Height", FLUIDS);
			text(lang, "mechanicalPumpRange", "Mechanical Pump Range", FLUIDS);
			text(lang, "hosePulleyBlockThreshold", "Hose Pulley Block Threshold", FLUIDS);
			text(lang, "hosePulleyRange", "Hose Pulley Range", FLUIDS);

			// Tooltips
			tooltip(lang, "fluidTankCapacity", "[In Buckets] The amount of liquid a tank can hold per block.", FLUIDS);
			tooltip(lang, "fluidTankMaxHeight", "[In Blocks] The maximum height a fluid tank can reach.", FLUIDS);
			tooltip(lang, "mechanicalPumpRange", "[In Blocks] The maximum distance a mechanical pump can push or pull liquids on either side.", FLUIDS);
			tooltip(lang, "hosePulleyBlockThreshold", "[In Blocks]\n[-1 to disable this behaviour] The minimum amount of fluid blocks the hose pulley needs to find before deeming it an infinite source.", FLUIDS);
			tooltip(lang, "hosePulleyRange", "[In Blocks] The maximum distance a hose pulley can draw fluid blocks from.", FLUIDS);
		}

		private static void addKinetics(JLang lang) {
			final String KINETICS = "kinetics";
			text(lang, KINETICS, "Kinetics");

			// Option Names
			text(lang, "disableStress", "Disable Stress", KINETICS); // I don't want to stress
			text(lang, "maxBeltLength", "Max Belt Length", KINETICS);
			text(lang, "crushingDamage", "Crushing Damage", KINETICS);
			text(lang, "maxMotorSpeed", "Max Motor Speed", KINETICS);
			text(lang, "waterWheelBaseSpeed", "Water Wheel Base Speed", KINETICS);
			text(lang, "waterWheelFlowSpeed", "Water Wheel Flow Speed", KINETICS);
			text(lang, "furnaceEngineSpeed", "Furnace Engine Speed", KINETICS);
			text(lang, "maxRotationSpeed", "Maximum Rotation Speed", KINETICS);
			text(lang, "ignoreDeployerAttacks", "Ignore Deployer Attacks", KINETICS);
			text(lang, "kineticValidationFrequency", "Kinetic Validation Frequency", KINETICS);
			text(lang, "crankHungerMultiplier", "Crank Hunger Multiplier", KINETICS);
			text(lang, "fanPushDistance", "Fan Push Distance", KINETICS);
			text(lang, "fanPullDistance", "Fan Pull Distance", KINETICS);
			text(lang, "fanBlockCheckRate", "Fan Block CheckRate", KINETICS);
			text(lang, "fanRotationArgmax", "Fan Rotation Arg Max", KINETICS);
			text(lang, "generatingFanSpeed", "Generating Fan Speed", KINETICS);
			text(lang, "inWorldProcessingTime", "In-World Processing Time", KINETICS);
			text(lang, "maxBlocksMoved", "Max Blocks Moved", KINETICS);
			text(lang, "maxChassisRange", "Max Chassis Range", KINETICS);
			text(lang, "maxPistonPoles", "Max Piston Poles", KINETICS);
			text(lang, "maxRopeLength", "Max Rope Length", KINETICS);
			text(lang, "maxCartCouplingLength", "Max Minecart Coupling Length", KINETICS);
			text(lang, "mediumSpeed", "Medium Speed", KINETICS);
			text(lang, "fastSpeed", "Fast Speed", KINETICS);
			text(lang, "mediumStressImpact", "Medium Stress Impact", KINETICS);
			text(lang, "highStressImpact", "High Stress Impact", KINETICS);
			text(lang, "mediumCapacity", "Medium Capacity", KINETICS);
			text(lang, "highCapacity", "High Capacity", KINETICS);

			// Tooltips
		}
	}

	/*
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
	*/
}
