package com.simibubi.create.content.contraptions.base;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.foundation.item.ItemDescription;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class KineticBlock extends Block implements BlockEntityProvider, Rotating {

	protected static final ItemDescription.Palette color = ItemDescription.Palette.Red;

	public KineticBlock(Settings properties) {
		super(properties);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		// onBlockAdded is useless for init, as sometimes the TE gets re-instantiated

		// however, if a block change occurs that does not change kinetic connections,
		// we can prevent a major re-propagation here

		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if (blockEntity instanceof KineticBlockEntity) {
			KineticBlockEntity kineticBlockEntity = (KineticBlockEntity) blockEntity;
			kineticBlockEntity.preventSpeedUpdate = false;

			if (oldState.getBlock() != state.getBlock())
				return;
			if (state.getBlock().hasBlockEntity() != oldState.getBlock().hasBlockEntity())
				return;
			if (!areStatesKineticallyEquivalent(oldState, state))
				return;

			kineticBlockEntity.preventSpeedUpdate = true;
		}
	}

	@Override
	public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
		return false;
	}

	@Override
	public boolean hasIntegratedCogwheel(WorldView world, BlockPos pos, BlockState state) {
		return false;
	}

	public boolean hasBlockEntity(BlockState state) {
		return true;
	}

	protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
		return getRotationAxis(newState) == getRotationAxis(oldState);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
		super.neighborUpdate(state, world, pos, block, fromPos, notify);
		if (world.isClient())
			return;

		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof KineticBlockEntity)) return;
		KineticBlockEntity kbe = (KineticBlockEntity) blockEntity;

		if (kbe.preventSpeedUpdate) {
			kbe.preventSpeedUpdate = false;
			return;
		}

		// Remove previous information when block is added
		kbe.warnOfMovement();
		kbe.clearKineticInformation();
		kbe.updateSpeed = true;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		if (world.isClient)
			return;

		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof KineticBlockEntity)) return;

		KineticBlockEntity kte = (KineticBlockEntity) blockEntity;
		kte.effects.queueRotationIndicators();
	}

	public float getParticleTargetRadius() {
		return .65f;
	}

	public float getParticleInitialRadius() {
		return .75f;
	}

}
