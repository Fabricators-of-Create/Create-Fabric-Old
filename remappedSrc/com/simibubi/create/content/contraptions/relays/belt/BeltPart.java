package com.simibubi.create.content.contraptions.relays.belt;

import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.util.StringIdentifiable;

public enum BeltPart implements StringIdentifiable {
	START, MIDDLE, END, PULLEY;

	@Override
	public String asString() {
		return Lang.asId(name());
	}
}