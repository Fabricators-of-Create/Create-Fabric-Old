package com.simibubi.create.content.logistics.block.depot;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.DirectBeltInputBehaviour;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
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
import net.minecraftforge.items.ItemStackHandler;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DepotBlock extends Block implements ITE<DepotTileEntity>, IWrenchable {

	public DepotBlock(Settings p_i48440_1_) {
		super(p_i48440_1_);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState p_220053_1_, BlockView p_220053_2_, BlockPos p_220053_3_,
		ShapeContext p_220053_4_) {
		return AllShapes.DEPOT;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.DEPOT.create();
	}

	@Override
	public Class<DepotTileEntity> getTileEntityClass() {
		return DepotTileEntity.class;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
		BlockHitResult ray) {
		if (ray.getSide() != Direction.UP)
			return ActionResult.PASS;
		if (world.isClient)
			return ActionResult.SUCCESS;

		withTileEntityDo(world, pos, te -> {
			ItemStack heldItem = player.getStackInHand(hand);
			boolean wasEmptyHanded = heldItem.isEmpty();
			boolean shouldntPlaceItem = AllBlocks.MECHANICAL_ARM.isIn(heldItem);

			ItemStack mainItemStack = te.getHeldItemStack();
			if (!mainItemStack.isEmpty()) {
				player.inventory.offerOrDrop(world, mainItemStack);
				te.setHeldItem(null);
			}
			ItemStackHandler outputs = te.processingOutputBuffer;
			for (int i = 0; i < outputs.getSlots(); i++)
				player.inventory.offerOrDrop(world, outputs.extractItem(i, 64, false));

			if (!wasEmptyHanded && !shouldntPlaceItem) {
				TransportedItemStack transported = new TransportedItemStack(heldItem);
				transported.insertedFrom = player.getHorizontalFacing();
				transported.prevBeltPosition = .25f;
				transported.beltPosition = .25f;
				te.setHeldItem(transported);
				player.setStackInHand(hand, ItemStack.EMPTY);
			}

			te.markDirty();
			te.sendData();
		});

		return ActionResult.SUCCESS;
	}

	@Override
	public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.hasTileEntity() || state.getBlock() == newState.getBlock()) 
			return;
		withTileEntityDo(worldIn, pos, te -> {
			ItemHelper.dropContents(worldIn, pos, te.processingOutputBuffer);
			if (!te.getHeldItemStack()
				.isEmpty())
				ItemScatterer.spawn(worldIn, pos.getX(), pos.getY(), pos.getZ(), te.getHeldItemStack());
		});
		worldIn.removeBlockEntity(pos);
	}

	@Override
	public void onEntityLand(BlockView worldIn, Entity entityIn) {
		super.onEntityLand(worldIn, entityIn);
		if (!AllBlocks.DEPOT.has(worldIn.getBlockState(entityIn.getBlockPos())))
			return;
		if (!(entityIn instanceof ItemEntity))
			return;
		if (!entityIn.isAlive())
			return;
		if (entityIn.world.isClient)
			return;
		ItemEntity itemEntity = (ItemEntity) entityIn;
		DirectBeltInputBehaviour inputBehaviour =
			TileEntityBehaviour.get(worldIn, entityIn.getBlockPos(), DirectBeltInputBehaviour.TYPE);
		if (inputBehaviour == null)
			return;
		ItemStack remainder = inputBehaviour.handleInsertion(itemEntity.getStack(), Direction.DOWN, false);
		itemEntity.setStack(remainder);
		if (remainder.isEmpty())
			itemEntity.remove();
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World worldIn, BlockPos pos) {
		try {
			return ItemHelper.calcRedstoneFromInventory(getTileEntity(worldIn, pos).itemHandler);
		} catch (TileEntityException ignored) {
		}
		return 0;
	}

}
