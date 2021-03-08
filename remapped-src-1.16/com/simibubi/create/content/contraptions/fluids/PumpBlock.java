package com.simibubi.create.content.contraptions.fluids;

import java.util.Random;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class PumpBlock extends DirectionalKineticBlock implements Waterloggable {

	public PumpBlock(Settings p_i48415_1_) {
		super(p_i48415_1_);
		setDefaultState(super.getDefaultState().with(Properties.WATERLOGGED, false));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.MECHANICAL_PUMP.create();
	}

	@Override
	public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
		return originalState.with(FACING, originalState.get(FACING)
			.getOpposite());
	}

	@Override
	public BlockState updateAfterWrenched(BlockState newState, ItemUsageContext context) {
		BlockState state = super.updateAfterWrenched(newState, context);
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		if (world.isClient)
			return state;
		BlockEntity tileEntity = world.getBlockEntity(pos);
		if (!(tileEntity instanceof PumpTileEntity))
			return state;
		PumpTileEntity pump = (PumpTileEntity) tileEntity;
		pump.sidesToUpdate.forEach(MutableBoolean::setTrue);
		pump.reversed = !pump.reversed;
		return state;
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(FACING)
			.getAxis();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView p_220053_2_, BlockPos p_220053_3_,
		ShapeContext p_220053_4_) {
		return AllShapes.PUMP.get(state.get(FACING));
	}

	@Override
	public boolean hasIntegratedCogwheel(WorldView world, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block otherBlock, BlockPos neighborPos,
		boolean isMoving) {
		DebugInfoSender.sendNeighborUpdate(world, pos);
		Direction d = FluidPropagator.validateNeighbourChange(state, world, pos, otherBlock, neighborPos, isMoving);
		if (d == null)
			return;
		if (!isOpenAt(state, d))
			return;
		world.getBlockTickScheduler()
			.schedule(pos, this, 1, TickPriority.HIGH);
//		if (world.isRemote)
//			return;
//		if (otherBlock instanceof FluidPipeBlock)
//			return;
//		TileEntity tileEntity = world.getTileEntity(pos);
//		if (!(tileEntity instanceof PumpTileEntity))
//			return;
//		PumpTileEntity pump = (PumpTileEntity) tileEntity;
//		Direction facing = state.get(FACING);
//		for (boolean front : Iterate.trueAndFalse) {
//			Direction side = front ? facing : facing.getOpposite();
//			if (!pos.offset(side)
//				.equals(neighborPos))
//				continue;
//			pump.updatePipesOnSide(side);
//		}
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false)
			: Fluids.EMPTY.getDefaultState();
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(Properties.WATERLOGGED);
		super.appendProperties(builder);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbourState,
		WorldAccess world, BlockPos pos, BlockPos neighbourPos) {
		if (state.get(Properties.WATERLOGGED)) {
			world.getFluidTickScheduler()
				.schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return state;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		FluidState FluidState = context.getWorld()
			.getFluidState(context.getBlockPos());
		return super.getPlacementState(context).with(Properties.WATERLOGGED,
			Boolean.valueOf(FluidState.getFluid() == Fluids.WATER));
	}

	public static boolean isPump(BlockState state) {
		return state.getBlock() instanceof PumpBlock;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (world.isClient)
			return;
		if (state != oldState)
			world.getBlockTickScheduler()
				.schedule(pos, this, 1, TickPriority.HIGH);
	}

	public static boolean isOpenAt(BlockState state, Direction d) {
		return d.getAxis() == state.get(FACING)
			.getAxis();
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random r) {
		FluidPropagator.propagateChangedPipe(world, pos, state);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		boolean blockTypeChanged = state.getBlock() != newState.getBlock();
		if (blockTypeChanged && !world.isClient)
			FluidPropagator.propagateChangedPipe(world, pos, state);
		if (state.hasTileEntity() && (blockTypeChanged || !newState.hasTileEntity()))
			world.removeBlockEntity(pos);
	}
	
}
