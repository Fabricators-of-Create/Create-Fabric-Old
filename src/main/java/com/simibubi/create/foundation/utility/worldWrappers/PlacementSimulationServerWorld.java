package com.simibubi.create.foundation.utility.worldWrappers;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.function.Predicate;

public class PlacementSimulationServerWorld extends WrappedServerWorld {
	public HashMap<BlockPos, BlockState> blocksAdded;

	public PlacementSimulationServerWorld(ServerWorld wrapped) {
		super(wrapped);
		blocksAdded = new HashMap<>();
	}
	
	public void clear() {
		blocksAdded.clear();
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState newState, int flags) {
		blocksAdded.put(pos.toImmutable(), newState);
		return true;
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state) {
		return setBlockState(pos, state, 0);
	}

	@Override
	public boolean testBlockState(BlockPos pos, Predicate<BlockState> condition) {
		return condition.test(getBlockState(pos));
	}
	
	@Override
	public boolean canSetBlock(BlockPos pos) {
		return true;
	}

	@Override
	public boolean isRegionLoaded(BlockPos min, BlockPos max) {
		return true;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (blocksAdded.containsKey(pos))
			return blocksAdded.get(pos);
		return Blocks.AIR.getDefaultState();
	}
	
}