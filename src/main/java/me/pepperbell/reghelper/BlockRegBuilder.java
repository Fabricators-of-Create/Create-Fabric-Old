package me.pepperbell.reghelper;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BlockRegBuilder<T extends Block> {
	private final Identifier identifier;
	private final Function<FabricBlockSettings, T> blockFactory;
	private FabricBlockSettings initialSettings;
	private Deque<Consumer<FabricBlockSettings>> settingsDeque = new ArrayDeque<>();
	private Supplier<RenderLayer> renderLayer;
	private BlockItemBuilder<T> itemBuilder;
	private Deque<Consumer<T>> onRegister = new ArrayDeque<>();
	private Deque<Consumer<BlockItemBuilder<T>>> beforeRegisterItem = new ArrayDeque<>();
	private Deque<Consumer<Item>> onRegisterItem = new ArrayDeque<>();

	private BlockRegBuilder(Identifier identifier, Function<FabricBlockSettings, T> blockFactory) {
		this.identifier = identifier;
		this.blockFactory = blockFactory;
	}

	public Identifier getId() {
		return identifier;
	}

	public boolean hasItemBuilder() {
		return itemBuilder != null;
	}

	public BlockRegBuilder<T> initialProperties(Material material) {
		initialSettings = FabricBlockSettings.of(material);
		return this;
	}

	public BlockRegBuilder<T> initialProperties(Material material, MaterialColor color) {
		initialSettings = FabricBlockSettings.of(material, color);
		return this;
	}

	public BlockRegBuilder<T> initialProperties(Supplier<Block> block) {
		initialSettings = FabricBlockSettings.copyOf(block.get());
		return this;
	}

	public BlockRegBuilder<T> properties(Consumer<FabricBlockSettings> consumer) {
		settingsDeque.add(consumer);
		return this;
	}

	public BlockRegBuilder<T> addLayer(Supplier<Supplier<RenderLayer>> renderLayer) {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			this.renderLayer = renderLayer.get();
		}
		return this;
	}

	public BlockItemBuilder<T> item(BiFunction<T, FabricItemSettings, Item> function) {
		if (!hasItemBuilder()) {
			itemBuilder = BlockItemBuilder.create(this, function);
		}
		return itemBuilder;
	}

	public BlockItemBuilder<T> item() {
		if (!hasItemBuilder()) {
			itemBuilder = BlockItemBuilder.create(this);
		}
		return itemBuilder;
	}

	public BlockRegBuilder<T> simpleItem() {
		return item().build();
	}

	public BlockRegBuilder<T> consume(Consumer<BlockRegBuilder<T>> consumer) {
		consumer.accept(this);
		return this;
	}

	public BlockRegBuilder<T> onRegister(Consumer<T> consumer) {
		onRegister.add(consumer);
		return this;
	}

	public BlockRegBuilder<T> beforeRegisterItem(Consumer<BlockItemBuilder<T>> consumer) {
		beforeRegisterItem.add(consumer);
		return this;
	}

	public BlockRegBuilder<T> onRegisterItem(Consumer<Item> consumer) {
		onRegisterItem.add(consumer);
		return this;
	}

	public T register() {
		FabricBlockSettings settings = initialSettings;
		for (Consumer<FabricBlockSettings> consumer : settingsDeque) {
			consumer.accept(settings);
		}
		T block = blockFactory.apply(settings);
		Registry.register(Registry.BLOCK, identifier, block);
		if (renderLayer != null) {
			BlockRenderLayerMap.INSTANCE.putBlock(block, renderLayer.get());
		}
		for (Consumer<T> consumer : onRegister) {
			consumer.accept(block);
		}
		if (hasItemBuilder()) {
			for (Consumer<BlockItemBuilder<T>> consumer : beforeRegisterItem) {
				consumer.accept(itemBuilder);
			}
			Item item = itemBuilder.register(block);
			for (Consumer<Item> consumer : onRegisterItem) {
				consumer.accept(item);
			}
		}
		return block;
	}

	public static <T extends Block> BlockRegBuilder<T> create(Identifier identifier, Function<FabricBlockSettings, T> function) {
		return new BlockRegBuilder<T>(identifier, function);
	}
}
