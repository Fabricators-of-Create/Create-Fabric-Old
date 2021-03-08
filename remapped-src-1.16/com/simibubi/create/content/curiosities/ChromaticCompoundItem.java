package com.simibubi.create.content.curiosities;

import java.util.Random;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.config.CRecipes;
import com.simibubi.create.foundation.utility.ColorHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;

public class ChromaticCompoundItem extends Item {

	public ChromaticCompoundItem(Settings properties) {
		super(properties);
	}

	@Override
	public boolean shouldSyncTagToClient() {
		return true;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		int light = stack.getOrCreateTag()
			.getInt("CollectingLight");
		return 1 - light / (float) AllConfigs.SERVER.recipes.lightSourceCountForRefinedRadiance.get();
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		int light = stack.getOrCreateTag()
			.getInt("CollectingLight");
		return light > 0;
	}

	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return ColorHelper.mixColors(0x413c69, 0xFFFFFF, (float) (1 - getDurabilityForDisplay(stack)));
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return showDurabilityBar(stack) ? 1 : 16;
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		double y = entity.getY();
		double yMotion = entity.getVelocity().y;
		World world = entity.world;
		CompoundTag data = entity.getPersistentData();
		CompoundTag itemData = entity.getStack()
			.getOrCreateTag();

		Vec3d positionVec = entity.getPos();
		CRecipes config = AllConfigs.SERVER.recipes;
		if (world.isClient) {
			int light = itemData.getInt("CollectingLight");
			if (RANDOM.nextInt(config.lightSourceCountForRefinedRadiance.get() + 20) < light) {
				Vec3d start = VecHelper.offsetRandomly(positionVec, RANDOM, 3);
				Vec3d motion = positionVec.subtract(start)
					.normalize()
					.multiply(.2f);
				world.addParticle(ParticleTypes.END_ROD, start.x, start.y, start.z, motion.x, motion.y, motion.z);
			}
			return false;
		}

		// Convert to Shadow steel if in void
		if (y < 0 && y - yMotion < -10 && config.enableShadowSteelRecipe.get()) {
			ItemStack newStack = AllItems.SHADOW_STEEL.asStack();
			newStack.setCount(stack.getCount());
			data.putBoolean("FromVoid", true);
			entity.setStack(newStack);
		}

		if (!config.enableRefinedRadianceRecipe.get())
			return false;

		// Convert to Refined Radiance if eaten enough light sources
		if (itemData.getInt("CollectingLight") >= config.lightSourceCountForRefinedRadiance.get()) {
			ItemStack newStack = AllItems.REFINED_RADIANCE.asStack();
			ItemEntity newEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), newStack);
			newEntity.setVelocity(entity.getVelocity());
			newEntity.getPersistentData()
				.putBoolean("FromLight", true);
			itemData.remove("CollectingLight");
			world.spawnEntity(newEntity);

			stack.split(1);
			entity.setStack(stack);
			if (stack.isEmpty())
				entity.remove();
			return false;
		}

		// Is inside beacon beam?
		boolean isOverBeacon = false;
		int entityX = MathHelper.floor(entity.getX());
		int entityZ = MathHelper.floor(entity.getZ());
		int localWorldHeight = world.getTopY(Heightmap.Type.WORLD_SURFACE, entityX, entityZ);

		BlockPos.Mutable testPos = new BlockPos.Mutable(
				entityX,
				Math.min(MathHelper.floor(entity.getY()), localWorldHeight),
				entityZ);

		while (testPos.getY() > 0) {
			testPos.move(Direction.DOWN);
			BlockState state = world.getBlockState(testPos);
			if (state.getOpacity(world, testPos) >= 15 && state.getBlock() != Blocks.BEDROCK)
				break;
			if (state.getBlock() == Blocks.BEACON) {
				BlockEntity te = world.getBlockEntity(testPos);

				if (!(te instanceof BeaconBlockEntity)) break;

				BeaconBlockEntity bte = (BeaconBlockEntity) te;

				if (bte.getLevel() != 0 && !bte.beamSegments.isEmpty()) isOverBeacon = true;

				break;
			}
		}

		if (isOverBeacon) {
			ItemStack newStack = AllItems.REFINED_RADIANCE.asStack();
			newStack.setCount(stack.getCount());
			data.putBoolean("FromLight", true);
			entity.setStack(newStack);
			return false;
		}

		// Find a light source and eat it.
		Random r = world.random;
		int range = 3;
		float rate = 1 / 2f;
		if (r.nextFloat() > rate)
			return false;

		BlockPos randomOffset = new BlockPos(VecHelper.offsetRandomly(positionVec, r, range));
		BlockState state = world.getBlockState(randomOffset);
		if (state.getLightValue(world, randomOffset) == 0)
			return false;
		if (state.getHardness(world, randomOffset) == -1)
			return false;
		if (state.getBlock() == Blocks.BEACON)
			return false;

		RaycastContext context = new RaycastContext(positionVec, VecHelper.getCenterOf(randomOffset),
			ShapeType.COLLIDER, FluidHandling.NONE, entity);
		if (!randomOffset.equals(world.raycast(context)
			.getBlockPos()))
			return false;

		world.breakBlock(randomOffset, false);

		ItemStack newStack = stack.split(1);
		newStack.getOrCreateTag()
			.putInt("CollectingLight", itemData.getInt("CollectingLight") + 1);
		ItemEntity newEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), newStack);
		newEntity.setVelocity(entity.getVelocity());
		newEntity.setToDefaultPickupDelay();
		world.spawnEntity(newEntity);
		entity.lifespan = 6000;
		if (stack.isEmpty())
			entity.remove();

		return false;
	}

}
