package com.simibubi.create.foundation.render.backend;

import java.util.concurrent.ConcurrentHashMap;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.KineticDebugger;
import com.simibubi.create.foundation.mixin.accessor.GameRendererAccessor;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.MixinHelper;
import com.simibubi.create.foundation.utility.WorldAttached;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

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
		map.forEach(be -> {
			map.remove(be);

			CreateClient.kineticRenderer.update(be);
		});
	}

	public static boolean available() {
		return Backend.canUseInstancing();
	}

	public static boolean available(World world) {
		return Backend.canUseInstancing() /*&& !(world instanceof SchematicWorld)*/;
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
	public static Matrix4f getProjectionMatrix() {
		if (projectionMatrixThisFrame != null) return projectionMatrixThisFrame;

		float partialTicks = AnimationTickHolder.getPartialTicks();
		MinecraftClient mc = MinecraftClient.getInstance();
		GameRenderer gameRenderer = mc.gameRenderer;
		GameRendererAccessor gra = MixinHelper.cast(gameRenderer);
		ClientPlayerEntity player = mc.player;

		MatrixStack matrixstack = new MatrixStack();
		matrixstack.peek()
			.getModel()
			.multiply(gameRenderer.getBasicProjectionMatrix(gameRenderer.getCamera(), partialTicks, true));
		gra.create$bobViewWhenHurt(matrixstack, partialTicks);
		if (mc.options.bobView) {
			gra.create$bobView(matrixstack, partialTicks);
		}

		float portalTime = MathHelper.lerp(partialTicks, player.lastNauseaStrength, player.nextNauseaStrength);
		if (portalTime > 0.0F) {
			int i = 20;
			if (player.hasStatusEffect(StatusEffects.NAUSEA)) {
				i = 7;
			}

			float f1 = 5.0F / (portalTime * portalTime + 5.0F) - portalTime * 0.04F;
			f1 = f1 * f1;
			Vector3f vector3f = new Vector3f(0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
			matrixstack.multiply(vector3f.getDegreesQuaternion(((float)gra.create$ticks() + partialTicks) * (float)i));
			matrixstack.scale(1.0F / f1, 1.0F, 1.0F);
			float f2 = -((float)gra.create$ticks() + partialTicks) * (float)i;
			matrixstack.multiply(vector3f.getDegreesQuaternion(f2));
		}

		Matrix4f matrix4f = matrixstack.peek().getModel();
		gameRenderer.loadProjectionMatrix(matrix4f);

		projectionMatrixThisFrame = matrix4f;
		return projectionMatrixThisFrame;
	}
}
