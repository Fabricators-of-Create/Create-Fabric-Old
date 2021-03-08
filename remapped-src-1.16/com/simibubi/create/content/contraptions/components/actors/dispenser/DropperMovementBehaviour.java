package com.simibubi.create.content.contraptions.components.actors.dispenser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class DropperMovementBehaviour extends MovementBehaviour {
	protected static final MovedDefaultDispenseItemBehaviour defaultBehaviour = new MovedDefaultDispenseItemBehaviour();
	private static final Random RNG = new Random();

	protected void activate(MovementContext context, BlockPos pos) {
		DispenseItemLocation location = getDispenseLocation(context);
		if (location.isEmpty()) {
			context.world.syncWorldEvent(1001, pos, 0);
		} else {
			setItemStackAt(location, defaultBehaviour.dispense(getItemStackAt(location, context), context, pos), context);
		}
	}

	@Override
	public void visitNewPosition(MovementContext context, BlockPos pos) {
		if (context.world.isClient)
			return;
		collectItems(context);
		activate(context, pos);
	}

	private void collectItems(MovementContext context) {
		getStacks(context).stream().filter(itemStack -> !itemStack.isEmpty() && itemStack.getItem() != Items.AIR && itemStack.getMaxCount() > itemStack.getCount()).forEach(itemStack -> itemStack.increment(
			ItemHelper.extract(context.contraption.inventory, itemStack::isItemEqualIgnoreDamage, ItemHelper.ExtractionCountMode.UPTO, itemStack.getMaxCount() - itemStack.getCount(), false).getCount()));
	}

	private void updateTemporaryData(MovementContext context) {
		if (!(context.temporaryData instanceof DefaultedList) && context.world != null) {
			DefaultedList<ItemStack> stacks = DefaultedList.ofSize(getInvSize(), ItemStack.EMPTY);
			Inventories.fromTag(context.tileData, stacks);
			context.temporaryData = stacks;
		}
	}

	@SuppressWarnings("unchecked")
	private DefaultedList<ItemStack> getStacks(MovementContext context) {
		updateTemporaryData(context);
		return (DefaultedList<ItemStack>) context.temporaryData;
	}

	private ArrayList<DispenseItemLocation> getUseableLocations(MovementContext context) {
		ArrayList<DispenseItemLocation> useable = new ArrayList<>();
		for (int slot = 0; slot < getInvSize(); slot++) {
			DispenseItemLocation location = new DispenseItemLocation(true, slot);
			ItemStack testStack = getItemStackAt(location, context);
			if (testStack == null || testStack.isEmpty())
				continue;
			if (testStack.getMaxCount() == 1) {
				location = new DispenseItemLocation(false, ItemHelper.findFirstMatchingSlotIndex(context.contraption.inventory, testStack::isItemEqualIgnoreDamage));
				if (!getItemStackAt(location, context).isEmpty())
					useable.add(location);
			} else if (testStack.getCount() >= 2)
				useable.add(location);
		}
		return useable;
	}

	@Override
	public void writeExtraData(MovementContext context) {
		DefaultedList<ItemStack> stacks = getStacks(context);
		if (stacks == null)
			return;
		Inventories.toTag(context.tileData, stacks);
	}

	@Override
	public void stopMoving(MovementContext context) {
		super.stopMoving(context);
		writeExtraData(context);
	}

	protected DispenseItemLocation getDispenseLocation(MovementContext context) {
		int i = -1;
		int j = 1;
		List<DispenseItemLocation> useableLocations = getUseableLocations(context);
		for (int k = 0; k < useableLocations.size(); ++k) {
			if (RNG.nextInt(j++) == 0) {
				i = k;
			}
		}
		if (i < 0)
			return DispenseItemLocation.NONE;
		else
			return useableLocations.get(i);
	}

	protected ItemStack getItemStackAt(DispenseItemLocation location, MovementContext context) {
		if (location.isInternal()) {
			return getStacks(context).get(location.getSlot());
		} else {
			return context.contraption.inventory.getStackInSlot(location.getSlot());
		}
	}

	protected void setItemStackAt(DispenseItemLocation location, ItemStack stack, MovementContext context) {
		if (location.isInternal()) {
			getStacks(context).set(location.getSlot(), stack);
		} else {
			context.contraption.inventory.setStackInSlot(location.getSlot(), stack);
		}
	}

	private static int getInvSize() {
		return 9;
	}
}
