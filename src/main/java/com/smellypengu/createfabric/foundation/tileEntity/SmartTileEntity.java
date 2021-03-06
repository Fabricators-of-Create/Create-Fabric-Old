package com.smellypengu.createfabric.foundation.tileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.smellypengu.createfabric.foundation.tileEntity.behaviour.BehaviourType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public abstract class SmartTileEntity extends SyncedTileEntity implements BlockEntityRenderer<SyncedTileEntity> {

	private Map<BehaviourType<?>, TileEntityBehaviour> behaviours;
	private boolean initialized;
	private boolean firstNbtRead;
	private int lazyTickRate;
	private int lazyTickCounter;

	public SmartTileEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		behaviours = new HashMap<>();
		initialized = false;
		firstNbtRead = true;
		setLazyTickRate(10);

		ArrayList<TileEntityBehaviour> list = new ArrayList<>();
		addBehaviours(list);
		list.forEach(b -> behaviours.put(b.getType(), b));
	}

	public abstract void addBehaviours(List<TileEntityBehaviour> behaviours);

	/**
	 * Gets called just before reading tile data for behaviours. Register anything
	 * here that depends on your custom te data.
	 */
	public void addBehavioursDeferred(List<TileEntityBehaviour> behaviours) {}

	@Override
	public void render(SyncedTileEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		if (!initialized && hasWorld()) {
			initialize();
			initialized = true;
		}

		if (lazyTickCounter-- <= 0) {
			lazyTickCounter = lazyTickRate;
			lazyTick();
		}

		behaviours.values()
			.forEach(TileEntityBehaviour::tick);
	}

	public void initialize() {
		behaviours.values()
			.forEach(TileEntityBehaviour::initialize);
		lazyTick();
	}

	@Override
	public final CompoundTag writeNbt(CompoundTag compound) {
		write(compound, false);
		return compound;
	}

	@Override
	public final CompoundTag writeToClient(CompoundTag compound) {
		write(compound, true);
		return compound;
	}

	@Override
	public final void readClientUpdate(CompoundTag tag) {
		read(tag, true);
	}

	@Override
	public final void readNbt(CompoundTag tag) {
		read(tag, false);
	}

	/**
	 * Hook only these in future subclasses of STE
	 */
	protected void read(CompoundTag compound, boolean clientPacket) {
		if (firstNbtRead) {
			firstNbtRead = false;
			ArrayList<TileEntityBehaviour> list = new ArrayList<>();
			addBehavioursDeferred(list);
			list.forEach(b -> behaviours.put(b.getType(), b));
		}
		super.readNbt(compound);
		behaviours.values()
			.forEach(tb -> tb.read(compound, clientPacket));
	}

	/**
	 * Hook only these in future subclasses of STE
	 */
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.writeNbt(compound);
		behaviours.values()
			.forEach(tb -> tb.write(compound, clientPacket));
	}

	@Override
	public void markRemoved() {
		forEachBehaviour(TileEntityBehaviour::remove);
		super.markRemoved();
	}

	public void setLazyTickRate(int slowTickRate) {
		this.lazyTickRate = slowTickRate;
		this.lazyTickCounter = slowTickRate;
	}

	public void lazyTick() {

	}

	protected void forEachBehaviour(Consumer<TileEntityBehaviour> action) {
		behaviours.values()
			.forEach(action);
	}

	protected void attachBehaviourLate(TileEntityBehaviour behaviour) {
		behaviours.put(behaviour.getType(), behaviour);
		behaviour.initialize();
	}

	protected void removeBehaviour(BehaviourType<?> type) {
		TileEntityBehaviour remove = behaviours.remove(type);
		if (remove != null)
			remove.remove();
	}

	@SuppressWarnings("unchecked")
	public <T extends TileEntityBehaviour> T getBehaviour(BehaviourType<T> type) {
		if (behaviours.containsKey(type))
			return (T) behaviours.get(type);
		return null;
	}
	//TODO AAAAAA
	/*protected boolean isItemHandlerCap(Capability<?> cap) {
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}
	
	protected boolean isFluidHandlerCap(Capability<?> cap) {
		return cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
	}*/

}
