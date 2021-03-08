package com.simibubi.create.foundation.utility.worldWrappers;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WrappedServerWorld extends ServerWorld {

	protected World world;

	public WrappedServerWorld(World world) {
		// Replace null with world.getChunkProvider().chunkManager.field_219266_t ? We had null in 1.15
		super(world.getServer(), Util.getMainWorkerExecutor(), getLevelSaveFromWorld(world), (ServerWorldProperties) world.getLevelProperties(), world.getRegistryKey(), world.getDimension(), null, ((ServerChunkManager) world.getChunkManager()).getChunkGenerator(), world.isDebugWorld(), world.getBiomeAccess().seed, Collections.EMPTY_LIST, false); //, world.field_25143);
		this.world = world;
	}

	@Override
	public float getSkyAngleRadians(float p_72826_1_) {
		return 0;
	}
	
	@Override
	public int getLightLevel(BlockPos pos) {
		return 15;
	}

	@Override
	public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
		world.updateListeners(pos, oldState, newState, flags);
	}

	@Override
	public ServerTickScheduler<Block> getBlockTickScheduler() {
		TickScheduler<Block> tl =  world.getBlockTickScheduler();
		if (tl instanceof ServerTickScheduler)
			return (ServerTickScheduler<Block>) tl;
		return super.getBlockTickScheduler();
	}

	@Override
	public ServerTickScheduler<Fluid> getFluidTickScheduler() {
		TickScheduler<Fluid> tl =  world.getFluidTickScheduler();
		if (tl instanceof ServerTickScheduler)
			return (ServerTickScheduler<Fluid>) tl;
		return super.getFluidTickScheduler();
	}

	@Override
	public void syncWorldEvent(PlayerEntity player, int type, BlockPos pos, int data) {
	}

	@Override
	public List<ServerPlayerEntity> getPlayers() {
		return Collections.emptyList();
	}

	@Override
	public void playSound(PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category,
			float volume, float pitch) {
	}

	@Override
	public void playSoundFromEntity(PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_,
			SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
	}

	@Override
	public Entity getEntityById(int id) {
		return null;
	}

	@Override
	public MapState getMapState(String mapName) {
		return null;
	}

	@Override
	public boolean spawnEntity(Entity entityIn) {
		entityIn.setWorld(world);
		return world.spawnEntity(entityIn);
	}

	@Override
	public void putMapState(MapState mapDataIn) {
	}

	@Override
	public int getNextMapId() {
		return 0;
	}

	@Override
	public void setBlockBreakingInfo(int breakerId, BlockPos pos, int progress) {
	}

	@Override
	public RecipeManager getRecipeManager() {
		return world.getRecipeManager();
	}

	@Override
	public TagManager getTagManager() {
		return world.getTagManager();
	}

	@Override
	public Biome getGeneratorStoredBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
		return world.getGeneratorStoredBiome(p_225604_1_, p_225604_2_, p_225604_3_);
	}

	private static LevelStorage.Session getLevelSaveFromWorld(World world) {
		return ObfuscationReflectionHelper.getPrivateValue(MinecraftServer.class, world.getServer(), "field_71310_m");
	}
}
