package com.smellypengu.createfabric.foundation.utility.worldWrappers;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.function.BiFunction;

public class RayTraceWorld implements BlockView {

	private World template;
	private BiFunction<BlockPos, BlockState, BlockState> stateGetter;

	public RayTraceWorld(World template, BiFunction<BlockPos, BlockState, BlockState> stateGetter) {
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

	@Override
	public int getHeight() {
		return template.getHeight();
	}

	@Override
	public int getBottomY() {
		return template.getBottomY();
	}
}
