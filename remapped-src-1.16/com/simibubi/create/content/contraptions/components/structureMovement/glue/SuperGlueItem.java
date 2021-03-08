package com.simibubi.create.content.contraptions.components.structureMovement.glue;

import com.simibubi.create.foundation.utility.VecHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SuperGlueItem extends Item {

	public SuperGlueItem(Settings properties) {
		super(properties);
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return 99;
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockPos blockpos = context.getBlockPos();
		Direction direction = context.getSide();
		BlockPos blockpos1 = blockpos.offset(direction);
		PlayerEntity playerentity = context.getPlayer();
		ItemStack itemstack = context.getStack();

		if (playerentity == null || !this.canPlace(playerentity, direction, itemstack, blockpos1))
			return ActionResult.FAIL;

		World world = context.getWorld();
		SuperGlueEntity entity = new SuperGlueEntity(world, blockpos1, direction);
		CompoundTag compoundnbt = itemstack.getTag();
		if (compoundnbt != null)
			EntityType.loadFromEntityTag(world, playerentity, entity, compoundnbt);

		if (!entity.onValidSurface())
			return ActionResult.FAIL;

		if (!world.isClient) {
			entity.playPlaceSound();
			world.spawnEntity(entity);
		}
		itemstack.damage(1, playerentity, SuperGlueItem::onBroken);

		return ActionResult.SUCCESS;
	}

	public static void onBroken(PlayerEntity player) {

	}

	protected boolean canPlace(PlayerEntity entity, Direction facing, ItemStack stack, BlockPos pos) {
		return !World.isOutOfBuildLimitVertically(pos) && entity.canPlaceOn(pos, facing, stack);
	}

	@Environment(EnvType.CLIENT)
	public static void spawnParticles(World world, BlockPos pos, Direction direction, boolean fullBlock) {
		Vec3d vec = Vec3d.of(direction.getVector());
		Vec3d plane = VecHelper.axisAlingedPlaneOf(vec);
		Vec3d facePos = VecHelper.getCenterOf(pos)
			.add(vec.multiply(.5f));

		float distance = fullBlock ? 1f : .25f + .25f * (world.random.nextFloat() - .5f);
		plane = plane.multiply(distance);
		ItemStack stack = new ItemStack(Items.SLIME_BALL);

		for (int i = fullBlock ? 40 : 15; i > 0; i--) {
			Vec3d offset = VecHelper.rotate(plane, 360 * world.random.nextFloat(), direction.getAxis());
			Vec3d motion = offset.normalize()
				.multiply(1 / 16f);
			if (fullBlock)
				offset = new Vec3d(MathHelper.clamp(offset.x, -.5, .5), MathHelper.clamp(offset.y, -.5, .5),
					MathHelper.clamp(offset.z, -.5, .5));
			Vec3d particlePos = facePos.add(offset);
			world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), particlePos.x, particlePos.y,
				particlePos.z, motion.x, motion.y, motion.z);
		}

	}

}
