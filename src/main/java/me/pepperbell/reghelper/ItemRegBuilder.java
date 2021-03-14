package me.pepperbell.reghelper;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Function;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemRegBuilder<T extends Item> {
	private final Identifier identifier;
	private final Function<FabricItemSettings, T> function;
	private FabricItemSettings initialSettings = new FabricItemSettings();
	private Deque<Consumer<FabricItemSettings>> settingsDeque = new ArrayDeque<>();
	private Deque<Consumer<T>> onRegister = new ArrayDeque<>();

	private ItemRegBuilder(Identifier identifier, Function<FabricItemSettings, T> function) {
		this.identifier = identifier;
		this.function = function;
	}

	public Identifier getId() {
		return identifier;
	}

	public ItemRegBuilder<T> properties(Consumer<FabricItemSettings> consumer) {
		settingsDeque.add(consumer);
		return this;
	}

	public ItemRegBuilder<T> onRegister(Consumer<T> consumer) {
		onRegister.add(consumer);
		return this;
	}

	public T register() {
		FabricItemSettings settings = initialSettings;
		for (Consumer<FabricItemSettings> consumer : settingsDeque) {
			consumer.accept(settings);
		}
		T item = function.apply(settings);
		Registry.register(Registry.ITEM, identifier, item);
		for (Consumer<T> consumer : onRegister) {
			consumer.accept(item);
		}
		return item;
	}
	
	public static <T extends Item> ItemRegBuilder<T> create(Identifier identifier, Function<FabricItemSettings, T> function) {
		return new ItemRegBuilder<T>(identifier, function);
	}
}
