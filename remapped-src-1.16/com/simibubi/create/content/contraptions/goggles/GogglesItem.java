package com.simibubi.create.content.contraptions.goggles;

import com.simibubi.create.AllItems;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GogglesItem extends Item {

	public GogglesItem(Settings properties) {
		super(properties);
		DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
	}

	@Override
	public EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return EquipmentSlot.HEAD;
	}

	public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getStackInHand(handIn);
		EquipmentSlot equipmentslottype = MobEntity.getPreferredEquipmentSlot(itemstack);
		ItemStack itemstack1 = playerIn.getEquippedStack(equipmentslottype);
		if (itemstack1.isEmpty()) {
			playerIn.equipStack(equipmentslottype, itemstack.copy());
			itemstack.setCount(0);
			return new TypedActionResult<>(ActionResult.SUCCESS, itemstack);
		} else {
			return new TypedActionResult<>(ActionResult.FAIL, itemstack);
		}
	}

	public static boolean canSeeParticles(PlayerEntity player) {
		for (ItemStack itemStack : player.getArmorItems())
			if (AllItems.GOGGLES.isIn(itemStack))
				return true;
		return false;
	}

}
