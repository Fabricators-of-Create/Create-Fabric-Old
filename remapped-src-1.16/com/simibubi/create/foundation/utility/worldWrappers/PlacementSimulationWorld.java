package com.simibubi.create.foundation.utility.worldWrappers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.LightingProvider;

public class PlacementSimulationWorld extends WrappedWorld {
	public HashMap<BlockPos, BlockState> blocksAdded;
	public HashMap<BlockPos, BlockEntity> tesAdded;

	public HashSet<ChunkSectionPos> spannedChunks;
	public LightingProvider lighter;
	public WrappedChunkProvider chunkProvider;
	private final BlockPos.Mutable scratch = new BlockPos.Mutable();

	public PlacementSimulationWorld(World wrapped) {
		this(wrapped, new WrappedChunkProvider());
	}

	public PlacementSimulationWorld(World wrapped, WrappedChunkProvider chunkProvider) {
		super(wrapped, chunkProvider);
		this.chunkProvider = chunkProvider.setWorld(this);
		spannedChunks = new HashSet<>();
		lighter = new LightingProvider(chunkProvider, true, false); // blockLight, skyLight
		blocksAdded = new HashMap<>();
		tesAdded = new HashMap<>();
	}

	@Override
	public LightingProvider getLightingProvider() {
		return lighter;
	}

	public void setTileEntities(Collection<BlockEntity> tileEntities) {
		tesAdded.clear();
		tileEntities.forEach(te -> tesAdded.put(te.getPos(), te));
	}

	public void clear() {
		blocksAdded.clear();
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState newState, int flags) {

		ChunkSectionPos sectionPos = ChunkSectionPos.from(pos);

		if (spannedChunks.add(sectionPos)) {
			lighter.setSectionStatus(sectionPos, false);
		}

		lighter.checkBlock(pos);

		blocksAdded.put(pos, newState);
		return true;
	}

	@Override
	public boolean setBlockState(BlockPos pos, BlockState state) {
		return setBlockState(pos, state, 0);
	}

	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return tesAdded.get(pos);
	}

	@Override
	public boolean testBlockState(BlockPos pos, Predicate<BlockState> condition) {
		return condition.test(getBlockState(pos));
	}

	@Override
	public boolean canSetBlock(BlockPos pos) {
		return true;
	}

	@Override
	public boolean isAreaLoaded(BlockPos center, int range) {
		return true;
	}

	public BlockState getBlockState(int x, int y, int z) {
		return getBlockState(scratch.set(x, y, z));
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		BlockState state = blocksAdded.get(pos);
		if (state != null)
			return state;
		else
			return Blocks.AIR.getDefaultState();
	}

}
