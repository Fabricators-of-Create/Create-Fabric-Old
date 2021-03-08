package com.simibubi.create.content.contraptions.components.structureMovement.mounted;

import javax.annotation.Nonnull;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CartAssemblerBlockItem extends BlockItem {

	public CartAssemblerBlockItem(Block block, Settings properties) {
		super(block, properties);
	}

	@Override
	@Nonnull
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (tryPlaceAssembler(context)) {
			context.getWorld().playSound(null, context.getBlockPos(), SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1, 1);
			return ActionResult.SUCCESS;
		}
		return super.useOnBlock(context);
	}

	public boolean tryPlaceAssembler(ItemUsageContext context) {
		BlockPos pos = context.getBlockPos();
		World world = context.getWorld();
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		PlayerEntity player = context.getPlayer();

		if (player == null)
			return false;
		if (!(block instanceof AbstractRailBlock)) {
			Lang.sendStatus(player, "block.cart_assembler.invalid");
			return false;
		}

		RailShape shape = state.get(((AbstractRailBlock) block).getShapeProperty());
		if (shape != RailShape.EAST_WEST && shape != RailShape.NORTH_SOUTH)
			return false;

		BlockState newState = AllBlocks.CART_ASSEMBLER.getDefaultState()
			.with(CartAssemblerBlock.RAIL_SHAPE, shape);
		CartAssembleRailType newType = null;
		for (CartAssembleRailType type : CartAssembleRailType.values())
			if (type.matches.test(state))
				newType = type;
		if (newType == null)
			return false;
		if (world.isClient)
			return true;

		newState = newState.with(CartAssemblerBlock.RAIL_TYPE, newType);
		world.setBlockState(pos, newState);
		if (!player.isCreative())
			context.getStack().decrement(1);
		return true;
	}
}