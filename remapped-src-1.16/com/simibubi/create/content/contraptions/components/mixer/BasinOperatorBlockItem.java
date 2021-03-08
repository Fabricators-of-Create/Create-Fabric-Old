package com.simibubi.create.content.contraptions.components.mixer;

import com.simibubi.create.AllBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BasinOperatorBlockItem extends BlockItem {

	public BasinOperatorBlockItem(Block block, Settings builder) {
		super(block, builder);
	}

	@Override
	public ActionResult place(ItemPlacementContext context) {
		BlockPos placedOnPos = context.getBlockPos()
			.offset(context.getSide()
				.getOpposite());
		BlockState placedOnState = context.getWorld()
			.getBlockState(placedOnPos);
		if (AllBlocks.BASIN.has(placedOnState) || AllBlocks.BELT.has(placedOnState)
			|| AllBlocks.DEPOT.has(placedOnState)) {
			if (context.getWorld()
				.getBlockState(placedOnPos.up(2))
				.getMaterial()
				.isReplaceable())
				context = ItemPlacementContext.offset(context, placedOnPos.up(2), Direction.UP);
			else
				return ActionResult.FAIL;
		}

		return super.place(context);
	}

}