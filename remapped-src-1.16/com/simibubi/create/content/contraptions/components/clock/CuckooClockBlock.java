package com.simibubi.create.content.contraptions.components.clock;

import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class CuckooClockBlock extends HorizontalKineticBlock {

	private boolean mysterious;

	public static CuckooClockBlock regular(Settings properties) {
		return new CuckooClockBlock(false, properties);
	}
	
	public static CuckooClockBlock mysterious(Settings properties) {
		return new CuckooClockBlock(true, properties);
	}
	
	protected CuckooClockBlock(boolean mysterious, Settings properties) {
		super(properties);
		this.mysterious = mysterious;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.CUCKOO_CLOCK.create();
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState p_220053_1_, BlockView p_220053_2_, BlockPos p_220053_3_,
		ShapeContext p_220053_4_) {
		return AllShapes.CUCKOO_CLOCK;
	}

	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> items) {
		if (!mysterious)
			super.addStacksForDisplay(group, items);
	}
	
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction preferred = getPreferredHorizontalFacing(context);
		if (preferred != null)
			return getDefaultState().with(HORIZONTAL_FACING, preferred.getOpposite());
		return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlayerFacing().getOpposite());
	}

	@Override
	public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
		return face == state.get(HORIZONTAL_FACING).getOpposite();
	}

	public static boolean containsSurprise(BlockState state) {
		Block block = state.getBlock();
		return block instanceof CuckooClockBlock && ((CuckooClockBlock) block).mysterious;
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(HORIZONTAL_FACING).getAxis();
	}

}
