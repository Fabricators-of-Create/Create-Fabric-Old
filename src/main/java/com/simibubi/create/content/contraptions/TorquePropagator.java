package com.simibubi.create.content.contraptions;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import net.minecraft.world.WorldAccess;

import java.util.HashMap;
import java.util.Map;

public class TorquePropagator {

	static Map<WorldAccess, Map<Long, KineticNetwork>> networks = new HashMap<>();

	public void onLoadWorld(WorldAccess world) {
		networks.put(world, new HashMap<>());
		Create.logger.debug("Prepared Kinetic Network Space for " + world.getDimension()); // TODO COULD BE WRONG HERE
	}

	public void onUnloadWorld(WorldAccess world) {
		networks.remove(world);
		Create.logger.debug("Removed Kinetic Network Space for " + world.getDimension());
	}

	public KineticNetwork getOrCreateNetworkFor(KineticBlockEntity te) {
		Long id = te.network;
		KineticNetwork network;
		Map<Long, KineticNetwork> map = networks.get(te.getWorld());
		if (id == null)
			return null;

		if (!map.containsKey(id)) {
			network = new KineticNetwork();
			network.id = te.network;
			map.put(id, network);
		}
		network = map.get(id);
		return network;
	}

}
