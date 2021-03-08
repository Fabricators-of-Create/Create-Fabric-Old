package com.simibubi.create.content.logistics.block.chute;

import java.util.HashMap;
import java.util.Map;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.logistics.block.funnel.FunnelBlock;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ChuteBlock extends AbstractChuteBlock {
	
	public static final Property<Shape> SHAPE = EnumProperty.of("shape", Shape.class);
	public static final DirectionProperty FACING = Properties.HOPPER_FACING;

	public ChuteBlock(Settings p_i48440_1_) {
		super(p_i48440_1_);
		setDefaultState(getDefaultState().with(SHAPE, Shape.NORMAL)
			.with(FACING, Direction.DOWN));
	}

	public enum Shape implements StringIdentifiable {
		INTERSECTION, WINDOW, NORMAL;

		@Override
		public String asString() {
			return Lang.asId(name());
		}
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.CHUTE.create();
	}

	@Override
	public Direction getFacing(BlockState state) {
		return state.get(FACING);
	}

	@Override
	public boolean isOpen(BlockState state) {
		return state.get(FACING) == Direction.DOWN || state.get(SHAPE) == Shape.INTERSECTION;
	}

	@Override
	public boolean isTransparent(BlockState state) {
		return state.get(SHAPE) == Shape.WINDOW;
	}

	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		Shape shape = state.get(SHAPE);
		boolean down = state.get(FACING) == Direction.DOWN;
		if (!context.getWorld().isClient && down && shape != Shape.INTERSECTION) {
			context.getWorld()
				.setBlockState(context.getBlockPos(),
					state.with(SHAPE, shape == Shape.WINDOW ? Shape.NORMAL : Shape.WINDOW));
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState state = super.getPlacementState(ctx);
		Direction face = ctx.getSide();
		if (face.getAxis()
			.isHorizontal() && !ctx.shouldCancelInteraction()) {
			World world = ctx.getWorld();
			BlockPos pos = ctx.getBlockPos();
			return updateChuteState(state.with(FACING, face), world.getBlockState(pos.up()), world, pos);
		}
		return state;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> p_206840_1_) {
		super.appendProperties(p_206840_1_.add(SHAPE, FACING));
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		BlockState above = world.getBlockState(pos.up());
		return !isChute(above) || getChuteFacing(above) == Direction.DOWN;
	}

	@Override
	public BlockState updateChuteState(BlockState state, BlockState above, BlockView world, BlockPos pos) {
		if (!(state.getBlock() instanceof ChuteBlock))
			return state;

		Map<Direction, Boolean> connections = new HashMap<>();
		int amtConnections = 0;
		Direction facing = state.get(FACING);
		boolean vertical = facing == Direction.DOWN;

		if (!vertical) {
			BlockState target = world.getBlockState(pos.down()
				.offset(facing.getOpposite()));
			if (!isChute(target))
				return state.with(FACING, Direction.DOWN)
					.with(SHAPE, Shape.NORMAL);
		}

		for (Direction direction : Iterate.horizontalDirections) {
			BlockState diagonalInputChute = world.getBlockState(pos.up()
				.offset(direction));
			boolean value =
				diagonalInputChute.getBlock() instanceof ChuteBlock && diagonalInputChute.get(FACING) == direction;
			connections.put(direction, value);
			if (value)
				amtConnections++;
		}

		boolean noConnections = amtConnections == 0;
		if (vertical)
			return state.with(SHAPE,
				noConnections ? state.get(SHAPE) == Shape.WINDOW ? Shape.WINDOW : Shape.NORMAL : Shape.INTERSECTION);
		if (noConnections)
			return state.with(SHAPE, Shape.INTERSECTION);
		if (connections.get(Direction.NORTH) && connections.get(Direction.SOUTH))
			return state.with(SHAPE, Shape.INTERSECTION);
		if (connections.get(Direction.EAST) && connections.get(Direction.WEST))
			return state.with(SHAPE, Shape.INTERSECTION);
		if (amtConnections == 1 && connections.get(facing) && !(getChuteFacing(above) == Direction.DOWN)
			&& !(above.getBlock() instanceof FunnelBlock && FunnelBlock.getFunnelFacing(above) == Direction.DOWN))
			return state.with(SHAPE, Shape.NORMAL);
		return state.with(SHAPE, Shape.INTERSECTION);
	}

}
