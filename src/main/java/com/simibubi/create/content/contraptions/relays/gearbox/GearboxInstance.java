package com.simibubi.create.content.contraptions.relays.gearbox;

import java.util.EnumMap;
import java.util.Map;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticBlockInstance;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.foundation.render.backend.instancing.InstanceKey;
import com.simibubi.create.foundation.render.backend.instancing.InstancedBlockRenderer;
import com.simibubi.create.foundation.render.backend.instancing.InstancedModel;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderRegistry;
import com.simibubi.create.foundation.utility.Iterate;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;

public class GearboxInstance extends KineticBlockInstance<GearboxBlockEntity> {
	public static void register(BlockEntityType<? extends GearboxBlockEntity> type) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			InstancedTileRenderRegistry.instance.register(type, GearboxInstance::new);
	}

	protected EnumMap<Direction, InstanceKey<RotatingData>> keys;
	protected Direction sourceFacing;

	public GearboxInstance(InstancedBlockRenderer<?> modelManager, GearboxBlockEntity tile) {
		super(modelManager, tile);
	}

	@Override
	protected void init() {
		keys = new EnumMap<>(Direction.class);

		final Direction.Axis boxAxis = lastState.get(Properties.AXIS);

		int blockLight = world.getLightLevel(LightType.BLOCK, pos);
		int skyLight = world.getLightLevel(LightType.SKY, pos);
		updateSourceFacing();

		for (Direction direction : Iterate.directions) {
			final Direction.Axis axis = direction.getAxis();
			if (boxAxis == axis)
				continue;

			InstancedModel<RotatingData> shaft = AllBlockPartials.SHAFT_HALF.renderOnDirectionalSouthRotating(modelManager, lastState, direction);

			InstanceKey<RotatingData> key = shaft.setupInstance(data -> {
				data.setBlockLight(blockLight)
					.setSkyLight(skyLight)
					.setRotationalSpeed(getSpeed(direction))
					.setRotationOffset(getRotationOffset(axis))
					.setRotationAxis(Direction.get(Direction.AxisDirection.POSITIVE, axis).getUnitVector())
					.setBlockEntity(tile);
			});
			keys.put(direction, key);
		}
	}

	private float getSpeed(Direction direction) {
		float speed = tile.getSpeed();

		if (speed != 0 && sourceFacing != null) {
			if (sourceFacing.getAxis() == direction.getAxis())
				speed *= sourceFacing == direction ? 1 : -1;
			else if (sourceFacing.getDirection() == direction.getDirection())
				speed *= -1;
		}
		return speed;
	}

	protected void updateSourceFacing() {
		if (tile.hasSource()) {
			BlockPos source = tile.source.subtract(pos);
			sourceFacing = Direction.getFacing(source.getX(), source.getY(), source.getZ());
		} else {
			sourceFacing = null;
		}
	}

	@Override
	public void onUpdate() {
		updateSourceFacing();
		for (Map.Entry<Direction, InstanceKey<RotatingData>> key : keys.entrySet()) {
			key.getValue().modifyInstance(data -> {
				Direction direction = key.getKey();
				Direction.Axis axis = direction.getAxis();

				data.setColor(tile.network)
					.setRotationalSpeed(getSpeed(direction))
					.setRotationOffset(getRotationOffset(axis))
					.setRotationAxis(Direction.get(Direction.AxisDirection.POSITIVE, axis).getUnitVector());
			});
		}
	}

	@Override
	public void updateLight() {
		int blockLight = tile.getWorld().getLightLevel(LightType.BLOCK, pos);
		int skyLight = tile.getWorld().getLightLevel(LightType.SKY, pos);

		for (InstanceKey<RotatingData> key : keys.values()) {
			key.modifyInstance(data -> data.setBlockLight(blockLight).setSkyLight(skyLight));
		}
	}

	@Override
	public void remove() {
		keys.values().forEach(InstanceKey::delete);
		keys.clear();
	}
}
