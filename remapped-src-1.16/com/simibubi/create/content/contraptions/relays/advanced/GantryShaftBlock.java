package com.simibubi.create.content.contraptions.relays.advanced;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.placement.IPlacementHelper;
import com.simibubi.create.foundation.utility.placement.PlacementHelpers;
import com.simibubi.create.foundation.utility.placement.PlacementOffset;
import com.simibubi.create.foundation.utility.placement.util.PoleHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class GantryShaftBlock extends DirectionalKineticBlock {

	public static final Property<Part> PART = EnumProperty.of("part", Part.class);
	public static final BooleanProperty POWERED = Properties.POWERED;

	private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

	public enum Part implements StringIdentifiable {
		START, MIDDLE, END, SINGLE;

		@Override
		public String asString() {
			return Lang.asId(name());
		}
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(PART, POWERED));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray) {
		ItemStack heldItem = player.getStackInHand(hand);

		IPlacementHelper placementHelper = PlacementHelpers.get(placementHelperId);
		if (!placementHelper.matchesItem(heldItem))
			return ActionResult.PASS;

		return placementHelper.getOffset(world, state, pos, ray).placeInWorld(world, ((BlockItem) heldItem.getItem()), player, hand, ray);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView p_220053_2_, BlockPos p_220053_3_,
		ShapeContext p_220053_4_) {
		return AllShapes.EIGHT_VOXEL_POLE.get(state.get(FACING)
			.getAxis());
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbour, WorldAccess world,
		BlockPos pos, BlockPos neighbourPos) {
		Direction facing = state.get(FACING);
		Axis axis = facing.getAxis();
		if (direction.getAxis() != axis)
			return state;
		boolean connect = AllBlocks.GANTRY_SHAFT.has(neighbour) && neighbour.get(FACING) == facing;

		Part part = state.get(PART);
		if (direction.getDirection() == facing.getDirection()) {
			if (connect) {
				if (part == Part.END)
					part = Part.MIDDLE;
				if (part == Part.SINGLE)
					part = Part.START;
			} else {
				if (part == Part.MIDDLE)
					part = Part.END;
				if (part == Part.START)
					part = Part.SINGLE;
			}
		} else {
			if (connect) {
				if (part == Part.START)
					part = Part.MIDDLE;
				if (part == Part.SINGLE)
					part = Part.END;
			} else {
				if (part == Part.MIDDLE)
					part = Part.START;
				if (part == Part.END)
					part = Part.SINGLE;
			}
		}

		return state.with(PART, part);
	}

	public GantryShaftBlock(Settings properties) {
		super(properties);
		setDefaultState(getDefaultState().with(POWERED, false)
			.with(PART, Part.SINGLE));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState state = super.getPlacementState(context);
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		Direction face = context.getSide();

		BlockState neighbour = world.getBlockState(pos.offset(state.get(FACING)
			.getOpposite()));

		BlockState clickedState =
			AllBlocks.GANTRY_SHAFT.has(neighbour) ? neighbour : world.getBlockState(pos.offset(face.getOpposite()));

		if (AllBlocks.GANTRY_SHAFT.has(clickedState) && clickedState.get(FACING)
			.getAxis() == state.get(FACING)
				.getAxis()) {
			Direction facing = clickedState.get(FACING);
			state = state.with(FACING, context.getPlayer() == null || !context.getPlayer()
				.isSneaking() ? facing : facing.getOpposite());
		}

		return state.with(POWERED, shouldBePowered(state, world, pos));
	}

	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		ActionResult onWrenched = super.onWrenched(state, context);
		if (onWrenched.isAccepted()) {
			BlockPos pos = context.getBlockPos();
			World world = context.getWorld();
			neighborUpdate(world.getBlockState(pos), world, pos, state.getBlock(), pos, false);
		}
		return onWrenched;
	}

	@Override
	public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block p_220069_4_, BlockPos p_220069_5_,
		boolean p_220069_6_) {
		if (worldIn.isClient)
			return;
		boolean previouslyPowered = state.get(POWERED);
		boolean shouldPower = worldIn.isReceivingRedstonePower(pos); // shouldBePowered(state, worldIn, pos);

		if (!previouslyPowered && !shouldPower && shouldBePowered(state, worldIn, pos)) {
			worldIn.setBlockState(pos, state.with(POWERED, true), 3);
			return;
		}

		if (previouslyPowered == shouldPower)
			return;

		// Collect affected gantry shafts
		List<BlockPos> toUpdate = new ArrayList<>();
		Direction facing = state.get(FACING);
		Axis axis = facing.getAxis();
		for (Direction d : Iterate.directionsInAxis(axis)) {
			BlockPos currentPos = pos.offset(d);
			while (true) {
				if (!worldIn.canSetBlock(currentPos))
					break;
				BlockState currentState = worldIn.getBlockState(currentPos);
				if (!(currentState.getBlock() instanceof GantryShaftBlock))
					break;
				if (currentState.get(FACING) != facing)
					break;
				if (!shouldPower && currentState.get(POWERED) && worldIn.isReceivingRedstonePower(currentPos))
					return;
				if (currentState.get(POWERED) == shouldPower)
					break;
				toUpdate.add(currentPos);
				currentPos = currentPos.offset(d);
			}
		}

		toUpdate.add(pos);
		for (BlockPos blockPos : toUpdate) {
			BlockState blockState = worldIn.getBlockState(blockPos);
			BlockEntity te = worldIn.getBlockEntity(blockPos);
			if (te instanceof KineticTileEntity)
				((KineticTileEntity) te).detachKinetics();
			if (blockState.getBlock() instanceof GantryShaftBlock)
				worldIn.setBlockState(blockPos, blockState.with(POWERED, shouldPower), 2);
		}
	}

	protected boolean shouldBePowered(BlockState state, World worldIn, BlockPos pos) {
		boolean shouldPower = worldIn.isReceivingRedstonePower(pos);

		Direction facing = state.get(FACING);
		for (Direction d : Iterate.directionsInAxis(facing.getAxis())) {
			BlockPos neighbourPos = pos.offset(d);
			if (!worldIn.canSetBlock(neighbourPos))
				continue;
			BlockState neighbourState = worldIn.getBlockState(neighbourPos);
			if (!(neighbourState.getBlock() instanceof GantryShaftBlock))
				continue;
			if (neighbourState.get(FACING) != facing)
				continue;
			shouldPower |= neighbourState.get(POWERED);
		}

		return shouldPower;
	}

	@Override
	public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.get(FACING)
			.getAxis();
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(FACING)
			.getAxis();
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.GANTRY_SHAFT.create();
	}

	@Override
	protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
		return super.areStatesKineticallyEquivalent(oldState, newState)
			&& oldState.get(POWERED) == newState.get(POWERED);
	}

	public static class PlacementHelper extends PoleHelper<Direction> {

		public PlacementHelper() {
			super(AllBlocks.GANTRY_SHAFT::has, s -> s.get(FACING)
				.getAxis(), FACING);
		}

		@Override
		public Predicate<ItemStack> getItemPredicate() {
			return AllBlocks.GANTRY_SHAFT::isIn;
		}

		@Override
		public PlacementOffset getOffset(World world, BlockState state, BlockPos pos, BlockHitResult ray) {
			PlacementOffset offset = super.getOffset(world, state, pos, ray);
			if (!offset.isSuccessful())
				return offset;
			return PlacementOffset.success(offset.getPos(), offset.getTransform()
				.andThen(s -> s.with(POWERED, state.get(POWERED))));
		}
	}

}
