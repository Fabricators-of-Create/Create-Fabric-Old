package com.simibubi.create.content.schematics;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.utility.NBTProcessors;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class SchematicProcessor extends StructureProcessor {
	
	public static final SchematicProcessor INSTANCE = new SchematicProcessor();
	public static final Codec<SchematicProcessor> CODEC = Codec.unit(() -> {
		return INSTANCE;
	});
	
	public static StructureProcessorType<SchematicProcessor> TYPE;
	
	public static void register() {
		TYPE = StructureProcessorType.register("schematic", CODEC);
	}
	
	@Nullable
	@Override
	public Structure.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos anotherPos, Structure.StructureBlockInfo rawInfo,
			Structure.StructureBlockInfo info, StructurePlacementData settings, @Nullable Structure template) {
		if (info.tag != null) {
			BlockEntity te = info.state.createTileEntity(world);
			if (te != null) {
				CompoundTag nbt = NBTProcessors.process(te, info.tag, false);
				if (nbt != info.tag)
					return new Structure.StructureBlockInfo(info.pos, info.state, nbt);
			}
		}
		return info;
	}

	@Nullable
	@Override
	public Structure.StructureEntityInfo processEntity(WorldView world, BlockPos pos, Structure.StructureEntityInfo rawInfo,
			Structure.StructureEntityInfo info, StructurePlacementData settings, Structure template) {
		return EntityType.fromTag(info.tag).flatMap(type -> {
			if (world instanceof World) {
				Entity e = type.create((World) world);
				if (e != null && !e.entityDataRequiresOperator()) {
					return Optional.of(info);
				}
			}
			return Optional.empty();
		}).orElse(null);
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return TYPE;
	}

}
