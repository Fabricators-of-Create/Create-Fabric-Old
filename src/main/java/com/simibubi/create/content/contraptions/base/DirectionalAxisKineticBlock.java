package com.simibubi.create.content.contraptions.base;

import com.simibubi.create.foundation.utility.DirectionHelper;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class DirectionalAxisKineticBlock extends DirectionalKineticBlock {

	public static final BooleanProperty AXIS_ALONG_FIRST_COORDINATE = BooleanProperty.of("axis_along_first");

	public DirectionalAxisKineticBlock(Settings properties) {
		super(properties);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(AXIS_ALONG_FIRST_COORDINATE);
		super.appendProperties(builder);
	}

	protected Direction getFacingForPlacement(ItemPlacementContext context) {
		Direction facing = context.getPlayerLookDirection()
			.getOpposite();
		if (context.getPlayer() != null && context.getPlayer()
			.isSneaking())
			facing = facing.getOpposite();
		return facing;
	}

	protected boolean getAxisAlignmentForPlacement(ItemPlacementContext context) {
		return context.getPlayerFacing()
			.getAxis() == Direction.Axis.X;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction facing = getFacingForPlacement(context);
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		boolean alongFirst = false;
		Direction.Axis faceAxis = facing.getAxis();

		if (faceAxis.isHorizontal()) {
			alongFirst = faceAxis == Direction.Axis.Z;
			Direction positivePerpendicular = DirectionHelper.getPositivePerpendicular(faceAxis);

			boolean shaftAbove = prefersConnectionTo(world, pos, Direction.UP, true);
			boolean shaftBelow = prefersConnectionTo(world, pos, Direction.DOWN, true);
			boolean preferLeft = prefersConnectionTo(world, pos, positivePerpendicular, false);
			boolean preferRight = prefersConnectionTo(world, pos, positivePerpendicular.getOpposite(), false);

			if (shaftAbove || shaftBelow || preferLeft || preferRight)
				alongFirst = faceAxis == Direction.Axis.X;
		}

		if (faceAxis.isVertical()) {
			alongFirst = getAxisAlignmentForPlacement(context);
			Direction prefferedSide = null;

			for (Direction side : Iterate.horizontalDirections) {
				if (!prefersConnectionTo(world, pos, side, true)
					&& !prefersConnectionTo(world, pos, side.rotateYClockwise(), false)) // TODO I THINK ITS rotateYClockwise
					continue;
				if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
					prefferedSide = null;
					break;
				}
				prefferedSide = side;
			}

			if (prefferedSide != null)
				alongFirst = prefferedSide.getAxis() == Direction.Axis.X;
		}

		return this.getDefaultState()
			.with(FACING, facing)
			.with(AXIS_ALONG_FIRST_COORDINATE, alongFirst);
	}

	protected boolean prefersConnectionTo(World reader, BlockPos pos, Direction facing, boolean shaftAxis) {
		if (!shaftAxis)
			return false;
		BlockPos neighbourPos = pos.offset(facing);
		BlockState blockState = reader.getBlockState(neighbourPos);
		Block block = blockState.getBlock();
		return block instanceof Rotating && ((Rotating) block).hasShaftTowards(reader, neighbourPos, blockState, facing.getOpposite());
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		Direction.Axis pistonAxis = state.get(FACING)
			.getAxis();
		boolean alongFirst = state.get(AXIS_ALONG_FIRST_COORDINATE);

		if (pistonAxis == Direction.Axis.X)
			return alongFirst ? Direction.Axis.Y : Direction.Axis.Z;
		if (pistonAxis == Direction.Axis.Y)
			return alongFirst ? Direction.Axis.X : Direction.Axis.Z;
		if (pistonAxis == Direction.Axis.Z)
			return alongFirst ? Direction.Axis.X : Direction.Axis.Y;

		throw new IllegalStateException("Unknown axis??");
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot) {
		if (rot.ordinal() % 2 == 1)
			state = state.cycle(AXIS_ALONG_FIRST_COORDINATE);
		return super.rotate(state, rot);
	}

	@Override
	public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == getRotationAxis(state);
	}

}
