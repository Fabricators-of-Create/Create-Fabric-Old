package com.simibubi.create.foundation.resource.translation;

import net.devtech.arrp.json.lang.JLang;

@SuppressWarnings("CommentedOutCode")
public final class EnUsTranslation extends CreateTranslation {
	public static final EnUsTranslation INSTANCE = new EnUsTranslation();
	private EnUsTranslation() {
		super("en_us", EnUsTranslation::addAll);
	}

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
		text(lang, "maxBeltLength", "Maximum Belt Length", KINETICS);
		text(lang, "crushingDamage", "Crushing Damage", KINETICS);
		text(lang, "maxMotorSpeed", "Maximum Motor Speed", KINETICS);
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
		text(lang, "maxBlocksMoved", "Maximum Blocks Moved", KINETICS);
		text(lang, "maxChassisRange", "Maximum Chassis Range", KINETICS);
		text(lang, "maxPistonPoles", "Maximum Piston Poles", KINETICS);
		text(lang, "maxRopeLength", "Maximum Rope Length", KINETICS);
		text(lang, "maxCartCouplingLength", "Max Minecart Coupling Length", KINETICS);
		text(lang, "mediumSpeed", "Medium Speed", KINETICS);
		text(lang, "fastSpeed", "Fast Speed", KINETICS);
		text(lang, "mediumStressImpact", "Medium Stress Impact", KINETICS);
		text(lang, "highStressImpact", "High Stress Impact", KINETICS);
		text(lang, "mediumCapacity", "Medium Capacity", KINETICS);
		text(lang, "highCapacity", "High Capacity", KINETICS);

		// Tooltips
		tooltip(lang, "disableStress", "Disable the Stress mechanic altogether.", KINETICS); // I don't want to stress
		tooltip(lang, "maxBeltLength", "Maximum length in blocks of mechanical belts.", KINETICS);
		tooltip(lang, "crushingDamage", "Damage dealt by active Crushing Wheels.", KINETICS);
		tooltip(lang, "maxMotorSpeed", "[in Revolutions per Minute] Maximum allowed speed of a configurable motor.", KINETICS);
		tooltip(lang, "waterWheelBaseSpeed", "[in Revolutions per Minute] Added rotation speed by a water wheel when at least one flow is present.", KINETICS);
		tooltip(lang, "waterWheelFlowSpeed", "[in Revolutions per Minute] Rotation speed gained by a water wheel for each side with running fluids. (halved if not against blades)", KINETICS);
		tooltip(lang, "furnaceEngineSpeed", "[in Revolutions per Minute] Base rotation speed for the furnace engine generator", KINETICS);
		tooltip(lang, "maxRotationSpeed", "[in Revolutions per Minute] Maximum allowed rotation speed for any Kinetic Tile.", KINETICS);
		tooltip(lang, "ignoreDeployerAttacks", "Select what mobs should ignore Deployers when attacked by them.", KINETICS);
		tooltip(lang, "kineticValidationFrequency", "Game ticks between Kinetic Blocks checking whether their source is still valid.", KINETICS);
		tooltip(lang, "crankHungerMultiplier", "Multiplier used for calculating exhaustion from speed when a crank is turned.", KINETICS);
		tooltip(lang, "fanPushDistance", "Maximum distance in blocks Fans can push entities.", KINETICS);
		tooltip(lang, "fanPullDistance", "Maximum distance in blocks from where Fans can pull entities.", KINETICS);
		tooltip(lang, "fanBlockCheckRate", "Game ticks between Fans checking for anything blocking their air flow.", KINETICS);
		tooltip(lang, "fanRotationArgmax", "[in Revolutions per Minute] Rotation speed at which the maximum stats of fans are reached.", KINETICS);
		tooltip(lang, "generatingFanSpeed", "[in Revolutions per Minute] Rotation speed generated by a vertical fan above fire.", KINETICS);
		tooltip(lang, "inWorldProcessingTime", "Game ticks required for a Fan-based processing recipe to take effect.", KINETICS);
		tooltip(lang, "maxBlocksMoved", "Maximum amount of blocks in a structure movable by Pistons, Bearings or other means.", KINETICS);
		tooltip(lang, "maxChassisRange", "Maximum value of a chassis attachment range.", KINETICS);
		tooltip(lang, "maxPistonPoles", "Maximum amount of extension poles behind a Mechanical Piston.", KINETICS);
		tooltip(lang, "maxRopeLength", "Max length of rope available off a Rope Pulley.", KINETICS);
		tooltip(lang, "maxCartCouplingLength", "Maximum allowed distance of two coupled minecarts.", KINETICS);
		tooltip(lang, "mediumSpeed", "[in Revolutions per Minute] Minimum speed of rotation to be considered 'medium'", KINETICS);
		tooltip(lang, "fastSpeed", "[in Revolutions per Minute] Minimum speed of rotation to be considered 'fast'", KINETICS);
		tooltip(lang, "mediumStressImpact", "[in Stress Units] Minimum stress impact to be considered 'medium'", KINETICS);
		tooltip(lang, "highStressImpact", "[in Stress Units] Minimum stress impact to be considered 'high'", KINETICS);
		tooltip(lang, "mediumCapacity", "[in Stress Units] Minimum added Capacity by sources to be considered 'medium'", KINETICS);
		tooltip(lang, "highCapacity", "[in Stress Units] Minimum added Capacity by sources to be considered 'high'", KINETICS);
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
