package com.simibubi.create.content.contraptions;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.WorldHelper;
import net.minecraft.world.WorldAccess;

import java.util.HashMap;
import java.util.Map;

public class TorquePropagator {
	static Map<WorldAccess, Map<Long, KineticNetwork>> networks = new HashMap<>();

	public void onLoadWorld(WorldAccess world) {
		networks.put(world, new HashMap<>());
		Create.logger.debug("Prepared Kinetic Network Space for " + WorldHelper.getDimensionID(world));
	}

	public void onUnloadWorld(WorldAccess world) {
		networks.remove(world);
		Create.logger.debug("Removed Kinetic Network Space for " + WorldHelper.getDimensionID(world));
	}

	public KineticNetwork getOrCreateNetworkFor(KineticBlockEntity be) {
		Long id = be.network;
		KineticNetwork network;
		Map<Long, KineticNetwork> map = networks.get(be.getWorld());
		if (id == null)
			return null;

		if (!map.containsKey(id)) {
			network = new KineticNetwork();
			network.id = be.network;
			map.put(id, network);
		}
		network = map.get(id);
		return network;
	}

}
