package com.simibubi.create.content.contraptions.components.waterwheel;

import java.util.HashMap;
import java.util.Map;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class WaterWheelBlockEntity extends GeneratingKineticBlockEntity {

	private Map<Direction, Float> flows;

	public WaterWheelBlockEntity() {
		super(AllBlockEntities.WATER_WHEEL);
		flows = new HashMap<>();
		for (Direction d : Iterate.directions)
			setFlow(d, 0);
		setLazyTickRate(20);
	}

	@Override
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		super.fromTag(state, compound, clientPacket);
		if (compound.contains("Flows")) {
			for (Direction d : Iterate.directions)
				setFlow(d, compound.getCompound("Flows")
					.getFloat(d.asString()));
		}
	}

	@Override
	public Box makeRenderBoundingBox() {
		return new Box(pos).expand(1);
	}

	@Override
	protected void toTag(CompoundTag compound, boolean clientPacket) {
		CompoundTag flows = new CompoundTag();
		for (Direction d : Iterate.directions)
			flows.putFloat(d.asString(), this.flows.get(d));
		compound.put("Flows", flows);

		super.toTag(compound, clientPacket);
	}

	public void setFlow(Direction direction, float speed) {
		flows.put(direction, speed);
		markDirty();
	}

	@Override
	public float getGeneratedSpeed() {
		float speed = 0;
		for (Float f : flows.values())
			speed += f;
		if (speed != 0)
			speed += 4 /*AllConfigs.SERVER.kinetics.waterWheelBaseSpeed.get()*/ * Math.signum(speed);
		return speed;
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		AllBlocks.WATER_WHEEL
			.updateAllSides(getCachedState(), world, pos);
	}

}
