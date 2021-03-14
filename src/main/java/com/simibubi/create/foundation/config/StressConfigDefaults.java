package com.simibubi.create.foundation.config;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import me.pepperbell.reghelper.BlockRegBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

public class StressConfigDefaults {
	/**
	 * Increment this number if all stress entries should be forced to update in the next release.
	 * Worlds from the previous version will overwrite potentially changed values
	 * with the new defaults.
	 */
	public static final int forcedUpdateVersion = 1;

	static Map<Identifier, Double> registeredDefaultImpacts = new HashMap<>();
	static Map<Identifier, Double> registeredDefaultCapacities = new HashMap<>();

	public static void setNoImpact(Identifier identifier) {
		setImpact(identifier, 0);
	}

	public static void setImpact(Identifier identifier, double impact) {
		registeredDefaultImpacts.put(identifier, impact);
	}

	public static void setCapacity(Identifier identifier, double capacity) {
		registeredDefaultCapacities.put(identifier, capacity);
	}

	public static <T extends Block> Consumer<BlockRegBuilder<T>> noImpactConsumer() {
		return (builder) -> setNoImpact(builder.getId());
	}

	public static <T extends Block> Consumer<BlockRegBuilder<T>> impactConsumer(double impact) {
		return (builder) -> setImpact(builder.getId(), impact);
	}

	public static <T extends Block> Consumer<BlockRegBuilder<T>> capacityConsumer(double capacity) {
		return (builder) -> setCapacity(builder.getId(), capacity);
	}
}
