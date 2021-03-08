package com.simibubi.create.foundation.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

import javax.annotation.Nullable;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.logistics.item.filter.FilterItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;

public final class NBTProcessors {

	private static final Map<BlockEntityType<?>, UnaryOperator<CompoundTag>> processors = new HashMap<>();
	private static final Map<BlockEntityType<?>, UnaryOperator<CompoundTag>> survivalProcessors = new HashMap<>();

	public static synchronized void addProcessor(BlockEntityType<?> type, UnaryOperator<CompoundTag> processor) {
		processors.put(type, processor);
	}

	public static synchronized void addSurvivalProcessor(BlockEntityType<?> type, UnaryOperator<CompoundTag> processor) {
		survivalProcessors.put(type, processor);
	}

	static {
		addProcessor(BlockEntityType.SIGN, data -> {
			for (int i = 0; i < 4; ++i) {
				String s = data.getString("Text" + (i + 1));
				Text textcomponent = Text.Serializer.fromJson(s.isEmpty() ? "\"\"" : s);
				if (textcomponent != null && textcomponent.getStyle() != null
						&& textcomponent.getStyle().getClickEvent() != null)
					return null;
			}
			return data;
		});
		addSurvivalProcessor(AllTileEntities.FUNNEL.get(), data -> {
			if (data.contains("Filter")) {
				ItemStack filter = ItemStack.fromTag(data.getCompound("Filter"));
				if (filter.getItem() instanceof FilterItem)
					data.remove("Filter");
			}
			return data;
		});
	}

	private NBTProcessors() {
	}

	@Nullable
	public static CompoundTag process(BlockEntity tileEntity, CompoundTag compound, boolean survival) {
		if (compound == null)
			return null;
		BlockEntityType<?> type = tileEntity.getType();
		if (survival && survivalProcessors.containsKey(type))
			compound = survivalProcessors.get(type).apply(compound);
		if (compound != null && processors.containsKey(type))
			return processors.get(type).apply(compound);
		if (tileEntity.copyItemDataRequiresOperator())
			return null;
		return compound;
	}

}
