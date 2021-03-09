package com.smellypengu.createfabric.foundation.block.entity.behaviour.scrollvalue;

import com.smellypengu.createfabric.foundation.networking.BlockEntityConfigurationPacket;
import com.smellypengu.createfabric.foundation.block.entity.SmartBlockEntity;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class ScrollValueUpdatePacket extends BlockEntityConfigurationPacket<SmartBlockEntity> {
	int value;

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
