package com.simibubi.create.content.contraptions.relays.advanced;

import java.util.function.Predicate;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.HorizontalAxisKineticBlock;
import com.simibubi.create.content.contraptions.relays.elementary.CogWheelBlock;
import com.simibubi.create.content.contraptions.relays.elementary.CogwheelBlockItem;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.placement.IPlacementHelper;
import com.simibubi.create.foundation.utility.placement.PlacementHelpers;
import com.simibubi.create.foundation.utility.placement.PlacementOffset;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SpeedControllerBlock extends HorizontalAxisKineticBlock implements ITE<SpeedControllerTileEntity> {

	private static final int placementHelperId = PlacementHelpers.register(new PlacementHelper());

	public SpeedControllerBlock(Settings properties) {
		super(properties);
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.ROTATION_SPEED_CONTROLLER.create();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState above = context.getWorld()
			.getBlockState(context.getBlockPos()
				.up());
		if (CogWheelBlock.isLargeCog(above) && above.get(CogWheelBlock.AXIS)
			.isHorizontal())
			return getDefaultState().with(HORIZONTAL_AXIS, above.get(CogWheelBlock.AXIS) == Axis.X ? Axis.Z : Axis.X);
		return super.getPlacementState(context);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block p_220069_4_, BlockPos neighbourPos,
		boolean p_220069_6_) {
		if (neighbourPos.equals(pos.up()))
			withTileEntityDo(world, pos, SpeedControllerTileEntity::updateBracket);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
		BlockHitResult ray) {

		ItemStack heldItem = player.getStackInHand(hand);
		IPlacementHelper helper = PlacementHelpers.get(placementHelperId);
		if (helper.matchesItem(heldItem))
			return helper.getOffset(world, state, pos, ray).placeInWorld(world, (BlockItem) heldItem.getItem(), player, hand, ray);

		return ActionResult.PASS;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		return AllShapes.SPEED_CONTROLLER;
	}

	@MethodsReturnNonnullByDefault
	private static class PlacementHelper implements IPlacementHelper {
		@Override
		public Predicate<ItemStack> getItemPredicate() {
			return AllBlocks.LARGE_COGWHEEL::isIn;
		}

		@Override
		public Predicate<BlockState> getStatePredicate() {
			return AllBlocks.ROTATION_SPEED_CONTROLLER::has;
		}

		@Override
		public PlacementOffset getOffset(World world, BlockState state, BlockPos pos, BlockHitResult ray) {
			BlockPos newPos = pos.up();
			if (!world.getBlockState(newPos)
				.getMaterial()
				.isReplaceable())
				return PlacementOffset.fail();

			Axis newAxis = state.get(HORIZONTAL_AXIS) == Axis.X ? Axis.Z : Axis.X;

			if (CogwheelBlockItem.DiagonalCogHelper.hasLargeCogwheelNeighbor(world, newPos, newAxis)
				|| CogwheelBlockItem.DiagonalCogHelper.hasSmallCogwheelNeighbor(world, newPos, newAxis))
				return PlacementOffset.fail();

			return PlacementOffset.success(newPos, s -> s.with(CogWheelBlock.AXIS, newAxis));
		}

		@Override
		public void renderAt(BlockPos pos, BlockState state, BlockHitResult ray, PlacementOffset offset) {
			//IPlacementHelper.renderArrow(VecHelper.getCenterOf(pos), VecHelper.getCenterOf(offset.getPos()),
			//	Direction.getFacingFromAxis(Direction.AxisDirection.POSITIVE,
			//		state.get(HORIZONTAL_AXIS) == Axis.X ? Axis.Z : Axis.X));

			displayGhost(offset);
		}
	}

	@Override
	public Class<SpeedControllerTileEntity> getTileEntityClass() {
		return SpeedControllerTileEntity.class;
	}
}
