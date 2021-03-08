package com.simibubi.create.content.contraptions.components.deployer;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringBehaviour;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeployerBlock extends DirectionalAxisKineticBlock implements ITE<DeployerTileEntity> {

	public DeployerBlock(Settings properties) {
		super(properties);
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.DEPLOYER.create();
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.NORMAL;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		return AllShapes.CASING_12PX.get(state.get(FACING));
	}

	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		if (context.getSide() == state.get(FACING)) {
			if (!context.getWorld().isClient)
				withTileEntityDo(context.getWorld(), context.getBlockPos(), DeployerTileEntity::changeMode);
			return ActionResult.SUCCESS;
		}
		return super.onWrenched(state, context);
	}

	@Override
	public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
			withTileEntityDo(worldIn, pos, te -> {
				if (te.player != null && !isMoving) {
					te.player.inventory.dropAll();
					te.overflowItems.forEach(itemstack -> te.player.dropItem(itemstack, true, false));
					te.player.remove();
					te.player = null;
				}
			});

			TileEntityBehaviour.destroy(worldIn, pos, FilteringBehaviour.TYPE);
			worldIn.removeBlockEntity(pos);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
		BlockHitResult hit) {
		ItemStack heldByPlayer = player.getStackInHand(handIn)
			.copy();
		if (AllItems.WRENCH.isIn(heldByPlayer))
			return ActionResult.PASS;

		if (hit.getSide() != state.get(FACING))
			return ActionResult.PASS;
		if (worldIn.isClient)
			return ActionResult.SUCCESS;

		withTileEntityDo(worldIn, pos, te -> {
			ItemStack heldByDeployer = te.player.getMainHandStack()
				.copy();
			if (heldByDeployer.isEmpty() && heldByPlayer.isEmpty())
				return;

			player.setStackInHand(handIn, heldByDeployer);
			te.player.setStackInHand(Hand.MAIN_HAND, heldByPlayer);
			te.sendData();
		});

		return ActionResult.SUCCESS;
	}

	@Override
	public Class<DeployerTileEntity> getTileEntityClass() {
		return DeployerTileEntity.class;
	}
	
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onBlockAdded(state, world, pos, oldState, isMoving);
		withTileEntityDo(world, pos, DeployerTileEntity::redstoneUpdate);
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block p_220069_4_,
		BlockPos p_220069_5_, boolean p_220069_6_) {
		withTileEntityDo(world, pos, DeployerTileEntity::redstoneUpdate);
	}

}
