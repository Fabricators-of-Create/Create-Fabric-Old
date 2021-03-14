package com.simibubi.create.content;

import java.util.IdentityHashMap;
import java.util.Map;

import com.simibubi.create.foundation.item.ItemDescription.Palette;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum AllSections {

	/** Create's kinetic mechanisms */
	KINETICS(Palette.Red),

	/** Item transport and other Utility */
	LOGISTICS(Palette.Yellow),

	/** Tools for strucuture movement and replication */
	SCHEMATICS(Palette.Blue),

	/** Decorative blocks */
	PALETTES(Palette.Green),

	/** Base materials, ingredients and tools */
	MATERIALS(Palette.Green),
	
	/** Helpful gadgets and other shenanigans */
	CURIOSITIES(Palette.Purple),

	/** Fallback section */
	UNASSIGNED(Palette.Gray)

	;

	private static Map<Object, AllSections> sectionLookup = new IdentityHashMap<>();

	private Palette tooltipPalette;

	private AllSections(Palette tooltipPalette) {
		this.tooltipPalette = tooltipPalette;
	}

	public Palette getTooltipPalette() {
		return tooltipPalette;
	}

	public static void addToSection(Object entry, AllSections section) {
		sectionLookup.put(entry, section);
	}

	public static AllSections of(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof BlockItem)
			return ofBlock(((BlockItem) item).getBlock());
		return ofItem(item);
	}

	static AllSections ofItem(Item item) {
		return sectionLookup.getOrDefault(item, UNASSIGNED);
	}

	static AllSections ofBlock(Block block) {
		return sectionLookup.getOrDefault(block, UNASSIGNED);
	}
}
