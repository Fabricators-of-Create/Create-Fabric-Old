package com.simibubi.create.content.contraptions.fluids.potion;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.simibubi.create.content.contraptions.fluids.potion.PotionFluid.BottleType;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

public class PotionFluidHandler {

	public static Pair<FluidStack, ItemStack> emptyPotion(ItemStack stack, boolean simulate) {
		FluidStack fluid = getFluidFromPotionItem(stack);
		if (!simulate)
			stack.decrement(1);
		return Pair.of(fluid, new ItemStack(Items.GLASS_BOTTLE));
	}

	public static FluidIngredient potionIngredient(Potion potion, int amount) {
		return FluidIngredient.fromFluidStack(FluidHelper.copyStackWithAmount(PotionFluidHandler
			.getFluidFromPotionItem(PotionUtil.setPotion(new ItemStack(Items.POTION), potion)), amount));
	}

	public static FluidStack getFluidFromPotionItem(ItemStack stack) {
		Potion potion = PotionUtil.getPotion(stack);
		List<StatusEffectInstance> list = PotionUtil.getCustomPotionEffects(stack);
		FluidStack fluid = PotionFluid.withEffects(250, potion, list);
		BottleType bottleTypeFromItem = bottleTypeFromItem(stack);
		if (potion == Potions.WATER && list.isEmpty() && bottleTypeFromItem == BottleType.REGULAR)
			return new FluidStack(Fluids.WATER, fluid.getAmount());
		NBTHelper.writeEnum(fluid.getOrCreateTag(), "Bottle", bottleTypeFromItem);
		return fluid;
	}

	public static BottleType bottleTypeFromItem(ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.LINGERING_POTION)
			return BottleType.LINGERING;
		if (item == Items.SPLASH_POTION)
			return BottleType.SPLASH;
		return BottleType.REGULAR;
	}

	public static ItemConvertible itemFromBottleType(BottleType type) {
		switch (type) {
		case LINGERING:
			return Items.LINGERING_POTION;
		case SPLASH:
			return Items.SPLASH_POTION;
		case REGULAR:
		default:
			return Items.POTION;
		}
	}

	public static int getRequiredAmountForFilledBottle(ItemStack stack, FluidStack availableFluid) {
		return 250;
	}

	public static ItemStack fillBottle(ItemStack stack, FluidStack availableFluid) {
		CompoundTag tag = availableFluid.getOrCreateTag();
		ItemStack potionStack = new ItemStack(itemFromBottleType(NBTHelper.readEnum(tag, "Bottle", BottleType.class)));
		PotionUtil.setPotion(potionStack, PotionUtil.getPotion(tag));
		PotionUtil.setCustomPotionEffects(potionStack, PotionUtil.getCustomPotionEffects(tag));
		return potionStack;
	}

	public static Text getPotionName(FluidStack fs) {
		CompoundTag tag = fs.getOrCreateTag();
		ItemConvertible itemFromBottleType = itemFromBottleType(NBTHelper.readEnum(tag, "Bottle", BottleType.class));
		return new TranslatableText(PotionUtil.getPotion(tag)
			.finishTranslationKey(itemFromBottleType.asItem()
				.getTranslationKey() + ".effect."));
	}

	// Modified version of PotionUtils#addPotionTooltip
	@Environment(EnvType.CLIENT)
	public static void addPotionTooltip(FluidStack fs, List<Text> tooltip, float p_185182_2_) {
		List<StatusEffectInstance> list = PotionUtil.getPotionEffects(fs.getOrCreateTag());
		List<net.minecraft.util.Pair<String, EntityAttributeModifier>> list1 = Lists.newArrayList();
		if (list.isEmpty()) {
			tooltip.add((new TranslatableText("effect.none")).formatted(Formatting.GRAY));
		} else {
			for (StatusEffectInstance effectinstance : list) {
				TranslatableText textcomponent = new TranslatableText(effectinstance.getTranslationKey());
				StatusEffect effect = effectinstance.getEffectType();
				Map<EntityAttribute, EntityAttributeModifier> map = effect.getAttributeModifiers();
				if (!map.isEmpty()) {
					for (Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
						EntityAttributeModifier attributemodifier = entry.getValue();
						EntityAttributeModifier attributemodifier1 = new EntityAttributeModifier(attributemodifier.getName(),
							effect.adjustModifierAmount(effectinstance.getAmplifier(), attributemodifier),
							attributemodifier.getOperation());
						list1.add(new net.minecraft.util.Pair<>(
							entry.getKey().getTranslationKey(),
							attributemodifier1));
					}
				}

				if (effectinstance.getAmplifier() > 0) {
					textcomponent.append(" ")
						.append(new TranslatableText("potion.potency." + effectinstance.getAmplifier()).getString());
				}

				if (effectinstance.getDuration() > 20) {
					textcomponent.append(" (")
						.append(StatusEffectUtil.durationToString(effectinstance, p_185182_2_))
						.append(")");
				}

				tooltip.add(textcomponent.formatted(effect.getType()
					.getFormatting()));
			}
		}

		if (!list1.isEmpty()) {
			tooltip.add(new LiteralText(""));
			tooltip.add((new TranslatableText("potion.whenDrank")).formatted(Formatting.DARK_PURPLE));

			for (net.minecraft.util.Pair<String, EntityAttributeModifier> tuple : list1) {
				EntityAttributeModifier attributemodifier2 = tuple.getRight();
				double d0 = attributemodifier2.getValue();
				double d1;
				if (attributemodifier2.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE
					&& attributemodifier2.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
					d1 = attributemodifier2.getValue();
				} else {
					d1 = attributemodifier2.getValue() * 100.0D;
				}

				if (d0 > 0.0D) {
					tooltip.add((new TranslatableText(
						"attribute.modifier.plus." + attributemodifier2.getOperation()
							.getId(),
						ItemStack.MODIFIER_FORMAT.format(d1),
						new TranslatableText(tuple.getLeft())))
							.formatted(Formatting.BLUE));
				} else if (d0 < 0.0D) {
					d1 = d1 * -1.0D;
					tooltip.add((new TranslatableText(
						"attribute.modifier.take." + attributemodifier2.getOperation()
							.getId(),
						ItemStack.MODIFIER_FORMAT.format(d1),
						new TranslatableText(tuple.getLeft())))
							.formatted(Formatting.RED));
				}
			}
		}

	}

}
