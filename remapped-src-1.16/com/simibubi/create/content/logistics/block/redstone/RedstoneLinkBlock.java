package com.simibubi.create.content.logistics.block.redstone;

import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.block.ProperDirectionalBlock;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class RedstoneLinkBlock extends ProperDirectionalBlock implements ITE<RedstoneLinkTileEntity> {

	public static final BooleanProperty POWERED = Properties.POWERED;
	public static final BooleanProperty RECEIVER = BooleanProperty.of("receiver");

	public RedstoneLinkBlock(Settings properties) {
		super(properties);
		setDefaultState(getDefaultState().with(POWERED, false)
			.with(RECEIVER, false));
	}

	@Override
	public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
		boolean isMoving) {
		Direction blockFacing = state.get(FACING);

		if (fromPos.equals(pos.offset(blockFacing.getOpposite()))) {
			if (!canPlaceAt(state, worldIn, pos)) {
				worldIn.breakBlock(pos, true);
				return;
			}
		}

		updateTransmittedSignal(state, worldIn, pos, blockFacing);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		updateTransmittedSignal(state, worldIn, pos, state.get(FACING));
	}

	private void updateTransmittedSignal(BlockState state, World worldIn, BlockPos pos, Direction blockFacing) {
		if (worldIn.isClient)
			return;
		if (state.get(RECEIVER))
			return;

		int power = getPower(worldIn, pos);

		boolean previouslyPowered = state.get(POWERED);
		if (previouslyPowered != power > 0)
			worldIn.setBlockState(pos, state.cycle(POWERED), 2);

		int transmit = power;
		withTileEntityDo(worldIn, pos, te -> te.transmit(transmit));
	}

	private int getPower(World worldIn, BlockPos pos) {
		int power = 0;
		for (Direction direction : Iterate.directions)
			power = Math.max(worldIn.getEmittedRedstonePower(pos.offset(direction), direction), power);
		for (Direction direction : Iterate.directions)
			power = Math.max(worldIn.getEmittedRedstonePower(pos.offset(direction), Direction.UP), power);
		return power;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return state.get(POWERED) && state.get(RECEIVER);
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side) {
		if (side != blockState.get(FACING))
			return 0;
		return getWeakRedstonePower(blockState, blockAccess, pos, side);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView blockAccess, BlockPos pos, Direction side) {
		if (!state.get(RECEIVER))
			return 0;
		try {
			RedstoneLinkTileEntity tileEntity = getTileEntity(blockAccess, pos);
			return tileEntity.getReceivedSignal();
		} catch (TileEntityException e) {
		}
		return 0;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(POWERED, RECEIVER);
		super.appendProperties(builder);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.REDSTONE_LINK.create();
	}

	@Override
	public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
		BlockHitResult hit) {
		if (player.isSneaking())
			return toggleMode(state, worldIn, pos);
		return ActionResult.PASS;
	}

	public ActionResult toggleMode(BlockState state, World worldIn, BlockPos pos) {
		if (worldIn.isClient)
			return ActionResult.SUCCESS;
		try {
			RedstoneLinkTileEntity te = getTileEntity(worldIn, pos);
			Boolean wasReceiver = state.get(RECEIVER);
			boolean blockPowered = worldIn.isReceivingRedstonePower(pos);
			worldIn.setBlockState(pos, state.cycle(RECEIVER)
				.with(POWERED, blockPowered), 3);
			te.transmit(wasReceiver ? 0 : getPower(worldIn, pos));
			return ActionResult.SUCCESS;
		} catch (TileEntityException e) {
		}
		return ActionResult.PASS;
	}

	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		if (toggleMode(state, context.getWorld(), context.getBlockPos()) == ActionResult.SUCCESS)
			return ActionResult.SUCCESS;
		return super.onWrenched(state, context);
	}

	@Override
	public BlockState getRotatedBlockState(BlockState originalState, Direction _targetedFace) {
		return originalState;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return side != null;
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos) {
		BlockPos neighbourPos = pos.offset(state.get(FACING)
			.getOpposite());
		BlockState neighbour = worldIn.getBlockState(neighbourPos);
		return !neighbour.getMaterial().isReplaceable();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState state = getDefaultState();
		state = state.with(FACING, context.getSide());
		return state;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		return AllShapes.REDSTONE_BRIDGE.get(state.get(FACING));
	}

	@Override
	public Class<RedstoneLinkTileEntity> getTileEntityClass() {
		return RedstoneLinkTileEntity.class;
	}

}
