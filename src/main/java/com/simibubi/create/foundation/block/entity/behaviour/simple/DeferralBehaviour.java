package com.simibubi.create.foundation.block.entity.behaviour.simple;

import java.util.function.Supplier;

import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.block.entity.behaviour.BehaviourType;

import net.minecraft.nbt.CompoundTag;

public class DeferralBehaviour extends BlockEntityBehaviour {

	public static BehaviourType<DeferralBehaviour> TYPE = new BehaviourType<>();

	private boolean needsUpdate;
	private final Supplier<Boolean> callback;

	public DeferralBehaviour(SmartBlockEntity te, Supplier<Boolean> callback) {
		super(te);
		this.callback = callback;
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		nbt.putBoolean("NeedsUpdate", needsUpdate);
		super.write(nbt, clientPacket);
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		needsUpdate = nbt.getBoolean("NeedsUpdate");
		super.read(nbt, clientPacket);
	}

	@Override
	public void tick() {
		super.tick();
		if (needsUpdate && callback.get())
			needsUpdate = false;
	}

	public void scheduleUpdate() {
		needsUpdate = true;
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

}
