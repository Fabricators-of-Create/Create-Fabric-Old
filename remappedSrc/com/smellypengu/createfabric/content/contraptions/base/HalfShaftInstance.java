package com.smellypengu.createfabric.content.contraptions.base;

import com.smellypengu.createfabric.AllBlockPartials;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedModel;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedTileRenderRegistry;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedTileRenderer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class HalfShaftInstance extends SingleRotatingInstance {
    public static void register(BlockEntityType<? extends KineticTileEntity> type) {
        /**DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->*/
                InstancedTileRenderRegistry.instance.register(type, HalfShaftInstance::new);
    }

    public HalfShaftInstance(InstancedTileRenderer<?> modelManager, KineticTileEntity tile) {
        super(modelManager, tile);
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
