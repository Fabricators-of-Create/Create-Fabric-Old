package com.simibubi.create.content.contraptions.processing.burner;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerTileEntity.FuelType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BlazeBurnerHandler {

	@SubscribeEvent
	public static void thrownEggsGetEatenByBurner(ProjectileImpactEvent.Throwable event) {
		if (!(event.getThrowable() instanceof EggEntity))
			return;

		if (event.getRayTraceResult()
			.getType() != HitResult.Type.BLOCK)
			return;

		BlockEntity tile = event.getThrowable().world.getBlockEntity(new BlockPos(event.getRayTraceResult()
			.getPos()));
		if (!(tile instanceof BlazeBurnerTileEntity)) {
			return;
		}

		event.setCanceled(true);
		event.getThrowable()
			.setVelocity(Vec3d.ZERO);
		event.getThrowable()
			.remove();

		World world = event.getThrowable().world;
		if (world.isClient)
			return;
		
		BlazeBurnerTileEntity heater = (BlazeBurnerTileEntity) tile;
		if (heater.activeFuel != FuelType.SPECIAL) {
			heater.activeFuel = FuelType.NORMAL;
			heater.remainingBurnTime =
				MathHelper.clamp(heater.remainingBurnTime + 80, 0, BlazeBurnerTileEntity.maxHeatCapacity);
			heater.updateBlockState();
			heater.notifyUpdate();
		}
		
		world.playSound(null, heater.getPos(), AllSoundEvents.BLAZE_MUNCH.get(), SoundCategory.BLOCKS, .5F, 1F);
	}

}
