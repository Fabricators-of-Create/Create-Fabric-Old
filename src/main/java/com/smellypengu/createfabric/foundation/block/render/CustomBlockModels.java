package com.smellypengu.createfabric.foundation.block.render;

import net.minecraft.block.Block;
import net.minecraft.client.render.model.BakedModel;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CustomBlockModels {

	private List<Pair<Supplier<? extends Block>, Function<BakedModel, ? extends BakedModel>>> registered;
	private Map<Block, Function<BakedModel, ? extends BakedModel>> customModels;

	public CustomBlockModels() {
		registered = new ArrayList<>();
		customModels = new IdentityHashMap<>();
	}

	public void register(Supplier<? extends Block> entry,
						 Function<BakedModel, ? extends BakedModel> behaviour) {
		registered.add(Pair.of(entry, behaviour));
	}

	public void foreach(BiConsumer<Block, Function<BakedModel, ? extends BakedModel>> consumer) {
		loadEntriesIfMissing();
		customModels.forEach(consumer);
	}

	private void loadEntriesIfMissing() {
		if (customModels.isEmpty())
			loadEntries();
	}

	private void loadEntries() {
		customModels.clear();
		registered.forEach(p -> {
			Block key = p.getKey()
				.get();
			
			Function<BakedModel, ? extends BakedModel> existingModel = customModels.get(key);
			if (existingModel != null) {
				customModels.put(key, p.getValue()
					.andThen(existingModel));
				return;
			}
			
			customModels.put(key, p.getValue());
		});
	}

}
