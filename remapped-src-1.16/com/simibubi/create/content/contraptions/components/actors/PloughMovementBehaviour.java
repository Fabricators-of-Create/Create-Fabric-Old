package com.simibubi.create.content.contraptions.components.actors;

import static net.minecraft.block.HorizontalFacingBlock.FACING;

import com.simibubi.create.content.contraptions.components.actors.PloughBlock.PloughFakePlayer;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;

public class PloughMovementBehaviour extends BlockBreakingMovementBehaviour {

	@Override
	public boolean isActive(MovementContext context) {
		return !VecHelper.isVecPointingTowards(context.relativeMotion, context.state.get(FACING)
			.getOpposite());
	}

	@Override
	public void visitNewPosition(MovementContext context, BlockPos pos) {
		super.visitNewPosition(context, pos);
		World world = context.world;
		if (world.isClient)
			return;
		BlockPos below = pos.down();
		if (!world.canSetBlock(below))
			return;

		Vec3d vec = VecHelper.getCenterOf(pos);
		PloughFakePlayer player = getPlayer(context);

		if (player == null)
			return;

		BlockHitResult ray = world
			.raycast(new RaycastContext(vec, vec.add(0, -1, 0), ShapeType.OUTLINE, FluidHandling.NONE, player));
		if (ray.getType() != Type.BLOCK)
			return;

		ItemUsageContext ctx = new ItemUsageContext(player, Hand.MAIN_HAND, ray);
		new ItemStack(Items.DIAMOND_HOE).useOnBlock(ctx);
	}

	@Override
	public Vec3d getActiveAreaOffset(MovementContext context) {
		return Vec3d.of(context.state.get(FACING)
			.getVector()).multiply(.45);
	}

	@Override
	protected boolean throwsEntities() {
		return true;
	}

	@Override
	public boolean canBreak(World world, BlockPos breakingPos, BlockState state) {
		return state.getCollisionShape(world, breakingPos)
			.isEmpty() && !(state.getBlock() instanceof FluidBlock)
			&& !(world.getBlockState(breakingPos.down())
				.getBlock() instanceof FarmlandBlock);
	}

	@Override
	public void stopMoving(MovementContext context) {
		super.stopMoving(context);
		if (context.temporaryData instanceof PloughFakePlayer)
			((PloughFakePlayer) context.temporaryData).remove();
	}

	private PloughFakePlayer getPlayer(MovementContext context) {
		if (!(context.temporaryData instanceof PloughFakePlayer) && context.world != null) {
			PloughFakePlayer player = new PloughFakePlayer((ServerWorld) context.world);
			player.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.DIAMOND_HOE));
			context.temporaryData = player;
		}
		return (PloughFakePlayer) context.temporaryData;
	}

}
