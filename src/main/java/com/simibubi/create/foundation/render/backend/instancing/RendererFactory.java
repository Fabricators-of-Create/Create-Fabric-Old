package com.simibubi.create.foundation.render.backend.instancing;

import net.minecraft.block.entity.BlockEntity;

@FunctionalInterface
public interface RendererFactory<T extends BlockEntity> {
    BlockEntityInstance<? super T> create(InstancedBlockRenderer<?> manager, T te);
}
