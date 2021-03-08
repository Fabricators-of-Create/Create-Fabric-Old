package com.simibubi.create.content.contraptions.components.actors;

import static net.minecraft.block.HorizontalFacingBlock.FACING;

import org.apache.commons.lang3.mutable.MutableBoolean;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.content.contraptions.components.structureMovement.render.RenderedContraption;
import com.simibubi.create.foundation.render.backend.FastRenderDispatcher;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.KelpBlock;
import net.minecraft.block.KelpPlantBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class HarvesterMovementBehaviour extends MovementBehaviour {

	@Override
	public boolean isActive(MovementContext context) {
		return !VecHelper.isVecPointingTowards(context.relativeMotion, context.state.get(FACING)
			.getOpposite());
	}

	@Override
	public boolean hasSpecialInstancedRendering() {
		return true;
	}

	@Override
	public void addInstance(RenderedContraption contraption, MovementContext context) {
		HarvesterRenderer.addInstanceForContraption(contraption, context);
	}

	@Override
	public void renderInContraption(MovementContext context, MatrixStack ms, MatrixStack msLocal,
		VertexConsumerProvider buffers) {
		if (!FastRenderDispatcher.available())
			HarvesterRenderer.renderInContraption(context, ms, msLocal, buffers);
	}

	@Override
	public Vec3d getActiveAreaOffset(MovementContext context) {
		return Vec3d.of(context.state.get(FACING)
			.getVector())
			.multiply(.45);
	}

	@Override
	public void visitNewPosition(MovementContext context, BlockPos pos) {
		World world = context.world;
		BlockState stateVisited = world.getBlockState(pos);
		boolean notCropButCuttable = false;

		if (world.isClient)
			return;

		if (!isValidCrop(world, pos, stateVisited)) {
			if (isValidOther(world, pos, stateVisited))
				notCropButCuttable = true;
			else
				return;
		}

		MutableBoolean seedSubtracted = new MutableBoolean(notCropButCuttable);
		BlockState state = stateVisited;
		BlockHelper.destroyBlock(world, pos, 1, stack -> {
			if (!seedSubtracted.getValue() && stack.isItemEqualIgnoreDamage(new ItemStack(state.getBlock()))) {
				stack.decrement(1);
				seedSubtracted.setTrue();
			}
			dropItem(context, stack);
		});

		world.setBlockState(pos, cutCrop(world, pos, stateVisited));
	}

	private boolean isValidCrop(World world, BlockPos pos, BlockState state) {
		if (state.getBlock() instanceof CropBlock) {
			CropBlock crop = (CropBlock) state.getBlock();
			if (!crop.isMature(state))
				return false;
			return true;
		}
		if (state.getCollisionShape(world, pos)
			.isEmpty() || state.getBlock() instanceof CocoaBlock) {
			for (Property<?> property : state.getProperties()) {
				if (!(property instanceof IntProperty))
					continue;
				if (!property.getName()
					.equals(Properties.AGE_1.getName()))
					continue;
				if (((IntProperty) property).getValues()
					.size() - 1 != state.get((IntProperty) property)
						.intValue())
					continue;
				return true;
			}
		}

		return false;
	}

	private boolean isValidOther(World world, BlockPos pos, BlockState state) {
		if (state.getBlock() instanceof CropBlock)
			return false;
		if (state.getBlock() instanceof SugarCaneBlock)
			return true;

		if (state.getCollisionShape(world, pos)
			.isEmpty() || state.getBlock() instanceof CocoaBlock) {
			if (state.getBlock() instanceof KelpPlantBlock)
				return true;
			if (state.getBlock() instanceof KelpBlock)
				return true;

			for (Property<?> property : state.getProperties()) {
				if (!(property instanceof IntProperty))
					continue;
				if (!property.getName()
					.equals(Properties.AGE_1.getName()))
					continue;
				return false;
			}

			if (state.getBlock() instanceof IPlantable)
				return true;
		}

		return false;
	}

	private BlockState cutCrop(World world, BlockPos pos, BlockState state) {
		if (state.getBlock() instanceof CropBlock) {
			CropBlock crop = (CropBlock) state.getBlock();
			return crop.withAge(0);
		}
		if (state.getBlock() == Blocks.SUGAR_CANE || state.getBlock() == Blocks.KELP) {
			if (state.getFluidState()
				.isEmpty())
				return Blocks.AIR.getDefaultState();
			return state.getFluidState()
				.getBlockState();
		}
		if (state.getCollisionShape(world, pos)
			.isEmpty() || state.getBlock() instanceof CocoaBlock) {
			for (Property<?> property : state.getProperties()) {
				if (!(property instanceof IntProperty))
					continue;
				if (!property.getName()
					.equals(Properties.AGE_1.getName()))
					continue;
				return state.with((IntProperty) property, Integer.valueOf(0));
			}
		}

		if (state.getFluidState()
			.isEmpty())
			return Blocks.AIR.getDefaultState();
		return state.getFluidState()
			.getBlockState();
	}

}
