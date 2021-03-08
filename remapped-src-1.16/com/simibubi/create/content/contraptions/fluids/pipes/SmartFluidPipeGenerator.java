package com.simibubi.create.content.contraptions.fluids.pipes;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraftforge.client.model.generators.ModelFile;

public class SmartFluidPipeGenerator extends SpecialBlockStateGen {

	@Override
	protected int getXRotation(BlockState state) {
		WallMountLocation attachFace = state.get(SmartFluidPipeBlock.FACE);
		return attachFace == WallMountLocation.CEILING ? 180 : attachFace == WallMountLocation.FLOOR ? 0 : 270;
	}

	@Override
	protected int getYRotation(BlockState state) {
		WallMountLocation attachFace = state.get(SmartFluidPipeBlock.FACE);
		int angle = horizontalAngle(state.get(SmartFluidPipeBlock.FACING));
		return angle + (attachFace == WallMountLocation.CEILING ? 180 : 0);
	}

	@Override
	public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
		BlockState state) {
		return AssetLookup.partialBaseModel(ctx, prov);
	}

}
