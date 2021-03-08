package com.simibubi.create.content.schematics.filtering;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.simibubi.create.content.schematics.SchematicWorld;
import com.simibubi.create.content.schematics.item.SchematicItem;
import com.simibubi.create.foundation.utility.WorldAttached;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SchematicInstances {

	public static WorldAttached<Cache<Integer, SchematicWorld>> loadedSchematics;

	static {
		loadedSchematics = new WorldAttached<>(() -> CacheBuilder.newBuilder()
			.expireAfterAccess(5, TimeUnit.MINUTES)
			.build());
	}

	public static void register() {}

	@Nullable
	public static SchematicWorld get(World world, ItemStack schematic) {
		Cache<Integer, SchematicWorld> map = loadedSchematics.get(world);
		int hash = getHash(schematic);
		try {
			return map.get(hash, () -> loadWorld(world, schematic));
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static SchematicWorld loadWorld(World wrapped, ItemStack schematic) {
		if (schematic == null || !schematic.hasTag())
			return null;
		if (!schematic.getTag()
			.getBoolean("Deployed"))
			return null;

		Structure activeTemplate = SchematicItem.loadSchematic(schematic);

		if (activeTemplate.getSize()
			.equals(BlockPos.ORIGIN))
			return null;

		BlockPos anchor = NbtHelper.toBlockPos(schematic.getTag()
			.getCompound("Anchor"));
		SchematicWorld world = new SchematicWorld(anchor, wrapped);
		StructurePlacementData settings = SchematicItem.getSettings(schematic);
		activeTemplate.place(world, anchor, settings, wrapped.getRandom());

		return world;
	}

	public static void clearHash(ItemStack schematic) {
		if (schematic == null || !schematic.hasTag())
			return;
		schematic.getTag()
			.remove("SchematicHash");
	}

	public static int getHash(ItemStack schematic) {
		if (schematic == null || !schematic.hasTag())
			return -1;
		CompoundTag tag = schematic.getTag();
		if (!tag.contains("SchematicHash"))
			tag.putInt("SchematicHash", tag.toString()
				.hashCode());
		return tag.getInt("SchematicHash");
	}

}
