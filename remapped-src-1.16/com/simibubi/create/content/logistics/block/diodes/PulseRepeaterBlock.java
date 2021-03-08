package com.simibubi.create.content.logistics.block.diodes;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;

public class PulseRepeaterBlock extends AbstractDiodeBlock {

	public static BooleanProperty PULSING = BooleanProperty.of("pulsing");

	public PulseRepeaterBlock(Settings properties) {
		super(properties);
		setDefaultState(getDefaultState().with(PULSING, false).with(POWERED, false));
	}

	@Override
	protected int getUpdateDelayInternal(BlockState state) {
		return 1;
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, BlockView world, BlockPos pos, Direction side) {
		if (side == null)
			return false;
		return side.getAxis() == state.get(FACING).getAxis();
	}
	
	@Override
	public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		boolean powered = state.get(POWERED);
		boolean pulsing = state.get(PULSING);
		boolean shouldPower = hasPower(worldIn, pos, state);

		if (pulsing) {
			worldIn.setBlockState(pos, state.with(POWERED, shouldPower).with(PULSING, false), 2);
		} else if (powered && !shouldPower) {
			worldIn.setBlockState(pos, state.with(POWERED, false).with(PULSING, false), 2);
		} else if (!powered) {
			worldIn.setBlockState(pos, state.with(POWERED, true).with(PULSING, true), 2);
			worldIn.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), TickPriority.HIGH);
		}

	}

	@Override
	protected int getOutputLevel(BlockView worldIn, BlockPos pos, BlockState state) {
		return state.get(PULSING) ? 15 : 0;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, PULSING);
		super.appendProperties(builder);
	}

}
