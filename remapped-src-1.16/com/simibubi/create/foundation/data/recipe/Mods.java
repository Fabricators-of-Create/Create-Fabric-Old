package com.simibubi.create.foundation.data.recipe;

import net.minecraft.util.Identifier;

public enum Mods {

	MEK("mekanism", true), 
	TH("thermal", false), 
	MW("mysticalworld", false), 
	SM("silents_mechanisms", false), 
	IE("immersiveengineering", true),
	EID("eidolon", false),
	INF("iceandfire", false)

	;

	private String id;
	private boolean reversedPrefix;

	private Mods(String id, boolean reversedPrefix) {
		this.id = id;
		this.reversedPrefix = reversedPrefix;}

	public Identifier ingotOf(String type) {
		return new Identifier(id, reversedPrefix ? "ingot_" + type : type + "_ingot");
	}
	
	public Identifier nuggetOf(String type) {
		return new Identifier(id, reversedPrefix ? "nugget_" + type : type + "_nugget");
	}
	
	public String getId() {
		return id;
	}
	
}
