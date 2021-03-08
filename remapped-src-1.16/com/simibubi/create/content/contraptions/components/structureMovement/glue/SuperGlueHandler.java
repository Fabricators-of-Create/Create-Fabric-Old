package com.simibubi.create.content.contraptions.components.structureMovement.glue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.placement.IPlacementHelper;
import com.simibubi.create.foundation.utility.worldWrappers.RayTraceWorld;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.network.PacketDistributor;

@EventBusSubscriber
public class SuperGlueHandler {

	public static Map<Direction, SuperGlueEntity> gatherGlue(WorldAccess world, BlockPos pos) {
		List<SuperGlueEntity> entities = world.getNonSpectatingEntities(SuperGlueEntity.class, new Box(pos));
		Map<Direction, SuperGlueEntity> map = new HashMap<>();
		for (SuperGlueEntity entity : entities)
			map.put(entity.getAttachedDirection(pos), entity);
		return map;
	}

	@SubscribeEvent
	public static void glueListensForBlockPlacement(EntityPlaceEvent event) {
		WorldAccess world = event.getWorld();
		Entity entity = event.getEntity();
		BlockPos pos = event.getPos();

		if (entity == null || world == null || pos == null)
			return;
		if (world.isClient())
			return;

		Map<Direction, SuperGlueEntity> gatheredGlue = gatherGlue(world, pos);
		for (Direction direction : gatheredGlue.keySet())
			AllPackets.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
				new GlueEffectPacket(pos, direction, true));

		if (entity instanceof PlayerEntity)
			glueInOffHandAppliesOnBlockPlace(event, pos, (PlayerEntity) entity);
	}

	public static void glueInOffHandAppliesOnBlockPlace(EntityPlaceEvent event, BlockPos pos, PlayerEntity placer) {
		ItemStack itemstack = placer.getOffHandStack();
		EntityAttributeInstance reachAttribute = placer.getAttributeInstance(ForgeMod.REACH_DISTANCE.get());
		if (!AllItems.SUPER_GLUE.isIn(itemstack) || reachAttribute == null)
			return;
		if (AllItems.WRENCH.isIn(placer.getMainHandStack()))
			return;
		if (event.getPlacedAgainst() == IPlacementHelper.ID)
			return;

		double distance = reachAttribute.getValue();
		Vec3d start = placer.getCameraPosVec(1);
		Vec3d look = placer.getRotationVec(1);
		Vec3d end = start.add(look.x * distance, look.y * distance, look.z * distance);
		World world = placer.world;

		RayTraceWorld rayTraceWorld =
			new RayTraceWorld(world, (p, state) -> p.equals(pos) ? Blocks.AIR.getDefaultState() : state);
		BlockHitResult ray = rayTraceWorld.raycast(
			new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, placer));

		Direction face = ray.getSide();
		if (face == null || ray.getType() == Type.MISS)
			return;

		if (!ray.getBlockPos()
			.offset(face)
			.equals(pos)) {
			event.setCanceled(true);
			return;
		}

		SuperGlueEntity entity = new SuperGlueEntity(world, ray.getBlockPos(), face.getOpposite());
		CompoundTag compoundnbt = itemstack.getTag();
		if (compoundnbt != null)
			EntityType.loadFromEntityTag(world, placer, entity, compoundnbt);

		if (entity.onValidSurface()) {
			if (!world.isClient) {
				entity.playPlaceSound();
				world.spawnEntity(entity);
				AllPackets.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
					new GlueEffectPacket(ray.getBlockPos(), face, true));
			}
			itemstack.damage(1, placer, SuperGlueItem::onBroken);
		}
	}

}
