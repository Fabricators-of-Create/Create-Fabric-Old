package com.simibubi.create.foundation.worldgen;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class OreFeature {

    public String id;

    protected boolean enable;
    protected int clusterSize;
    protected int clusterCount;
//    protected YOffset bottom;
//    protected YOffset top;
	protected int minHeight;
	protected int maxHeight;

    private final Block block;
    private Biome.Category specificCategory;

    public OreFeature(Block block, int clusterSize, int clusterCount) {
        this.block = block;
        this.enable = true;
        this.clusterSize = clusterSize;
        this.clusterCount = clusterCount;
//        this.bottom = YOffset.aboveBottom(aboveBottom);
//        this.top = YOffset.belowTop(belowTop);
		this.minHeight = 0;
		this.maxHeight = 256;
    }

    public OreFeature inBiomes(Biome.Category category) {
        specificCategory = category;
        return this;
    }

    public Optional<ConfiguredFeature<?, ?>> createFeature(Biome biome) {
        if (specificCategory != null && biome.getCategory() != specificCategory)
            return Optional.empty();
        if (!canGenerate())
			return Optional.empty();

		return Optional.of(Feature.ORE
            .configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, block.getDefaultState(), clusterSize))
            //.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(aboveBottom, belowTop)))
			.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(0, minHeight, maxHeight)))
            .spreadHorizontally().repeat(clusterCount));
    }

    public void setId(String id) {
        this.id = id;
    }

	protected boolean canGenerate() {
		return minHeight < maxHeight && clusterSize > 0 && enable /*&& !AllConfigs.COMMON.worldGen.disable.get()*/;
	}

}
