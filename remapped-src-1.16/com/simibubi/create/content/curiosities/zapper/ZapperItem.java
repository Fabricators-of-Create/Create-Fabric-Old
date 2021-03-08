package com.simibubi.create.content.curiosities.zapper;

import java.util.List;

import javax.annotation.Nonnull;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.NBTProcessors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class ZapperItem extends Item {

	public ZapperItem(Settings properties) {
		super(properties.maxCount(1)
			.rarity(Rarity.UNCOMMON));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
		if (stack.hasTag() && stack.getTag()
			.contains("BlockUsed")) {
			String usedblock = NbtHelper.toBlockState(stack.getTag()
				.getCompound("BlockUsed"))
				.getBlock()
				.getTranslationKey();
			ItemDescription.add(tooltip,
				Lang.translate("blockzapper.usingBlock",
					new TranslatableText(usedblock).formatted(Formatting.GRAY))
					.formatted(Formatting.DARK_GRAY));
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		boolean differentBlock = false;
		if (oldStack.hasTag() && newStack.hasTag() && oldStack.getTag()
			.contains("BlockUsed")
			&& newStack.getTag()
				.contains("BlockUsed"))
			differentBlock = NbtHelper.toBlockState(oldStack.getTag()
				.getCompound("BlockUsed")) != NbtHelper.toBlockState(
					newStack.getTag()
						.getCompound("BlockUsed"));
		return slotChanged || !isZapper(newStack) || differentBlock;
	}

	public boolean isZapper(ItemStack newStack) {
		return newStack.getItem() instanceof ZapperItem;
	}

	@Nonnull
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		// Shift -> open GUI
		if (context.getPlayer() != null && context.getPlayer()
			.isSneaking()) {
			if (context.getWorld().isClient) {
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
					openHandgunGUI(context.getStack(), context.getHand() == Hand.OFF_HAND);
				});
				applyCooldown(context.getPlayer(), context.getStack(), false);
			}
			return ActionResult.SUCCESS;
		}
		return super.useOnBlock(context);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack item = player.getStackInHand(hand);
		CompoundTag nbt = item.getOrCreateTag();

		// Shift -> Open GUI
		if (player.isSneaking()) {
			if (world.isClient) {
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
					openHandgunGUI(item, hand == Hand.OFF_HAND);
				});
				applyCooldown(player, item, false);
			}
			return new TypedActionResult<>(ActionResult.SUCCESS, item);
		}

		boolean mainHand = hand == Hand.MAIN_HAND;
		boolean isSwap = item.getTag()
			.contains("_Swap");
		boolean gunInOtherHand = isZapper(player.getStackInHand(mainHand ? Hand.OFF_HAND : Hand.MAIN_HAND));

		// Pass To Offhand
		if (mainHand && isSwap && gunInOtherHand)
			return new TypedActionResult<>(ActionResult.FAIL, item);
		if (mainHand && !isSwap && gunInOtherHand)
			item.getTag()
				.putBoolean("_Swap", true);
		if (!mainHand && isSwap)
			item.getTag()
				.remove("_Swap");
		if (!mainHand && gunInOtherHand)
			player.getStackInHand(Hand.MAIN_HAND)
				.getTag()
				.remove("_Swap");
		player.setCurrentHand(hand);

		// Check if can be used
		Text msg = validateUsage(item);
		if (msg != null) {
			world.playSound(player, player.getBlockPos(), AllSoundEvents.BLOCKZAPPER_DENY.get(), SoundCategory.BLOCKS,
				1f, 0.5f);
			player.sendMessage(msg.copy().formatted(Formatting.RED), true);
			return new TypedActionResult<>(ActionResult.FAIL, item);
		}

		BlockState stateToUse = Blocks.AIR.getDefaultState();
		if (nbt.contains("BlockUsed"))
			stateToUse = NbtHelper.toBlockState(nbt.getCompound("BlockUsed"));
		stateToUse = BlockHelper.setZeroAge(stateToUse);
		CompoundTag data = null;
		if (AllBlockTags.SAFE_NBT.matches(stateToUse) && nbt.contains("BlockData", NBT.TAG_COMPOUND)) {
			data = nbt.getCompound("BlockData");
		}

		// Raytrace - Find the target
		Vec3d start = player.getPos()
			.add(0, player.getStandingEyeHeight(), 0);
		Vec3d range = player.getRotationVector()
			.multiply(getZappingRange(item));
		BlockHitResult raytrace = world
			.raycast(new RaycastContext(start, start.add(range), ShapeType.OUTLINE, FluidHandling.NONE, player));
		BlockPos pos = raytrace.getBlockPos();
		BlockState stateReplaced = world.getBlockState(pos);

		// No target
		if (pos == null || stateReplaced.getBlock() == Blocks.AIR) {
			applyCooldown(player, item, gunInOtherHand);
			return new TypedActionResult<>(ActionResult.SUCCESS, item);
		}

		// Find exact position of gun barrel for VFX
		float yaw = (float) ((player.yaw) / -180 * Math.PI);
		float pitch = (float) ((player.pitch) / -180 * Math.PI);
		Vec3d barrelPosNoTransform =
			new Vec3d(mainHand == (player.getMainArm() == Arm.RIGHT) ? -.35f : .35f, -0.1f, 1);
		Vec3d barrelPos = start.add(barrelPosNoTransform.rotateX(pitch)
			.rotateY(yaw));

		// Client side
		if (world.isClient) {
			ZapperRenderHandler.dontAnimateItem(hand);
			return new TypedActionResult<>(ActionResult.SUCCESS, item);
		}

		// Server side
		if (activate(world, player, item, stateToUse, raytrace, data)) {
			applyCooldown(player, item, gunInOtherHand);
			AllPackets.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> player),
				new ZapperBeamPacket(barrelPos, raytrace.getPos(), hand, false));
			AllPackets.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
				new ZapperBeamPacket(barrelPos, raytrace.getPos(), hand, true));
		}

		return new TypedActionResult<>(ActionResult.SUCCESS, item);
	}

	public Text validateUsage(ItemStack item) {
		CompoundTag tag = item.getOrCreateTag();
		if (!canActivateWithoutSelectedBlock(item) && !tag.contains("BlockUsed"))
			return Lang.createTranslationTextComponent("blockzapper.leftClickToSet");
		return null;
	}

	protected abstract boolean activate(World world, PlayerEntity player, ItemStack item, BlockState stateToUse,
		BlockHitResult raytrace, CompoundTag data);

	@Environment(EnvType.CLIENT)
	protected abstract void openHandgunGUI(ItemStack item, boolean b);

	protected abstract int getCooldownDelay(ItemStack item);

	protected abstract int getZappingRange(ItemStack stack);

	protected boolean canActivateWithoutSelectedBlock(ItemStack stack) {
		return false;
	}

	protected void applyCooldown(PlayerEntity playerIn, ItemStack item, boolean dual) {
		int delay = getCooldownDelay(item);
		playerIn.getItemCooldownManager()
			.set(item.getItem(), dual ? delay * 2 / 3 : delay);
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
		return true;
	}

	@Override
	public boolean canMine(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
		return false;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.NONE;
	}

	public static void setTileData(World world, BlockPos pos, BlockState state, CompoundTag data, PlayerEntity player) {
		if (data != null && AllBlockTags.SAFE_NBT.matches(state)) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (tile != null) {
				data = NBTProcessors.process(tile, data, !player.isCreative());
				if (data == null)
					return;
				data.putInt("x", pos.getX());
				data.putInt("y", pos.getY());
				data.putInt("z", pos.getZ());
				tile.fromTag(state, data);
			}
		}
	}

}
