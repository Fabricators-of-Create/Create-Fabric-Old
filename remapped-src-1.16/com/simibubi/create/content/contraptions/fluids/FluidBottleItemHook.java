package com.simibubi.create.content.contraptions.fluids;

import com.simibubi.create.Create;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class FluidBottleItemHook extends Item {

	public FluidBottleItemHook(Settings p_i48487_1_) {
		super(p_i48487_1_);
	}

	@SubscribeEvent
	public static void preventWaterBottlesFromCreatesFluids(PlayerInteractEvent.RightClickItem event) {
		ItemStack itemStack = event.getItemStack();
		if (itemStack.isEmpty())
			return;
		if (!(itemStack.getItem() instanceof GlassBottleItem))
			return;

		World world = event.getWorld();
		PlayerEntity player = event.getPlayer();
		HitResult raytraceresult = raycast(world, player, RaycastContext.FluidHandling.SOURCE_ONLY);
		if (raytraceresult.getType() != HitResult.Type.BLOCK)
			return;
		BlockPos blockpos = ((BlockHitResult) raytraceresult).getBlockPos();
		if (!world.canPlayerModifyAt(player, blockpos))
			return;

		FluidState fluidState = world.getFluidState(blockpos);
		if (fluidState.isIn(FluidTags.WATER) && fluidState.getFluid()
			.getRegistryName()
			.getNamespace()
			.equals(Create.ID)) {
			event.setCancellationResult(ActionResult.PASS);
			event.setCanceled(true);
			return;
		}

		return;
	}

}
