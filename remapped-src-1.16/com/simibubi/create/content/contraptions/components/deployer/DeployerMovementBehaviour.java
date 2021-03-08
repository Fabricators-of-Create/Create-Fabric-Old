package com.simibubi.create.content.contraptions.components.deployer;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity.Mode;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.content.logistics.item.filter.FilterItem;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants.NBT;

public class DeployerMovementBehaviour extends MovementBehaviour {

	@Override
	public Vec3d getActiveAreaOffset(MovementContext context) {
		return Vec3d.of(context.state.get(DeployerBlock.FACING)
			.getVector())
			.multiply(2);
	}

	@Override
	public void visitNewPosition(MovementContext context, BlockPos pos) {
		if (context.world.isClient)
			return;

		tryGrabbingItem(context);
		DeployerFakePlayer player = getPlayer(context);
		Mode mode = getMode(context);
		if (mode == Mode.USE && !DeployerHandler.shouldActivate(player.getMainHandStack(), context.world, pos))
			return;

		activate(context, pos, player, mode);
		tryDisposeOfExcess(context);
		context.stall = player.blockBreakingProgress != null;
	}

	public void activate(MovementContext context, BlockPos pos, DeployerFakePlayer player, Mode mode) {
		Vec3d facingVec = Vec3d.of(context.state.get(DeployerBlock.FACING)
			.getVector());
		facingVec = context.rotation.apply(facingVec);
		Vec3d vec = context.position.subtract(facingVec.multiply(2));
		player.yaw = AbstractContraptionEntity.yawFromVector(facingVec);
		player.pitch = AbstractContraptionEntity.pitchFromVector(facingVec) - 90;

		DeployerHandler.activate(player, vec, pos, facingVec, mode);
	}

	@Override
	public void tick(MovementContext context) {
		if (context.world.isClient)
			return;
		if (!context.stall)
			return;

		DeployerFakePlayer player = getPlayer(context);
		Mode mode = getMode(context);

		Pair<BlockPos, Float> blockBreakingProgress = player.blockBreakingProgress;
		if (blockBreakingProgress != null) {
			int timer = context.data.getInt("Timer");
			if (timer < 20) {
				timer++;
				context.data.putInt("Timer", timer);
				return;
			}

			context.data.remove("Timer");
			activate(context, blockBreakingProgress.getKey(), player, mode);
			tryDisposeOfExcess(context);
		}

		context.stall = player.blockBreakingProgress != null;
	}

	@Override
	public void stopMoving(MovementContext context) {
		if (context.world.isClient)
			return;

		DeployerFakePlayer player = getPlayer(context);
		if (player == null)
			return;

		context.tileData.put("Inventory", player.inventory.serialize(new ListTag()));
		player.remove();
	}

	private void tryGrabbingItem(MovementContext context) {
		DeployerFakePlayer player = getPlayer(context);
		if (player == null)
			return;
		if (player.getMainHandStack()
			.isEmpty()) {
			ItemStack filter = getFilter(context);
			ItemStack held = ItemHelper.extract(context.contraption.inventory,
				stack -> FilterItem.test(context.world, stack, filter), 1, false);
			player.setStackInHand(Hand.MAIN_HAND, held);
		}
	}

	private void tryDisposeOfExcess(MovementContext context) {
		DeployerFakePlayer player = getPlayer(context);
		if (player == null)
			return;
		PlayerInventory inv = player.inventory;
		ItemStack filter = getFilter(context);

		for (List<ItemStack> list : Arrays.asList(inv.armor, inv.offHand, inv.main)) {
			for (int i = 0; i < list.size(); ++i) {
				ItemStack itemstack = list.get(i);
				if (itemstack.isEmpty())
					continue;

				if (list == inv.main && i == inv.selectedSlot
					&& FilterItem.test(context.world, itemstack, filter))
					continue;

				dropItem(context, itemstack);
				list.set(i, ItemStack.EMPTY);
			}
		}
	}

	@Override
	public void writeExtraData(MovementContext context) {
		DeployerFakePlayer player = getPlayer(context);
		if (player == null)
			return;
		context.data.put("HeldItem", player.getMainHandStack()
			.serializeNBT());
	}

	private DeployerFakePlayer getPlayer(MovementContext context) {
		if (!(context.temporaryData instanceof DeployerFakePlayer) && context.world instanceof ServerWorld) {
			DeployerFakePlayer deployerFakePlayer = new DeployerFakePlayer((ServerWorld) context.world);
			deployerFakePlayer.inventory.deserialize(context.tileData.getList("Inventory", NBT.TAG_COMPOUND));
			if (context.data.contains("HeldItem"))
				deployerFakePlayer.setStackInHand(Hand.MAIN_HAND, ItemStack.fromTag(context.data.getCompound("HeldItem")));
			context.tileData.remove("Inventory");
			context.temporaryData = deployerFakePlayer;
		}
		return (DeployerFakePlayer) context.temporaryData;
	}

	private ItemStack getFilter(MovementContext context) {
		return ItemStack.fromTag(context.tileData.getCompound("Filter"));
	}

	private Mode getMode(MovementContext context) {
		return NBTHelper.readEnum(context.tileData, "Mode", Mode.class);
	}

	@Override
	public void renderInContraption(MovementContext context, MatrixStack ms, MatrixStack msLocal,
		VertexConsumerProvider buffers) {
		DeployerRenderer.renderInContraption(context, ms, msLocal, buffers);
	}

}
