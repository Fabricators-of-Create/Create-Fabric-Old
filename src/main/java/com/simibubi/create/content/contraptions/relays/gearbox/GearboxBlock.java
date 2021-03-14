package com.simibubi.create.content.contraptions.relays.gearbox;

import java.util.Arrays;
import java.util.List;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.base.RotatedPillarKineticBlock;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class GearboxBlock extends RotatedPillarKineticBlock {

	public GearboxBlock(Settings properties) {
		super(properties);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return AllBlockEntities.GEARBOX.instantiate();
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.PUSH_ONLY;
	}

	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> items) {
		super.addStacksForDisplay(group, items);
		items.add(AllItems.VERTICAL_GEARBOX.getDefaultStack());
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, Builder builder) {
		if (state.get(AXIS).isVertical())
			return super.getDroppedStacks(state, builder);
		return Arrays.asList(new ItemStack(AllItems.VERTICAL_GEARBOX));
	}

	@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		if (state.get(AXIS).isVertical())
			return super.getPickStack(world, pos, state);
		return new ItemStack(AllItems.VERTICAL_GEARBOX);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return getDefaultState().with(AXIS, Axis.Y);
	}

	// IRotate:

	@Override
	public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() != state.get(AXIS);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(AXIS);
	}
}
