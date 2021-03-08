package com.simibubi.create.content.contraptions.relays.elementary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.simibubi.create.foundation.block.render.WrappedBakedModel;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.VirtualEmptyModelData;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

public class BracketedKineticBlockModel extends WrappedBakedModel {

	private static ModelProperty<BracketedModelData> BRACKET_PROPERTY = new ModelProperty<>();

	public BracketedKineticBlockModel(BakedModel template) {
		super(template);
	}

	@Override
	public IModelData getModelData(BlockRenderView world, BlockPos pos, BlockState state, IModelData tileData) {
		if (tileData == VirtualEmptyModelData.INSTANCE)
			return tileData;
		BracketedModelData data = new BracketedModelData();
		BracketedTileEntityBehaviour attachmentBehaviour =
			TileEntityBehaviour.get(world, pos, BracketedTileEntityBehaviour.TYPE);
		if (attachmentBehaviour != null)
			data.putBracket(attachmentBehaviour.getBracket());
		return new ModelDataMap.Builder().withInitial(BRACKET_PROPERTY, data)
			.build();
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData data) {
		if (data instanceof ModelDataMap) {
			List<BakedQuad> quads = new ArrayList<>();
			ModelDataMap modelDataMap = (ModelDataMap) data;
			if (modelDataMap.hasProperty(BRACKET_PROPERTY)) {
				quads = new ArrayList<>(quads);
				addQuads(quads, state, side, rand, modelDataMap, modelDataMap.getData(BRACKET_PROPERTY));
			}
			return quads;
		}
		return super.getQuads(state, side, rand, data);
	}

	private void addQuads(List<BakedQuad> quads, BlockState state, Direction side, Random rand, IModelData data,
		BracketedModelData pipeData) {
		BakedModel bracket = pipeData.getBracket();
		if (bracket == null)
			return;
		quads.addAll(bracket.getQuads(state, side, rand, data));
	}

	private class BracketedModelData {
		BakedModel bracket;

		public void putBracket(BlockState state) {
			this.bracket = MinecraftClient.getInstance()
				.getBlockRenderManager()
				.getModel(state);
		}

		public BakedModel getBracket() {
			return bracket;
		}

	}

}
