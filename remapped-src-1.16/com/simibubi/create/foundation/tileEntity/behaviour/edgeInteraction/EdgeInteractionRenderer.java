package com.simibubi.create.foundation.tileEntity.behaviour.edgeInteraction;

import java.util.List;

import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.Vec3d;

public class EdgeInteractionRenderer {

	public static void tick() {
		MinecraftClient mc = MinecraftClient.getInstance();
		HitResult target = mc.crosshairTarget;
		if (target == null || !(target instanceof BlockHitResult))
			return;

		BlockHitResult result = (BlockHitResult) target;
		ClientWorld world = mc.world;
		BlockPos pos = result.getBlockPos();
		PlayerEntity player = mc.player;
		ItemStack heldItem = player.getMainHandStack();

		if (player.isSneaking())
			return;
		EdgeInteractionBehaviour behaviour = TileEntityBehaviour.get(world, pos, EdgeInteractionBehaviour.TYPE);
		if (behaviour == null)
			return;
		if (behaviour.requiredItem.orElse(heldItem.getItem()) != heldItem.getItem())
			return;

		Direction face = result.getSide();
		List<Direction> connectiveSides = EdgeInteractionHandler.getConnectiveSides(world, pos, face, behaviour);
		if (connectiveSides.isEmpty())
			return;

		Direction closestEdge = connectiveSides.get(0);
		double bestDistance = Double.MAX_VALUE;
		Vec3d center = VecHelper.getCenterOf(pos);
		for (Direction direction : connectiveSides) {
			double distance = Vec3d.of(direction.getVector()).subtract(target.getPos()
				.subtract(center))
				.length();
			if (distance > bestDistance)
				continue;
			bestDistance = distance;
			closestEdge = direction;
		}

		Box bb = EdgeInteractionHandler.getBB(pos, closestEdge);
		boolean hit = bb.contains(target.getPos());

		ValueBox box = new ValueBox(LiteralText.EMPTY, bb.offset(-pos.getX(), -pos.getY(), -pos.getZ()), pos);
		Vec3d textOffset = Vec3d.ZERO;

		boolean positive = closestEdge.getDirection() == AxisDirection.POSITIVE;
		if (positive) {
			if (face.getAxis()
				.isHorizontal()) {
				if (closestEdge.getAxis()
					.isVertical())
					textOffset = textOffset.add(0, -128, 0);
				else
					textOffset = textOffset.add(-128, 0, 0);
			} else
				textOffset = textOffset.add(-128, 0, 0);
		}

		box.offsetLabel(textOffset)
			.withColors(0x7A6A2C, 0xB79D64)
			.passive(!hit);

		CreateClient.outliner.showValueBox("edge", box)
			.lineWidth(1 / 64f)
			.withFaceTexture(hit ? AllSpecialTextures.THIN_CHECKERED : null)
			.highlightFace(face);

	}

}
