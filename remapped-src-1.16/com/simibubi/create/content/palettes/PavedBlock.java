package com.simibubi.create.content.palettes;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public class PavedBlock extends Block {

	public static final BooleanProperty COVERED = BooleanProperty.of("covered");

	public PavedBlock(Settings properties) {
		super(properties);
		setDefaultState(getDefaultState().with(COVERED, false));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(COVERED));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return getDefaultState().with(COVERED, context.getWorld()
				.getBlockState(context.getBlockPos().up())
				.getBlock() == this);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction face, BlockState neighbour, WorldAccess worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		if (face == Direction.UP)
			return stateIn.with(COVERED, worldIn.getBlockState(facingPos).getBlock() == this);
		return stateIn;
	}

}
