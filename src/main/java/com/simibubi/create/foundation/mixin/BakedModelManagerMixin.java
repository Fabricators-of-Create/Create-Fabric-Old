package com.simibubi.create.foundation.mixin;

import static com.simibubi.create.CreateClient.getCustomBlockModels;
import static com.simibubi.create.CreateClient.getCustomItemModels;
import static com.simibubi.create.CreateClient.getCustomRenderedItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.foundation.mixinterface.BakedModelManagerExtension;

import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin implements BakedModelManagerExtension {
	@Shadow
	private Map<Identifier, BakedModel> models;

	@Inject(at = @At("TAIL"), method = "apply(Lnet/minecraft/client/render/model/ModelLoader;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V")
	public void onModelBake(ModelLoader modelLoader, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
		AllBlockPartials.onModelBake();

		getCustomBlockModels()
			.foreach((block, modelFunc) -> swapModels(modelLoader.getBakedModelMap(), getAllBlockStateModelLocations(block), modelFunc));
		getCustomItemModels()
			.foreach((item, modelFunc) -> swapModels(modelLoader.getBakedModelMap(), getItemModelLocation(item), modelFunc));
		getCustomRenderedItems().foreach((item, modelFunc) -> {
			swapModels(modelLoader.getBakedModelMap(), getItemModelLocation(item), m -> modelFunc.apply(m)
				.loadPartials(modelLoader));
		});
	}

	@Override
	public BakedModel getModel(Identifier id) {
		return models.get(id);
	}

	private static Identifier getItemModelLocation(Item item) {
		return new ModelIdentifier(item.getName().toString(), "inventory");
	}

	private static List<Identifier> getAllBlockStateModelLocations(Block block) {
		List<Identifier> models = new ArrayList<>();
		block.getStateManager()
			.getStates()
			.forEach(state -> {
				models.add(getBlockModelLocation(block, BlockModels.propertyMapToString(state.getEntries())));
			});
		return models;
	}

	private static Identifier getBlockModelLocation(Block block, String suffix) {
		return new Identifier(block.getName().toString(), suffix);
	}

	private static <T extends BakedModel> void swapModels(Map<Identifier, BakedModel> modelRegistry,
												   List<Identifier> locations, Function<BakedModel, T> factory) {
		locations.forEach(location -> {
			swapModels(modelRegistry, location, factory);
		});
	}

	private static <T extends BakedModel> void swapModels(Map<Identifier, BakedModel> modelRegistry,
												   Identifier location, Function<BakedModel, T> factory) {
		modelRegistry.put(location, factory.apply(modelRegistry.get(location)));
	}
}
