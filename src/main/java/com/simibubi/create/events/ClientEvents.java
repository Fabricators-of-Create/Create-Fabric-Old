package com.simibubi.create.events;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.KineticDebugger;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.ChassisRangeDisplay;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.contraptions.goggles.GoggleOverlayRenderer;
import com.simibubi.create.content.contraptions.relays.belt.item.BeltConnectorHandler;
import com.simibubi.create.content.curiosities.symmetry.SymmetryHandler;
import com.simibubi.create.content.curiosities.tools.DeforesterItem;
import com.simibubi.create.events.custom.ClientWorldEvents;
import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.ScrollValueRenderer;
import com.simibubi.create.foundation.block.render.SpriteShifter;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.render.backend.FastRenderDispatcher;
import com.simibubi.create.foundation.render.backend.RenderWork;
import com.simibubi.create.foundation.renderState.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.placement.PlacementHelpers;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class ClientEvents {
	private static final String itemPrefix = "item." + Create.ID;
	private static final String blockPrefix = "block." + Create.ID;

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::onTick);
		ClientWorldEvents.LOAD.register(ClientEvents::onLoadWorld);
		ClientWorldEvents.UNLOAD.register(ClientEvents::onUnloadWorld);
		WorldRenderEvents.END.register(ClientEvents::onRenderWorld);
		ItemTooltipCallback.EVENT.register(ClientEvents::addToItemTooltip);

		ClientTickEvents.END_CLIENT_TICK.register(SymmetryHandler::onClientTick);
		WorldRenderEvents.END.register(SymmetryHandler::render);
		PlayerBlockBreakEvents.AFTER.register(SymmetryHandler::onBlockDestroyed);

		AllBlockPartials.onModelRegistry();

		HudRenderCallback.EVENT.register(GoggleOverlayRenderer::lookingAtBlocksThroughGogglesShowsTooltip);
		PlayerBlockBreakEvents.AFTER.register(DeforesterItem::onBlockDestroyed);
		ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlas, registry) -> SpriteShifter.getAllTargetSprites().forEach(registry::register));
	}

	public static void onTick(MinecraftClient client) {
		if (!isGameActive())
			return;

		AnimationTickHolder.tick();
		FastRenderDispatcher.tick();

		ScreenOpener.tick();
		BeltConnectorHandler.tick();
		ScrollValueRenderer.tick();
		ChassisRangeDisplay.tick();
		KineticDebugger.tick();
		PlacementHelpers.tick();
		CreateClient.outliner.tickOutlines();
		CreateClient.ghostBlocks.tickGhosts();
		ContraptionRenderDispatcher.tick();
	}

	public static void onLoadWorld(MinecraftClient client, ClientWorld world) {
		CreateClient.invalidateRenderers();
		AnimationTickHolder.reset();
		world.blockEntities.forEach(CreateClient.kineticRenderer::add);
	}

	public static void onUnloadWorld(MinecraftClient client, ClientWorld world) {
		CreateClient.invalidateRenderers();
		AnimationTickHolder.reset();
	}

	public static void onRenderWorld(WorldRenderContext context) {
		ContraptionRenderDispatcher.renderTick();

		Vec3d cameraPos = context.camera().getPos();

		MatrixStack ms = context.matrixStack();
		ms.push();
		ms.translate(-cameraPos.getX(), -cameraPos.getY(), -cameraPos.getZ());
		SuperRenderTypeBuffer buffer = SuperRenderTypeBuffer.getInstance();

//		CouplingRenderer.renderAll(ms, buffer);
//		CreateClient.schematicHandler.render(ms, buffer);
		CreateClient.ghostBlocks.renderAll(ms, buffer);

		CreateClient.outliner.renderOutlines(ms, buffer);
//		LightVolumeDebugger.render(ms, buffer);
//		CollisionDebugger.render(ms, buffer);
		buffer.draw();
		RenderSystem.enableCull();

		ms.pop();

		RenderWork.runAll();
		FastRenderDispatcher.endFrame();
	}

	public static void addToItemTooltip(ItemStack stack, TooltipContext context, List<Text> texts) {
		/*if (!AllConfigs.CLIENT.tooltips.get())
			return;*/
		if (MinecraftClient.getInstance().player == null)
			return;

		String translationKey = stack.getItem()
			.getTranslationKey(stack);
		if (!translationKey.startsWith(itemPrefix) && !translationKey.startsWith(blockPrefix))
			return;

		if (TooltipHelper.hasTooltip(stack, MinecraftClient.getInstance().player)) {
			List<Text> toolTip = new ArrayList<>();
			toolTip.add(texts.remove(0));
			TooltipHelper.getTooltip(stack)
				.addInformation(toolTip);
			texts.addAll(0, toolTip);
		}
	}

	protected static boolean isGameActive() {
		return !(MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().player == null);
	}
}
