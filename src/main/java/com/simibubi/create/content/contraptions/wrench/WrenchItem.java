package com.simibubi.create.content.contraptions.wrench;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllItems;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class WrenchItem extends Item {

	public WrenchItem(Settings properties) {
		super(properties);
	}

	@NotNull
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity player = context.getPlayer();
		if (player == null || !player.canModifyBlocks())
			return super.useOnBlock(context);

		BlockState state = context.getWorld()
			.getBlockState(context.getBlockPos());
		if (!(state.getBlock() instanceof Wrenchable))
			return super.useOnBlock(context);
		Wrenchable actor = (Wrenchable) state.getBlock();

		if (player.isSneaking())
			return actor.onSneakWrenched(state, context);
		return actor.onWrenched(state, context);
	}
	
	public static void wrenchInstaKillsMinecarts(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
		if (!(entity instanceof AbstractMinecartEntity))
			return;
		if (!playerEntity.isHolding(AllItems.WRENCH))
			return;
		if (playerEntity.isCreative())
			return;
		AbstractMinecartEntity minecart = (AbstractMinecartEntity) entity;
		minecart.damage(DamageSource.player(playerEntity), 100);
	}
	
}
