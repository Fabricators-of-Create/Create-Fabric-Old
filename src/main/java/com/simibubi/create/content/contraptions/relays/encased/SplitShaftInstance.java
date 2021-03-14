package com.simibubi.create.content.contraptions.relays.encased;

import java.util.ArrayList;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.KineticBlockInstance;
import com.simibubi.create.content.contraptions.base.Rotating;
import com.simibubi.create.content.contraptions.base.RotatingData;
import com.simibubi.create.foundation.render.backend.instancing.InstanceKey;
import com.simibubi.create.foundation.render.backend.instancing.InstancedBlockRenderer;
import com.simibubi.create.foundation.render.backend.instancing.InstancedModel;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderRegistry;
import com.simibubi.create.foundation.utility.Iterate;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;

public class SplitShaftInstance extends KineticBlockInstance<SplitShaftBlockEntity> {
    public static void register(BlockEntityType<? extends SplitShaftBlockEntity> type) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			InstancedTileRenderRegistry.instance.register(type, SplitShaftInstance::new);
    }

    protected ArrayList<InstanceKey<RotatingData>> keys;

    public SplitShaftInstance(InstancedBlockRenderer modelManager, SplitShaftBlockEntity tile) {
        super(modelManager, tile);
    }

    @Override
    protected void init() {
        keys = new ArrayList<>(2);

        Block block = lastState.getBlock();
        final Direction.Axis boxAxis = ((Rotating) block).getRotationAxis(lastState);

        float speed = tile.getSpeed();

        for (Direction dir : Iterate.directionsInAxis(boxAxis)) {

            InstancedModel<RotatingData> half = AllBlockPartials.SHAFT_HALF.renderOnDirectionalSouthRotating(modelManager, lastState, dir);

            float splitSpeed = speed * tile.getRotationSpeedModifier(dir);

            keys.add(half.setupInstance(setupFunc(splitSpeed, boxAxis)));
        }
    }

    @Override
    public void onUpdate() {
        Block block = lastState.getBlock();
        final Direction.Axis boxAxis = ((Rotating) block).getRotationAxis(lastState);

        Direction[] directions = Iterate.directionsInAxis(boxAxis);

        for (int i : Iterate.zeroAndOne) {
            updateRotation(keys.get(i), directions[i]);
        }
    }

    @Override
    public void updateLight() {
        for (InstanceKey<RotatingData> key : keys) {
            key.modifyInstance(this::relight);
        }
    }

    @Override
    public void remove() {
        keys.forEach(InstanceKey::delete);
        keys.clear();
    }

    protected void updateRotation(InstanceKey<RotatingData> key, Direction dir) {
        key.modifyInstance(data -> {
            Direction.Axis axis = dir.getAxis();

            data.setColor(tile.network)
                .setRotationalSpeed(tile.getSpeed() * tile.getRotationSpeedModifier(dir))
                .setRotationOffset(getRotationOffset(axis))
                .setRotationAxis(Direction.get(Direction.AxisDirection.POSITIVE, axis).getUnitVector());
        });
    }
}
