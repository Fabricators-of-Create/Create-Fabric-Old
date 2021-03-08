package com.simibubi.create.foundation.renderState;

import java.util.SortedMap;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.util.Util;

public class SuperRenderTypeBuffer implements VertexConsumerProvider {

	static SuperRenderTypeBuffer instance;

	public static SuperRenderTypeBuffer getInstance() {
		if (instance == null)
			instance = new SuperRenderTypeBuffer();
		return instance;
	}

	SuperRenderTypeBufferPhase earlyBuffer;
	SuperRenderTypeBufferPhase defaultBuffer;
	SuperRenderTypeBufferPhase lateBuffer;

	public SuperRenderTypeBuffer() {
		earlyBuffer = new SuperRenderTypeBufferPhase();
		defaultBuffer = new SuperRenderTypeBufferPhase();
		lateBuffer = new SuperRenderTypeBufferPhase();
	}

	public VertexConsumer getEarlyBuffer(RenderLayer type) {
		return earlyBuffer.getBuffer(type);
	}

	@Override
	public VertexConsumer getBuffer(RenderLayer type) {
		return defaultBuffer.getBuffer(type);
	}

	public VertexConsumer getLateBuffer(RenderLayer type) {
		return lateBuffer.getBuffer(type);
	}

	public void draw() {
		RenderSystem.disableCull();
		earlyBuffer.draw();
		defaultBuffer.draw();
		lateBuffer.draw();
	}

	public void draw(RenderLayer type) {
		RenderSystem.disableCull();
		earlyBuffer.draw(type);
		defaultBuffer.draw(type);
		lateBuffer.draw(type);
	}

	private static class SuperRenderTypeBufferPhase extends VertexConsumerProvider.Immediate {

		// Visible clones from net.minecraft.client.renderer.RenderTypeBuffers
		static final BlockBufferBuilderStorage blockBuilders = new BlockBufferBuilderStorage();
		static final SortedMap<RenderLayer, BufferBuilder> createEntityBuilders() {
			return Util.make(new Object2ObjectLinkedOpenHashMap<>(), (map) -> {
				map.put(TexturedRenderLayers.getEntitySolid(), blockBuilders.get(RenderLayer.getSolid()));
				assign(map, RenderTypes.getOutlineSolid());
				map.put(TexturedRenderLayers.getEntityCutout(), blockBuilders.get(RenderLayer.getCutout()));
				map.put(TexturedRenderLayers.getBannerPatterns(), blockBuilders.get(RenderLayer.getCutoutMipped()));
				map.put(TexturedRenderLayers.getEntityTranslucentCull(), blockBuilders.get(RenderLayer.getTranslucent())); // FIXME new equivalent of getEntityTranslucent() ?
				assign(map, TexturedRenderLayers.getShieldPatterns());
				assign(map, TexturedRenderLayers.getBeds());
				assign(map, TexturedRenderLayers.getShulkerBoxes());
				assign(map, TexturedRenderLayers.getSign());
				assign(map, TexturedRenderLayers.getChest());
				assign(map, RenderLayer.getTranslucentNoCrumbling());
				assign(map, RenderLayer.getGlint());
				assign(map, RenderLayer.getEntityGlint());
				assign(map, RenderLayer.getWaterMask());
				ModelLoader.BLOCK_DESTRUCTION_RENDER_LAYERS.forEach((p_228488_1_) -> {
					assign(map, p_228488_1_);
				});
			});
		}
			

		private static void assign(Object2ObjectLinkedOpenHashMap<RenderLayer, BufferBuilder> map, RenderLayer type) {
			map.put(type, new BufferBuilder(type.getExpectedBufferSize()));
		}

		protected SuperRenderTypeBufferPhase() {
			super(new BufferBuilder(256), createEntityBuilders());
		}

	}

}
