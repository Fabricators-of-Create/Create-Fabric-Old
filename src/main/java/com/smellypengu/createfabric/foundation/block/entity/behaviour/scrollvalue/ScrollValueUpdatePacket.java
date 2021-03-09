package com.smellypengu.createfabric.foundation.block.entity.behaviour.scrollvalue;

import com.smellypengu.createfabric.foundation.networking.TileEntityConfigurationPacket;
import com.smellypengu.createfabric.foundation.block.entity.SmartBlockEntity;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

public class ScrollValueUpdatePacket extends TileEntityConfigurationPacket<SmartBlockEntity> {

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
	protected void applySettings(SmartBlockEntity te) {
		ScrollValueBehaviour behaviour = te.getBehaviour(ScrollValueBehaviour.TYPE);
		if (behaviour == null)
			return;
		behaviour.setValue(value);
	}

}
