package com.simibubi.create.content.curiosities.tools;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.foundation.utility.VecHelper;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SandPaperItem extends Item {

	public SandPaperItem(Settings properties) {
		super(properties.maxDamage(8));
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.EAT;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getStackInHand(handIn);
		TypedActionResult<ItemStack> FAIL = new TypedActionResult<>(ActionResult.FAIL, itemstack);

		if (itemstack.getOrCreateTag()
			.contains("Polishing")) {
			playerIn.setCurrentHand(handIn);
			return new TypedActionResult<>(ActionResult.PASS, itemstack);
		}

		Hand otherHand = handIn == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
		ItemStack itemInOtherHand = playerIn.getStackInHand(otherHand);
		if (SandPaperPolishingRecipe.canPolish(worldIn, itemInOtherHand)) {
			ItemStack item = itemInOtherHand.copy();
			ItemStack toPolish = item.split(1);
			playerIn.setCurrentHand(handIn);
			itemstack.getOrCreateTag()
				.put("Polishing", toPolish.serializeNBT());
			playerIn.setStackInHand(otherHand, item);
			return new TypedActionResult<>(ActionResult.SUCCESS, itemstack);
		}

		HitResult raytraceresult = raycast(worldIn, playerIn, RaycastContext.FluidHandling.NONE);
		if (!(raytraceresult instanceof BlockHitResult))
			return FAIL;
		BlockHitResult ray = (BlockHitResult) raytraceresult;
		Vec3d hitVec = ray.getPos();

		Box bb = new Box(hitVec, hitVec).expand(1f);
		ItemEntity pickUp = null;
		for (ItemEntity itemEntity : worldIn.getNonSpectatingEntities(ItemEntity.class, bb)) {
			if (!itemEntity.isAlive())
				continue;
			if (itemEntity.getPos()
				.distanceTo(playerIn.getPos()) > 3)
				continue;
			ItemStack stack = itemEntity.getStack();
			if (!SandPaperPolishingRecipe.canPolish(worldIn, stack))
				continue;
			pickUp = itemEntity;
			break;
		}

		if (pickUp == null)
			return FAIL;

		ItemStack item = pickUp.getStack()
			.copy();
		ItemStack toPolish = item.split(1);

		playerIn.setCurrentHand(handIn);

		if (!worldIn.isClient) {
			itemstack.getOrCreateTag()
				.put("Polishing", toPolish.serializeNBT());
			if (item.isEmpty())
				pickUp.remove();
			else
				pickUp.setStack(item);
		}

		return new TypedActionResult<>(ActionResult.SUCCESS, itemstack);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public int getItemEnchantability(ItemStack stack) {
		return 1;
	}

	@Override
	public ItemStack finishUsing(ItemStack stack, World worldIn, LivingEntity entityLiving) {
		if (!(entityLiving instanceof PlayerEntity))
			return stack;
		PlayerEntity player = (PlayerEntity) entityLiving;
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains("Polishing")) {
			ItemStack toPolish = ItemStack.fromTag(tag.getCompound("Polishing"));
			ItemStack polished =
				SandPaperPolishingRecipe.applyPolish(worldIn, entityLiving.getPos(), toPolish, stack);

			if (worldIn.isClient) {
				spawnParticles(entityLiving.getCameraPosVec(1)
					.add(entityLiving.getRotationVector()
						.multiply(.5f)),
					toPolish, worldIn);
				return stack;
			}

			if (!polished.isEmpty()) {
				if (player instanceof FakePlayer) {
					player.dropItem(polished, false, false);
				} else {
					player.inventory.offerOrDrop(worldIn, polished);
				}
			}
			tag.remove("Polishing");
			stack.damage(1, entityLiving, p -> p.sendToolBreakStatus(p.getActiveHand()));
		}

		return stack;
	}

	public static void spawnParticles(Vec3d location, ItemStack polishedStack, World world) {
		for (int i = 0; i < 20; i++) {
			Vec3d motion = VecHelper.offsetRandomly(Vec3d.ZERO, world.random, 1 / 8f);
			world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, polishedStack), location.x, location.y,
				location.z, motion.x, motion.y, motion.z);
		}
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if (!(entityLiving instanceof PlayerEntity))
			return;
		PlayerEntity player = (PlayerEntity) entityLiving;
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains("Polishing")) {
			ItemStack toPolish = ItemStack.fromTag(tag.getCompound("Polishing"));
			player.inventory.offerOrDrop(worldIn, toPolish);
			tag.remove("Polishing");
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return 32;
	}

	@Override
	public int getEnchantability() {
		return 5;
	}

}
