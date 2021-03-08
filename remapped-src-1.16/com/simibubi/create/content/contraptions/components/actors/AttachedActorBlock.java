package com.simibubi.create.content.contraptions.components.actors;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.utility.BlockHelper;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AttachedActorBlock extends HorizontalFacingBlock implements IWrenchable {

	protected AttachedActorBlock(Settings p_i48377_1_) {
		super(p_i48377_1_);
	}

	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		return ActionResult.FAIL;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		Direction direction = state.get(FACING);
		return AllShapes.HARVESTER_BASE.get(direction);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(FACING);
		super.appendProperties(builder);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos) {
		Direction direction = state.get(FACING);
		BlockPos offset = pos.offset(direction.getOpposite());
		return BlockHelper.hasBlockSolidSide(worldIn.getBlockState(offset), worldIn, offset, direction);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction facing;
		if (context.getSide().getAxis().isVertical())
			facing = context.getPlayerFacing().getOpposite();
		else {
			BlockState blockState =
				context.getWorld().getBlockState(context.getBlockPos().offset(context.getSide().getOpposite()));
			if (blockState.getBlock() instanceof AttachedActorBlock)
				facing = blockState.get(FACING);
			else
				facing = context.getSide();
		}
		return getDefaultState().with(FACING, facing);
	}

}
