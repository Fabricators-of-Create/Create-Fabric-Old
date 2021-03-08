package com.simibubi.create.foundation.utility.worldWrappers.chunk;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.simibubi.create.foundation.utility.worldWrappers.PlacementSimulationWorld;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.feature.StructureFeature;

public class WrappedChunk implements Chunk {

    final PlacementSimulationWorld world;
    boolean needsLight;
    final int x;
    final int z;
    final ChunkPos pos;

    private final ChunkSection[] sections;

    public WrappedChunk(PlacementSimulationWorld world, int x, int z) {
        this.world = world;
        this.needsLight = true;
        this.x = x;
        this.z = z;
        this.pos = new ChunkPos(x, z);

        this.sections = new ChunkSection[16];

        for (int i = 0; i < 16; i++) {
            sections[i] = new WrappedChunkSection(this, i << 4);
        }
    }

    @Override
    public Stream<BlockPos> getLightSourcesStream() {
        return world.blocksAdded
                .entrySet()
                .stream()
                .filter(it -> {
                    BlockPos blockPos = it.getKey();
                    boolean chunkContains = blockPos.getX() >> 4 == x && blockPos.getZ() >> 4 == z;
                    return chunkContains && it.getValue().getLightValue(world, blockPos) != 0;
                })
                .map(Map.Entry::getKey);
    }

    @Override
    public ChunkSection[] getSectionArray() {
        return sections;
    }

    @Nullable
    @Override
    public BlockState setBlockState(BlockPos p_177436_1_, BlockState p_177436_2_, boolean p_177436_3_) {
        return null;
    }

    @Override
    public void setBlockEntity(BlockPos p_177426_1_, BlockEntity p_177426_2_) {

    }

    @Override
    public void addEntity(Entity p_76612_1_) {

    }

    @Override
    public Set<BlockPos> getBlockEntityPositions() {
        return null;
    }

    @Override
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        return null;
    }

    @Override
    public void setHeightmap(Heightmap.Type p_201607_1_, long[] p_201607_2_) {

    }

    @Override
    public Heightmap getHeightmap(Heightmap.Type p_217303_1_) {
        return null;
    }

    @Override
    public int sampleHeightmap(Heightmap.Type p_201576_1_, int p_201576_2_, int p_201576_3_) {
        return 0;
    }

    @Override
    public ChunkPos getPos() {
        return null;
    }

    @Override
    public void setLastSaveTime(long p_177432_1_) {

    }

    @Nullable
    @Override
    public BiomeArray getBiomeArray() {
        return null;
    }

    @Override
    public void setShouldSave(boolean p_177427_1_) {

    }

    @Override
    public boolean needsSaving() {
        return false;
    }

    @Override
    public ChunkStatus getStatus() {
        return null;
    }

    @Override
    public void removeBlockEntity(BlockPos p_177425_1_) {

    }

    @Override
    public ShortList[] getPostProcessingLists() {
        return new ShortList[0];
    }

    @Nullable
    @Override
    public CompoundTag getBlockEntityTag(BlockPos p_201579_1_) {
        return null;
    }

    @Nullable
    @Override
    public CompoundTag getPackedBlockEntityTag(BlockPos p_223134_1_) {
        return null;
    }

    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return null;
    }

    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return null;
    }

    @Override
    public UpgradeData getUpgradeData() {
        return null;
    }

    @Override
    public void setInhabitedTime(long p_177415_1_) {

    }

    @Override
    public long getInhabitedTime() {
        return 0;
    }

    @Override
    public boolean isLightOn() {
        return needsLight;
    }

    @Override
    public void setLightOn(boolean needsLight) {
        this.needsLight = needsLight;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return world.getBlockState(pos);
    }

	@Override
	public FluidState getFluidState(BlockPos p_204610_1_) {
		return null;
	}

	@Override
	public void addStructureReference(StructureFeature<?> arg0, long arg1) {
		
	}

	@Override
	public Map<StructureFeature<?>, LongSet> getStructureReferences() {
		return null;
	}

	@Override
	public LongSet getStructureReferences(StructureFeature<?> arg0) {
		return null;
	}

	@Override
	public StructureStart<?> getStructureStart(StructureFeature<?> arg0) {
		return null;
	}

	@Override
	public void setStructureReferences(Map<StructureFeature<?>, LongSet> arg0) {
		
	}

	@Override
	public void setStructureStart(StructureFeature<?> arg0, StructureStart<?> arg1) {
		
	}

	@Override
	public void setStructureStarts(Map<StructureFeature<?>, StructureStart<?>> p_201612_1_) {
		
	}

	@Override
	public Map<StructureFeature<?>, StructureStart<?>> getStructureStarts() {
		return null;
	}

}
