package com.simibubi.create.content.contraptions.base;

import com.simibubi.create.foundation.item.ItemDescription.Palette;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class KineticBlock extends Block implements BlockEntityProvider, Rotating {
	protected static final Palette color = Palette.Red;

	public KineticBlock(Settings properties) {
		super(properties);
	}

//	@Override
//	public ToolType getHarvestTool(BlockState state) {
//		return null;
//	}
//
//	@Override
//	public boolean canHarvestBlock(BlockState state, BlockView world, BlockPos pos, PlayerEntity player) {
//		for (ToolType toolType : player.getMainHandStack()
//			.getToolTypes()) {
//			if (isToolEffective(state, toolType))
//				return true;
//		}
//		return super.canHarvestBlock(state, world, pos, player);
//	}
//
//	@Override
//	public boolean isToolEffective(BlockState state, ToolType tool) {
//		return tool == ToolType.AXE || tool == ToolType.PICKAXE;
//	}

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

	protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
		return getRotationAxis(newState) == getRotationAxis(oldState);
	}

	@Override
	public void prepare(BlockState stateIn, WorldAccess worldIn, BlockPos pos, int flags, int count) {
		if (worldIn.isClient())
			return;

		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if (!(blockEntity instanceof KineticBlockEntity))
			return;
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
	public void onPlaced(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (worldIn.isClient)
			return;

		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if (!(blockEntity instanceof KineticBlockEntity))
			return;

		KineticBlockEntity kbe = (KineticBlockEntity) blockEntity;
		kbe.effects.queueRotationIndicators();
	}

	public float getParticleTargetRadius() {
		return .65f;
	}

	public float getParticleInitialRadius() {
		return .75f;
	}

}
