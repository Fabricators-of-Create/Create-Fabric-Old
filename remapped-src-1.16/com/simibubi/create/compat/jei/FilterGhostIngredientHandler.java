package com.simibubi.create.compat.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.apache.logging.log4j.LogManager;

import com.simibubi.create.content.logistics.item.filter.AbstractFilterContainer;
import com.simibubi.create.content.logistics.item.filter.AbstractFilterScreen;
import com.simibubi.create.content.logistics.item.filter.AttributeFilterScreen;
import com.simibubi.create.content.logistics.item.filter.FilterScreenPacket;
import com.simibubi.create.foundation.networking.AllPackets;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.minecraft.client.util.Rect2i;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.slot.Slot;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FilterGhostIngredientHandler<T extends AbstractFilterContainer> implements IGhostIngredientHandler<AbstractFilterScreen<T>> {

	@Override
	public <I> List<Target<I>> getTargets(AbstractFilterScreen<T> gui, I ingredient, boolean doStart) {
		List<Target<I>> targets = new ArrayList<>();
		boolean isAttributeFilter = gui instanceof AttributeFilterScreen;

		if (ingredient instanceof ItemStack) {
			for (int i = 36; i < gui.getScreenHandler().slots.size(); i++) {
				targets.add(new FilterGhostTarget<>(gui, i - 36, isAttributeFilter));

				//Only accept items in 1st slot. 2nd is used for functionality, don't wanna override that one
				if (isAttributeFilter) break;
			}
		}

		return targets;
	}

	@Override
	public void onComplete() {}

	@Override
	public boolean shouldHighlightTargets() {
		//TODO change to false and highlight the slots ourself in some better way
		return true;
	}

	private static class FilterGhostTarget<I, T extends AbstractFilterContainer> implements Target<I> {

		private final Rect2i area;
		private final AbstractFilterScreen<T> gui;
		private final int slotIndex;
		private final boolean isAttributeFilter;


		public FilterGhostTarget(AbstractFilterScreen<T> gui, int slotIndex, boolean isAttributeFilter) {
			this.gui = gui;
			this.slotIndex = slotIndex;
			this.isAttributeFilter = isAttributeFilter;
			Slot slot = gui.getScreenHandler().slots.get(slotIndex + 36);
			this.area = new Rect2i(
					gui.getGuiLeft() + slot.x,
					gui.getGuiTop() + slot.y,
					16,
					16);
		}

		@Override
		public Rect2i getArea() {
			return area;
		}

		@Override
		public void accept(I ingredient) {
			ItemStack stack = ((ItemStack) ingredient).copy();
			LogManager.getLogger().info(stack);
			stack.setCount(1);
			gui.getScreenHandler().filterInventory.setStackInSlot(slotIndex, stack);

			if (isAttributeFilter) return;

			//sync new filter contents with server
			CompoundTag data = new CompoundTag();
			data.putInt("Slot", slotIndex);
			data.put("Item", stack.serializeNBT());
			AllPackets.channel.sendToServer(new FilterScreenPacket(FilterScreenPacket.Option.UPDATE_FILTER_ITEM, data));
		}
	}
}
