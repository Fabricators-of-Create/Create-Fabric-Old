package com.simibubi.create.content.contraptions.base;

import com.simibubi.create.content.contraptions.KineticNetwork;
import com.simibubi.create.content.contraptions.goggles.GoggleInformationProvider;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public abstract class GeneratingKineticBlockEntity extends KineticBlockEntity {
	public boolean reActivateSource;

	public GeneratingKineticBlockEntity(BlockEntityType<?> typeIn) {
		super(typeIn);
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
		BlockEntity blockEntity = world.getBlockEntity(source);
		if (!(blockEntity instanceof KineticBlockEntity))
			return;
		KineticBlockEntity sourceTe = (KineticBlockEntity) blockEntity;
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
	public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
		boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		float stressBase = calculateAddedStressCapacity();
		if (stressBase != 0 && Rotating.StressImpact.isEnabled()) {
			tooltip.add(new LiteralText(spacing).append(Lang.translate("gui.goggles.generator_stats")));
			tooltip.add(new LiteralText(spacing).append(Lang.translate("tooltip.capacityProvided").formatted(Formatting.GRAY)));

			float speed = getTheoreticalSpeed();
			if (speed != getGeneratedSpeed() && speed != 0)
				stressBase *= getGeneratedSpeed() / speed;

			speed = Math.abs(speed);
			float stressTotal = stressBase * speed;

			tooltip.add(componentSpacing.copy().append(new LiteralText(" " + GoggleInformationProvider.format(stressTotal))
				.append(Lang.translate("generic.unit.stress")).append(" ").formatted(Formatting.AQUA)).append(Lang.translate("gui.goggles.at_current_speed").formatted(Formatting.DARK_GRAY)));

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
				Rotating.SpeedLevel levelBefore = Rotating.SpeedLevel.of(this.speed);
				Rotating.SpeedLevel levelafter = Rotating.SpeedLevel.of(speed);
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
