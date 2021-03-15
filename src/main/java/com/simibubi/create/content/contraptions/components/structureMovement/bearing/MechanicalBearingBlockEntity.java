package com.simibubi.create.content.contraptions.components.structureMovement.bearing;

import static net.minecraft.state.property.Properties.FACING;

import java.util.List;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.content.contraptions.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.contraptions.components.structureMovement.ControlContraption;
import com.simibubi.create.content.contraptions.components.structureMovement.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.DisplayAssemblyExceptionsProvider;
import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class MechanicalBearingBlockEntity extends GeneratingKineticBlockEntity implements BearingBlockEntity, DisplayAssemblyExceptionsProvider {

	protected ScrollOptionBehaviour<ControlContraption.RotationMode> movementMode;
	protected ControlledContraptionEntity movedContraption;
	protected float angle;
	protected boolean running;
	protected boolean assembleNextTick;
	protected float clientAngleDiff;
	protected AssemblyException lastException;

	public MechanicalBearingBlockEntity(BlockEntityType<? extends MechanicalBearingBlockEntity> type) {
		super(type);
		setLazyTickRate(3);
	}

	public MechanicalBearingBlockEntity() {
		super(AllBlockEntities.MECHANICAL_BEARING);
		setLazyTickRate(3);
	}

	@Override
	public boolean isWoodenTop() {
		return false;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		movementMode = new ScrollOptionBehaviour<>(ControlContraption.RotationMode.class, Lang.translate("contraptions.movement_mode"),
			this, getMovementModeSlot());
		movementMode.requiresWrench();
		behaviours.add(movementMode);
	}

	@Override
	public void markRemoved() {
		if (!world.isClient)
			disassemble();
		super.markRemoved();
	}

	@Override
	public void toTag(CompoundTag compound, boolean clientPacket) {
		compound.putBoolean("Running", running);
		compound.putFloat("Angle", angle);
		AssemblyException.write(compound, lastException);
		super.toTag(compound, clientPacket);
	}

	@Override
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		float angleBefore = angle;
		running = compound.getBoolean("Running");
		angle = compound.getFloat("Angle");
		lastException = AssemblyException.read(compound);
		super.fromTag(state, compound, clientPacket);
		if (!clientPacket)
			return;
		if (running) {
			clientAngleDiff = AngleHelper.getShortestAngleDiff(angleBefore, angle);
			angle = angleBefore;
		} else
			movedContraption = null;
	}

	@Override
	public float getInterpolatedAngle(float partialTicks) {
		if (movedContraption == null || movedContraption.isStalled() || !running)
			partialTicks = 0;
		return MathHelper.lerp(partialTicks, angle, angle + getAngularSpeed());
	}

	@Override
	public void onSpeedChanged(float prevSpeed) {
		super.onSpeedChanged(prevSpeed);
		assembleNextTick = true;
	}

	public float getAngularSpeed() {
		float speed = (isWindmill() ? getGeneratedSpeed() : getSpeed()) * 3 / 10f;
		if (getSpeed() == 0)
			speed = 0;
		if (world.isClient) {
			//speed *= ServerSpeedProvider.get();
			speed += clientAngleDiff / 3f;
		}
		return speed;
	}

	@Override
	public AssemblyException getLastAssemblyException() {
		return lastException;
	}

	protected boolean isWindmill() {
		return false;
	}

	@Override
	public BlockPos getBlockPosition() {
		return pos;
	}

	public void assemble() {
		if (!(world.getBlockState(pos)
			.getBlock() instanceof BearingBlock))
			return;

		Direction direction = getCachedState().get(FACING);
		BearingContraption contraption = new BearingContraption(isWindmill(), direction);
		try {
			if (!contraption.assemble(world, pos))
				return;

			lastException = null;
		} catch (AssemblyException e) {
			lastException = e;
			sendData();
			return;
		}

		/*if (isWindmill())
			AllTriggers.triggerForNearbyPlayers(AllTriggers.WINDMILL, world, pos, 5);
		if (contraption.getSailBlocks() >= 16 * 8)
			AllTriggers.triggerForNearbyPlayers(AllTriggers.MAXED_WINDMILL, world, pos, 5);*/
		
		contraption.removeBlocksFromWorld(world, BlockPos.ORIGIN);
		movedContraption = ControlledContraptionEntity.create(world, this, contraption);
		BlockPos anchor = pos.offset(direction);
		movedContraption.updatePosition(anchor.getX(), anchor.getY(), anchor.getZ());
		movedContraption.setRotationAxis(direction.getAxis());
		world.spawnEntity(movedContraption);

		running = true;
		angle = 0;
		sendData();
		updateGeneratedRotation();
	}

	public void disassemble() {
		if (!running && movedContraption == null)
			return;
		angle = 0;
		if (isWindmill())
			applyRotation();
		if (movedContraption != null)
			movedContraption.disassemble();

		movedContraption = null;
		running = false;
		updateGeneratedRotation();
		assembleNextTick = false;
		sendData();
	}

	@Override
	public void tick() {
		super.tick();

		if (world.isClient)
			clientAngleDiff /= 2;

		if (!world.isClient && assembleNextTick) {
			assembleNextTick = false;
			if (running) {
				boolean canDisassemble = movementMode.get() == RotationMode.ROTATE_PLACE
					|| (isNearInitialAngle() && movementMode.get() == RotationMode.ROTATE_PLACE_RETURNED);
				if (speed == 0 && (canDisassemble || movedContraption == null || movedContraption.getContraption()
					.getBlocks()
					.isEmpty())) {
					if (movedContraption != null)
						movedContraption.getContraption()
							.stop(world);
					disassemble();
				}
				return;
			} else {
				if (speed == 0 && !isWindmill())
					return;
				assemble();
			}
			return;
		}

		if (!running)
			return;

		if (!(movedContraption != null && movedContraption.isStalled())) {
			float angularSpeed = getAngularSpeed();
			float newAngle = angle + angularSpeed;
			angle = (float) (newAngle % 360);
		}

		applyRotation();
	}

	public boolean isNearInitialAngle() {
		return Math.abs(angle) < 45 || Math.abs(angle) > 7 * 45;
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		if (movedContraption != null && !world.isClient)
			sendData();
	}

	protected void applyRotation() {
		if (movedContraption == null)
			return;
		movedContraption.setAngle(angle);
		BlockState blockState = getCachedState();
		if (blockState.contains(Properties.FACING))
			movedContraption.setRotationAxis(blockState.get(Properties.FACING)
				.getAxis());
	}

	@Override
	public void attach(ControlledContraptionEntity contraption) {
		BlockState blockState = getCachedState();
		if (!(contraption.getContraption() instanceof BearingContraption))
			return;
		if (!BlockHelper.hasBlockStateProperty(blockState, FACING))
			return;

		this.movedContraption = contraption;
		markDirty();
		BlockPos anchor = pos.offset(blockState.get(FACING));
		movedContraption.updatePosition(anchor.getX(), anchor.getY(), anchor.getZ());
		if (!world.isClient) {
			this.running = true;
			sendData();
		}
	}

	@Override
	public void onStall() {
		if (!world.isClient)
			sendData();
	}

	@Override
	public boolean isValid() {
		return !isRemoved();
	}

	@Override
	public void collided() {}

	@Override
	public boolean isAttachedTo(AbstractContraptionEntity contraption) {
		return movedContraption == contraption;
	}

	public boolean isRunning() {
		return running;
	}

	@Override
	public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
		if (super.addToTooltip(tooltip, isPlayerSneaking))
			return true;
		if (isPlayerSneaking)
			return false;
		if (!isWindmill() && getSpeed() == 0)
			return false;
		if (running)
			return false;
		BlockState state = getCachedState();
		if (!(state.getBlock() instanceof BearingBlock))
			return false;

		BlockState attachedState = world.getBlockState(pos.offset(state.get(BearingBlock.FACING)));
		if (attachedState.getMaterial()
			.isReplaceable())
			return false;
		TooltipHelper.addHint(tooltip, "hint.empty_bearing");
		return true;
	}

	@Override
	public boolean shouldRenderAsBE() {
		return true;
	}
}
