package me.pepperbell.reghelper;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockEntityTypeRegBuilder<T extends BlockEntity> {
	private final Identifier identifier;
	private final Supplier<T> supplier;
	private Block[] validBlocks = new Block[0];
	private Function<BlockEntityRenderDispatcher, BlockEntityRenderer<? super T>> rendererFactory;
	private Deque<Consumer<BlockEntityType<T>>> onRegister = new ArrayDeque<>();

	private BlockEntityTypeRegBuilder(Identifier identifier, Supplier<T> supplier) {
		this.identifier = identifier;
		this.supplier = supplier;
	}

	public Identifier getId() {
		return identifier;
	}

	public BlockEntityTypeRegBuilder<T> validBlocks(Block... blocks) {
		validBlocks = ArrayUtils.addAll(validBlocks, blocks);
		return this;
	}

	public BlockEntityTypeRegBuilder<T> renderer(Supplier<Function<BlockEntityRenderDispatcher, BlockEntityRenderer<? super T>>> factorySupplier) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			rendererFactory = factorySupplier.get();
		}
		return this;
	}

	public BlockEntityTypeRegBuilder<T> onRegister(Consumer<BlockEntityType<T>> consumer) {
		onRegister.add(consumer);
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BlockEntityType<T> register() {
		BlockEntityType<T> type = BlockEntityType.Builder.create(supplier, validBlocks).build(null);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, identifier, type);
		if (rendererFactory != null) {
			// For some reason this method does not allow the BER to render for a superclass of T
			BlockEntityRendererRegistry.INSTANCE.register((BlockEntityType) type, (Function) rendererFactory);
		}
		for (Consumer<BlockEntityType<T>> consumer : onRegister) {
			consumer.accept(type);
		}
		return type;
	}

	public static <T extends BlockEntity> BlockEntityTypeRegBuilder<T> create(Identifier identifier, Supplier<T> supplier) {
		return new BlockEntityTypeRegBuilder<T>(identifier, supplier);
	}
}
