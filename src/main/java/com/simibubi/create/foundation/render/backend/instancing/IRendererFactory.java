package com.simibubi.create.foundation.render.backend.instancing;

import net.minecraft.block.entity.BlockEntity;

@FunctionalInterface
public interface IRendererFactory<T extends BlockEntity> {
    TileEntityInstance<? super T> create(InstancedTileRenderer manager, T te);
}
