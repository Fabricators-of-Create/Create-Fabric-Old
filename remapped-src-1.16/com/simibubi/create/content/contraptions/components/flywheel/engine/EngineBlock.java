package com.simibubi.create.content.contraptions.components.flywheel.engine;

import javax.annotation.Nullable;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EngineBlock extends HorizontalFacingBlock implements IWrenchable {

	protected EngineBlock(Settings builder) {
		super(builder);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos) {
		return isValidPosition(state, worldIn, pos, state.get(FACING));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		return ActionResult.FAIL;
	}
	
	@Override
	public abstract BlockEntity createTileEntity(BlockState state, BlockView world);

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction facing = context.getSide();
		return getDefaultState().with(FACING,
				facing.getAxis().isVertical() ? context.getPlayerFacing().getOpposite() : facing);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACING));
	}

	@Override
	public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		if (worldIn.isClient)
			return;

		if (fromPos.equals(getBaseBlockPos(state, pos))) {
			if (!canPlaceAt(state, worldIn, pos)) {
				worldIn.breakBlock(pos, true);
				return;
			}
		}
	}

	private boolean isValidPosition(BlockState state, BlockView world, BlockPos pos, Direction facing) {
		BlockPos baseBlockPos = getBaseBlockPos(state, pos);
		if (!isValidBaseBlock(world.getBlockState(baseBlockPos), world, pos))
			return false;
		for (Direction otherFacing : Iterate.horizontalDirections) {
			if (otherFacing == facing)
				continue;
			BlockPos otherPos = baseBlockPos.offset(otherFacing);
			BlockState otherState = world.getBlockState(otherPos);
			if (otherState.getBlock() instanceof EngineBlock
					&& getBaseBlockPos(otherState, otherPos).equals(baseBlockPos))
				return false;
		}

		return true;
	}
	
	public static BlockPos getBaseBlockPos(BlockState state, BlockPos pos) {
		return pos.offset(state.get(FACING).getOpposite());
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public abstract AllBlockPartials getFrameModel();

	protected abstract boolean isValidBaseBlock(BlockState baseBlock, BlockView world, BlockPos pos);

}
