package me.pepperbell.reghelper;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class BlockItemBuilder<T extends Block> {
	private final BlockRegBuilder<T> parent;
	private final BiFunction<T, FabricItemSettings, Item> function;
	private FabricItemSettings initialSettings = new FabricItemSettings();
	private Deque<Consumer<FabricItemSettings>> settingsDeque = new ArrayDeque<>();

	private BlockItemBuilder(BlockRegBuilder<T> parent, BiFunction<T, FabricItemSettings, Item> function) {
		this.parent = parent;
		this.function = function;
	}

	public BlockItemBuilder<T> properties(Consumer<FabricItemSettings> consumer) {
		settingsDeque.add(consumer);
		return this;
	}

	public BlockRegBuilder<T> build() {
		return parent;
	}

	public Item register(T block) {
		FabricItemSettings settings = initialSettings;
		for (Consumer<FabricItemSettings> consumer : settingsDeque) {
			consumer.accept(settings);
		}
		Item item = function.apply(block, settings);
		Registry.register(Registry.ITEM, parent.getId(), item);
		return item;
	}

	public static <T extends Block> BlockItemBuilder<T> create(BlockRegBuilder<T> parent, BiFunction<T, FabricItemSettings, Item> function) {
		return new BlockItemBuilder<T>(parent, function);
	}

	public static <T extends Block> BlockItemBuilder<T> create(BlockRegBuilder<T> parent) {
		return new BlockItemBuilder<T>(parent, BlockItem::new);
	}
}
