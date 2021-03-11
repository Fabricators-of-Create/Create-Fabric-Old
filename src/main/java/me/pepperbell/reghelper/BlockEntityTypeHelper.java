package me.pepperbell.reghelper;

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

public class BlockEntityTypeHelper<T extends BlockEntity> {
	private final Identifier identifier;
	private final Supplier<T> supplier;
	private Block[] validBlocks = new Block[0];
	private Function<BlockEntityRenderDispatcher, BlockEntityRenderer<? super T>> rendererFactory;
	private Consumer<BlockEntityType<T>> onRegister;

	private BlockEntityTypeHelper(Identifier identifier, Supplier<T> supplier) {
		this.identifier = identifier;
		this.supplier = supplier;
	}

	public BlockEntityTypeHelper<T> validBlocks(Block... blocks) {
		validBlocks = ArrayUtils.addAll(validBlocks, blocks);
		return this;
	}

	public BlockEntityTypeHelper<T> renderer(Supplier<Function<BlockEntityRenderDispatcher, BlockEntityRenderer<? super T>>> factorySupplier) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			rendererFactory = factorySupplier.get();
		}
		return this;
	}

	public BlockEntityTypeHelper<T> onRegister(Consumer<BlockEntityType<T>> consumer) {
		this.onRegister = consumer;
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
		if (onRegister != null) {
			onRegister.accept(type);
		}
		return type;
	}

	public static <T extends BlockEntity> BlockEntityTypeHelper<T> create(Identifier identifier, Supplier<T> supplier) {
		return new BlockEntityTypeHelper<T>(identifier, supplier);
	}
}
