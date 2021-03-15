package com.simibubi.create.content.contraptions.components.fan;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.registrate.util.nullness.MethodsReturnNonnullByDefault;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@MethodsReturnNonnullByDefault
public interface AirCurrentSource {
	@Nullable
	AirCurrent getAirCurrent();

	@Nullable
	World getAirCurrentWorld();

	BlockPos getAirCurrentPos();

	float getSpeed();

	Direction getAirflowOriginSide();

	@Nullable
	Direction getAirFlowDirection();

	default float getMaxDistance() {
		float speed = Math.abs(this.getSpeed());
		//CKinetics config = AllConfigs.SERVER.kinetics;
		float distanceFactor = Math.min(speed / 256, 1);//config.fanRotationArgmax.get(), 1);
		float pushDistance = MathHelper.lerp(distanceFactor, 3, 20);//config.fanPushDistance.get());
		float pullDistance = MathHelper.lerp(distanceFactor, 3f, 20);//config.fanPullDistance.get());
		return this.getSpeed() > 0 ? pushDistance : pullDistance;
	}

	boolean isSourceRemoved();
}
