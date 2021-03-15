package com.simibubi.create.foundation.render.backend.instancing;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.render.backend.Backend;
import com.simibubi.create.foundation.render.backend.gl.BasicProgram;
import com.simibubi.create.foundation.render.backend.gl.shader.ShaderCallback;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public abstract class InstancedBlockRenderer<P extends BasicProgram> {
    protected Map<BlockEntity, BlockEntityInstance<?>> instances = new HashMap<>();

    protected Map<MaterialType<?>, RenderMaterial<P, ?>> materials = new HashMap<>();

    protected InstancedBlockRenderer() {
        registerMaterials();
    }

    public abstract BlockPos getOriginCoordinate();

    public abstract void registerMaterials();

    public void tick() {
        int ticks = AnimationTickHolder.getTicks();

        // Clean up twice a second. This doesn't have to happen every tick,
        // but this does need to be run to ensure we don't miss anything.
        if (ticks % 10 == 0) {
            clean();
        }
    }

    public void render(RenderLayer layer, Matrix4f viewProjection, double camX, double camY, double camZ) {
        render(layer, viewProjection, camX, camY, camZ, null);
    }

    public void render(RenderLayer layer, Matrix4f viewProjection, double camX, double camY, double camZ, ShaderCallback<P> callback) {
        for (RenderMaterial<P, ?> material : materials.values()) {
            if (material.canRenderInLayer(layer))
                material.render(layer, viewProjection, camX, camY, camZ, callback);
        }
    }

    @SuppressWarnings("unchecked")
    public <M extends InstancedModel<?>> RenderMaterial<P, M> getMaterial(MaterialType<M> materialType) {
        return (RenderMaterial<P, M>) materials.get(materialType);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityInstance<? super T> getInstance(T tile) {
        return getInstance(tile, true);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends BlockEntity> BlockEntityInstance<? super T> getInstance(T tile, boolean create) {
        if (!Backend.canUseInstancing() || isTileUnloaded(tile)) return null;

        BlockEntityInstance<?> instance = instances.get(tile);

        if (instance != null) {
            return (BlockEntityInstance<? super T>) instance;
        } else if (create) {
            BlockEntityInstance<? super T> renderer = InstancedTileRenderRegistry.instance.create(this, tile);

            if (renderer != null) {
                instances.put(tile, renderer);
            }

            return renderer;
        } else {
            return null;
        }
    }

    public <T extends BlockEntity> void onLightUpdate(T tile) {
        if (!Backend.canUseInstancing()) return;

        if (tile instanceof InstanceRendered) {
            BlockEntityInstance<? super T> instance = getInstance(tile, false);

            if (instance != null)
                instance.updateLight();
        }
    }

    public <T extends BlockEntity> void add(T tile) {
        if (!Backend.canUseInstancing()) return;

        if (tile instanceof InstanceRendered) {
            getInstance(tile);
        }
    }

    public <T extends BlockEntity> void update(T tile) {
        if (!Backend.canUseInstancing()) return;

        if (tile instanceof InstanceRendered) {
            BlockEntityInstance<? super T> instance = getInstance(tile, false);

            if (instance != null)
                instance.update();
        }
    }

    public <T extends BlockEntity> void remove(T tile) {
        if (!Backend.canUseInstancing()) return;

        if (tile instanceof InstanceRendered) {
            BlockEntityInstance<? super T> instance = getInstance(tile, false);

            if (instance != null) {
                instance.remove();
                instances.remove(tile);
            }
        }
    }

    public void clean() {
        instances.keySet().removeIf(BlockEntity::isRemoved);
    }

    public void invalidate() {
        for (RenderMaterial<?, ?> material : materials.values()) {
            material.delete();
        }
        instances.clear();
    }

    public boolean isTileUnloaded(BlockEntity tile) {
        if (tile.isRemoved()) return true;

        World world = tile.getWorld();

        if (world == null) return true;

        BlockPos pos = tile.getPos();

        WorldChunk existingChunk = world.getChunk(pos.getX() >> 4, pos.getZ() >> 4);

        return existingChunk == null;
    }
}
