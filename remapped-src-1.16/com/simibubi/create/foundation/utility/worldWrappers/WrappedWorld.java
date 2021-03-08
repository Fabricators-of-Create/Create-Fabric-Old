package com.simibubi.create.foundation.utility.worldWrappers;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.light.LightingProvider;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WrappedWorld extends World {

	protected World world;
	private WrappedChunkProvider provider;

	public WrappedWorld(World world, WrappedChunkProvider provider) {
		this(world);
		this.provider = provider;
	}

	public WrappedWorld(World world) {
		super((MutableWorldProperties) world.getLevelProperties(), world.getRegistryKey(), world.getDimension(), world::getProfiler,
			world.isClient, world.isDebugWorld(), 0);
		this.world = world;
	}

	public World getWorld() {
		return world;
	}
	
	@Override
	public LightingProvider getLightingProvider() {
		return super.getLightingProvider();
	}
	
	@Override
	public BlockState getBlockState(@Nullable BlockPos pos) {
		return world.getBlockState(pos);
	}

	@Override
	public boolean testBlockState(@Nullable BlockPos p_217375_1_, @Nullable Predicate<BlockState> p_217375_2_) {
		return world.testBlockState(p_217375_1_, p_217375_2_);
	}

	@Override
	public BlockEntity getBlockEntity(@Nullable BlockPos pos) {
		return world.getBlockEntity(pos);
	}

	@Override
	public boolean setBlockState(@Nullable BlockPos pos, @Nullable BlockState newState, int flags) {
		return world.setBlockState(pos, newState, flags);
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
	public TickScheduler<Block> getBlockTickScheduler() {
		return world.getBlockTickScheduler();
	}

	@Override
	public TickScheduler<Fluid> getFluidTickScheduler() {
		return world.getFluidTickScheduler();
	}

	@Override
	public ChunkManager getChunkManager() {
		return provider;
	}

	@Override
	public void syncWorldEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data) {}

	@Override
	public List<? extends PlayerEntity> getPlayers() {
		return Collections.emptyList();
	}

	@Override
	public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent soundIn,
		SoundCategory category, float volume, float pitch) {}

	@Override
	public void playSoundFromEntity(@Nullable PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_,
		SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {}

	@Override
	public Entity getEntityById(int id) {
		return null;
	}

	@Override
	public MapState getMapState(String mapName) {
		return null;
	}

	@Override
	public boolean spawnEntity(@Nullable Entity entityIn) {
		if (entityIn == null)
			return false;
		entityIn.setWorld(world);
		return world.spawnEntity(entityIn);
	}

	@Override
	public void putMapState(MapState mapDataIn) {}

	@Override
	public int getNextMapId() {
		return world.getNextMapId();
	}

	@Override
	public void setBlockBreakingInfo(int breakerId, BlockPos pos, int progress) {}

	@Override
	public Scoreboard getScoreboard() {
		return world.getScoreboard();
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

	@Override
	public DynamicRegistryManager getRegistryManager() {
		return world.getRegistryManager();
	}

	@Override
	public float getBrightness(Direction p_230487_1_, boolean p_230487_2_) {
		return world.getBrightness(p_230487_1_, p_230487_2_);
	}
}
