package com.simibubi.create.content.contraptions.relays.belt;

import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class BeltHelper {

	public static boolean isItemUpright(ItemStack stack) {
		return true; /**stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) TODO CapabilityFluidHandler / AllItemTags CHECK
			.isPresent()
			|| stack.getItem()
				.isIn(AllItemTags.UPRIGHT_ON_BELT.tag);*/
	}

	public static BeltTileEntity getSegmentTE(World world, BlockPos pos) {
		if (!world.isRegionLoaded(pos, BlockPos.ORIGIN))
			return null;
		BlockEntity tileEntity = world.getBlockEntity(pos);
		if (!(tileEntity instanceof BeltTileEntity))
			return null;
		return (BeltTileEntity) tileEntity;
	}

	public static BeltTileEntity getControllerTE(World world, BlockPos pos) {
		BeltTileEntity segment = getSegmentTE(world, pos);
		if (segment == null)
			return null;
		BlockPos controllerPos = segment.controller;
		if (controllerPos == null)
			return null;
		return getSegmentTE(world, controllerPos);
	}

	public static BeltTileEntity getBeltForOffset(BeltTileEntity controller, float offset) {
		return getBeltAtSegment(controller, (int) Math.floor(offset));
	}

	public static BeltTileEntity getBeltAtSegment(BeltTileEntity controller, int segment) {
		BlockPos pos = getPositionForOffset(controller, segment);
		BlockEntity te = controller.getWorld()
			.getBlockEntity(pos);
		if (te == null || !(te instanceof BeltTileEntity))
			return null;
		return (BeltTileEntity) te;
	}

	public static BlockPos getPositionForOffset(BeltTileEntity controller, int offset) {
		BlockPos pos = controller.getPos();
		Vec3i vec = controller.getBeltFacing()
			.getVector();
		BeltSlope slope = controller.getCachedState()
			.get(BeltBlock.SLOPE);
		int verticality = slope == BeltSlope.DOWNWARD ? -1 : slope == BeltSlope.UPWARD ? 1 : 0;

		return pos.add(offset * vec.getX(), MathHelper.clamp(offset, 0, controller.beltLength - 1) * verticality,
			offset * vec.getZ());
	}

	public static Vec3d getVectorForOffset(BeltTileEntity controller, float offset) {
		BeltSlope slope = controller.getCachedState()
			.get(BeltBlock.SLOPE);
		int verticality = slope == BeltSlope.DOWNWARD ? -1 : slope == BeltSlope.UPWARD ? 1 : 0;
		float verticalMovement = verticality;
		if (offset < .5)
			verticalMovement = 0;
		verticalMovement = verticalMovement * (Math.min(offset, controller.beltLength - .5f) - .5f);

		Vec3d vec = VecHelper.getCenterOf(controller.getPos());
		Vec3d horizontalMovement = new Vec3d(controller.getBeltFacing()
			.getUnitVector()).multiply(offset - .5f);

		if (slope == BeltSlope.VERTICAL)
			horizontalMovement = Vec3d.ZERO;

		vec = vec.add(horizontalMovement)
			.add(0, verticalMovement, 0);
		return vec;
	}

}
