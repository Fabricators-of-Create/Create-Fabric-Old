package com.simibubi.create.foundation.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorage;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
	@Accessor("session")
	LevelStorage.Session create$session();
}
