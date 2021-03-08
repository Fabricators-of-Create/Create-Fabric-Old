package com.simibubi.create.content.schematics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Sets;
import com.simibubi.create.content.schematics.ItemRequirement.ItemUseType;
import com.simibubi.create.foundation.utility.Lang;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class MaterialChecklist {

	public static final int MAX_ENTRIES_PER_PAGE = 5;

	public Object2IntMap<Item> gathered = new Object2IntArrayMap<>();
	public Object2IntMap<Item> required = new Object2IntArrayMap<>();
	public Object2IntMap<Item> damageRequired = new Object2IntArrayMap<>();
	public boolean blocksNotLoaded;

	public void warnBlockNotLoaded() {
		blocksNotLoaded = true;
	}

	public void require(ItemRequirement requirement) {
		if (requirement.isEmpty())
			return;
		if (requirement.isInvalid())
			return;

		for (ItemStack stack : requirement.requiredItems) {
			if (requirement.getUsage() == ItemUseType.DAMAGE)
				putOrIncrement(damageRequired, stack);
			if (requirement.getUsage() == ItemUseType.CONSUME)
				putOrIncrement(required, stack);
		}
	}

	private void putOrIncrement(Object2IntMap<Item> map, ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.AIR)
			return;
		if (map.containsKey(item))
			map.put(item, map.getInt(item) + stack.getCount());
		else
			map.put(item, stack.getCount());
	}

	public void collect(ItemStack stack) {
		Item item = stack.getItem();
		if (required.containsKey(item) || damageRequired.containsKey(item))
			if (gathered.containsKey(item))
				gathered.put(item, gathered.getInt(item) + stack.getCount());
			else
				gathered.put(item, stack.getCount());
	}

	public ItemStack createItem() {
		ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

		CompoundTag tag = book.getOrCreateTag();
		ListTag pages = new ListTag();

		int itemsWritten = 0;
		MutableText textComponent;

		if (blocksNotLoaded) {
			textComponent = new LiteralText("\n" + Formatting.RED);
			textComponent =
				textComponent.append(Lang.createTranslationTextComponent("materialChecklist.blocksNotLoaded"));
			pages.add(StringTag.of(Text.Serializer.toJson(textComponent)));
		}

		List<Item> keys = new ArrayList<>(Sets.union(required.keySet(), damageRequired.keySet()));
		Collections.sort(keys, (item1, item2) -> {
			Locale locale = Locale.ENGLISH;
			String name1 = new TranslatableText(item1.getTranslationKey()).getString()
				.toLowerCase(locale);
			String name2 = new TranslatableText(item2.getTranslationKey()).getString()
				.toLowerCase(locale);
			return name1.compareTo(name2);
		});

		textComponent = new LiteralText("");
		List<Item> completed = new ArrayList<>();
		for (Item item : keys) {
			int amount = getRequiredAmount(item);
			if (gathered.containsKey(item))
				amount -= gathered.getInt(item);

			if (amount <= 0) {
				completed.add(item);
				continue;
			}

			if (itemsWritten == MAX_ENTRIES_PER_PAGE) {
				itemsWritten = 0;
				textComponent.append(new LiteralText("\n >>>").formatted(Formatting.BLUE));
				pages.add(StringTag.of(Text.Serializer.toJson(textComponent)));
				textComponent = new LiteralText("");
			}

			itemsWritten++;
			textComponent.append(entry(new ItemStack(item), amount, true));
		}

		for (Item item : completed) {
			if (itemsWritten == MAX_ENTRIES_PER_PAGE) {
				itemsWritten = 0;
				textComponent.append(new LiteralText("\n >>>").formatted(Formatting.DARK_GREEN));
				pages.add(StringTag.of(Text.Serializer.toJson(textComponent)));
				textComponent = new LiteralText("");
			}

			itemsWritten++;
			textComponent.append(entry(new ItemStack(item), getRequiredAmount(item), false));
		}

		pages.add(StringTag.of(Text.Serializer.toJson(textComponent)));

		tag.put("pages", pages);
		tag.putString("author", "Schematicannon");
		tag.putString("title", Formatting.BLUE + "Material Checklist");
		textComponent = Lang.createTranslationTextComponent("materialChecklist")
			.setStyle(Style.EMPTY.withColor(Formatting.BLUE)
				.withItalic(Boolean.FALSE));
		book.getOrCreateSubTag("display")
			.putString("Name", Text.Serializer.toJson(textComponent));
		book.setTag(tag);

		return book;
	}

	public int getRequiredAmount(Item item) {
		int amount = required.getOrDefault(item, 0);
		if (damageRequired.containsKey(item))
			amount += Math.ceil(damageRequired.getInt(item) / (float) new ItemStack(item).getMaxDamage());
		return amount;
	}

	private Text entry(ItemStack item, int amount, boolean unfinished) {
		int stacks = amount / 64;
		int remainder = amount % 64;
		MutableText tc = new TranslatableText(item.getTranslationKey());
		if (!unfinished)
			tc.append(" \u2714");
		tc.formatted(unfinished ? Formatting.BLUE : Formatting.DARK_GREEN);
		return tc.append(new LiteralText("\n" + " x" + amount).formatted(Formatting.BLACK))
			.append(
				new LiteralText(" | " + stacks + "\u25A4 +" + remainder + "\n").formatted(Formatting.GRAY));
	}

}
