package com.simibubi.create.foundation.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.biome.source.BiomeAccess;

@Mixin(BiomeAccess.class)
public interface BiomeAccessAccessor {
	@Accessor("seed")
	long create$seed();
}
