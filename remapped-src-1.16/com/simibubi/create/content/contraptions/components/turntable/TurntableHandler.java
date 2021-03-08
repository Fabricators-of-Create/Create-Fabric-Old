package com.simibubi.create.content.contraptions.components.turntable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TurntableHandler {

	public static void gameRenderTick() {
		MinecraftClient mc = MinecraftClient.getInstance();
		BlockPos pos = mc.player.getBlockPos();

		if (!AllBlocks.TURNTABLE.has(mc.world.getBlockState(pos)))
			return;
		if (!mc.player.isOnGround())
			return;
		if (mc.isPaused())
			return;

		BlockEntity tileEntity = mc.world.getBlockEntity(pos);
		if (!(tileEntity instanceof TurntableTileEntity))
			return;
		
		TurntableTileEntity turnTable = (TurntableTileEntity) tileEntity;
		float speed = turnTable.getSpeed() * 3/10;

		if (speed == 0)
			return;
		
		Vec3d origin = VecHelper.getCenterOf(pos);
		Vec3d offset = mc.player.getPos().subtract(origin);
		
		if (offset.length() > 1/4f)
			speed *= MathHelper.clamp((1/2f - offset.length()) * 2, 0, 1);

		mc.player.yaw = mc.player.prevYaw - speed * AnimationTickHolder.getPartialTicks();
		mc.player.bodyYaw = mc.player.yaw;
	}

}
