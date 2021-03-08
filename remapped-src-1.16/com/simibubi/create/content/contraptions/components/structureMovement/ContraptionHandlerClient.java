package com.simibubi.create.content.contraptions.components.structureMovement;

import org.apache.commons.lang3.mutable.MutableObject;

import com.simibubi.create.content.contraptions.components.structureMovement.sync.ContraptionInteractionPacket;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.RaycastHelper;
import com.simibubi.create.foundation.utility.RaycastHelper.PredicateTraceResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.ClickInputEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ContraptionHandlerClient {

	@SubscribeEvent
	@Environment(EnvType.CLIENT)
	public static void preventRemotePlayersWalkingAnimations(PlayerTickEvent event) {
		if (event.phase == Phase.START)
			return;
		if (!(event.player instanceof OtherClientPlayerEntity))
			return;
		OtherClientPlayerEntity remotePlayer = (OtherClientPlayerEntity) event.player;
		CompoundTag data = remotePlayer.getPersistentData();
		if (!data.contains("LastOverrideLimbSwingUpdate"))
			return;

		int lastOverride = data.getInt("LastOverrideLimbSwingUpdate");
		data.putInt("LastOverrideLimbSwingUpdate", lastOverride + 1);
		if (lastOverride > 5) {
			data.remove("LastOverrideLimbSwingUpdate");
			data.remove("OverrideLimbSwing");
			return;
		}

		float limbSwing = data.getFloat("OverrideLimbSwing");
		remotePlayer.prevX = remotePlayer.getX() - (limbSwing / 4);
		remotePlayer.prevZ = remotePlayer.getZ();
	}

	@SubscribeEvent
	@Environment(EnvType.CLIENT)
	public static void rightClickingOnContraptionsGetsHandledLocally(ClickInputEvent event) {
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayerEntity player = mc.player;
		if (player == null)
			return;
		if (mc.world == null)
			return;
		if (!event.isUseItem())
			return;
		Vec3d origin = RaycastHelper.getTraceOrigin(player);

		double reach = mc.interactionManager.getReachDistance();
		if (mc.crosshairTarget != null && mc.crosshairTarget.getPos() != null)
			reach = Math.min(mc.crosshairTarget.getPos()
				.distanceTo(origin), reach);

		Vec3d target = RaycastHelper.getTraceTarget(player, reach, origin);
		for (AbstractContraptionEntity contraptionEntity : mc.world
			.getNonSpectatingEntities(AbstractContraptionEntity.class, new Box(origin, target))) {

			Vec3d localOrigin = contraptionEntity.toLocalVector(origin, 1);
			Vec3d localTarget = contraptionEntity.toLocalVector(target, 1);
			Contraption contraption = contraptionEntity.getContraption();

			MutableObject<BlockHitResult> mutableResult = new MutableObject<>();
			PredicateTraceResult predicateResult = RaycastHelper.rayTraceUntil(localOrigin, localTarget, p -> {
				StructureBlockInfo blockInfo = contraption.getBlocks()
					.get(p);
				if (blockInfo == null)
					return false;
				BlockState state = blockInfo.state;
				VoxelShape raytraceShape = state.getOutlineShape(MinecraftClient.getInstance().world, BlockPos.ORIGIN.down());
				if (raytraceShape.isEmpty())
					return false;
				BlockHitResult rayTrace = raytraceShape.raycast(localOrigin, localTarget, p);
				if (rayTrace != null) {
					mutableResult.setValue(rayTrace);
					return true;
				}
				return false;
			});

			if (predicateResult == null || predicateResult.missed())
				return;

			BlockHitResult rayTraceResult = mutableResult.getValue();
			Hand hand = event.getHand();
			Direction face = rayTraceResult.getSide();
			BlockPos pos = rayTraceResult.getBlockPos();

			if (!contraptionEntity.handlePlayerInteraction(player, pos, face, hand))
				return;
			AllPackets.channel.sendToServer(new ContraptionInteractionPacket(contraptionEntity, hand, pos, face));
			event.setCanceled(true);
			event.setSwingHand(false);
		}
	}

}
