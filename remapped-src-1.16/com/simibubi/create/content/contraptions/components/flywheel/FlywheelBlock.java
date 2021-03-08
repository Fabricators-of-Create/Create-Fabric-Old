package com.simibubi.create.content.contraptions.components.flywheel;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class FlywheelBlock extends HorizontalKineticBlock {

	public static EnumProperty<ConnectionState> CONNECTION = EnumProperty.of("connection", ConnectionState.class);

	public FlywheelBlock(Settings properties) {
		super(properties);
		setDefaultState(getDefaultState().with(CONNECTION, ConnectionState.NONE));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(CONNECTION));
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.FLYWHEEL.create();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction preferred = getPreferredHorizontalFacing(context);
		if (preferred != null)
			return getDefaultState().with(HORIZONTAL_FACING, preferred.getOpposite());
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlayerFacing());
	}

	public static boolean isConnected(BlockState state) {
		return getConnection(state) != null;
	}

	public static Direction getConnection(BlockState state) {
		Direction facing = state.get(HORIZONTAL_FACING);
		ConnectionState connection = state.get(CONNECTION);

		if (connection == ConnectionState.LEFT)
			return facing.rotateYCounterclockwise();
		if (connection == ConnectionState.RIGHT)
			return facing.rotateYClockwise();
		return null;
	}

	public static void setConnection(World world, BlockPos pos, BlockState state, Direction direction) {
		Direction facing = state.get(HORIZONTAL_FACING);
		ConnectionState connection = ConnectionState.NONE;

		if (direction == facing.rotateYClockwise())
			connection = ConnectionState.RIGHT;
		if (direction == facing.rotateYCounterclockwise())
			connection = ConnectionState.LEFT;

		world.setBlockState(pos, state.with(CONNECTION, connection), 18);
		AllTriggers.triggerForNearbyPlayers(AllTriggers.FLYWHEEL, world, pos, 4);
	}

	@Override
	public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
		return face == state.get(HORIZONTAL_FACING).getOpposite();
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(HORIZONTAL_FACING).getAxis();
	}

	public enum ConnectionState implements StringIdentifiable {
		NONE, LEFT, RIGHT;

		@Override
		public String asString() {
			return Lang.asId(name());
		}
	}

}
