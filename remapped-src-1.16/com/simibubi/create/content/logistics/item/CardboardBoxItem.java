package com.simibubi.create.content.logistics.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

public class CardboardBoxItem extends Item {

	static final int SLOTS = 9;
	static final List<CardboardBoxItem> ALL_BOXES = new ArrayList<>();

	public CardboardBoxItem(Settings properties) {
		super(properties);
		ALL_BOXES.add(this);
	}

	@Override
	public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!playerIn.isSneaking())
			return super.use(worldIn, playerIn, handIn);

		ItemStack box = playerIn.getStackInHand(handIn);
		for (ItemStack stack : getContents(box))
			playerIn.inventory.offerOrDrop(worldIn, stack);

		if (!playerIn.isCreative()) {
			box.decrement(1);
		}
		return new TypedActionResult<>(ActionResult.SUCCESS, box);
	}

	public static ItemStack containing(List<ItemStack> stacks) {
		ItemStack box = new ItemStack(randomBox());
		CompoundTag compound = new CompoundTag();

		DefaultedList<ItemStack> list = DefaultedList.of();
		list.addAll(stacks);
		Inventories.toTag(compound, list);

		box.setTag(compound);
		return box;
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
	}
	
	public static void addAddress(ItemStack box, String address) {
		box.getOrCreateTag().putString("Address", address);
	}

	public static boolean matchAddress(ItemStack box, String other) {
		String address = box.getTag().getString("Address");
		if (address == null || address.isEmpty())
			return false;
		if (address.equals("*"))
			return true;
		if (address.equals(other))
			return true;
		if (address.endsWith("*") && other.startsWith(address.substring(0, address.length() - 1)))
			return true;

		return false;
	}

	public static List<ItemStack> getContents(ItemStack box) {
		DefaultedList<ItemStack> list = DefaultedList.ofSize(SLOTS, ItemStack.EMPTY);
		Inventories.fromTag(box.getOrCreateTag(), list);
		return list;
	}

	public static CardboardBoxItem randomBox() {
		return ALL_BOXES.get(new Random().nextInt(ALL_BOXES.size()));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
		super.appendTooltip(stack, worldIn, tooltip, flagIn);
		CompoundTag compoundnbt = stack.getOrCreateTag();

		if (compoundnbt.contains("Address", Constants.NBT.TAG_STRING)) {
			tooltip.add(new LiteralText("-> " + compoundnbt.getString("Address"))
					.formatted(Formatting.GOLD));
		}

		if (!compoundnbt.contains("Items", Constants.NBT.TAG_LIST))
			return;

		int i = 0;
		int j = 0;

		for (ItemStack itemstack : getContents(stack)) {
			if (itemstack.isEmpty())
				continue;

			++j;
			if (i <= 4) {
				++i;
				Text itextcomponent = itemstack.getName();
				tooltip.add(itextcomponent.copy().append(" x").append(String.valueOf(itemstack.getCount()))
					.formatted(Formatting.GRAY));
			}
		}

		if (j - i > 0) {
			tooltip.add((new TranslatableText("container.shulkerBox.more", j - i))
					.formatted(Formatting.ITALIC));
		}
	}

}
