package com.simibubi.create.foundation.block.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.simibubi.create.Create;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.util.Identifier;

@SuppressWarnings("deprecation")
public abstract class CustomRenderedItemModel extends WrappedBakedModel {

	protected String basePath;
	protected Map<String, BakedModel> partials = new HashMap<>();
	protected BuiltinModelItemRenderer renderer;

	public CustomRenderedItemModel(BakedModel template, String basePath) {
		super(template);
		this.basePath = basePath;
		this.renderer = createRenderer();
	}

	public final List<Identifier> getModelLocations() {
		return partials.keySet().stream().map(this::getPartialModelLocation).collect(Collectors.toList());
	}

	public BuiltinModelItemRenderer getRenderer() {
		return renderer;
	}

	public abstract BuiltinModelItemRenderer createRenderer();

	@Override
	public boolean isBuiltin() {
		return true;
	}

	protected void addPartials(String... partials) {
		this.partials.clear();
		for (String name : partials)
			this.partials.put(name, null);
	}

	public CustomRenderedItemModel loadPartials(ModelLoader modelLoader) {
		for (String name : partials.keySet())
			partials.put(name, loadModel(modelLoader, name));
		return this;
	}

	private BakedModel loadModel(ModelLoader modelLoader, String name) {
		return modelLoader.bake(getPartialModelLocation(name), ModelRotation.X0_Y0);
	}

	private Identifier getPartialModelLocation(String name) {
		return new Identifier(Create.ID, "item/" + basePath + "/" + name);
	}

	public BakedModel getPartial(String name) {
		return partials.get(name);
	}
}
