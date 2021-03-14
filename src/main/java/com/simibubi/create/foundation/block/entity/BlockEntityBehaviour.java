package com.simibubi.create.foundation.block.entity;

import com.simibubi.create.foundation.block.entity.behaviour.BehaviourType;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class BlockEntityBehaviour {
	public SmartBlockEntity blockEntity;
	private int lazyTickRate;
	private int lazyTickCounter;

	public BlockEntityBehaviour(SmartBlockEntity be) {
		blockEntity = be;
		setLazyTickRate(10);
	}

	public static <T extends BlockEntityBehaviour> T get(BlockView reader, BlockPos pos,
														 BehaviourType<T> type) {
		return get(reader.getBlockEntity(pos), type);
	}

	public static <T extends BlockEntityBehaviour> void destroy(BlockView reader, BlockPos pos,
																BehaviourType<T> type) {
		T behaviour = get(reader.getBlockEntity(pos), type);
		if (behaviour != null)
			behaviour.destroy();
	}

	public static <T extends BlockEntityBehaviour> T get(BlockEntity te, BehaviourType<T> type) {
		if (te == null)
			return null;
		if (!(te instanceof SmartBlockEntity))
			return null;
		SmartBlockEntity ste = (SmartBlockEntity) te;
		return ste.getBehaviour(type);
	}

	public abstract BehaviourType<?> getType();

	public void initialize() {

	}

	public void tick() {
		if (lazyTickCounter-- <= 0) {
			lazyTickCounter = lazyTickRate;
			lazyTick();
		}

	}

	public void read(CompoundTag nbt, boolean clientPacket) {

	}

	public void write(CompoundTag nbt, boolean clientPacket) {

	}

	public void onBlockChanged(BlockState oldState) {

	}

	public void onNeighborChanged(Direction direction) {

	}

	public void remove() {

	}

	public void destroy() {

	}

	public void setLazyTickRate(int slowTickRate) {
		this.lazyTickRate = slowTickRate;
		this.lazyTickCounter = slowTickRate;
	}

	public void lazyTick() {

	}

	public BlockPos getPos() {
		return blockEntity.getPos();
	}

	public World getWorld() {
		return blockEntity.getWorld();
	}

}
