package com.simibubi.create.foundation.mixinterface;

import net.fabricmc.fabric.api.util.TriState;

public interface EntityTypeExtension {
	TriState getAlwaysUpdateVelocity();

	void setAlwaysUpdateVelocity(TriState value);
}
