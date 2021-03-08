package com.simibubi.create.content.schematics.packet;

import java.util.function.Supplier;

import com.simibubi.create.content.schematics.SchematicProcessor;
import com.simibubi.create.content.schematics.item.SchematicItem;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SchematicPlacePacket extends SimplePacketBase {

	public ItemStack stack;

	public SchematicPlacePacket(ItemStack stack) {
		this.stack = stack;
	}

	public SchematicPlacePacket(PacketByteBuf buffer) {
		stack = buffer.readItemStack();
	}

	public void write(PacketByteBuf buffer) {
		buffer.writeItemStack(stack);
	}

	public void handle(Supplier<Context> context) {
		context.get().enqueueWork(() -> {
			ServerPlayerEntity player = context.get().getSender();
			if (player == null)
				return;
			Structure t = SchematicItem.loadSchematic(stack);
			StructurePlacementData settings = SchematicItem.getSettings(stack);
			if (player.isCreativeLevelTwoOp())
				settings.removeProcessor(SchematicProcessor.INSTANCE); // remove processor
			settings.setIgnoreEntities(false);
			t.place(player.getServerWorld(), NbtHelper.toBlockPos(stack.getTag().getCompound("Anchor")),
					settings, player.getRandom());
		});
		context.get().setPacketHandled(true);
	}

}
