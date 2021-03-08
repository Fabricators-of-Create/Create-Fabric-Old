package com.simibubi.create.content.curiosities.zapper.terrainzapper;

import java.util.List;
import java.util.stream.Collectors;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public class WorldshaperRenderHandler {

	private static List<BlockPos> renderedShape;
	private static BlockPos renderedPosition;

	public static void tick() {
		gatherSelectedBlocks();
		if (renderedPosition == null)
			return;

		CreateClient.outliner.showCluster("terrainZapper", renderedShape.stream()
			.map(pos -> pos.add(renderedPosition))
			.collect(Collectors.toList()))
			.colored(0xbfbfbf)
			.lineWidth(1 / 32f)
			.withFaceTexture(AllSpecialTextures.CHECKERED);
	}

	protected static void gatherSelectedBlocks() {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		ItemStack heldMain = player.getMainHandStack();
		ItemStack heldOff = player.getOffHandStack();
		boolean zapperInMain = AllItems.WORLDSHAPER.isIn(heldMain);
		boolean zapperInOff = AllItems.WORLDSHAPER.isIn(heldOff);

		if (zapperInMain) {
			CompoundTag tag = heldMain.getOrCreateTag();
			if (!tag.contains("_Swap") || !zapperInOff) {
				createBrushOutline(tag, player, heldMain);
				return;
			}
		}

		if (zapperInOff) {
			CompoundTag tag = heldOff.getOrCreateTag();
			createBrushOutline(tag, player, heldOff);
			return;
		}

		renderedPosition = null;
	}

	public static void createBrushOutline(CompoundTag tag, ClientPlayerEntity player, ItemStack zapper) {
		if (!tag.contains("BrushParams")) {
			renderedPosition = null;
			return;
		}

		Brush brush = NBTHelper.readEnum(tag, "Brush", TerrainBrushes.class)
			.get();
		PlacementOptions placement = NBTHelper.readEnum(tag, "Placement", PlacementOptions.class);
		BlockPos params = NbtHelper.toBlockPos(tag.getCompound("BrushParams"));
		brush.set(params.getX(), params.getY(), params.getZ());
		renderedShape = brush.getIncludedPositions();

		Vec3d start = player.getPos()
			.add(0, player.getStandingEyeHeight(), 0);
		Vec3d range = player.getRotationVector()
			.multiply(128);
		BlockHitResult raytrace = player.world
			.raycast(new RaycastContext(start, start.add(range), ShapeType.OUTLINE, FluidHandling.NONE, player));
		if (raytrace == null || raytrace.getType() == Type.MISS) {
			renderedPosition = null;
			return;
		}

		BlockPos pos = raytrace.getBlockPos();
		renderedPosition = pos.add(brush.getOffset(player.getRotationVector(), raytrace.getSide(), placement));
	}

}
