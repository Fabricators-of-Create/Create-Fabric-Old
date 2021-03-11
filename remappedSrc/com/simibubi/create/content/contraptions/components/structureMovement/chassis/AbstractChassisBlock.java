package com.simibubi.create.content.contraptions.components.structureMovement.chassis;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.wrench.Wrenchable;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class AbstractChassisBlock extends PillarBlock implements BlockEntityProvider, Wrenchable {

	public AbstractChassisBlock(Settings properties) {
		super(properties);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return AllBlockEntities.CHASSIS.instantiate();
	}

	@Override
	public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
		BlockHitResult hit) {
		if (!player.canModifyBlocks())
			return ActionResult.PASS;

		ItemStack heldItem = player.getStackInHand(handIn);
		boolean isSlimeBall = heldItem.isItemEqualIgnoreDamage(AllItems.SUPER_GLUE.getDefaultStack()) ||
			heldItem.isItemEqualIgnoreDamage(Items.SLIME_BALL.getDefaultStack());

		BooleanProperty affectedSide = getGlueableSide(state, hit.getSide());
		if (affectedSide == null)
			return ActionResult.PASS;

		if (isSlimeBall && state.get(affectedSide)) {
			for (Direction face : Iterate.directions) {
				BooleanProperty glueableSide = getGlueableSide(state, face);
				if (glueableSide != null && !state.get(glueableSide)) {
					if (worldIn.isClient) {
						Vec3d vec = hit.getPos();
						worldIn.addParticle(ParticleTypes.ITEM_SLIME, vec.x, vec.y, vec.z, 0, 0, 0);
						return ActionResult.SUCCESS;
					}
					//worldIn.playSound(null, pos, AllSoundEvents.SLIME_ADDED.get(), SoundCategory.BLOCKS, .5f, 1);
					state = state.with(glueableSide, true);
				}
			}
			if (!worldIn.isClient)
				worldIn.setBlockState(pos, state);
			return ActionResult.SUCCESS;
		}

		if ((!heldItem.isEmpty() || !player.isSneaking()) && !isSlimeBall)
			return ActionResult.PASS;
		if (state.get(affectedSide) == isSlimeBall)
			return ActionResult.PASS;
		if (worldIn.isClient) {
			Vec3d vec = hit.getPos();
			worldIn.addParticle(ParticleTypes.ITEM_SLIME, vec.x, vec.y, vec.z, 0, 0, 0);
			return ActionResult.SUCCESS;
		}

		//worldIn.playSound(null, pos, AllSoundEvents.SLIME_ADDED.get(), SoundCategory.BLOCKS, .5f, 1);
		worldIn.setBlockState(pos, state.with(affectedSide, isSlimeBall));
		return ActionResult.SUCCESS;
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		if (rotation == BlockRotation.NONE)
			return state;

		BlockState rotated = super.rotate(state, rotation);
		for (Direction face : Iterate.directions) {
			BooleanProperty glueableSide = getGlueableSide(rotated, face);
			if (glueableSide != null)
				rotated = rotated.with(glueableSide, false);
		}

		for (Direction face : Iterate.directions) {
			BooleanProperty glueableSide = getGlueableSide(state, face);
			if (glueableSide == null || !state.get(glueableSide))
				continue;
			Direction rotatedFacing = rotation.rotate(face);
			BooleanProperty rotatedGlueableSide = getGlueableSide(rotated, rotatedFacing);
			if (rotatedGlueableSide != null)
				rotated = rotated.with(rotatedGlueableSide, true);
		}

		return rotated;
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirrorIn) {
		if (mirrorIn == BlockMirror.NONE)
			return state;

		BlockState mirrored = state;
		for (Direction face : Iterate.directions) {
			BooleanProperty glueableSide = getGlueableSide(mirrored, face);
			if (glueableSide != null)
				mirrored = mirrored.with(glueableSide, false);
		}

		for (Direction face : Iterate.directions) {
			BooleanProperty glueableSide = getGlueableSide(state, face);
			if (glueableSide == null || !state.get(glueableSide))
				continue;
			Direction mirroredFacing = mirrorIn.apply(face);
			BooleanProperty mirroredGlueableSide = getGlueableSide(mirrored, mirroredFacing);
			if (mirroredGlueableSide != null)
				mirrored = mirrored.with(mirroredGlueableSide, true);
		}

		return mirrored;
	}

	public abstract BooleanProperty getGlueableSide(BlockState state, Direction face);

}
