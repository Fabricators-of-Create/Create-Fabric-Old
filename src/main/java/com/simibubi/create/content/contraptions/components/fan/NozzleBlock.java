package com.simibubi.create.content.contraptions.components.fan;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.block.ProperDirectionalBlock;
import com.simibubi.create.registrate.util.nullness.MethodsReturnNonnullByDefault;
import com.simibubi.create.registrate.util.nullness.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NozzleBlock extends ProperDirectionalBlock implements BlockEntityProvider {

	public NozzleBlock(Settings p_i48415_1_) {
		super(p_i48415_1_);
	}
	
	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		return ActionResult.FAIL;
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return AllBlockEntities.NOZZLE.instantiate();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return getDefaultState().with(FACING, context.getSide());
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		return AllShapes.NOZZLE.get(state.get(FACING));
	}
	
	@Override
	public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		if (worldIn.isClient)
			return;

		if (fromPos.equals(pos.offset(state.get(FACING).getOpposite())))
			if (!canPlaceAt(state, worldIn, pos)) {
				worldIn.breakBlock(pos, true);
				return;
			}
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos) {
		Direction towardsFan = state.get(FACING).getOpposite();
		BlockEntity te = worldIn.getBlockEntity(pos.offset(towardsFan));
		return te instanceof AirCurrentSource
				&& ((AirCurrentSource) te).getAirflowOriginSide() == towardsFan.getOpposite();
	}

}
