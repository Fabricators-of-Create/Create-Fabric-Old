package com.simibubi.create.content.logistics.block.diodes;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTileEntities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class AdjustableRepeaterBlock extends AbstractDiodeBlock {

	public static BooleanProperty POWERING = BooleanProperty.of("powering");

	public AdjustableRepeaterBlock(Settings properties) {
		super(properties);
		setDefaultState(getDefaultState().with(POWERED, false)
			.with(POWERING, false));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(POWERED, POWERING, FACING);
		super.appendProperties(builder);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllBlocks.ADJUSTABLE_REPEATER.is(this) ? AllTileEntities.ADJUSTABLE_REPEATER.create()
			: AllTileEntities.ADJUSTABLE_PULSE_REPEATER.create();
	}

	@Override
	protected int getOutputLevel(BlockView worldIn, BlockPos pos, BlockState state) {
		return state.get(POWERING) ? 15 : 0;
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side) {
		return blockState.get(FACING) == side ? this.getOutputLevel(blockAccess, pos, blockState) : 0;
	}

	@Override
	protected int getUpdateDelayInternal(BlockState p_196346_1_) {
		return 0;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockView world, BlockPos pos, Direction side) {
		if (side == null)
			return false;
		return side.getAxis() == state.get(FACING)
			.getAxis();
	}

}
