package com.simibubi.create.foundation.block.entity.behaviour.scrollvalue;

import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class ScrollValueUpdatePacket extends BlockEntityConfigurationPacket<SmartBlockEntity> {
	private int value;

	public ScrollValueUpdatePacket() {}

	public ScrollValueUpdatePacket(BlockPos pos, int amount) {
		super(pos);
		this.value = amount;
	}

	@Override
	protected void writeSettings(PacketByteBuf buffer) {
		buffer.writeInt(value);
	}

	@Override
	protected void readSettings(PacketByteBuf buffer) {
		value = buffer.readInt();
	}

	@Override
	protected void applySettings(SmartBlockEntity be) {
		ScrollValueBehaviour behaviour = be.getBehaviour(ScrollValueBehaviour.TYPE);
		if (behaviour == null)
			return;
		behaviour.setValue(value);
	}
}
