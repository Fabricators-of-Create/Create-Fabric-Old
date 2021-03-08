package com.simibubi.create.content.logistics.block.belts.tunnel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.relays.belt.BeltHelper;
import com.simibubi.create.content.contraptions.relays.belt.BeltTileEntity;
import com.simibubi.create.content.contraptions.relays.belt.BeltTileEntity.CasingType;
import com.simibubi.create.foundation.advancement.AllTriggers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BeltTunnelItem extends BlockItem {

	public BeltTunnelItem(Block p_i48527_1_, Settings p_i48527_2_) {
		super(p_i48527_1_, p_i48527_2_);
	}

	@Override
	protected boolean canPlace(ItemPlacementContext ctx, BlockState state) {
		PlayerEntity playerentity = ctx.getPlayer();
		ShapeContext iselectioncontext =
			playerentity == null ? ShapeContext.absent() : ShapeContext.of(playerentity);
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		return (!this.checkStatePlacement() || AllBlocks.ANDESITE_TUNNEL.get()
			.isValidPositionForPlacement(state, world, pos)) && world.canPlace(state, pos, iselectioncontext);
	}

	@Override
	protected boolean postPlacement(BlockPos pos, World world, PlayerEntity p_195943_3_, ItemStack p_195943_4_,
		BlockState state) {
		boolean flag = super.postPlacement(pos, world, p_195943_3_, p_195943_4_, state);
		if (!world.isClient) {
			BeltTileEntity belt = BeltHelper.getSegmentTE(world, pos.down());
			if (belt != null) {
				AllTriggers.triggerFor(AllTriggers.PLACE_TUNNEL, p_195943_3_);
				if (belt.casing == CasingType.NONE)
					belt.setCasingType(AllBlocks.ANDESITE_TUNNEL.has(state) ? CasingType.ANDESITE : CasingType.BRASS);
			}
		}
		return flag;
	}

}
