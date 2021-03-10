package com.simibubi.create.foundation.block.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.simibubi.create.foundation.block.entity.behaviour.BehaviourType;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;

public abstract class SmartBlockEntity extends SyncedBlockEntity implements Tickable {

	private final Map<BehaviourType<?>, BlockEntityBehaviour> behaviours;
	private boolean initialized;
	private boolean firstNbtRead;
	private int lazyTickRate;
	private int lazyTickCounter;

	public SmartBlockEntity(BlockEntityType<?> blockEntityTypeIn) {
		super(blockEntityTypeIn);
		behaviours = new HashMap<>();
		initialized = false;
		firstNbtRead = true;
		setLazyTickRate(10);

		ArrayList<BlockEntityBehaviour> list = new ArrayList<>();
		addBehaviours(list);
		list.forEach(b -> behaviours.put(b.getType(), b));
	}

	public abstract void addBehaviours(List<BlockEntityBehaviour> behaviours);

	/**
	 * Gets called just before reading tile data for behaviours. Register anything
	 * here that depends on your custom te data.
	 */
	public void addBehavioursDeferred(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	public void tick() {
		if (!initialized && hasWorld()) {
			initialize();
			initialized = true;
		}

		if (lazyTickCounter-- <= 0) {
			lazyTickCounter = lazyTickRate;
			lazyTick();
		}

		behaviours.values()
			.forEach(BlockEntityBehaviour::tick);
	}

	public void initialize() {
		behaviours.values()
			.forEach(BlockEntityBehaviour::initialize);
		lazyTick();
	}

	@Override
	public final CompoundTag writeToClient(CompoundTag compound) {
		toTag(compound, true);
		return compound;
	}

	@Override
	public final CompoundTag toTag(CompoundTag compound) {
		toTag(compound, false);
		return compound;
	}

	@Override
	public final void readClientUpdate(BlockState state, CompoundTag tag) {
		fromTag(state, tag, true);
	}

	@Override
	public final void fromTag(BlockState state, CompoundTag tag) {
		fromTag(state, tag, false);
	}

	/**
	 * Hook only these in future subclasses of STE
	 */
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		if (firstNbtRead) {
			firstNbtRead = false;
			ArrayList<BlockEntityBehaviour> list = new ArrayList<>();
			addBehavioursDeferred(list);
			list.forEach(b -> behaviours.put(b.getType(), b));
		}
		super.fromTag(state, compound);
		behaviours.values()
			.forEach(tb -> tb.read(compound, clientPacket));
	}

	/**
	 * Hook only these in future subclasses of STE
	 */
	protected void toTag(CompoundTag compound, boolean clientPacket) {
		super.toTag(compound);
		behaviours.values()
			.forEach(tb -> tb.write(compound, clientPacket));
	}

	@Override
	public void markRemoved() {
		forEachBehaviour(BlockEntityBehaviour::remove);
		super.markRemoved();
	}

	public void setLazyTickRate(int slowTickRate) {
		this.lazyTickRate = slowTickRate;
		this.lazyTickCounter = slowTickRate;
	}

	public void lazyTick() {

	}

	protected void forEachBehaviour(Consumer<BlockEntityBehaviour> action) {
		behaviours.values()
			.forEach(action);
	}

	protected void attachBehaviourLate(BlockEntityBehaviour behaviour) {
		behaviours.put(behaviour.getType(), behaviour);
		behaviour.initialize();
	}

	protected void removeBehaviour(BehaviourType<?> type) {
		BlockEntityBehaviour remove = behaviours.remove(type);
		if (remove != null)
			remove.remove();
	}

	@SuppressWarnings("unchecked")
	public <T extends BlockEntityBehaviour> T getBehaviour(BehaviourType<T> type) {
		if (behaviours.containsKey(type))
			return (T) behaviours.get(type);
		return null;
	}

//	protected boolean isItemHandlerCap(Capability<?> cap) {
//		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
//	}
//	
//	protected boolean isFluidHandlerCap(Capability<?> cap) {
//		return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
//	}

}
