package com.simibubi.create.content.contraptions;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import com.simibubi.create.content.contraptions.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.contraptions.base.Rotating;
import com.simibubi.create.foundation.utility.ColorHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

public class KineticDebugger {

	public static void tick() {
		if (!isActive()) {
			if (KineticBlockEntityRenderer.rainbowMode) {
				KineticBlockEntityRenderer.rainbowMode = false;
				CreateClient.bufferCache.invalidate();
			}
			return;
		}

		KineticBlockEntity te = getSelectedTE();
		if (te == null)
			return;

		World world = MinecraftClient.getInstance().world;
		BlockPos toOutline = te.hasSource() ? te.source : te.getPos();
		BlockState state = te.getCachedState();
		VoxelShape shape = world.getBlockState(toOutline)
			.getSidesShape(world, toOutline); // TODO EITHER THIS OR .getCullingShape

		if (te.getTheoreticalSpeed() != 0 && !shape.isEmpty())
			CreateClient.outliner.chaseAABB("kineticSource", shape.getBoundingBox()
				.offset(toOutline))
				.lineWidth(1 / 16f)
				.colored(te.hasSource() ? ColorHelper.colorFromLong(te.network) : 0xffcc00);

		if (state.getBlock() instanceof Rotating) {
			Direction.Axis axis = ((Rotating) state.getBlock()).getRotationAxis(state);
			Vec3d vec = new Vec3d(Direction.get(Direction.AxisDirection.POSITIVE, axis)
				.getUnitVector());
			Vec3d center = VecHelper.getCenterOf(te.getPos());
			CreateClient.outliner.showLine("rotationAxis", center.add(vec), center.subtract(vec))
				.lineWidth(1 / 16f);
		}

	}

	public static boolean isActive() {
		return MinecraftClient.getInstance().options.debugEnabled; //&& AllConfigs.CLIENT.rainbowDebug.get(); TODO CONFIG THING
	}

	public static KineticBlockEntity getSelectedTE() {
		HitResult obj = MinecraftClient.getInstance().crosshairTarget;
		ClientWorld world = MinecraftClient.getInstance().world;
		if (obj == null)
			return null;
		if (world == null)
			return null;
		if (!(obj instanceof BlockHitResult))
			return null;

		BlockHitResult ray = (BlockHitResult) obj;
		BlockEntity te = world.getBlockEntity(ray.getBlockPos());
		if (!(te instanceof KineticBlockEntity))
			return null;

		return (KineticBlockEntity) te;
	}

}
