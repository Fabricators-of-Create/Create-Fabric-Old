package com.simibubi.create.foundation.render;

import java.util.ArrayList;

import com.simibubi.create.content.contraptions.base.KineticRenderMaterials;
import com.simibubi.create.content.contraptions.base.RotatingInstancedModel;
import com.simibubi.create.content.contraptions.relays.belt.BeltInstancedModel;
import com.simibubi.create.foundation.render.backend.gl.BasicProgram;
import com.simibubi.create.foundation.render.backend.gl.shader.ShaderCallback;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderer;
import com.simibubi.create.foundation.render.backend.instancing.RenderMaterial;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;

public class KineticRenderer extends InstancedTileRenderer<BasicProgram> {
    public static int MAX_ORIGIN_DISTANCE = 100;

    public BlockPos originCoordinate = BlockPos.ORIGIN;

    @Override
    public void registerMaterials() {
        materials.put(KineticRenderMaterials.BELTS, new RenderMaterial<>(this, AllProgramSpecs.BELT, BeltInstancedModel::new));
        materials.put(KineticRenderMaterials.ROTATING, new RenderMaterial<>(this, AllProgramSpecs.ROTATING, RotatingInstancedModel::new));
    }

    @Override
    public BlockPos getOriginCoordinate() {
        return originCoordinate;
    }

    @Override
    public void tick() {
        super.tick();

        MinecraftClient mc = MinecraftClient.getInstance();
        Entity renderViewEntity = mc.cameraEntity;

        if (renderViewEntity == null) return;

        BlockPos renderViewPosition = renderViewEntity.getBlockPos();

        int dX = Math.abs(renderViewPosition.getX() - originCoordinate.getX());
        int dY = Math.abs(renderViewPosition.getY() - originCoordinate.getY());
        int dZ = Math.abs(renderViewPosition.getZ() - originCoordinate.getZ());

        if (dX > MAX_ORIGIN_DISTANCE ||
            dY > MAX_ORIGIN_DISTANCE ||
            dZ > MAX_ORIGIN_DISTANCE) {

            originCoordinate = renderViewPosition;

            ArrayList<BlockEntity> instancedTiles = new ArrayList<>(instances.keySet());
            invalidate();
            instancedTiles.forEach(this::add);
        }
    }

    @Override
    public void render(RenderLayer layer, Matrix4f viewProjection, double camX, double camY, double camZ, ShaderCallback<BasicProgram> callback) {
        BlockPos originCoordinate = getOriginCoordinate();

        camX -= originCoordinate.getX();
        camY -= originCoordinate.getY();
        camZ -= originCoordinate.getZ();

        Matrix4f translate = Matrix4f.translate((float) -camX, (float) -camY, (float) -camZ);

        translate.multiplyBackward(viewProjection);

        super.render(layer, translate, camX, camY, camZ, callback);
    }
}
