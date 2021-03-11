package com.simibubi.create.foundation.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.TranslatableText;

@Config(name = "create")
public class AllConfigs implements ConfigData {

	public static void register() {
		AutoConfig.register(AllConfigs.class, Toml4jConfigSerializer::new);
	}

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
}