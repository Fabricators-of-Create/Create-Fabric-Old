package com.simibubi.create.content.contraptions.fluids.pipes;

import java.util.Optional;

import com.simibubi.create.content.contraptions.relays.elementary.BracketedTileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BracketBlockItem extends BlockItem {

	public BracketBlockItem(Block p_i48527_1_, Settings p_i48527_2_) {
		super(p_i48527_1_, p_i48527_2_);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState state = world.getBlockState(pos);
		BracketBlock bracketBlock = getBracketBlock();
		PlayerEntity player = context.getPlayer();

		BracketedTileEntityBehaviour behaviour = TileEntityBehaviour.get(world, pos, BracketedTileEntityBehaviour.TYPE);

		if (behaviour == null)
			return ActionResult.FAIL;
		if (!behaviour.canHaveBracket())
			return ActionResult.FAIL;
		if (world.isClient)
			return ActionResult.SUCCESS;

		Optional<BlockState> suitableBracket = bracketBlock.getSuitableBracket(state, context.getSide());
		if (!suitableBracket.isPresent() && player != null)
			suitableBracket =
				bracketBlock.getSuitableBracket(state, Direction.getEntityFacingOrder(player)[0].getOpposite());
		if (!suitableBracket.isPresent())
			return ActionResult.SUCCESS;

		BlockState bracket = behaviour.getBracket();
		behaviour.applyBracket(suitableBracket.get());
		
		if (!world.isClient && player != null)
			behaviour.triggerAdvancements(world, player, state);
		
		if (player == null || !player.isCreative()) {
			context.getStack()
				.decrement(1);
			if (bracket != Blocks.AIR.getDefaultState()) {
				ItemStack returnedStack = new ItemStack(bracket.getBlock());
				if (player == null)
					Block.dropStack(world, pos, returnedStack);
				else
					player.inventory.offerOrDrop(world, returnedStack);
			}
		}
		return ActionResult.SUCCESS;
	}

	private BracketBlock getBracketBlock() {
		return (BracketBlock) getBlock();
	}

}
