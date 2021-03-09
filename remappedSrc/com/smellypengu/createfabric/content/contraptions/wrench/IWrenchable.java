package com.smellypengu.createfabric.content.contraptions.wrench;

import com.smellypengu.createfabric.content.contraptions.base.*;
import com.smellypengu.createfabric.foundation.utility.DirectionHelper;
import com.smellypengu.createfabric.foundation.utility.VoxelShaper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface IWrenchable {

	default ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		World world = context.getWorld();
		BlockState rotated = getRotatedBlockState(state, context.getSide());
		if (!rotated.canPlaceAt(world, context.getBlockPos()))
			return ActionResult.PASS;

		KineticTileEntity.switchToBlockState(world, context.getBlockPos(), updateAfterWrenched(rotated, context));

		BlockEntity te = context.getWorld()
			.getBlockEntity(context.getBlockPos());
		/**if (te != null) TODO MIGHT BE VERY IMPORTANT IWRENCHABLE!!!!!!
			te.updateContainingBlockInfo();*/
		/**if (te instanceof GeneratingKineticTileEntity) { TODO GeneratingKineticTileEntity CHECK
			((GeneratingKineticTileEntity) te).updateGeneratedRotation();
		}*/

		return ActionResult.SUCCESS;
	}

	default BlockState updateAfterWrenched(BlockState newState, ItemUsageContext context) {
//		return newState;
		return Block.postProcessState(newState, context.getWorld(), context.getBlockPos());
	}

	default ActionResult onSneakWrenched(BlockState state, ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		PlayerEntity player = context.getPlayer();
		if (world instanceof ServerWorld) {
			if (player != null && !player.isCreative())
				Block.getDroppedStacks(state, (ServerWorld) world, pos, world.getBlockEntity(pos), player, context.getStack())
					.forEach(itemStack -> {
						player.getInventory().offerOrDrop(itemStack);
					});
			state.onStacksDropped((ServerWorld) world, pos, ItemStack.EMPTY);
			world.removeBlock(pos, false);
		}
		return ActionResult.SUCCESS;
	}

	default BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
		BlockState newState = originalState;

		if (targetedFace.getAxis() == Direction.Axis.Y) {
			/**if (originalState.contains(HorizontalAxisKineticBlock.HORIZONTAL_AXIS)) TODO HorizontalAxisKineticBlock CHECK
				return originalState.with(HorizontalAxisKineticBlock.HORIZONTAL_AXIS, DirectionHelper
					.rotateAround(VoxelShaper.axisAsFace(originalState.get(HorizontalAxisKineticBlock.HORIZONTAL_AXIS)),
						targetedFace.getAxis())
					.getAxis());*/
			if (originalState.contains(HorizontalKineticBlock.HORIZONTAL_FACING))
				return originalState.with(HorizontalKineticBlock.HORIZONTAL_FACING, DirectionHelper
					.rotateAround(originalState.get(HorizontalKineticBlock.HORIZONTAL_FACING), targetedFace.getAxis()));
		}

		if (originalState.contains(RotatedPillarKineticBlock.AXIS))
			return originalState.with(RotatedPillarKineticBlock.AXIS,
				DirectionHelper
					.rotateAround(VoxelShaper.axisAsFace(originalState.get(RotatedPillarKineticBlock.AXIS)),
						targetedFace.getAxis())
					.getAxis());

		if (!originalState.contains(DirectionalKineticBlock.FACING))
			return originalState;

		Direction stateFacing = originalState.get(DirectionalKineticBlock.FACING);

		if (stateFacing.getAxis()
			.equals(targetedFace.getAxis())) {
			if (originalState.contains(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE))
				return originalState.cycle(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
			else
				return originalState;
		} else {
			do {
				newState = newState.with(DirectionalKineticBlock.FACING,
					DirectionHelper.rotateAround(newState.get(DirectionalKineticBlock.FACING), targetedFace.getAxis()));
				if (targetedFace.getAxis() == Direction.Axis.Y
					&& newState.contains(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE))
					newState = newState.cycle(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
			} while (newState.get(DirectionalKineticBlock.FACING)
				.getAxis()
				.equals(targetedFace.getAxis()));
		}
		return newState;
	}
}
