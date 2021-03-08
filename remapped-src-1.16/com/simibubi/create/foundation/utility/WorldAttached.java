package com.simibubi.create.foundation.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import net.minecraft.world.WorldAccess;

public class WorldAttached<T> {

	static List<Map<WorldAccess, ?>> allMaps = new ArrayList<>();
	Map<WorldAccess, T> attached;
	private Supplier<T> factory;

	public WorldAttached(Supplier<T> factory) {
		this.factory = factory;
		attached = new HashMap<>();
		allMaps.add(attached);
	}
	
	public static void invalidateWorld(WorldAccess world) {
		allMaps.forEach(m -> m.remove(world));
	}
	
	@Nullable
	public T get(WorldAccess world) {
		T t = attached.get(world);
		if (t != null)
			return t;
		T entry = factory.get();
		put(world, entry);
		return entry;
	}
	
	public void put(WorldAccess world, T entry) {
		attached.put(world, entry);
	}
	
}
