package com.simibubi.create.foundation.utility;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BlockFace extends Pair<BlockPos, Direction> {

	public BlockFace(BlockPos first, Direction second) {
		super(first, second);
	}

	public boolean isEquivalent(BlockFace other) {
		if (equals(other))
			return true;
		return getConnectedPos().equals(other.getPos()) && getPos().equals(other.getConnectedPos());
	}

	public BlockPos getPos() {
		return getFirst();
	}

	public Direction getFace() {
		return getSecond();
	}
	
	public Direction getOppositeFace() {
		return getSecond().getOpposite();
	}

	public BlockFace getOpposite() {
		return new BlockFace(getConnectedPos(), getOppositeFace());
	}

	public BlockPos getConnectedPos() {
		return getPos().offset(getFace());
	}

	public CompoundTag serializeNBT() {
		CompoundTag compoundNBT = new CompoundTag();
		compoundNBT.put("Pos", NbtHelper.fromBlockPos(getPos()));
		NBTHelper.writeEnum(compoundNBT, "Face", getFace());
		return compoundNBT;
	}

	public static BlockFace fromNBT(CompoundTag compound) {
		return new BlockFace(NbtHelper.toBlockPos(compound.getCompound("Pos")),
			NBTHelper.readEnum(compound, "Face", Direction.class));
	}

}
