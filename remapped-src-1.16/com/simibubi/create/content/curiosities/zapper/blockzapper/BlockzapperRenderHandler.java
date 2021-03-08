package com.simibubi.create.content.curiosities.zapper.blockzapper;

import java.util.Collections;
import java.util.List;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.CreateClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

public class BlockzapperRenderHandler {

	private static List<BlockPos> renderedShape;

	public static void tick() {
		gatherSelectedBlocks();
		if (renderedShape.isEmpty())
			return;

		CreateClient.outliner.showCluster("blockzapper", renderedShape)
			.colored(0xbfbfbf)
			.lineWidth(1 / 32f)
			.withFaceTexture(AllSpecialTextures.CHECKERED);
	}

	protected static void gatherSelectedBlocks() {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		ItemStack heldMain = player.getMainHandStack();
		ItemStack heldOff = player.getOffHandStack();
		boolean zapperInMain = AllItems.BLOCKZAPPER.isIn(heldMain);
		boolean zapperInOff = AllItems.BLOCKZAPPER.isIn(heldOff);

		if (zapperInMain) {
			CompoundTag tag = heldMain.getOrCreateTag();
			if (!tag.contains("_Swap") || !zapperInOff) {
				createOutline(player, heldMain);
				return;
			}
		}

		if (zapperInOff) {
			createOutline(player, heldOff);
			return;
		}

		renderedShape = Collections.emptyList();
	}

	private static void createOutline(ClientPlayerEntity player, ItemStack held) {
		if (!held.getOrCreateTag().contains("BlockUsed")) {
			renderedShape = Collections.emptyList();
			return;
		}
		renderedShape = BlockzapperItem.getSelectedBlocks(held, player.world, player);
	}

}
