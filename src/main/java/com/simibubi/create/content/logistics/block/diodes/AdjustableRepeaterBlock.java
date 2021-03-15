package com.simibubi.create.content.logistics.block.diodes;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.AllBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class AdjustableRepeaterBlock extends AbstractDiodeBlock implements BlockEntityProvider {

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
	public BlockEntity createBlockEntity(BlockView world) {
		return AllBlocks.ADJUSTABLE_REPEATER.is(this) ? AllBlockEntities.ADJUSTABLE_REPEATER.instantiate()
			: AllBlockEntities.ADJUSTABLE_PULSE_REPEATER.instantiate();
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

}
