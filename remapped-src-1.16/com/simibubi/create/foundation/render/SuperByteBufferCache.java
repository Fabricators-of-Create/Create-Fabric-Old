package com.simibubi.create.foundation.render;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.foundation.utility.VirtualEmptyModelData;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SuperByteBufferCache {

	Map<Compartment<?>, Cache<Object, SuperByteBuffer>> cache;

	public SuperByteBufferCache() {
		cache = new HashMap<>();
		registerCompartment(Compartment.GENERIC_TILE);
		registerCompartment(Compartment.PARTIAL);
		registerCompartment(Compartment.DIRECTIONAL_PARTIAL);
	}

	public SuperByteBuffer renderBlock(BlockState toRender) {
		return getGeneric(toRender, () -> standardBlockRender(toRender));
	}

	public SuperByteBuffer renderPartial(AllBlockPartials partial, BlockState referenceState) {
		return get(Compartment.PARTIAL, partial, () -> standardModelRender(partial.get(), referenceState));
	}
	public SuperByteBuffer renderPartial(AllBlockPartials partial, BlockState referenceState,
		MatrixStack modelTransform) {
		return get(Compartment.PARTIAL, partial, () -> standardModelRender(partial.get(), referenceState, modelTransform));
	}

	public SuperByteBuffer renderDirectionalPartial(AllBlockPartials partial, BlockState referenceState,
		Direction dir) {
		return get(Compartment.DIRECTIONAL_PARTIAL, Pair.of(dir, partial),
				   () -> standardModelRender(partial.get(), referenceState));
	}

	public SuperByteBuffer renderDirectionalPartial(AllBlockPartials partial, BlockState referenceState, Direction dir,
		MatrixStack modelTransform) {
		return get(Compartment.DIRECTIONAL_PARTIAL, Pair.of(dir, partial),
				   () -> standardModelRender(partial.get(), referenceState, modelTransform));
	}

	public SuperByteBuffer renderBlockIn(Compartment<BlockState> compartment, BlockState toRender) {
		return get(compartment, toRender, () -> standardBlockRender(toRender));
	}

	SuperByteBuffer getGeneric(BlockState key, Supplier<SuperByteBuffer> supplier) {
		return get(Compartment.GENERIC_TILE, key, supplier);
	}

	public <T> SuperByteBuffer get(Compartment<T> compartment, T key, Supplier<SuperByteBuffer> supplier) {
		Cache<Object, SuperByteBuffer> compartmentCache = this.cache.get(compartment);
		try {
			return compartmentCache.get(key, supplier::get);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void registerCompartment(Compartment<?> instance) {
		cache.put(instance, CacheBuilder.newBuilder()
			.build());
	}

	public void registerCompartment(Compartment<?> instance, long ticksUntilExpired) {
		cache.put(instance, CacheBuilder.newBuilder()
			.expireAfterAccess(ticksUntilExpired * 50, TimeUnit.MILLISECONDS)
			.build());
	}

	private SuperByteBuffer standardBlockRender(BlockState renderedState) {
		BlockRenderManager dispatcher = MinecraftClient.getInstance()
			.getBlockRenderManager();
		return standardModelRender(dispatcher.getModel(renderedState), renderedState);
	}

	private SuperByteBuffer standardModelRender(BakedModel model, BlockState referenceState) {
		return standardModelRender(model, referenceState, new MatrixStack());
	}

	private SuperByteBuffer standardModelRender(BakedModel model, BlockState referenceState, MatrixStack ms) {
		BufferBuilder builder = getBufferBuilder(model, referenceState, ms);

		return new SuperByteBuffer(builder);
	}

	public static BufferBuilder getBufferBuilder(BakedModel model, BlockState referenceState, MatrixStack ms) {
		MinecraftClient mc = MinecraftClient.getInstance();
		BlockRenderManager dispatcher = mc.getBlockRenderManager();
		BlockModelRenderer blockRenderer = dispatcher.getModelRenderer();
		BufferBuilder builder = new BufferBuilder(512);

		builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		blockRenderer.renderModel(mc.world, model, referenceState, BlockPos.ORIGIN.up(255), ms, builder, true, mc.world.random, 42, OverlayTexture.DEFAULT_UV, VirtualEmptyModelData.INSTANCE);
		builder.end();
		return builder;
	}


	public void invalidate() {
		cache.forEach((comp, cache) -> cache.invalidateAll());
	}

}
