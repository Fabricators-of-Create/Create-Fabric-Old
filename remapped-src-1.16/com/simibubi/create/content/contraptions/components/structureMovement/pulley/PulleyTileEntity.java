package com.simibubi.create.content.contraptions.components.structureMovement.pulley;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.contraptions.components.structureMovement.BlockMovementTraits;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionCollider;
import com.simibubi.create.content.contraptions.components.structureMovement.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.piston.LinearActuatorTileEntity;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.tileEntity.behaviour.ValueBoxTransform;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PulleyTileEntity extends LinearActuatorTileEntity {

	protected int initialOffset;

	public PulleyTileEntity(BlockEntityType<? extends PulleyTileEntity> type) {
		super(type);
	}

	@Override
	public Box makeRenderBoundingBox() {
		return super.makeRenderBoundingBox().stretch(0, -offset, 0);
	}

	@Override
	public double getSquaredRenderDistance() {
		return super.getSquaredRenderDistance() + offset * offset;
	}

	@Override
	protected void assemble() throws AssemblyException {
		if (!(world.getBlockState(pos)
			.getBlock() instanceof PulleyBlock))
			return;
		if (speed == 0)
			return;
		int maxLength = AllConfigs.SERVER.kinetics.maxRopeLength.get();
		int i = 1;
		while (i <= maxLength) {
			BlockPos ropePos = pos.down(i);
			BlockState ropeState = world.getBlockState(ropePos);
			if (!AllBlocks.ROPE.has(ropeState) && !AllBlocks.PULLEY_MAGNET.has(ropeState)) {
				break;
			}
			++i;
		}
		offset = i - 1;
		if (offset >= getExtensionRange() && getSpeed() > 0)
			return;
		if (offset <= 0 && getSpeed() < 0)
			return;

		// Collect Construct
		if (!world.isClient) {
			BlockPos anchor = pos.down(MathHelper.floor(offset + 1));
			initialOffset = MathHelper.floor(offset);
			PulleyContraption contraption = new PulleyContraption(initialOffset);
			boolean canAssembleStructure = contraption.assemble(world, anchor);

			if (canAssembleStructure) {
				Direction movementDirection = getSpeed() > 0 ? Direction.DOWN : Direction.UP;
				if (ContraptionCollider.isCollidingWithWorld(world, contraption, anchor.offset(movementDirection),
					movementDirection))
					canAssembleStructure = false;
			}

			if (!canAssembleStructure && getSpeed() > 0)
				return;

			for (i = ((int) offset); i > 0; i--) {
				BlockPos offset = pos.down(i);
				BlockState oldState = world.getBlockState(offset);
				if (oldState.getBlock() instanceof Waterloggable && oldState.contains(Properties.WATERLOGGED)
					&& oldState.get(Properties.WATERLOGGED)) {
					world.setBlockState(offset, Blocks.WATER.getDefaultState(), 66);
					continue;
				}
				world.setBlockState(offset, Blocks.AIR.getDefaultState(), 66);
			}

			if (!contraption.getBlocks().isEmpty()) {
				contraption.removeBlocksFromWorld(world, BlockPos.ORIGIN);
				movedContraption = ControlledContraptionEntity.create(world, this, contraption);
				movedContraption.updatePosition(anchor.getX(), anchor.getY(), anchor.getZ());
				world.spawnEntity(movedContraption);
				forceMove = true;
			}
		}

		clientOffsetDiff = 0;
		running = true;
		sendData();
	}

	@Override
	public void disassemble() {
		if (!running && movedContraption == null)
			return;
		offset = getGridOffset(offset);
		if (movedContraption != null)
			applyContraptionPosition();

		if (!world.isClient) {
			if (!removed) {
				if (offset > 0) {
					BlockPos magnetPos = pos.down((int) offset);
					FluidState ifluidstate = world.getFluidState(magnetPos);
					world.breakBlock(magnetPos, world.getBlockState(magnetPos)
						.getCollisionShape(world, magnetPos)
						.isEmpty());
					world.setBlockState(magnetPos, AllBlocks.PULLEY_MAGNET.getDefaultState()
						.with(Properties.WATERLOGGED,
							Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER)),
						66);
				}

				boolean[] waterlog = new boolean[(int) offset];

				for (int i = 1; i <= ((int) offset) - 1; i++) {
					BlockPos ropePos = pos.down(i);
					FluidState ifluidstate = world.getFluidState(ropePos);
					waterlog[i] = ifluidstate.getFluid() == Fluids.WATER;
					world.breakBlock(ropePos, world.getBlockState(ropePos)
						.getCollisionShape(world, ropePos)
						.isEmpty());
				}
				for (int i = 1; i <= ((int) offset) - 1; i++)
					world.setBlockState(pos.down(i), AllBlocks.ROPE.getDefaultState()
						.with(Properties.WATERLOGGED, waterlog[i]), 66);
			}

			if (movedContraption != null)
				movedContraption.disassemble();
		}

		if (movedContraption != null)
			movedContraption.remove();
		movedContraption = null;
		initialOffset = 0;
		running = false;
		sendData();
	}

	@Override
	protected Vec3d toPosition(float offset) {
		if (movedContraption.getContraption() instanceof PulleyContraption) {
			PulleyContraption contraption = (PulleyContraption) movedContraption.getContraption();
			return Vec3d.of(contraption.anchor).add(0, contraption.initialOffset - offset, 0);

		}
		return Vec3d.ZERO;
	}

	@Override
	protected void visitNewPosition() {
		super.visitNewPosition();
		if (world.isClient)
			return;
		if (movedContraption != null)
			return;
		if (getSpeed() <= 0)
			return;

		BlockPos posBelow = pos.down((int) (offset + getMovementSpeed()) + 1);
		BlockState state = world.getBlockState(posBelow);
		if (!BlockMovementTraits.movementNecessary(state, world, posBelow))
			return;
		if (BlockMovementTraits.isBrittle(state))
			return;

		disassemble();
		assembleNextTick = true;
	}

	@Override
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		initialOffset = compound.getInt("InitialOffset");
		super.fromTag(state, compound, clientPacket);
	}
	
	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putInt("InitialOffset", initialOffset);
		super.write(compound, clientPacket);
	}

	@Override
	protected int getExtensionRange() {
		return Math.max(0, Math.min(AllConfigs.SERVER.kinetics.maxRopeLength.get(), pos.getY() - 1));
	}

	@Override
	protected int getInitialOffset() {
		return initialOffset;
	}

	@Override
	protected Vec3d toMotionVector(float speed) {
		return new Vec3d(0, -speed, 0);
	}

	@Override
	protected ValueBoxTransform getMovementModeSlot() {
		return new CenteredSideValueBoxTransform((state, d) -> d == Direction.UP);
	}

}
