package com.smellypengu.createfabric.content.contraptions.relays.elementary;

import com.smellypengu.createfabric.AllBlockEntities;
import com.smellypengu.createfabric.content.contraptions.base.RotatedPillarKineticBlock;
import com.smellypengu.createfabric.content.contraptions.wrench.IWrenchableWithBracket;
import com.smellypengu.createfabric.foundation.block.entity.BlockEntityBehaviour;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.Optional;

public abstract class AbstractShaftBlock extends RotatedPillarKineticBlock implements Waterloggable, IWrenchableWithBracket {

	public AbstractShaftBlock(Settings properties) {
		super(properties);
		setDefaultState(super.getDefaultState().with(Properties.WATERLOGGED, false));
	}
	
	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		return IWrenchableWithBracket.super.onWrenched(state, context);
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.NORMAL;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return AllBlockEntities.SIMPLE_KINETIC.instantiate(pos, state);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state != newState && !moved)
			removeBracket(world, pos, true).ifPresent(stack -> Block.dropStack(world, pos, stack));
		super.onStateReplaced(state, world, pos, newState, moved);
	}
	
	// IRotate:


	@Override
	public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.get(AXIS);
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return state.get(AXIS);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false)
			: Fluids.EMPTY.getDefaultState();
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(Properties.WATERLOGGED);
		super.appendProperties(builder);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (state.get(Properties.WATERLOGGED)) {
			world.getFluidTickScheduler()
					.schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return state;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		FluidState ifluidstate = context.getWorld()
				.getFluidState(context.getBlockPos());
		return super.getPlacementState(context).with(Properties.WATERLOGGED,
				Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
	}

	@Override
	public Optional<ItemStack> removeBracket(BlockView world, BlockPos pos, boolean inOnReplacedContext) {
		BracketedBlockEntityBehaviour behaviour = BlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE);
		if (behaviour == null)
			return Optional.empty();
		BlockState bracket = behaviour.getBracket();
		behaviour.removeBracket(inOnReplacedContext);
		if (bracket == Blocks.AIR.getDefaultState())
			return Optional.empty();
		return Optional.of(new ItemStack(bracket.getBlock()));
	}
}
