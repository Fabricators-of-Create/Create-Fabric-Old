package com.simibubi.create.content.contraptions.components.structureMovement.bearing;

import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.WorldView;

public abstract class BearingBlock extends DirectionalKineticBlock {

	public BearingBlock(Settings properties) {
		super(properties);
	}

	@Override
	public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
		return face == state.get(FACING).getOpposite();
	}
	
	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(FACING).getAxis();
	}

	@Override
	public boolean showCapacityWithAnnotation() {
		return true;
	}

}
