package com.simibubi.create.content.curiosities;

import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ShadowSteelItem extends Item {

	public ShadowSteelItem(Settings properties) {
		super(properties);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		World world = entity.world;
		Vec3d pos = entity.getPos();

		if (world.isClient && entity.hasNoGravity()) {
			if (world.random.nextFloat() < MathHelper.clamp(entity.getStack().getCount() - 10,
					Math.min(entity.getVelocity().y * 20, 20), 100) / 64f) {
				Vec3d ppos = VecHelper.offsetRandomly(pos, world.random, .5f);
				world.addParticle(ParticleTypes.END_ROD, ppos.x, pos.y, ppos.z, 0, -.1f, 0);
			}

			if (!entity.getPersistentData().contains("ClientAnimationPlayed")) {
				Vec3d basemotion = new Vec3d(0, 1, 0);
				world.addParticle(ParticleTypes.FLASH, pos.x, pos.y, pos.z, 0, 0, 0);
				for (int i = 0; i < 20; i++) {
					Vec3d motion = VecHelper.offsetRandomly(basemotion, world.random, 1);
					world.addParticle(ParticleTypes.WITCH, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
					world.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
				}
				entity.getPersistentData().putBoolean("ClientAnimationPlayed", true);
			}

			return false;
		}

		if (!entity.getPersistentData().contains("FromVoid"))
			return false;

		entity.setNoGravity(true);
		float yMotion = (entity.fallDistance + 3) / 50f;
		entity.setVelocity(0, yMotion, 0);
		entity.lifespan = 6000;
		entity.getPersistentData().remove("FromVoid");
		return false;
	}

}
