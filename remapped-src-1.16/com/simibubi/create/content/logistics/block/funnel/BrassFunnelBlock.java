package com.simibubi.create.content.logistics.block.funnel;

import com.simibubi.create.AllBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class BrassFunnelBlock extends FunnelBlock {

	public BrassFunnelBlock(Settings p_i48415_1_) {
		super(p_i48415_1_);
	}

	@Override
	public BlockState getEquivalentBeltFunnel(BlockView world, BlockPos pos, BlockState state) {
		Direction facing = state.get(FACING);
		return AllBlocks.BRASS_BELT_FUNNEL.getDefaultState()
			.with(BeltFunnelBlock.FACING, facing)
			.with(POWERED, state.get(POWERED));
	}

}
