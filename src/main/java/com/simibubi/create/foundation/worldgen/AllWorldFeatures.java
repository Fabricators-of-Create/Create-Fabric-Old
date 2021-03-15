package com.simibubi.create.foundation.worldgen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.Lang;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public enum AllWorldFeatures {

    ZINC_ORE_DESERT(new OreFeature(AllBlocks.ZINC_ORE, 17, 5).inBiomes(Biome.Category.DESERT)),
    ZINC_ORE(new OreFeature(AllBlocks.ZINC_ORE, 14, 4)),

    ;

    /**
     * Increment this number if all worldgen entries should be overwritten in this
     * update. Worlds from the previous version will overwrite potentially changed
     * values with the new defaults.
     */
    public static final int forcedUpdateVersion = 1;

    public OreFeature feature;
    private final Map<Biome.Category, ConfiguredFeature<?, ?>> featureInstances;

    AllWorldFeatures(OreFeature feature) {
        this.feature = feature;
        this.featureInstances = new HashMap<>();
        this.feature.setId(Lang.asId(name()));
    }

    public static void reload() {
    	for (AllWorldFeatures entry : AllWorldFeatures.values()) {
    		for (Biome biome : BuiltinRegistries.BIOME) {
    			if (biome.getCategory() == Biome.Category.THEEND || biome.getCategory() == Biome.Category.NETHER) continue;
    			if (entry.featureInstances.containsKey(biome.getCategory())) continue;

				Optional<ConfiguredFeature<?, ?>> createFeature = entry.feature.createFeature(biome);
				if (!createFeature.isPresent()) continue;

				RegistryKey<ConfiguredFeature<?, ?>> x = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, new Identifier("create", entry.name().toLowerCase()  + "_" + biome.getCategory().toString().toLowerCase()));
			 	Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, x.getValue(), createFeature.get());

				entry.featureInstances.put(biome.getCategory(), createFeature.get());
				BiomeModifications.addFeature(BiomeSelectors.categories(biome.getCategory()), GenerationStep.Feature.UNDERGROUND_ORES, x);
			}
		}
    }

}
