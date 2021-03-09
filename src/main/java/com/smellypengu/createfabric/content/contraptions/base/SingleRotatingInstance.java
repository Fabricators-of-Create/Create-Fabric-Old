package com.smellypengu.createfabric.content.contraptions.base;

import com.smellypengu.createfabric.foundation.render.backend.instancing.InstanceKey;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedModel;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedTileRenderRegistry;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedTileRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;

import static com.smellypengu.createfabric.content.contraptions.base.KineticBlockEntityRenderer.KINETIC_TILE;

public class SingleRotatingInstance extends KineticTileInstance<KineticBlockEntity> {
    public static void register(BlockEntityType<? extends KineticBlockEntity> type) {
        /**DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->*/
                InstancedTileRenderRegistry.instance.register(type, SingleRotatingInstance::new);
    }

    protected InstanceKey<RotatingData> rotatingModelKey;

    public SingleRotatingInstance(InstancedTileRenderer modelManager, KineticBlockEntity tile) {
        super(modelManager, tile);
    }

    @Override
    protected void init() {
        Direction.Axis axis = ((IRotate) lastState.getBlock()).getRotationAxis(lastState);
        rotatingModelKey = getModel().setupInstance(setupFunc(tile.getSpeed(), axis));
    }

    @Override
    public void onUpdate() {
        Direction.Axis axis = ((IRotate) lastState.getBlock()).getRotationAxis(lastState);
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
