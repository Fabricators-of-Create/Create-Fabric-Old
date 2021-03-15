package com.simibubi.create.content.contraptions.base;

import static com.simibubi.create.content.contraptions.base.KineticBlockEntityRenderer.KINETIC_TILE;

import com.simibubi.create.foundation.render.backend.instancing.InstanceKey;
import com.simibubi.create.foundation.render.backend.instancing.InstancedBlockRenderer;
import com.simibubi.create.foundation.render.backend.instancing.InstancedModel;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;

public class SingleRotatingInstance extends KineticBlockInstance<KineticBlockEntity> {
	protected InstanceKey<RotatingData> rotatingModelKey;

	public SingleRotatingInstance(InstancedBlockRenderer<?> modelManager, KineticBlockEntity tile) {
		super(modelManager, tile);
	}

	public static void register(BlockEntityType<? extends KineticBlockEntity> type) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			InstancedTileRenderRegistry.instance.register(type, SingleRotatingInstance::new);
	}

	@Override
	protected void init() {
		Direction.Axis axis = ((Rotating) lastState.getBlock()).getRotationAxis(lastState);
		rotatingModelKey = getModel().setupInstance(setupFunc(tile.getSpeed(), axis));
	}

	@Override
	public void onUpdate() {
		Direction.Axis axis = ((Rotating) lastState.getBlock()).getRotationAxis(lastState);
		updateRotation(rotatingModelKey, axis);
	}

	@Override
	public void updateLight() {
		rotatingModelKey.modifyInstance(this::relight);
	}

	@Override
	public void remove() {
		rotatingModelKey.delete();
	}

	protected BlockState getRenderedBlockState() {
		return lastState;
	}

	protected InstancedModel<RotatingData> getModel() {
		return rotatingMaterial().getModel(KINETIC_TILE, getRenderedBlockState());
	}
}
