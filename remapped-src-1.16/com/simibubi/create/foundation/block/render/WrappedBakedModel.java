package com.simibubi.create.foundation.block.render;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

public class WrappedBakedModel implements BakedModel {

	protected BakedModel template;

	public WrappedBakedModel(BakedModel template) {
		this.template = template;
	}
	
	@Override
	public BakedModel getBakedModel() {
		return template;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return template.useAmbientOcclusion();
	}

	@Override
	public boolean hasDepth() {
		return template.hasDepth();
	}

	@Override
	public boolean isBuiltin() {
		return template.isBuiltin();
	}

	@Override
	public Sprite getParticleTexture(IModelData data) {
		return template.getParticleTexture(data);
	}

	@Override
	public ModelOverrideList getOverrides() {
		return template.getOverrides();
	}

	@Override
	public BakedModel handlePerspective(Mode cameraTransformType, MatrixStack mat) {
		template.handlePerspective(cameraTransformType, mat);
		return this;
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
		return getQuads(state, side, rand, EmptyModelData.INSTANCE);
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData data) {
		return template.getQuads(state, side, rand, data);
	}

	@Override
	public Sprite getSprite() {
		return getParticleTexture(EmptyModelData.INSTANCE);
	}

	@Override
	public boolean isSideLit() {
		return template.isSideLit();
	}
}
