package com.simibubi.create.foundation.utility;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;

public class WorldHelper {
	public static Identifier getDimensionID(WorldAccess world) {
		return world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).getId(world.getDimension());
	}
}
