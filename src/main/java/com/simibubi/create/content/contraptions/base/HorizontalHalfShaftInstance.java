package com.simibubi.create.content.contraptions.base;

import com.simibubi.create.foundation.render.backend.instancing.InstancedBlockRenderer;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class HorizontalHalfShaftInstance extends HalfShaftInstance {
    public static void register(BlockEntityType<? extends KineticBlockEntity> type) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
			InstancedTileRenderRegistry.instance.register(type, HorizontalHalfShaftInstance::new);
    }

    public HorizontalHalfShaftInstance(InstancedBlockRenderer<?> modelManager, KineticBlockEntity tile) {
        super(modelManager, tile);
    }

    @Override
    protected Direction getShaftDirection() {
        return lastState.get(Properties.HORIZONTAL_FACING).getOpposite();
    }
}
