package com.simibubi.create.content.contraptions.components.flywheel;

import com.simibubi.create.content.contraptions.base.GeneratingKineticTileEntity;
import com.simibubi.create.foundation.gui.widgets.InterpolatedChasingValue;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Box;

public class FlywheelTileEntity extends GeneratingKineticTileEntity {

	private float generatedCapacity;
	private float generatedSpeed;
	private int stoppingCooldown;

	// Client
	InterpolatedChasingValue visualSpeed = new InterpolatedChasingValue();
	float angle;

	public FlywheelTileEntity(BlockEntityType<? extends FlywheelTileEntity> type) {
		super(type);
	}

	public void setRotation(float speed, float capacity) {
		if (generatedSpeed != speed || generatedCapacity != capacity) {

			if (speed == 0) {
				if (stoppingCooldown == 0)
					stoppingCooldown = 40;
				return;
			}

			stoppingCooldown = 0;
			generatedSpeed = speed;
			generatedCapacity = capacity;
			updateGeneratedRotation();
		}
	}

	@Override
	public float getGeneratedSpeed() {
		return convertToDirection(generatedSpeed, getCachedState().get(FlywheelBlock.HORIZONTAL_FACING));
	}

	@Override
	public float calculateAddedStressCapacity() {
		return generatedCapacity;
	}

	@Override
	public Box makeRenderBoundingBox() {
		return super.makeRenderBoundingBox().expand(2);
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putFloat("GeneratedSpeed", generatedSpeed);
		compound.putFloat("GeneratedCapacity", generatedCapacity);
		compound.putInt("Cooldown", stoppingCooldown);
		super.write(compound, clientPacket);
	}

	@Override
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		generatedSpeed = compound.getFloat("GeneratedSpeed");
		generatedCapacity = compound.getFloat("GeneratedCapacity");
		stoppingCooldown = compound.getInt("Cooldown");
		super.fromTag(state, compound, clientPacket);
		if (clientPacket)
			visualSpeed.withSpeed(1 / 32f)
				.target(getGeneratedSpeed());
	}

	@Override
	public void tick() {
		super.tick();

		if (world.isClient) {
			visualSpeed.target(getGeneratedSpeed());
			visualSpeed.tick();
			angle += visualSpeed.value * 3 / 10f;
			angle %= 360;
			return;
		}

		/*
		 * After getting moved by pistons the generatedSpeed attribute reads 16 but the
		 * actual speed stays at 0, if it happens update rotation
		 */
		if (getGeneratedSpeed() != 0 && getSpeed() == 0)
			updateGeneratedRotation();

		if (stoppingCooldown == 0)
			return;

		stoppingCooldown--;
		if (stoppingCooldown == 0) {
			generatedCapacity = 0;
			generatedSpeed = 0;
			updateGeneratedRotation();
		}
	}

	@Override
	public boolean shouldRenderAsTE() {
		return true;
	}
}
