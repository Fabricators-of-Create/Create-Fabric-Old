package com.smellypengu.createfabric.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.smellypengu.createfabric.content.contraptions.KineticDebugger;
import com.smellypengu.createfabric.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.smellypengu.createfabric.content.contraptions.relays.belt.item.BeltConnectorHandler;
import com.smellypengu.createfabric.foundation.render.backend.FastRenderDispatcher;
import com.smellypengu.createfabric.foundation.render.backend.RenderWork;
import com.smellypengu.createfabric.foundation.renderState.SuperRenderTypeBuffer;
import com.smellypengu.createfabric.foundation.utility.AnimationTickHolder;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class ClientEvents {

    public static void onTick(MinecraftClient client) {
        AnimationTickHolder.tick();
        FastRenderDispatcher.tick();

        BeltConnectorHandler.tick();

        KineticDebugger.tick();
        //PlacementHelpers.tick();
        ContraptionRenderDispatcher.tick();
    }

    public static void onRenderWorld(WorldRenderContext context) {
        Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();

        MatrixStack ms = context.matrixStack();
        ms.push();
        ms.translate(-cameraPos.getX(), -cameraPos.getY(), -cameraPos.getZ());
        SuperRenderTypeBuffer buffer = SuperRenderTypeBuffer.getInstance();

        buffer.draw();
        RenderSystem.enableCull();

        ms.pop();

        RenderWork.runAll();
        FastRenderDispatcher.endFrame();
    }
}
