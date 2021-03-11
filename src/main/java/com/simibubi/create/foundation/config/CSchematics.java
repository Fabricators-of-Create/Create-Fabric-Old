package com.simibubi.create.foundation.config;

public class CSchematics {
	int maxSchematics = 10; // min 1, "The amount of Schematics a player can upload until previous ones are overwritten."
	int maxTotalSchematicSize = 256; // min 16, "[in KiloBytes]" "The maximum allowed file size of uploaded Schematics."
	int maxSchematicPacketSize = 1024; // min 256, max 32767, "[in Bytes]" "The maximum packet size uploaded Schematics are split into."
	int schematicIdleTimeout = 600; // min 100, "Amount of game ticks without new packets arriving until an active schematic upload process is discarded."
	// schematicannon group         public ConfigGroup schematicannon = group(0, "schematicannon", "Schematicannon");
	int schematicannonDelay = 10; // min 1, "Amount of game ticks between shots of the cannon. Higher => Slower"
	int schematicannonSkips = 10; // min 1, "Amount of block positions per tick scanned by a running cannon. Higher => Faster"
	float schematicannonGunpowderWorth = 20f; // min 0, max 100, "% of Schematicannon's Fuel filled by 1 Gunpowder."
	float schematicannonFuelUsage = 0.05f; // min 0, max 100,"% of Schematicannon's Fuel used for each fired block."
}
