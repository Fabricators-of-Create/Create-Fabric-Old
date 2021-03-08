package com.simibubi.create.content.logistics.block.diodes;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

public class PoweredLatchBlock extends ToggleLatchBlock {

	public static BooleanProperty POWERED_SIDE = BooleanProperty.of("powered_side");

	public PoweredLatchBlock(Settings properties) {
		super(properties);
		setDefaultState(getDefaultState().with(POWERED_SIDE, false));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(POWERED_SIDE));
	}

	@Override
	protected void updatePowered(World worldIn, BlockPos pos, BlockState state) {
		boolean back = state.get(POWERED);
		boolean shouldBack = hasPower(worldIn, pos, state);
		boolean side = state.get(POWERED_SIDE);
		boolean shouldSide = isPoweredOnSides(worldIn, pos, state);

		TickPriority tickpriority = TickPriority.HIGH;
		if (this.isTargetNotAligned(worldIn, pos, state))
			tickpriority = TickPriority.EXTREMELY_HIGH;
		else if (side || back)
			tickpriority = TickPriority.VERY_HIGH;

		if (worldIn.getBlockTickScheduler().isTicking(pos, this))
			return;
		if (back != shouldBack || side != shouldSide)
			worldIn.getBlockTickScheduler().schedule(pos, this, this.getUpdateDelayInternal(state), tickpriority);
	}

	protected boolean isPoweredOnSides(World worldIn, BlockPos pos, BlockState state) {
		Direction direction = state.get(FACING);
		Direction left = direction.rotateYClockwise();
		Direction right = direction.rotateYCounterclockwise();

		for (Direction d : new Direction[] { left, right }) {
			BlockPos blockpos = pos.offset(d);
			int i = worldIn.getEmittedRedstonePower(blockpos, d);
			if (i > 0)
				return true;
			BlockState blockstate = worldIn.getBlockState(blockpos);
			if (blockstate.getBlock() == Blocks.REDSTONE_WIRE && blockstate.get(RedstoneWireBlock.POWER) > 0)
				return true;
		}
		return false;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		boolean back = state.get(POWERED);
		boolean shouldBack = this.hasPower(worldIn, pos, state);
		boolean side = state.get(POWERED_SIDE);
		boolean shouldSide = isPoweredOnSides(worldIn, pos, state);
		BlockState stateIn = state;

		if (back != shouldBack) {
			state = state.with(POWERED, shouldBack);
			if (shouldBack)
				state = state.with(POWERING, true);
			else if (side)
				state = state.with(POWERING, false);
		}

		if (side != shouldSide) {
			state = state.with(POWERED_SIDE, shouldSide);
			if (shouldSide)
				state = state.with(POWERING, false);
			else if (back)
				state = state.with(POWERING, true);
		}

		if (state != stateIn)
			worldIn.setBlockState(pos, state, 2);
	}

	@Override
	protected ActionResult activated(World worldIn, BlockPos pos, BlockState state) {
		if (state.get(POWERED) != state.get(POWERED_SIDE))
			return ActionResult.PASS;
		if (!worldIn.isClient)
			worldIn.setBlockState(pos, state.cycle(POWERING), 2);
		return ActionResult.SUCCESS;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockView world, BlockPos pos, Direction side) {
		if (side == null)
			return false;
		return side.getAxis().isHorizontal();
	}

}
