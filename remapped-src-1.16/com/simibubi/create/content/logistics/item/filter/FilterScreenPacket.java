package com.simibubi.create.content.logistics.item.filter;

import java.util.function.Supplier;

import com.simibubi.create.content.logistics.item.filter.AttributeFilterContainer.WhitelistMode;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class FilterScreenPacket extends SimplePacketBase {

	public enum Option {
		CLEAR, WHITELIST, WHITELIST2, BLACKLIST, RESPECT_DATA, IGNORE_DATA, UPDATE_FILTER_ITEM, ADD_TAG, ADD_INVERTED_TAG;
	}

	private final Option option;
	private final CompoundTag data;

	public FilterScreenPacket(Option option) {
		this(option, new CompoundTag());
	}

	public FilterScreenPacket(Option option, CompoundTag data) {
		this.option = option;
		this.data = data;
	}

	public FilterScreenPacket(PacketByteBuf buffer) {
		option = Option.values()[buffer.readInt()];
		data = buffer.readCompoundTag();
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt(option.ordinal());
		buffer.writeCompoundTag(data);
	}

	@Override
	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			ServerPlayerEntity player = context.get().getSender();
			if (player == null)
				return;

			if (player.currentScreenHandler instanceof AbstractFilterContainer) {
				AbstractFilterContainer c = (AbstractFilterContainer) player.currentScreenHandler;
				if (option == Option.CLEAR) {
					c.clearContents();
					return;
				}
			}

			if (player.currentScreenHandler instanceof FilterContainer) {
				FilterContainer c = (FilterContainer) player.currentScreenHandler;
				if (option == Option.WHITELIST)
					c.blacklist = false;
				if (option == Option.BLACKLIST)
					c.blacklist = true;
				if (option == Option.RESPECT_DATA)
					c.respectNBT = true;
				if (option == Option.IGNORE_DATA)
					c.respectNBT = false;
				if (option == Option.UPDATE_FILTER_ITEM)
					c.filterInventory.setStackInSlot(
							data.getInt("Slot"),
							net.minecraft.item.ItemStack.fromTag(data.getCompound("Item")));
			}

			if (player.currentScreenHandler instanceof AttributeFilterContainer) {
				AttributeFilterContainer c = (AttributeFilterContainer) player.currentScreenHandler;
				if (option == Option.WHITELIST)
					c.whitelistMode = WhitelistMode.WHITELIST_DISJ;
				if (option == Option.WHITELIST2)
					c.whitelistMode = WhitelistMode.WHITELIST_CONJ;
				if (option == Option.BLACKLIST)
					c.whitelistMode = WhitelistMode.BLACKLIST;
				if (option == Option.ADD_TAG)
					c.appendSelectedAttribute(ItemAttribute.fromNBT(data), false);
				if (option == Option.ADD_INVERTED_TAG)
					c.appendSelectedAttribute(ItemAttribute.fromNBT(data), true);
			}

		});
		context.get().setPacketHandled(true);
	}

}
