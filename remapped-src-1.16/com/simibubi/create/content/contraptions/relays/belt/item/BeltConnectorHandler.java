package com.simibubi.create.content.contraptions.relays.belt.item;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.relays.elementary.ShaftBlock;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BeltConnectorHandler {

	private static Random r = new Random();

	public static void tick() {
		PlayerEntity player = MinecraftClient.getInstance().player;
		World world = MinecraftClient.getInstance().world;

		if (player == null || world == null)
			return;
		if (MinecraftClient.getInstance().currentScreen != null)
			return;

		for (Hand hand : Hand.values()) {
			ItemStack heldItem = player.getStackInHand(hand);

			if (!AllItems.BELT_CONNECTOR.isIn(heldItem))
				continue;
			if (!heldItem.hasTag())
				continue;

			CompoundTag tag = heldItem.getTag();
			if (!tag.contains("FirstPulley"))
				continue;

			BlockPos first = NbtHelper.toBlockPos(tag.getCompound("FirstPulley"));

			if (!BlockHelper.hasBlockStateProperty(world.getBlockState(first), Properties.AXIS))
				continue;
			Axis axis = world.getBlockState(first)
				.get(Properties.AXIS);

			HitResult rayTrace = MinecraftClient.getInstance().crosshairTarget;
			if (rayTrace == null || !(rayTrace instanceof BlockHitResult)) {
				if (r.nextInt(50) == 0) {
					world.addParticle(new DustParticleEffect(.3f, .9f, .5f, 1),
						first.getX() + .5f + randomOffset(.25f), first.getY() + .5f + randomOffset(.25f),
						first.getZ() + .5f + randomOffset(.25f), 0, 0, 0);
				}
				return;
			}

			BlockPos selected = ((BlockHitResult) rayTrace).getBlockPos();

			if (world.getBlockState(selected)
				.getMaterial()
				.isReplaceable())
				return;
			if (!ShaftBlock.isShaft(world.getBlockState(selected)))
				selected = selected.offset(((BlockHitResult) rayTrace).getSide());
			if (!selected.isWithinDistance(first, AllConfigs.SERVER.kinetics.maxBeltLength.get()))
				return;

			boolean canConnect =
				BeltConnectorItem.validateAxis(world, selected) && BeltConnectorItem.canConnect(world, first, selected);

			Vec3d start = Vec3d.of(first);
			Vec3d end = Vec3d.of(selected);
			Vec3d actualDiff = end.subtract(start);
			end = end.subtract(axis.choose(actualDiff.x, 0, 0), axis.choose(0, actualDiff.y, 0),
				axis.choose(0, 0, actualDiff.z));
			Vec3d diff = end.subtract(start);

			double x = Math.abs(diff.x);
			double y = Math.abs(diff.y);
			double z = Math.abs(diff.z);
			float length = (float) Math.max(x, Math.max(y, z));
			Vec3d step = diff.normalize();

			int sames = ((x == y) ? 1 : 0) + ((y == z) ? 1 : 0) + ((z == x) ? 1 : 0);
			if (sames == 0) {
				List<Vec3d> validDiffs = new LinkedList<>();
				for (int i = -1; i <= 1; i++)
					for (int j = -1; j <= 1; j++)
						for (int k = -1; k <= 1; k++) {
							if (axis.choose(i, j, k) != 0)
								continue;
							if (axis == Axis.Y && i != 0 && k != 0)
								continue;
							if (i == 0 && j == 0 && k == 0)
								continue;
							validDiffs.add(new Vec3d(i, j, k));
						}
				int closestIndex = 0;
				float closest = Float.MAX_VALUE;
				for (Vec3d validDiff : validDiffs) {
					double distanceTo = step.distanceTo(validDiff);
					if (distanceTo < closest) {
						closest = (float) distanceTo;
						closestIndex = validDiffs.indexOf(validDiff);
					}
				}
				step = validDiffs.get(closestIndex);
			}

			if (axis == Axis.Y && step.x != 0 && step.z != 0)
				return;

			step = new Vec3d(Math.signum(step.x), Math.signum(step.y), Math.signum(step.z));
			for (float f = 0; f < length; f += .0625f) {
				Vec3d position = start.add(step.multiply(f));
				if (r.nextInt(10) == 0) {
					world.addParticle(new DustParticleEffect(canConnect ? .3f : .9f, canConnect ? .9f : .3f, .5f, 1),
						position.x + .5f, position.y + .5f, position.z + .5f, 0, 0, 0);
				}
			}

			return;
		}
	}

	private static float randomOffset(float range) {
		return (r.nextFloat() - .5f) * 2 * range;
	}

}
