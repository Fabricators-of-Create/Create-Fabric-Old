package com.simibubi.create.content.curiosities.zapper;

import java.util.Objects;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.foundation.utility.BlockHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ZapperInteractionHandler {

	@SubscribeEvent
	public static void leftClickingBlocksWithTheZapperSelectsTheBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (event.getWorld().isClient)
			return;
		ItemStack heldItem = event.getPlayer().getMainHandStack();
		if (heldItem.getItem() instanceof ZapperItem && trySelect(heldItem, event.getPlayer())) {
			event.setCancellationResult(ActionResult.FAIL);
			event.setCanceled(true);
		}
	}

	public static boolean trySelect(ItemStack stack, PlayerEntity player) {
		if (player.isSneaking())
			return false;

		Vec3d start = player.getPos()
			.add(0, player.getStandingEyeHeight(), 0);
		Vec3d range = player.getRotationVector()
			.multiply(getRange(stack));
		BlockHitResult raytrace = player.world
			.raycast(new RaycastContext(start, start.add(range), ShapeType.OUTLINE, FluidHandling.NONE, player));
		BlockPos pos = raytrace.getBlockPos();
		if (pos == null)
			return false;

		player.world.setBlockBreakingInfo(player.getEntityId(), pos, -1);
		BlockState newState = player.world.getBlockState(pos);

		if (BlockHelper.getRequiredItem(newState)
			.isEmpty())
			return false;
		if (newState.hasTileEntity() && !AllBlockTags.SAFE_NBT.matches(newState))
			return false;
		if (BlockHelper.hasBlockStateProperty(newState, Properties.DOUBLE_BLOCK_HALF))
			return false;
		if (BlockHelper.hasBlockStateProperty(newState, Properties.ATTACHED))
			return false;
		if (BlockHelper.hasBlockStateProperty(newState, Properties.HANGING))
			return false;
		if (BlockHelper.hasBlockStateProperty(newState, Properties.BED_PART))
			return false;
		if (BlockHelper.hasBlockStateProperty(newState, Properties.STAIR_SHAPE))
			newState = newState.with(Properties.STAIR_SHAPE, StairShape.STRAIGHT);
		if (BlockHelper.hasBlockStateProperty(newState, Properties.PERSISTENT))
			newState = newState.with(Properties.PERSISTENT, true);
		if (BlockHelper.hasBlockStateProperty(newState, Properties.WATERLOGGED))
			newState = newState.with(Properties.WATERLOGGED, false);

		CompoundTag data = null;
		BlockEntity tile = player.world.getBlockEntity(pos);
		if (tile != null) {
			data = tile.toTag(new CompoundTag());
			data.remove("x");
			data.remove("y");
			data.remove("z");
			data.remove("id");
		}
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains("BlockUsed")
				&& NbtHelper.toBlockState(
						stack.getTag().getCompound("BlockUsed")) == newState
				&& Objects.equals(data, tag.get("BlockData"))) {
			return false;
		}

		tag.put("BlockUsed", NbtHelper.fromBlockState(newState));
		if (data == null)
			tag.remove("BlockData");
		else
			tag.put("BlockData", data);
		player.world.playSound(null, player.getBlockPos(), AllSoundEvents.BLOCKZAPPER_CONFIRM.get(),
			SoundCategory.BLOCKS, 0.5f, 0.8f);

		return true;
	}

	public static int getRange(ItemStack stack) {
		if (stack.getItem() instanceof ZapperItem)
			return ((ZapperItem) stack.getItem()).getZappingRange(stack);
		return 0;
	}
}
