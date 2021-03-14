package com.simibubi.create.foundation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

@Config(name = "create")
public class AllConfigs implements ConfigData {
	// use this to use the config
	// ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

	/* see CServer (old)
	static String recipes = "Packmakers' control panel for internal recipe compat";
	static String schematics = "Everything related to Schematic tools";
	static String kinetics = "Parameters and abilities of Create's kinetic mechanisms";
	static String fluids = "Create's liquid manipulation tools";
	static String logistics = "Tweaks for logistical components";
	static String curiosities = "Gadgets and other Shenanigans added by Create";
	static String infrastructure = "The Backbone of Create";
	 */

	// creating collapsible groups
	@ConfigEntry.Gui.CollapsibleObject
	CClient client = new CClient();
	@ConfigEntry.Gui.CollapsibleObject
	CCommon common = new CCommon();
	@ConfigEntry.Gui.CollapsibleObject
	CCuriosities curiosities = new CCuriosities();
	@ConfigEntry.Gui.CollapsibleObject
	CFluids fluids = new CFluids();
	@ConfigEntry.Gui.CollapsibleObject
	CKinetics kinetics = new CKinetics();
	@ConfigEntry.Gui.CollapsibleObject
	CLogistics logistics = new CLogistics();
	@ConfigEntry.Gui.CollapsibleObject
	CRecipes recipes = new CRecipes();
	@ConfigEntry.Gui.CollapsibleObject
	CSchematics schematics = new CSchematics();
	@ConfigEntry.Gui.CollapsibleObject
	CServer server = new CServer();
	@ConfigEntry.Gui.CollapsibleObject
	CStress stress = new CStress();
	@ConfigEntry.Gui.CollapsibleObject
	CWorldGen worldGen = new CWorldGen();



	public static void register() {
		validateConfigs();
		AutoConfig.register(AllConfigs.class, Toml4jConfigSerializer::new);
	}

	// MY EYES
	public static void validateConfigs() { // this is probably the wrong way to do it but post-validation documentation is literally 3 sentences
		final Logger LOGGER = LogManager.getLogger("Create Config Validation");
		String reset = " reset! Invalid Value!";
		if (!(CClient.fanParticleDensity >= 0) ||
			(CClient.fanParticleDensity <= 1)) 				{ CClient.fanParticleDensity = 0.5f; 		LOGGER.warn("fanParticleDensity" + reset);}
		if (!(CClient.overlayOffsetX >= Integer.MIN_VALUE)) { CClient.overlayOffsetX = 0; 				LOGGER.warn("overlayOffsetX" + reset);} // i know what the errors say. gonna leave it anyway.
		if (!(CClient.overlayOffsetY >= Integer.MIN_VALUE)) { CClient.overlayOffsetY = 0; 				LOGGER.warn("overlayOffsetY" + reset);}
		if (!(CCuriosities.maxSymmetryWandRange > 10)) 		{ CCuriosities.maxSymmetryWandRange = 50; 	LOGGER.warn("maxSymmetryWandRange" + reset);}
		//if (!(CCuriosities.zapperUndoLogLength > 0)) 		{ CCuriosities.zapperUndoLogLength = 10; 	LOGGER.warn("zapperUndoLogLength" + reset");}
		if (!(CFluids.fluidTankCapacity >= 1)) 				{ CFluids.fluidTankCapacity = 8; 			LOGGER.warn("fluidTankCapacity" + reset);}
		if (!(CFluids.fluidTankMaxHeight >= 1)) 			{ CFluids.fluidTankMaxHeight = 32; 			LOGGER.warn("fluidTankCapacity" + reset);}
		if (!(CFluids.mechanicalPumpRange >= 1)) 			{ CFluids.mechanicalPumpRange = 16; 		LOGGER.warn("mechanicalPumpRange" + reset);}
		if (!(CFluids.hosePulleyBlockThreshold >= -1)) 		{ CFluids.hosePulleyBlockThreshold = 10000; LOGGER.warn("hosePulleyBlockThreshold" + reset);}
		if (!(CFluids.hosePulleyRange >= 1)) 				{ CFluids.hosePulleyRange = 128; 			LOGGER.warn("hosePulleyRange" + reset);}
		if (!(CKinetics.maxBeltLength >= 5)) 				{ CKinetics.maxBeltLength = 20; 			LOGGER.warn("maxBeltLength" + reset);}
		if (!(CKinetics.crushingDamage >= 0)) 				{ CKinetics.crushingDamage = 4; 			LOGGER.warn("crushingDamage" + reset);}
		if (!(CKinetics.maxMotorSpeed >= 64)) 				{ CKinetics.maxMotorSpeed = 256; 			LOGGER.warn("maxMotorSpeed" + reset);}
		if (!(CKinetics.waterWheelBaseSpeed >= 1)) 			{ CKinetics.waterWheelBaseSpeed = 4; 		LOGGER.warn("waterWheelBaseSpeed" + reset);}
		if (!(CKinetics.waterWheelFlowSpeed >= 1)) 			{ CKinetics.waterWheelFlowSpeed = 4; 		LOGGER.warn("waterWheelFlowSpeed" + reset);}
		if (!(CKinetics.furnaceEngineSpeed >= 1)) 			{ CKinetics.furnaceEngineSpeed = 16; 		LOGGER.warn("furnaceEngineSpeed" + reset);}
		if (!(CKinetics.maxRotationSpeed >= 64)) 			{ CKinetics.maxRotationSpeed = 256; 		LOGGER.warn("maxRotationSpeed" + reset);}
		if (!(CKinetics.kineticValidationFrequency >= 5)) 	{ CKinetics.kineticValidationFrequency = 60;LOGGER.warn("kineticValidationFrequency" + reset);}
		if (!(CKinetics.crankHungerMultiplier >= 0) ||
			(CKinetics.crankHungerMultiplier <= 1)) 		{ CKinetics.crankHungerMultiplier = 0.01f; 	LOGGER.warn("crankHungerMultiplier" + reset);}
		if (!(CKinetics.fanPushDistance >= 5)) 				{ CKinetics.fanPushDistance = 20;			LOGGER.warn("fanPushDistance" + reset);}
		if (!(CKinetics.fanPullDistance >= 5)) 				{ CKinetics.fanPullDistance = 20;			LOGGER.warn("fanPullDistance" + reset);}
		if (!(CKinetics.fanBlockCheckRate >= 10)) 			{ CKinetics.fanBlockCheckRate = 30;			LOGGER.warn("fanBlockCheckRate" + reset);}
		if (!(CKinetics.fanRotationArgmax >= 64)) 			{ CKinetics.fanRotationArgmax = 256;		LOGGER.warn("fanRotationArgmax" + reset);}
		if (!(CKinetics.generatingFanSpeed >= 0)) 			{ CKinetics.generatingFanSpeed = 4;			LOGGER.warn("generatingFanSpeed" + reset);}
		if (!(CKinetics.maxBlocksMoved >= 1)) 				{ CKinetics.maxBlocksMoved = 2048;			LOGGER.warn("maxBlocksMoved" + reset);}
		if (!(CKinetics.maxChassisRange >= 1)) 				{ CKinetics.maxChassisRange = 16;			LOGGER.warn("maxChassisRange" + reset);}
		if (!(CKinetics.maxPistonPoles >= 1)) 				{ CKinetics.maxPistonPoles = 64;			LOGGER.warn("maxPistonPoles" + reset);}
		if (!(CKinetics.maxRopeLength >= 1)) 				{ CKinetics.maxRopeLength = 128;			LOGGER.warn("maxRopeLength" + reset);}
		if (!(CKinetics.maxCartCouplingLength >= 1)) 		{ CKinetics.maxCartCouplingLength = 32;		LOGGER.warn("maxCartCouplingLength" + reset);}
		/*if (!(CKinetics.crankHungerMultiplier >= 0) ||
			(CKinetics.crankHungerMultiplier <= 1)) 		{ CKinetics.crankHungerMultiplier = 0.01f; 	LOGGER.warn("crankHungerMultiplier" + reset);}
		if (!(CKinetics.crankHungerMultiplier >= 0) ||
			(CKinetics.crankHungerMultiplier <= 1)) 		{ CKinetics.crankHungerMultiplier = 0.01f; 	LOGGER.warn("crankHungerMultiplier" + reset);}
		if (!(CKinetics.crankHungerMultiplier >= 0) ||
			(CKinetics.crankHungerMultiplier <= 1)) 		{ CKinetics.crankHungerMultiplier = 0.01f; 	LOGGER.warn("crankHungerMultiplier" + reset);}
		if (!(CKinetics.crankHungerMultiplier >= 0) ||
			(CKinetics.crankHungerMultiplier <= 1)) 		{ CKinetics.crankHungerMultiplier = 0.01f; 	LOGGER.warn("crankHungerMultiplier" + reset);}
		if (!(CKinetics.crankHungerMultiplier >= 0) ||
			(CKinetics.crankHungerMultiplier <= 1)) 		{ CKinetics.crankHungerMultiplier = 0.01f; 	LOGGER.warn("crankHungerMultiplier" + reset);}
		if (!(CKinetics.crankHungerMultiplier >= 0) ||
			(CKinetics.crankHungerMultiplier <= 1)) 		{ CKinetics.crankHungerMultiplier = 0.01f; 	LOGGER.warn("crankHungerMultiplier" + reset);}*/
	}
}