package com.simibubi.create.foundation.worldgen;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
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

    private final Block block;
    private Biome.Category specificCategory;

    public OreFeature(Block block, int clusterSize, int clusterCount, int aboveBottom, int belowTop) {
        this.block = block;
        this.enable = true;
        this.clusterSize = clusterSize;
        this.clusterCount = clusterCount;
//        this.bottom = YOffset.aboveBottom(aboveBottom);
//        this.top = YOffset.belowTop(belowTop);
    }

    public OreFeature inBiomes(Biome.Category category) {
        specificCategory = category;
        return this;
    }

    public Optional<ConfiguredFeature<?, ?>> createFeature(Biome biome) {
        if (specificCategory != null && biome.getCategory() != specificCategory)
            return Optional.empty();

        return Optional.of(Feature.ORE
            .configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, block.getDefaultState(), clusterSize))
//            .decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(aboveBottom, belowTop)))
            .spreadHorizontally().repeat(clusterCount));
    }

    public void setId(String id) {
        this.id = id;
    }

}
