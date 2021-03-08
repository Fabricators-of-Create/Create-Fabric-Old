package com.simibubi.create.content.contraptions.fluids;

import java.util.Random;

import com.simibubi.create.AllParticleTypes;
import com.simibubi.create.content.contraptions.fluids.particle.FluidParticleData;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

public class FluidFX {

	static Random r = new Random();

	public static void splash(BlockPos pos, FluidStack fluidStack) {
		Fluid fluid = fluidStack.getFluid();
		if (fluid == Fluids.EMPTY)
			return;

		FluidState defaultState = fluid.getDefaultState();
		if (defaultState == null || defaultState.isEmpty()) {
			return;
		}

		BlockStateParticleEffect blockParticleData = new BlockStateParticleEffect(ParticleTypes.BLOCK, defaultState.getBlockState());
		Vec3d center = VecHelper.getCenterOf(pos);

		for (int i = 0; i < 20; i++) {
			Vec3d v = VecHelper.offsetRandomly(Vec3d.ZERO, r, .25f);
			particle(blockParticleData, center.add(v), v);
		}

	}

	public static ParticleEffect getFluidParticle(FluidStack fluid) {
		return new FluidParticleData(AllParticleTypes.FLUID_PARTICLE.get(), fluid);
	}

	public static ParticleEffect getDrippingParticle(FluidStack fluid) {
		ParticleEffect particle = null;
		if (FluidHelper.isWater(fluid.getFluid()))
			particle = ParticleTypes.DRIPPING_WATER;
		if (FluidHelper.isLava(fluid.getFluid()))
			particle = ParticleTypes.DRIPPING_LAVA;
		if (particle == null)
			particle = new FluidParticleData(AllParticleTypes.FLUID_DRIP.get(), fluid);
		return particle;
	}

	public static void spawnRimParticles(World world, BlockPos pos, Direction side, int amount, ParticleEffect particle,
		float rimRadius) {
		Vec3d directionVec = Vec3d.of(side.getVector());
		for (int i = 0; i < amount; i++) {
			Vec3d vec = VecHelper.offsetRandomly(Vec3d.ZERO, r, 1)
				.normalize();
			vec = VecHelper.clampComponentWise(vec, rimRadius)
				.multiply(VecHelper.axisAlingedPlaneOf(directionVec))
				.add(directionVec.multiply(.45 + r.nextFloat() / 16f));
			Vec3d m = vec.multiply(.05f);
			vec = vec.add(VecHelper.getCenterOf(pos));

			world.addImportantParticle(particle, vec.x, vec.y - 1 / 16f, vec.z, m.x, m.y, m.z);
		}
	}

	public static void spawnPouringLiquid(World world, BlockPos pos, int amount, ParticleEffect particle,
		float rimRadius, Vec3d directionVec, boolean inbound) {
		for (int i = 0; i < amount; i++) {
			Vec3d vec = VecHelper.offsetRandomly(Vec3d.ZERO, r, rimRadius * .75f);
			vec = vec.multiply(VecHelper.axisAlingedPlaneOf(directionVec))
				.add(directionVec.multiply(.5 + r.nextFloat() / 4f));
			Vec3d m = vec.multiply(1 / 4f);
			Vec3d centerOf = VecHelper.getCenterOf(pos);
			vec = vec.add(centerOf);
			if (inbound) {
				vec = vec.add(m);
				m = centerOf.add(directionVec.multiply(.5))
					.subtract(vec)
					.multiply(1 / 16f);
			}
			world.addImportantParticle(particle, vec.x, vec.y - 1 / 16f, vec.z, m.x, m.y, m.z);
		}
	}

	private static void particle(ParticleEffect data, Vec3d pos, Vec3d motion) {
		world().addParticle(data, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
	}

	private static World world() {
		return MinecraftClient.getInstance().world;
	}

}
