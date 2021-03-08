package com.simibubi.create.foundation.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.math.Box;

public class NBTHelper {

	public static void putMarker(CompoundTag nbt, String marker) {
		nbt.putBoolean(marker, true);
	}
	
	public static <T extends Enum<?>> T readEnum(CompoundTag nbt, String key, Class<T> enumClass) {
		T[] enumConstants = enumClass.getEnumConstants();
		String name = nbt.getString(key);
		if (enumConstants == null)
			throw new IllegalArgumentException("Non-Enum class passed to readEnum(): " + enumClass.getName());
		for (T t : enumConstants) {
			if (t.name().equals(name))
				return t;
		}
		return enumConstants[0];
	}
	
	public static <T extends Enum<?>> void writeEnum(CompoundTag nbt, String key, T enumConstant) {
		nbt.putString(key, enumConstant.name());
	}
	
	public static <T> ListTag writeCompoundList(Iterable<T> list, Function<T, CompoundTag> serializer) {
		ListTag listNBT = new ListTag();
		list.forEach(t -> listNBT.add(serializer.apply(t)));
		return listNBT;
	}

	public static <T> List<T> readCompoundList(ListTag listNBT, Function<CompoundTag, T> deserializer) {
		List<T> list = new ArrayList<>(listNBT.size());
		listNBT.forEach(inbt -> list.add(deserializer.apply((CompoundTag) inbt)));
		return list;
	}
	
	public static <T> void iterateCompoundList(ListTag listNBT, Consumer<CompoundTag> consumer) {
		listNBT.forEach(inbt -> consumer.accept((CompoundTag) inbt));
	}
	
	public static ListTag writeItemList(List<ItemStack> stacks) {
		return writeCompoundList(stacks, ItemStack::serializeNBT);
	}
	
	public static List<ItemStack> readItemList(ListTag stacks) {
		return readCompoundList(stacks, ItemStack::fromTag);
	}
	
	public static ListTag writeAABB(Box bb) {
		ListTag bbtag = new ListTag();
		bbtag.add(FloatTag.of((float) bb.minX));
		bbtag.add(FloatTag.of((float) bb.minY));
		bbtag.add(FloatTag.of((float) bb.minZ));
		bbtag.add(FloatTag.of((float) bb.maxX));
		bbtag.add(FloatTag.of((float) bb.maxY));
		bbtag.add(FloatTag.of((float) bb.maxZ));
		return bbtag;
	}

	public static Box readAABB(ListTag bbtag) {
		if (bbtag == null || bbtag.isEmpty())
			return null;
		return new Box(bbtag.getFloat(0), bbtag.getFloat(1), bbtag.getFloat(2), bbtag.getFloat(3),
				bbtag.getFloat(4), bbtag.getFloat(5));

	}

	@Nonnull
	public static Tag getINBT(CompoundTag nbt, String id) {
		Tag inbt = nbt.get(id);
		if (inbt != null)
			return inbt;
		return new CompoundTag();
	}

}
