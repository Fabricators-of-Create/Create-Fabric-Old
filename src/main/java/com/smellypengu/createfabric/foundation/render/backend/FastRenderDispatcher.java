package com.smellypengu.createfabric.foundation.render.backend;

import com.smellypengu.createfabric.CreateClient;
import com.smellypengu.createfabric.content.contraptions.KineticDebugger;
import com.smellypengu.createfabric.foundation.utility.WorldAttached;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

import java.util.concurrent.ConcurrentHashMap;

public class FastRenderDispatcher {

    public static WorldAttached<ConcurrentHashMap.KeySetView<BlockEntity, Boolean>> queuedUpdates = new WorldAttached<>(ConcurrentHashMap::newKeySet);

    private static Matrix4f projectionMatrixThisFrame = null;

    public static void endFrame() {
        projectionMatrixThisFrame = null;
    }

    public static void enqueueUpdate(BlockEntity te) {
        queuedUpdates.get(te.getWorld()).add(te);
    }

    public static void tick() {
        ClientWorld world = MinecraftClient.getInstance().world;

        CreateClient.kineticRenderer.tick();

        ConcurrentHashMap.KeySetView<BlockEntity, Boolean> map = queuedUpdates.get(world);
        map
                .forEach(te -> {
                    map.remove(te);

                    CreateClient.kineticRenderer.update(te);
                });
    }

    public static boolean available() {
        return Backend.canUseInstancing();
    }

    public static boolean available(World world) {
        return Backend.canUseInstancing(); //return Backend.canUseInstancing() && !(world instanceof SchematicWorld); TODO FIX SCHEMATIC
    }

    public static int getDebugMode() {
        return KineticDebugger.isActive() ? 1 : 0;
    }

    public static void refresh() {
        RenderWork.enqueue(MinecraftClient.getInstance().worldRenderer::reload);
    }

    public static void renderLayer(RenderLayer layer, Matrix4f viewProjection, double cameraX, double cameraY, double cameraZ) {
        if (!Backend.canUseInstancing()) return;

        layer.startDrawing();

        CreateClient.kineticRenderer.render(layer, viewProjection, cameraX, cameraY, cameraZ);

        layer.endDrawing();
    }

    // copied from GameRenderer.renderWorld
    /**public static Matrix4f getProjectionMatrix() {
        if (projectionMatrixThisFrame != null) return projectionMatrixThisFrame;

        float partialTicks = AnimationTickHolder.getPartialTicks();
        MinecraftClient mc = MinecraftClient.getInstance();
        GameRenderer gameRenderer = mc.gameRenderer;
        ClientPlayerEntity player = mc.player;

        MatrixStack matrixstack = new MatrixStack();
        matrixstack.peek().getModel().multiply(gameRenderer.getBasicProjectionMatrix(gameRenderer.getCamera(), partialTicks, true));
        //gameRenderer.bobViewWhenHurt(matrixstack, partialTicks); TODO bobView THING NOT SURE HOW TO FIX
        if (mc.options.bobView) {
            //gameRenderer.bobView(matrixstack, partialTicks);
        }

        float portalTime = MathHelper.lerp(partialTicks, player.lastNauseaStrength, player.nextNauseaStrength);
        if (portalTime > 0.0F) {
            int i = 20;
            if (player.hasStatusEffect(StatusEffects.NAUSEA)) {
                i = 7;
            }

            float f1 = 5.0F / (portalTime * portalTime + 5.0F) - portalTime * 0.04F;
            f1 = f1 * f1;
            Vec3f vector3f = new Vec3f(0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
            matrixstack.multiply(vector3f.getDegreesQuaternion(((float)mc.getTickDelta() + partialTicks) * (float)i)); //TODO TICK MIGHT BE WRONG
            matrixstack.scale(1.0F / f1, 1.0F, 1.0F);
            float f2 = -((float)mc.getTickDelta() + partialTicks) * (float)i;
            matrixstack.multiply(vector3f.getDegreesQuaternion(f2));
        }

        Matrix4f matrix4f = matrixstack.peek().getModel();
        gameRenderer.loadProjectionMatrix(matrix4f);

        projectionMatrixThisFrame = matrix4f;
        return projectionMatrixThisFrame;
    }*/
}
