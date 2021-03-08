package com.simibubi.create.content.contraptions.components.structureMovement.train;

import static net.minecraft.entity.Entity.squaredHorizontalLength;

import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.contraptions.components.structureMovement.train.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.components.structureMovement.train.capability.MinecartController;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Useful methods for dealing with Minecarts
 *
 */
public class MinecartSim2020 {
	private static final Map<RailShape, Pair<Vec3i, Vec3i>> MATRIX =
		Util.make(Maps.newEnumMap(RailShape.class), (map) -> {
			Vec3i west = Direction.WEST.getVector();
			Vec3i east = Direction.EAST.getVector();
			Vec3i north = Direction.NORTH.getVector();
			Vec3i south = Direction.SOUTH.getVector();
			map.put(RailShape.NORTH_SOUTH, Pair.of(north, south));
			map.put(RailShape.EAST_WEST, Pair.of(west, east));
			map.put(RailShape.ASCENDING_EAST, Pair.of(west.down(), east));
			map.put(RailShape.ASCENDING_WEST, Pair.of(west, east.down()));
			map.put(RailShape.ASCENDING_NORTH, Pair.of(north, south.down()));
			map.put(RailShape.ASCENDING_SOUTH, Pair.of(north.down(), south));
			map.put(RailShape.SOUTH_EAST, Pair.of(south, east));
			map.put(RailShape.SOUTH_WEST, Pair.of(south, west));
			map.put(RailShape.NORTH_WEST, Pair.of(north, west));
			map.put(RailShape.NORTH_EAST, Pair.of(north, east));
		});
	
	public static Vec3d predictNextPositionOf(AbstractMinecartEntity cart) {
		Vec3d position = cart.getPos();
		Vec3d motion = cart.getVelocity();
		return position.add(motion);
	}

	public static boolean canAddMotion(AbstractMinecartEntity c) {
		if (c instanceof FurnaceMinecartEntity)
			return MathHelper.approximatelyEquals(((FurnaceMinecartEntity) c).pushX, 0)
				&& MathHelper.approximatelyEquals(((FurnaceMinecartEntity) c).pushZ, 0);
		LazyOptional<MinecartController> capability =
			c.getCapability(CapabilityMinecartController.MINECART_CONTROLLER_CAPABILITY);
		if (capability.isPresent() && capability.orElse(null)
			.isStalled())
			return false;
		return true;
	}

	public static void moveCartAlongTrack(AbstractMinecartEntity cart, Vec3d forcedMovement, BlockPos cartPos,
		BlockState trackState) {

		if (forcedMovement.equals(Vec3d.ZERO))
			return;

		Vec3d previousMotion = cart.getVelocity();
		cart.fallDistance = 0.0F;

		double x = cart.getX();
		double y = cart.getY();
		double z = cart.getZ();

		double actualX = x;
		double actualY = y;
		double actualZ = z;

		Vec3d actualVec = cart.snapPositionToRail(actualX, actualY, actualZ);
		actualY = cartPos.getY() + 1;

		AbstractRailBlock abstractrailblock = (AbstractRailBlock) trackState.getBlock();
		RailShape railshape = abstractrailblock.getRailDirection(trackState, cart.world, cartPos, cart);
		switch (railshape) {
		case ASCENDING_EAST:
			forcedMovement = forcedMovement.add(-1 * cart.getSlopeAdjustment(), 0.0D, 0.0D);
			actualY++;
			break;
		case ASCENDING_WEST:
			forcedMovement = forcedMovement.add(cart.getSlopeAdjustment(), 0.0D, 0.0D);
			actualY++;
			break;
		case ASCENDING_NORTH:
			forcedMovement = forcedMovement.add(0.0D, 0.0D, cart.getSlopeAdjustment());
			actualY++;
			break;
		case ASCENDING_SOUTH:
			forcedMovement = forcedMovement.add(0.0D, 0.0D, -1 * cart.getSlopeAdjustment());
			actualY++;
		default:
			break;
		}

		Pair<Vec3i, Vec3i> pair = MATRIX.get(railshape);
		Vec3i vec3i = pair.getFirst();
		Vec3i vec3i1 = pair.getSecond();
		double d4 = (double) (vec3i1.getX() - vec3i.getX());
		double d5 = (double) (vec3i1.getZ() - vec3i.getZ());
//		double d6 = Math.sqrt(d4 * d4 + d5 * d5);
		double d7 = forcedMovement.x * d4 + forcedMovement.z * d5;
		if (d7 < 0.0D) {
			d4 = -d4;
			d5 = -d5;
		}

		double d23 = (double) cartPos.getX() + 0.5D + (double) vec3i.getX() * 0.5D;
		double d10 = (double) cartPos.getZ() + 0.5D + (double) vec3i.getZ() * 0.5D;
		double d12 = (double) cartPos.getX() + 0.5D + (double) vec3i1.getX() * 0.5D;
		double d13 = (double) cartPos.getZ() + 0.5D + (double) vec3i1.getZ() * 0.5D;
		d4 = d12 - d23;
		d5 = d13 - d10;
		double d14;
		if (d4 == 0.0D) {
			d14 = actualZ - (double) cartPos.getZ();
		} else if (d5 == 0.0D) {
			d14 = actualX - (double) cartPos.getX();
		} else {
			double d15 = actualX - d23;
			double d16 = actualZ - d10;
			d14 = (d15 * d4 + d16 * d5) * 2.0D;
		}

		actualX = d23 + d4 * d14;
		actualZ = d10 + d5 * d14;

		cart.updatePosition(actualX, actualY, actualZ);
		cart.setVelocity(forcedMovement);
		cart.moveMinecartOnRail(cartPos);

		x = cart.getX();
		y = cart.getY();
		z = cart.getZ();

		if (vec3i.getY() != 0 && MathHelper.floor(x) - cartPos.getX() == vec3i.getX()
			&& MathHelper.floor(z) - cartPos.getZ() == vec3i.getZ()) {
			cart.updatePosition(x, y + (double) vec3i.getY(), z);
		} else if (vec3i1.getY() != 0 && MathHelper.floor(x) - cartPos.getX() == vec3i1.getX()
			&& MathHelper.floor(z) - cartPos.getZ() == vec3i1.getZ()) {
			cart.updatePosition(x, y + (double) vec3i1.getY(), z);
		}

		x = cart.getX();
		y = cart.getY();
		z = cart.getZ();

		Vec3d Vector3d3 = cart.snapPositionToRail(x, y, z);
		if (Vector3d3 != null && actualVec != null) {
			double d17 = (actualVec.y - Vector3d3.y) * 0.05D;
			Vec3d Vector3d4 = cart.getVelocity();
			double d18 = Math.sqrt(squaredHorizontalLength(Vector3d4));
			if (d18 > 0.0D) {
				cart.setVelocity(Vector3d4.multiply((d18 + d17) / d18, 1.0D, (d18 + d17) / d18));
			}

			cart.updatePosition(x, Vector3d3.y, z);
		}

		x = cart.getX();
		y = cart.getY();
		z = cart.getZ();

		int j = MathHelper.floor(x);
		int i = MathHelper.floor(z);
		if (j != cartPos.getX() || i != cartPos.getZ()) {
			Vec3d Vector3d5 = cart.getVelocity();
			double d26 = Math.sqrt(squaredHorizontalLength(Vector3d5));
			cart.setVelocity(d26 * (double) (j - cartPos.getX()), Vector3d5.y, d26 * (double) (i - cartPos.getZ()));
		}

		cart.setVelocity(previousMotion);
	}

	public static Vec3d getRailVec(RailShape shape) {
		switch (shape) {
		case ASCENDING_NORTH:
		case ASCENDING_SOUTH:
		case NORTH_SOUTH:
			return new Vec3d(0, 0, 1);
		case ASCENDING_EAST:
		case ASCENDING_WEST:
		case EAST_WEST:
			return new Vec3d(1, 0, 0);
		case NORTH_EAST:
		case SOUTH_WEST:
			return new Vec3d(1, 0, 1).normalize();
		case NORTH_WEST:
		case SOUTH_EAST:
			return new Vec3d(1, 0, -1).normalize();
		default:
			return new Vec3d(0, 1, 0);
		}
	}

}
