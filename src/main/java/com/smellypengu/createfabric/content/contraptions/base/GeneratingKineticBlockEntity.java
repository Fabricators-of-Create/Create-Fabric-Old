package com.smellypengu.createfabric.content.contraptions.base;

import com.smellypengu.createfabric.content.contraptions.KineticNetwork;
import com.smellypengu.createfabric.content.contraptions.goggles.IHaveGoggleInformation;
import com.smellypengu.createfabric.foundation.utility.Lang;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public abstract class GeneratingKineticBlockEntity extends KineticBlockEntity {

	public boolean reActivateSource;

	public GeneratingKineticBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
	}

	protected void notifyStressCapacityChange(float capacity) {
		getOrCreateNetwork().updateCapacityFor(this, capacity);
	}

	@Override
	public void removeSource() {
		if (hasSource() && isSource())
			reActivateSource = true;
		super.removeSource();
	}

	@Override
	public void setSource(BlockPos source) {
		super.setSource(source);
		BlockEntity tileEntity = world.getBlockEntity(source);
		if (!(tileEntity instanceof KineticBlockEntity))
			return;
		KineticBlockEntity sourceTe = (KineticBlockEntity) tileEntity;
		if (reActivateSource && Math.abs(sourceTe.getSpeed()) >= Math.abs(getGeneratedSpeed()))
			reActivateSource = false;
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (reActivateSource) {
			updateGeneratedRotation();
			reActivateSource = false;
		}
	}

	@Override
	public boolean addToGoggleTooltip(List<String> tooltip, boolean isPlayerSneaking) {
		boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		float stressBase = calculateAddedStressCapacity();
		if (stressBase != 0 && IRotate.StressImpact.isEnabled()) {
			tooltip.add(spacing + Lang.translate("gui.goggles.generator_stats"));
			tooltip.add(spacing + Formatting.GRAY + Lang.translate("tooltip.capacityProvided"));

			float speed = getTheoreticalSpeed();
			if (speed != getGeneratedSpeed() && speed != 0)
				stressBase *= getGeneratedSpeed() / speed;

			speed = Math.abs(speed);
			float stressTotal = stressBase * speed;

			String stressString =
				spacing + "%s%s" + Lang.translate("generic.unit.stress") + " " + Formatting.DARK_GRAY + "%s";
			tooltip.add(" " + String.format(stressString, Formatting.AQUA, IHaveGoggleInformation.format(stressTotal),
				Lang.translate("gui.goggles.at_current_speed")));

			added = true;
		}

		return added;
	}

	public void updateGeneratedRotation() {
		float speed = getGeneratedSpeed();
		float prevSpeed = this.speed;

		if (world.isClient)
			return;

		if (prevSpeed != speed) {
			if (!hasSource()) {
				IRotate.SpeedLevel levelBefore = IRotate.SpeedLevel.of(this.speed);
				IRotate.SpeedLevel levelafter = IRotate.SpeedLevel.of(speed);
				if (levelBefore != levelafter)
					effects.queueRotationIndicators();
			}

			applyNewSpeed(prevSpeed, speed);
		}

		if (hasNetwork() && speed != 0) {
			KineticNetwork network = getOrCreateNetwork();
			notifyStressCapacityChange(calculateAddedStressCapacity());
			getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
			network.updateStress();
		}

		onSpeedChanged(prevSpeed);
		sendData();
	}

	public void applyNewSpeed(float prevSpeed, float speed) {

		// Speed changed to 0
		if (speed == 0) {
			if (hasSource()) {
				notifyStressCapacityChange(0);
				getOrCreateNetwork().updateStressFor(this, calculateStressApplied());
				return;
			}
			detachKinetics();
			setSpeed(0);
			setNetwork(null);
			return;
		}

		// Now turning - create a new Network
		if (prevSpeed == 0) {
			setSpeed(speed);
			setNetwork(createNetworkId());
			attachKinetics();
			return;
		}

		// Change speed when overpowered by other generator
		if (hasSource()) {

			// Staying below Overpowered speed
			if (Math.abs(prevSpeed) >= Math.abs(speed)) {
				if (Math.signum(prevSpeed) != Math.signum(speed))
					world.removeBlock(pos, true);
				return;
			}

			// Faster than attached network -> become the new source
			detachKinetics();
			setSpeed(speed);
			source = null;
			setNetwork(createNetworkId());
			attachKinetics();
			return;
		}

		// Reapply source
		detachKinetics();
		setSpeed(speed);
		attachKinetics();
	}

	public Long createNetworkId() {
		return pos.asLong();
	}
}
