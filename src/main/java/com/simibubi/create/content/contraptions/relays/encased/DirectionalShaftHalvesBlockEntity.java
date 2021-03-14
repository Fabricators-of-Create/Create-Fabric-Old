package com.simibubi.create.content.contraptions.relays.encased;

import com.simibubi.create.content.contraptions.base.KineticBlockEntity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class DirectionalShaftHalvesBlockEntity extends KineticBlockEntity {

	public DirectionalShaftHalvesBlockEntity(BlockEntityType<?> typeIn) {
		super(typeIn);
	}

	public Direction getSourceFacing() {
		BlockPos localSource = source.subtract(getPos());
		return Direction.getFacing(localSource.getX(), localSource.getY(), localSource.getZ());
	}

}
