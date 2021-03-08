package com.simibubi.create.content.palettes;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Environment(EnvType.CLIENT)
public class StandardFoliageColorHandler implements BlockColorProvider {

	@Override
	public int getColor(BlockState state, BlockRenderView light, BlockPos pos, int layer) {
		return pos != null && light != null ? BiomeColors.getGrassColor(light, pos) : GrassColors.getColor(0.5D, 1.0D);
	}

}
