package com.simibubi.create.content.contraptions.components.actors;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.foundation.utility.BlockHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlockBreakingMovementBehaviour extends MovementBehaviour {

	@Override
	public void startMoving(MovementContext context) {
		if (context.world.isClient)
			return;
		context.data.putInt("BreakerId", -BlockBreakingKineticTileEntity.NEXT_BREAKER_ID.incrementAndGet());
	}

	@Override
	public void visitNewPosition(MovementContext context, BlockPos pos) {
		World world = context.world;
		BlockState stateVisited = world.getBlockState(pos);

		if (!stateVisited.isSolidBlock(world, pos))
			damageEntities(context, pos, world);
		if (world.isClient)
			return;
		if (!canBreak(world, pos, stateVisited))
			return;

		context.data.put("BreakingPos", NbtHelper.fromBlockPos(pos));
		context.stall = true;
	}

	public void damageEntities(MovementContext context, BlockPos pos, World world) {
		DamageSource damageSource = getDamageSource();
		if (damageSource == null && !throwsEntities())
			return;
		Entities: for (Entity entity : world.getNonSpectatingEntities(Entity.class, new Box(pos))) {
			if (entity instanceof ItemEntity)
				continue;
			if (entity instanceof AbstractContraptionEntity)
				continue;
			if (entity instanceof AbstractMinecartEntity)
				for (Entity passenger : entity.getPassengersDeep())
					if (passenger instanceof AbstractContraptionEntity
							&& ((AbstractContraptionEntity) passenger).getContraption() == context.contraption)
						continue Entities;

			if (damageSource != null && !world.isClient) {
				float damage = (float) MathHelper.clamp(6 * Math.pow(context.relativeMotion.length(), 0.4) + 1, 2, 10);
				entity.damage(damageSource, damage);
			}
			if (throwsEntities() && (world.isClient == (entity instanceof PlayerEntity))) {
				Vec3d motionBoost = context.motion.add(0, context.motion.length() / 4f, 0);
				int maxBoost = 4;
				if (motionBoost.length() > maxBoost) {
					motionBoost = motionBoost.subtract(motionBoost.normalize().multiply(motionBoost.length() - maxBoost));
				}
				entity.setVelocity(entity.getVelocity().add(motionBoost));
				entity.velocityModified = true;
			}
		}
	}

	protected DamageSource getDamageSource() {
		return null;
	}

	protected boolean throwsEntities() {
		return getDamageSource() != null;
	}

	@Override
	public void stopMoving(MovementContext context) {
		CompoundTag data = context.data;
		if (context.world.isClient)
			return;
		if (!data.contains("BreakingPos"))
			return;

		World world = context.world;
		int id = data.getInt("BreakerId");
		BlockPos breakingPos = NbtHelper.toBlockPos(data.getCompound("BreakingPos"));

		data.remove("Progress");
		data.remove("TicksUntilNextProgress");
		data.remove("BreakingPos");

		context.stall = false;
		world.setBlockBreakingInfo(id, breakingPos, -1);
	}

	@Override
	public void tick(MovementContext context) {
		tickBreaker(context);

		CompoundTag data = context.data;
		if (!data.contains("WaitingTicks"))
			return;

		int waitingTicks = data.getInt("WaitingTicks");
		if (waitingTicks-- > 0) {
			data.putInt("WaitingTicks", waitingTicks);
			context.stall = true;
			return;
		}

		BlockPos pos = NbtHelper.toBlockPos(data.getCompound("LastPos"));
		data.remove("WaitingTicks");
		data.remove("LastPos");
		context.stall = false;
		visitNewPosition(context, pos);
	}

	public void tickBreaker(MovementContext context) {
		CompoundTag data = context.data;
		if (context.world.isClient)
			return;
		if (!data.contains("BreakingPos"))
			return;
		if (context.relativeMotion.equals(Vec3d.ZERO)) {
			context.stall = false;
			return;
		}

		int ticksUntilNextProgress = data.getInt("TicksUntilNextProgress");
		if (ticksUntilNextProgress-- > 0) {
			data.putInt("TicksUntilNextProgress", ticksUntilNextProgress);
			return;
		}

		World world = context.world;
		BlockPos breakingPos = NbtHelper.toBlockPos(data.getCompound("BreakingPos"));
		int destroyProgress = data.getInt("Progress");
		int id = data.getInt("BreakerId");
		BlockState stateToBreak = world.getBlockState(breakingPos);
		float blockHardness = stateToBreak.getHardness(world, breakingPos);

		if (!canBreak(world, breakingPos, stateToBreak)) {
			if (destroyProgress != 0) {
				destroyProgress = 0;
				data.remove("Progress");
				data.remove("TicksUntilNextProgress");
				data.remove("BreakingPos");
				world.setBlockBreakingInfo(id, breakingPos, -1);
			}
			context.stall = false;
			return;
		}

		float breakSpeed = MathHelper.clamp(Math.abs(context.getAnimationSpeed()) / 500f, 1 / 128f, 16f);
		destroyProgress += MathHelper.clamp((int) (breakSpeed / blockHardness), 1, 10 - destroyProgress);

		if (destroyProgress >= 10) {
			world.setBlockBreakingInfo(id, breakingPos, -1);
			
			// break falling blocks from top to bottom
			BlockPos ogPos = breakingPos;
			BlockState stateAbove = world.getBlockState(breakingPos.up());
			while (stateAbove.getBlock() instanceof FallingBlock) {
				breakingPos = breakingPos.up();
				stateAbove = world.getBlockState(breakingPos.up());
			}
			stateToBreak = world.getBlockState(breakingPos);
			
			context.stall = false;
			BlockHelper.destroyBlock(context.world, breakingPos, 1f, stack -> this.dropItem(context, stack));
			onBlockBroken(context, ogPos, stateToBreak);
			ticksUntilNextProgress = -1;
			data.remove("Progress");
			data.remove("TicksUntilNextProgress");
			data.remove("BreakingPos");
			return;
		}

		ticksUntilNextProgress = (int) (blockHardness / breakSpeed);
		world.setBlockBreakingInfo(id, breakingPos, (int) destroyProgress);
		data.putInt("TicksUntilNextProgress", ticksUntilNextProgress);
		data.putInt("Progress", destroyProgress);
	}

	public boolean canBreak(World world, BlockPos breakingPos, BlockState state) {
		float blockHardness = state.getHardness(world, breakingPos);
		return BlockBreakingKineticTileEntity.isBreakable(state, blockHardness);
	}

	protected void onBlockBroken(MovementContext context, BlockPos pos, BlockState brokenState) {
		// Check for falling blocks
		if (!(brokenState.getBlock() instanceof FallingBlock))
			return;

		CompoundTag data = context.data;
		data.putInt("WaitingTicks", 10);
		data.put("LastPos", NbtHelper.fromBlockPos(pos));
		context.stall = true;
	}

}
