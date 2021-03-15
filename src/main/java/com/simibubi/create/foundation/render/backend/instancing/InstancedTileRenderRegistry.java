package com.simibubi.create.foundation.render.backend.instancing;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class InstancedTileRenderRegistry {
    public static final InstancedTileRenderRegistry instance = new InstancedTileRenderRegistry();

    private final Map<BlockEntityType<?>, RendererFactory<?>> renderers = Maps.newHashMap();

    public <T extends BlockEntity> void register(BlockEntityType<? extends T> type, RendererFactory<? super T> rendererFactory) {
        this.renderers.put(type, rendererFactory);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends BlockEntity> BlockEntityInstance<? super T> create(InstancedBlockRenderer<?> manager, T tile) {
        BlockEntityType<?> type = tile.getType();
        RendererFactory<? super T> factory = (RendererFactory<? super T>) this.renderers.get(type);

        if (factory == null) return null;
        else return factory.create(manager, tile);
    }

}
