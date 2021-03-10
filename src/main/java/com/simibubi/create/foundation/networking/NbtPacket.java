package com.simibubi.create.foundation.networking;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel.ResponseTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

@Deprecated
public class NbtPacket implements C2SPacket {

	public ItemStack stack;
	public int slot;
	public Hand hand;

	public NbtPacket() {}

	public NbtPacket(ItemStack stack, Hand hand) {
		this(stack, -1);
		this.hand = hand;
	}
	
	public NbtPacket(ItemStack stack, int slot) {
		this.stack = stack;
		this.slot = slot;
		this.hand = Hand.MAIN_HAND;
	}

	@Override
	public void read(PacketByteBuf buffer) {
		stack = buffer.readItemStack();
		slot = buffer.readInt();
		hand = Hand.values()[buffer.readInt()];
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeItemStack(stack);
		buffer.writeInt(slot);
		buffer.writeInt(hand.ordinal());
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, ResponseTarget responseTarget) {
		server.execute(() -> {
			if (player == null) {
				return;
			}
//			if (!(stack.getItem() instanceof SymmetryWandItem || stack.getItem() instanceof ZapperItem)) {
//				return;
//			}
			stack.removeSubTag("AttributeModifiers");
			if (slot == -1) {
				ItemStack heldItem = player.getStackInHand(hand);
				if (heldItem.getItem() == stack.getItem()) {
					heldItem.setTag(stack.getTag());
				}
				return;
			}
			
			ItemStack heldInSlot = player.inventory.getStack(slot);
			if (heldInSlot.getItem() == stack.getItem()) {
				heldInSlot.setTag(stack.getTag());
			}
		});
	}

}
