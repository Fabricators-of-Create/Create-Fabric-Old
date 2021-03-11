package com.simibubi.create.content.curiosities;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BuildersTeaItem extends Item {

	public BuildersTeaItem(Settings p_i48487_1_) {
		super(p_i48487_1_);
	}

	public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
		PlayerEntity playerentity = entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
		if (playerentity instanceof ServerPlayerEntity)
			Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) playerentity, stack);

		if (!world.isClient) 
			entity.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 3 * 60 * 20, 0, false, false, false));

		if (playerentity != null) {
			playerentity.incrementStat(Stats.USED.getOrCreateStat(this));
			playerentity.getHungerManager().add(1, .6F);
			if (!playerentity.abilities.creativeMode)
				stack.decrement(1);
		}

		if (playerentity == null || !playerentity.abilities.creativeMode) {
			if (stack.isEmpty()) 
				return new ItemStack(Items.GLASS_BOTTLE);
			if (playerentity != null) 
				playerentity.inventory.insertStack(new ItemStack(Items.GLASS_BOTTLE));
		}

		return stack;
	}

	public int getMaxUseTime(ItemStack p_77626_1_) {
		return 42;
	}

	public UseAction getUseAction(ItemStack p_77661_1_) {
		return UseAction.DRINK;
	}

	public TypedActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
		p_77659_2_.setCurrentHand(p_77659_3_);
		return TypedActionResult.success(p_77659_2_.getStackInHand(p_77659_3_));
	}

}
