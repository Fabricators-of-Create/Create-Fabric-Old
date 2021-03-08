package com.simibubi.create.foundation.block.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.simibubi.create.Create;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraftforge.client.event.ModelBakeEvent;

@SuppressWarnings("deprecation")
public abstract class CustomRenderedItemModel extends WrappedBakedModel {

	protected String basePath;
	protected Map<String, BakedModel> partials = new HashMap<>();
	protected Mode currentPerspective;
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

	@Override
	public BakedModel handlePerspective(Mode cameraTransformType, MatrixStack mat) {
		currentPerspective = cameraTransformType;
		return super.handlePerspective(cameraTransformType, mat);
	}

	protected void addPartials(String... partials) {
		this.partials.clear();
		for (String name : partials)
			this.partials.put(name, null);
	}

	public CustomRenderedItemModel loadPartials(ModelBakeEvent event) {
		for (String name : partials.keySet())
			partials.put(name, loadModel(event, name));
		return this;
	}

	private BakedModel loadModel(ModelBakeEvent event, String name) {
		return event.getModelLoader().bake(getPartialModelLocation(name), ModelRotation.X0_Y0);
	}

	private Identifier getPartialModelLocation(String name) {
		return new Identifier(Create.ID, "item/" + basePath + "/" + name);
	}

	public Mode getCurrentPerspective() {
		return currentPerspective;
	}
	
	public BakedModel getPartial(String name) {
		return partials.get(name);
	}

}
