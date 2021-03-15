package com.simibubi.create.content.contraptions.components.structureMovement.bearing;

import java.util.List;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.NamedIconOptions;
import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;

public class WindmillBearingBlockEntity extends MechanicalBearingBlockEntity {

	protected ScrollOptionBehaviour<RotationDirection> movementDirection;
	protected float lastGeneratedSpeed;

	public WindmillBearingBlockEntity() {
		super(AllBlockEntities.WINDMILL_BEARING);
		setLazyTickRate(3);
	}

	@Override
	public void updateGeneratedRotation() {
		super.updateGeneratedRotation();
		lastGeneratedSpeed = getGeneratedSpeed();
	}
	
	@Override
	public void onSpeedChanged(float prevSpeed) {
		boolean cancelAssembly = assembleNextTick;
		super.onSpeedChanged(prevSpeed);
		assembleNextTick = cancelAssembly;
	}

	@Override
	public float getGeneratedSpeed() {
		if (!running)
			return 0;
		if (movedContraption == null)
			return lastGeneratedSpeed;
		int sails = ((BearingContraption) movedContraption.getContraption()).getSailBlocks() / 8;
		return MathHelper.clamp(sails, 1, 16) * getAngleSpeedDirection();
	}

	@Override
	protected boolean isWindmill() {
		return true;
	}

	protected float getAngleSpeedDirection() {
		RotationDirection rotationDirection = RotationDirection.values()[movementDirection.getValue()];
		return (rotationDirection == RotationDirection.CLOCKWISE ? 1 : -1);
	}

	@Override
	public void toTag(CompoundTag compound, boolean clientPacket) {
		compound.putFloat("LastGenerated", lastGeneratedSpeed);
		super.toTag(compound, clientPacket);
	}

	@Override
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		lastGeneratedSpeed = compound.getFloat("LastGenerated");
		super.fromTag(state, compound, clientPacket);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		behaviours.remove(movementMode);
		movementDirection = new ScrollOptionBehaviour<>(RotationDirection.class,
			Lang.translate("contraptions.windmill.rotation_direction"), this, getMovementModeSlot());
		movementDirection.requiresWrench();
		movementDirection.withCallback($ -> onDirectionChanged());
		behaviours.add(movementDirection);
	}

	private void onDirectionChanged() {
		if (!running)
			return;
		if (!world.isClient)
			updateGeneratedRotation();
	}

	@Override
	public boolean isWoodenTop() {
		return true;
	}

	static enum RotationDirection implements NamedIconOptions {

		CLOCKWISE(AllIcons.I_REFRESH), COUNTER_CLOCKWISE(AllIcons.I_ROTATE_CCW),

		;

		private String translationKey;
		private AllIcons icon;

		private RotationDirection(AllIcons icon) {
			this.icon = icon;
			translationKey = "generic." + Lang.asId(name());
		}

		@Override
		public AllIcons getIcon() {
			return icon;
		}

		@Override
		public String getTranslationKey() {
			return translationKey;
		}

	}

}
