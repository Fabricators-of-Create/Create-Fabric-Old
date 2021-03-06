package com.smellypengu.createfabric.foundation.render;

import com.smellypengu.createfabric.content.contraptions.base.KineticRenderMaterials;
import com.smellypengu.createfabric.content.contraptions.base.RotatingInstancedModel;
import com.smellypengu.createfabric.content.contraptions.relays.belt.BeltInstancedModel;
import com.smellypengu.createfabric.foundation.render.backend.gl.BasicProgram;
import com.smellypengu.createfabric.foundation.render.backend.gl.shader.ShaderCallback;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedTileRenderer;
import com.smellypengu.createfabric.foundation.render.backend.instancing.RenderMaterial;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;

import java.util.ArrayList;

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
        Entity renderViewEntity = mc.targetedEntity; //TODO IDK IF THIS IS RIGHT PLS EXPLAIN MY GOD renderViewEntity

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

        translate.multiply(viewProjection); // TODO MIGHT BE WRONG. ORIGINAL multiplyBackward

        super.render(layer, translate, camX, camY, camZ, callback);
    }
}
