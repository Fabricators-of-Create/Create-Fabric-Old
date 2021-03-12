package com.simibubi.create.content.contraptions.relays.gearbox;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.Rotating;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class VerticalGearboxItem extends BlockItem {

	public VerticalGearboxItem(Settings builder) {
		super(AllBlocks.GEARBOX, builder);
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
	}
	
	@Override
	public String getTranslationKey() {
		return "item.create.vertical_gearbox";
	}

	@Override
	public void appendBlocks(Map<Block, Item> p_195946_1_, Item p_195946_2_) {
	}

	@Override
	protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
		Direction.Axis prefferedAxis = null;
		for (Direction side : Iterate.horizontalDirections) {
			BlockState blockState = world.getBlockState(pos.offset(side));
			if (blockState.getBlock() instanceof Rotating) {
				if (((Rotating) blockState.getBlock()).hasShaftTowards(world, pos.offset(side), blockState,
					side.getOpposite()))
					if (prefferedAxis != null && prefferedAxis != side.getAxis()) {
						prefferedAxis = null;
						break;
					} else {
						prefferedAxis = side.getAxis();
					}
			}
		}

		Direction.Axis axis = prefferedAxis == null ? player.getHorizontalFacing()
			.rotateYClockwise()
			.getAxis() : prefferedAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
		world.setBlockState(pos, state.with(Properties.AXIS, axis));
		return super.postPlacement(pos, world, player, stack, state);
	}

}
