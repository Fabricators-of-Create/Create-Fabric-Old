package com.simibubi.create.foundation.block.entity.behaviour.scrollvalue;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.block.entity.behaviour.ValueBoxTransform.Sided;
import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.ScrollValueBehaviour.StepContext;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class ScrollValueHandler {
	@Environment(EnvType.CLIENT)
	public static boolean onScroll(double delta) {
		HitResult objectMouseOver = MinecraftClient.getInstance().crosshairTarget;
		if (!(objectMouseOver instanceof BlockHitResult))
			return false;

		BlockHitResult result = (BlockHitResult) objectMouseOver;
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientWorld world = mc.world;
		BlockPos blockPos = result.getBlockPos();

		ScrollValueBehaviour scrolling = BlockEntityBehaviour.get(world, blockPos, ScrollValueBehaviour.TYPE);
		if (scrolling == null)
			return false;
		if (!mc.player.canModifyBlocks())
			return false;
		if (scrolling.needsWrench && AllItems.WRENCH != mc.player.getMainHandStack().getItem())
			return false;
		if (scrolling.slotPositioning instanceof Sided)
			((Sided) scrolling.slotPositioning).fromSide(result.getSide());
		if (!scrolling.testHit(objectMouseOver.getPos()))
			return false;

		if (scrolling instanceof BulkScrollValueBehaviour && AllKeys.ctrlDown()) {
			BulkScrollValueBehaviour bulkScrolling = (BulkScrollValueBehaviour) scrolling;
			for (SmartBlockEntity be : bulkScrolling.getBulk()) {
				ScrollValueBehaviour other = be.getBehaviour(ScrollValueBehaviour.TYPE);
				if (other != null)
					applyTo(delta, other);
			}

		} else
			applyTo(delta, scrolling);

		return true;
	}

	protected static void applyTo(double delta, ScrollValueBehaviour scrolling) {
		scrolling.ticksUntilScrollPacket = 10;
		int valueBefore = scrolling.scrollableValue;

		StepContext context = new StepContext();
		context.control = AllKeys.ctrlDown();
		context.shift = AllKeys.shiftDown();
		context.currentValue = scrolling.scrollableValue;
		context.forward = delta > 0;

		double newValue = scrolling.scrollableValue + Math.signum(delta) * scrolling.step.apply(context);
		scrolling.scrollableValue = (int) MathHelper.clamp(newValue, scrolling.min, scrolling.max);

		if (valueBefore != scrolling.scrollableValue)
			scrolling.clientCallback.accept(scrolling.scrollableValue);
	}
}
