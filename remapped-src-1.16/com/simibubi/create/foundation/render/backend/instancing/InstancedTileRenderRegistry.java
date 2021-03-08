package com.simibubi.create.foundation.render.backend.instancing;

import java.util.Map;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import com.google.common.collect.Maps;

public class InstancedTileRenderRegistry {
    public static final InstancedTileRenderRegistry instance = new InstancedTileRenderRegistry();

    private final Map<BlockEntityType<?>, IRendererFactory<?>> renderers = Maps.newHashMap();

    public <T extends BlockEntity> void register(BlockEntityType<? extends T> type, IRendererFactory<? super T> rendererFactory) {
        this.renderers.put(type, rendererFactory);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends BlockEntity> TileEntityInstance<? super T> create(InstancedTileRenderer<?> manager, T tile) {
        BlockEntityType<?> type = tile.getType();
        IRendererFactory<? super T> factory = (IRendererFactory<? super T>) this.renderers.get(type);

        if (factory == null) return null;
        else return factory.create(manager, tile);
    }

}
