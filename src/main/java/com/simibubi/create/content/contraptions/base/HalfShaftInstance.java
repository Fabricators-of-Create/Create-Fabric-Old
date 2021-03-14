package com.simibubi.create.content.contraptions.base;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.foundation.render.backend.instancing.InstancedBlockRenderer;
import com.simibubi.create.foundation.render.backend.instancing.InstancedModel;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class HalfShaftInstance extends SingleRotatingInstance {
	public HalfShaftInstance(InstancedBlockRenderer<?> modelManager, KineticBlockEntity tile) {
		super(modelManager, tile);
	}

	public static void register(BlockEntityType<? extends KineticBlockEntity> type) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			InstancedTileRenderRegistry.instance.register(type, HalfShaftInstance::new);
	}

	@Override
	protected InstancedModel<RotatingData> getModel() {
		Direction dir = getShaftDirection();
		return AllBlockPartials.SHAFT_HALF.renderOnDirectionalSouthRotating(modelManager, lastState, dir);
	}

	protected Direction getShaftDirection() {
		return lastState.get(Properties.FACING);
	}
}
