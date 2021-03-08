package com.simibubi.create.content.logistics.block.redstone;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

public class ContactMovementBehaviour extends MovementBehaviour {

	@Override
	public Vec3d getActiveAreaOffset(MovementContext context) {
		return Vec3d.of(context.state.get(RedstoneContactBlock.FACING).getVector()).multiply(.65f);
	}

	@Override
	public void visitNewPosition(MovementContext context, BlockPos pos) {
		BlockState block = context.state;
		World world = context.world;

		if (world.isClient)
			return;
		if (context.firstMovement)
			return;

		deactivateLastVisitedContact(context);
		BlockState visitedState = world.getBlockState(pos);
		if (!AllBlocks.REDSTONE_CONTACT.has(visitedState))
			return;

		Vec3d contact = Vec3d.of(block.get(RedstoneContactBlock.FACING).getVector());
		contact = context.rotation.apply(contact);
		Direction direction = Direction.getFacing(contact.x, contact.y, contact.z);

		if (!RedstoneContactBlock.hasValidContact(world, pos.offset(direction.getOpposite()), direction))
			return;
		world.setBlockState(pos, visitedState.with(RedstoneContactBlock.POWERED, true));
		context.data.put("lastContact", NbtHelper.fromBlockPos(pos));
		return;
	}

	@Override
	public void stopMoving(MovementContext context) {
		deactivateLastVisitedContact(context);
	}

	public void deactivateLastVisitedContact(MovementContext context) {
		if (context.data.contains("lastContact")) {
			BlockPos last = NbtHelper.toBlockPos(context.data.getCompound("lastContact"));
			context.world.getBlockTickScheduler().schedule(last, AllBlocks.REDSTONE_CONTACT.get(), 1, TickPriority.NORMAL);
			context.data.remove("lastContact");
		}
	}

}
