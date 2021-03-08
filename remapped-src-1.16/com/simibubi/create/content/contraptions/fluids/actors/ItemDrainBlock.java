package com.simibubi.create.content.contraptions.fluids.actors;

import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.processing.EmptyingByBasin;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.fluid.FluidHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ItemDrainBlock extends Block implements IWrenchable, ITE<ItemDrainTileEntity> {

	public ItemDrainBlock(Settings p_i48440_1_) {
		super(p_i48440_1_);
	}

	@Override
	public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
		BlockHitResult hit) {
		ItemStack heldItem = player.getStackInHand(handIn);

		try {
			ItemDrainTileEntity te = getTileEntity(worldIn, pos);
			if (!heldItem.isEmpty()) {
				te.internalTank.allowInsertion();
				ActionResult tryExchange = tryExchange(worldIn, player, handIn, heldItem, te);
				te.internalTank.forbidInsertion();
				if (tryExchange.isAccepted())
					return tryExchange;
			}
			
			ItemStack heldItemStack = te.getHeldItemStack();
			if (!worldIn.isClient && !heldItemStack.isEmpty()) {
				player.inventory.offerOrDrop(worldIn, heldItemStack);
				te.heldItem = null;
				te.notifyUpdate();
			}
			return ActionResult.SUCCESS;
		} catch (TileEntityException e) {
		}

		return ActionResult.PASS;
	}

	protected ActionResult tryExchange(World worldIn, PlayerEntity player, Hand handIn, ItemStack heldItem,
		ItemDrainTileEntity te) {
		if (FluidHelper.tryEmptyItemIntoTE(worldIn, player, handIn, heldItem, te))
			return ActionResult.SUCCESS;
		if (EmptyingByBasin.canItemBeEmptied(worldIn, heldItem))
			return ActionResult.SUCCESS;
		return ActionResult.PASS;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState p_220053_1_, BlockView p_220053_2_, BlockPos p_220053_3_,
		ShapeContext p_220053_4_) {
		return AllShapes.CASING_13PX.get(Direction.UP);
	}

	@Override
	public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.hasTileEntity() || state.getBlock() == newState.getBlock())
			return;
		withTileEntityDo(worldIn, pos, te -> {
			ItemStack heldItemStack = te.getHeldItemStack();
			if (!heldItemStack.isEmpty())
				ItemScatterer.spawn(worldIn, pos.getX(), pos.getY(), pos.getZ(), heldItemStack);
		});
		worldIn.removeBlockEntity(pos);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.ITEM_DRAIN.create();
	}

	@Override
	public Class<ItemDrainTileEntity> getTileEntityClass() {
		return ItemDrainTileEntity.class;
	}

}
