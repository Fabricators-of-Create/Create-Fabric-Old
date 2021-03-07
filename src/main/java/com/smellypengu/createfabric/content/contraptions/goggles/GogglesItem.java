package com.smellypengu.createfabric.content.contraptions.goggles;

import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;

public class GogglesItem extends Item {

	public GogglesItem(Settings properties) {
		super(properties);
		DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
	}

	/**public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getStackInHand(handIn);
		EquipmentSlot equipmentslottype = MobEntity.getPreferredEquipmentSlot(itemstack);
		ItemStack itemstack1 = playerIn.getEquippedStack(equipmentslottype);
		if (itemstack1.isEmpty()) {
			playerIn.equipStack(equipmentslottype, itemstack.copy());
			itemstack.setCount(0);
			return new ActionResult<>(ActionResult.SUCCESS, itemstack);
		} else {
			return new ActionResult<>(ActionResult.FAIL, itemstack);
		}
	}*/

	/**public static boolean canSeeParticles(PlayerEntity player) {
		for (ItemStack itemStack : player.getArmorItems())
			if (AllItems.GOGGLES.isIn(itemStack))
				return true;
		return false;
	}*/

}
