package com.simibubi.create.content.contraptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.simibubi.create.content.contraptions.base.KineticBlockEntity;

public class KineticNetwork {

	public Long id;
	public boolean initialized;
	public boolean containsFlywheel;
	public Map<KineticBlockEntity, Float> sources;
	public Map<KineticBlockEntity, Float> members;

	private float currentCapacity;
	private float currentStress;
	private float unloadedCapacity;
	private float unloadedStress;
	private int unloadedMembers;

	public KineticNetwork() {
		sources = new HashMap<>();
		members = new HashMap<>();
		containsFlywheel = false;
	}

	private static float getStressMultiplierForSpeed(float speed) {
		return Math.abs(speed);
	}

	public void initFromTE(float maxStress, float currentStress, int members) {
		unloadedCapacity = maxStress;
		unloadedStress = currentStress;
		unloadedMembers = members;
		initialized = true;
		updateStress();
		updateCapacity();
	}

	public void addSilently(KineticBlockEntity te, float lastCapacity, float lastStress) {
		if (members.containsKey(te))
			return;
		if (te.isSource()) {
			unloadedCapacity -= lastCapacity * getStressMultiplierForSpeed(te.getGeneratedSpeed());
			float addedStressCapacity = te.calculateAddedStressCapacity();
			sources.put(te, addedStressCapacity);
			//containsFlywheel |= te instanceof FlywheelTileEntity; TODO CONTAINS FLYWHEEL CHECK
		}

		unloadedStress -= lastStress * getStressMultiplierForSpeed(te.getTheoreticalSpeed());
		float stressApplied = te.calculateStressApplied();
		members.put(te, stressApplied);

		unloadedMembers--;
		if (unloadedMembers < 0)
			unloadedMembers = 0;
		if (unloadedCapacity < 0)
			unloadedCapacity = 0;
		if (unloadedStress < 0)
			unloadedStress = 0;
	}

	public void add(KineticBlockEntity te) {
		if (members.containsKey(te))
			return;
		if (te.isSource())
			sources.put(te, te.calculateAddedStressCapacity());
		members.put(te, te.calculateStressApplied());
		updateFromNetwork(te);
		te.networkDirty = true;
	}

	public void updateCapacityFor(KineticBlockEntity te, float capacity) {
		sources.put(te, capacity);
		updateCapacity();
	}

	public void updateStressFor(KineticBlockEntity te, float stress) {
		members.put(te, stress);
		updateStress();
	}

	public void remove(KineticBlockEntity te) {
		if (!members.containsKey(te))
			return;
		if (te.isSource())
			sources.remove(te);
		members.remove(te);
		te.updateFromNetwork(0, 0, 0);

		if (members.isEmpty()) {
			TorquePropagator.networks.get(te.getWorld())
				.remove(this.id);
			return;
		}

		members.keySet()
			.stream()
			.findFirst()
			.map(member -> member.networkDirty = true);
	}

	public void sync() {
		for (KineticBlockEntity te : members.keySet())
			updateFromNetwork(te);
	}

	private void updateFromNetwork(KineticBlockEntity te) {
		boolean wasOverStressed = te.isOverStressed();
		te.updateFromNetwork(currentCapacity, currentStress, getSize());
		if (!wasOverStressed && te.isOverStressed() && te.getTheoreticalSpeed() != 0) {
			/*AllTriggers.triggerForNearbyPlayers(AllTriggers.OVERSTRESSED, te.getWorld(), te.getPos(), 4); TODO ADVANCEMENT
			if (containsFlywheel)
				//AllTriggers.triggerForNearbyPlayers(AllTriggers.OVERSTRESS_FLYWHEEL, te.getWorld(), te.getPos(), 4); TODO ADVANCEMENT*/
		}
	}

	public void updateCapacity() {
		float newMaxStress = calculateCapacity();
		if (currentCapacity != newMaxStress) {
			currentCapacity = newMaxStress;
			sync();
		}
	}

	public void updateStress() {
		float newStress = calculateStress();
		if (currentStress != newStress) {
			currentStress = newStress;
			sync();
		}
	}

	public void updateNetwork() {
		float newStress = calculateStress();
		float newMaxStress = calculateCapacity();
		if (currentStress != newStress || currentCapacity != newMaxStress) {
			currentStress = newStress;
			currentCapacity = newMaxStress;
			sync();
		}
	}

	public float calculateCapacity() {
		float presentCapacity = 0;
		containsFlywheel = false;
		for (Iterator<KineticBlockEntity> iterator = sources.keySet()
			.iterator(); iterator.hasNext(); ) {
			KineticBlockEntity te = iterator.next();
			if (te.getWorld()
				.getBlockEntity(te.getPos()) != te) {
				iterator.remove();
				continue;
			}
			//containsFlywheel |= te instanceof FlywheelTileEntity; TODO FLYWHEEL CHECK
			presentCapacity += getActualCapacityOf(te);
		}
		float newMaxStress = presentCapacity + unloadedCapacity;
		return newMaxStress;
	}

	public float calculateStress() {
		float presentStress = 0;
		for (Iterator<KineticBlockEntity> iterator = members.keySet()
			.iterator(); iterator.hasNext(); ) {
			KineticBlockEntity te = iterator.next();
			if (te.getWorld()
				.getBlockEntity(te.getPos()) != te) {
				iterator.remove();
				continue;
			}
			presentStress += getActualStressOf(te);
		}
		float newStress = presentStress + unloadedStress;
		return newStress;
	}

	public float getActualCapacityOf(KineticBlockEntity te) {
		return sources.get(te) * getStressMultiplierForSpeed(te.getGeneratedSpeed());
	}

	public float getActualStressOf(KineticBlockEntity te) {
		return members.get(te) * getStressMultiplierForSpeed(te.getTheoreticalSpeed());
	}

	public int getSize() {
		return unloadedMembers + members.size();
	}

}
