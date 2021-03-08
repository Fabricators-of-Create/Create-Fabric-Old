package com.simibubi.create.content.contraptions.fluids.particle;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;

public class BasinFluidParticle extends FluidStackParticle {

	BlockPos basinPos;
	Vec3d targetPos;
	Vec3d centerOfBasin;
	float yOffset;

	public BasinFluidParticle(ClientWorld world, FluidStack fluid, double x, double y, double z, double vx, double vy,
		double vz) {
		super(world, fluid, x, y, z, vx, vy, vz);
		gravityStrength = 0;
		velocityX = 0;
		velocityY = 0;
		velocityZ = 0;
		yOffset = world.random.nextFloat() * 1 / 32f;
		y += yOffset;
		scale = 0;
		maxAge = 60;
		Vec3d currentPos = new Vec3d(x, y, z);
		basinPos = new BlockPos(currentPos);
		centerOfBasin = VecHelper.getCenterOf(basinPos);

		if (vx != 0) {
			maxAge = 20;
			Vec3d centerOf = VecHelper.getCenterOf(basinPos);
			Vec3d diff = currentPos.subtract(centerOf)
				.multiply(1, 0, 1)
				.normalize()
				.multiply(.375);
			targetPos = centerOf.add(diff);
			prevPosX = x = centerOfBasin.x;
			prevPosZ = z = centerOfBasin.z;
		}
	}

	@Override
	public void tick() {
		super.tick();
		scale = targetPos != null ? Math.max(1 / 32f, ((1f * age) / maxAge) / 8)
			: 1 / 8f * (1 - ((Math.abs(age - (maxAge / 2)) / (1f * maxAge))));

		if (age % 2 == 0) {
			if (!AllBlocks.BASIN.has(world.getBlockState(basinPos))) {
				markDead();
				return;
			}

			BlockEntity tileEntity = world.getBlockEntity(basinPos);
			if (tileEntity instanceof BasinTileEntity) {
				float totalUnits = ((BasinTileEntity) tileEntity).getTotalFluidUnits(0);
				if (totalUnits < 1)
					totalUnits = 0;
				float fluidLevel = MathHelper.clamp(totalUnits / 2000, 0, 1);
				y = 2 / 16f + basinPos.getY() + 12 / 16f * fluidLevel + yOffset;
			}

		}

		if (targetPos != null) {
			float progess = (1f * age) / maxAge;
			Vec3d currentPos = centerOfBasin.add(targetPos.subtract(centerOfBasin)
				.multiply(progess));
			x = currentPos.x;
			z = currentPos.z;
		}
	}

	@Override
	public void buildGeometry(VertexConsumer vb, Camera info, float pt) {
		Quaternion rotation = info.getRotation();
		Quaternion prevRotation = new Quaternion(rotation);
		rotation.set(1, 0, 0, 1);
		rotation.normalize();
		super.buildGeometry(vb, info, pt);
		rotation.set(0, 0, 0, 1);
		rotation.hamiltonProduct(prevRotation);
	}

	@Override
	protected boolean canEvaporate() {
		return false;
	}

}
