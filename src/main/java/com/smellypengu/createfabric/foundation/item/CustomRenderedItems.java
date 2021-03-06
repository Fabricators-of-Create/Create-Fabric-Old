package com.smellypengu.createfabric.foundation.item;

import com.smellypengu.createfabric.foundation.block.render.CustomRenderedItemModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Item;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomRenderedItems {

	private List<Pair<Supplier<? extends Item>, Function<BakedModel, ? extends CustomRenderedItemModel>>> registered;
	private Map<Item, Function<BakedModel, ? extends CustomRenderedItemModel>> customModels;
	
	public CustomRenderedItems() {
		registered = new ArrayList<>();
		customModels = new IdentityHashMap<>();
	}

	public void register(Supplier<? extends Item> entry,
						 Function<BakedModel, ? extends CustomRenderedItemModel> behaviour) {
		registered.add(Pair.of(entry, behaviour));
	}
	
	public void foreach(BiConsumer<Item, Function<BakedModel, ? extends CustomRenderedItemModel>> consumer) {
		loadEntriesIfMissing();
		customModels.forEach(consumer);
	}

	private void loadEntriesIfMissing() {
		if (customModels.isEmpty())
			loadEntries();
	}

	private void loadEntries() {
		customModels.clear();
		registered.forEach(p -> customModels.put(p.getKey()
			.get(), p.getValue()));
	}

}
