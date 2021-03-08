package com.simibubi.create.content.contraptions.components.crank;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.DyeHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@ParametersAreNonnullByDefault
public class ValveHandleBlock extends HandCrankBlock {
	private final boolean inCreativeTab;

	public static ValveHandleBlock copper(Settings properties) {
		return new ValveHandleBlock(properties, true);
	}

	public static ValveHandleBlock dyed(Settings properties) {
		return new ValveHandleBlock(properties, false);
	}

	private ValveHandleBlock(Settings properties, boolean inCreativeTab) {
		super(properties);
		this.inCreativeTab = inCreativeTab;
	}

	@Override
	public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
		BlockHitResult hit) {
		ItemStack heldItem = player.getStackInHand(handIn);
		for (DyeColor color : DyeColor.values()) {
			if (!heldItem.getItem()
				.isIn(DyeHelper.getTagOfDye(color)))
				continue;
			if (worldIn.isClient)
				return ActionResult.SUCCESS;

			BlockState newState = AllBlocks.DYED_VALVE_HANDLES[color.ordinal()].getDefaultState()
				.with(FACING, state.get(FACING));
			if (newState != state)
				worldIn.setBlockState(pos, newState);
			return ActionResult.SUCCESS;
		}

		return super.onUse(state, worldIn, pos, player, handIn, hit);
	}

	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> p_149666_2_) {
		if (group != ItemGroup.SEARCH && !inCreativeTab)
			return;
		super.addStacksForDisplay(group, p_149666_2_);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public AllBlockPartials getRenderedHandle() {
		return null;
	}

	@Override
	public int getRotationSpeed() {
		return 16;
	}

}
