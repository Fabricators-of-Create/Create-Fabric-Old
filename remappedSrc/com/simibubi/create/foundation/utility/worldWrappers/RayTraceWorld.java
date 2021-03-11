package com.simibubi.create.foundation.utility.worldWrappers;

import java.util.function.BiFunction;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public class RayTraceWorld implements BlockView {

	private WorldAccess template;
	private BiFunction<BlockPos, BlockState, BlockState> stateGetter;

	public RayTraceWorld(WorldAccess template, BiFunction<BlockPos, BlockState, BlockState> stateGetter) {
		this.template = template;
		this.stateGetter = stateGetter;
	}

	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return template.getBlockEntity(pos);
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return stateGetter.apply(pos, template.getBlockState(pos));
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return template.getFluidState(pos);
	}

}
