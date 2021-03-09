package com.simibubi.create.foundation.item;

import com.google.common.base.Strings;
import com.mojang.bridge.game.Language;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.curiosities.tools.AllToolTiers;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Formatting;

import java.text.BreakIterator;
import java.util.*;
import java.util.function.Supplier;

import static net.minecraft.util.Formatting.*;

public class TooltipHelper {

	public static final int maxWidthPerLine = 200;
	public static final Map<String, ItemDescription> cachedTooltips = new HashMap<>();
	public static Language cachedLanguage;
	private static boolean gogglesMode;
	private static final Map<Item, Supplier<String>> tooltipReferrals = new HashMap<>();

	public static String holdShift(ItemDescription.Palette color, boolean highlighted) {
		Formatting colorFormat = highlighted ? color.hColor : color.color;
		return DARK_GRAY
			+ Lang.translate("tooltip.holdKey", colorFormat + Lang.translate("tooltip.keyShift") + DARK_GRAY);
	}

	public static void addHint(List<String> tooltip, String hintKey, Object... messageParams) {
		String spacing = IHaveGoggleInformation.spacing;
		tooltip.add(spacing + GOLD + Lang.translate(hintKey + ".title"));
		String hint = Lang.translate(hintKey);
		List<String> cutString = TooltipHelper.cutString(spacing + hint, GRAY, WHITE);
		for (int i = 0; i < cutString.size(); i++)
			tooltip.add((i == 0 ? "" : spacing) + cutString.get(i));
	}
	
	public static void referTo(ItemConvertible item, Supplier<? extends ItemConvertible> itemWithTooltip) {
		tooltipReferrals.put(item.asItem(), () -> itemWithTooltip.get()
			.asItem()
			.getTranslationKey());
	}
	
	public static void referTo(ItemConvertible item, String string) {
		tooltipReferrals.put(item.asItem(), () -> string);
	}

	public static List<String> cutString(String s, Formatting defaultColor, Formatting highlightColor) {
		return cutString(s, defaultColor, highlightColor, 0);
	}

	public static List<String> cutString(String s, Formatting defaultColor, Formatting highlightColor,
										 int indent) {
		// Apply markup
		String markedUp = s.replaceAll("_([^_]+)_", highlightColor + "$1" + defaultColor);

		String localeStr = MinecraftClient.getInstance().getLanguageManager().getLanguage().getCode();
		Locale locale =  Locale.forLanguageTag(localeStr.replace("_", "-"));

		// Split words
		List<String> words = new LinkedList<>();
		BreakIterator iterator = BreakIterator.getLineInstance(locale);
		iterator.setText(markedUp);
		int start = iterator.first();

		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			String word = markedUp.substring(start, end);
			words.add(word);
		}

		System.out.println(words.size());

		// Apply hard wrap
		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		List<String> lines = new LinkedList<>();
		StringBuilder currentLine = new StringBuilder();
		int width = 0;
		for (String word : words) {
			int newWidth = font.getWidth(word);
			if (width + newWidth > maxWidthPerLine) {
				if (width > 0) {
					String line = currentLine.toString();
					lines.add(line);
					currentLine = new StringBuilder();
					width = 0;
				} else {
					lines.add(word);
					continue;
				}
			}
			currentLine.append(word);
			width += newWidth;
		}
		if (width > 0) {
			lines.add(currentLine.toString());
		}

		// Format
		String lineStart = Strings.repeat(" ", indent);
		List<String> formattedLines = new ArrayList<>(lines.size());
		String format = defaultColor.toString();
		for (String line : lines) {
			String formattedLine = format + lineStart + line;
			formattedLines.add(formattedLine);
			format = Formatting.strip(formattedLine); // TODO COULD BE WRONG
		}
		return formattedLines;
	}

	private static void checkLocale() {
		Language currentLanguage = MinecraftClient.getInstance()
			.getLanguageManager()
			.getLanguage();
		if (cachedLanguage != currentLanguage) {
			cachedTooltips.clear();
			cachedLanguage = currentLanguage;
		}
	}

	public static boolean hasTooltip(ItemStack stack, PlayerEntity player) {
		checkLocale();

		boolean hasGlasses = AllItems.GOGGLES.isIn(player.getEquippedStack(EquipmentSlot.HEAD).getItem().getGroup());

		if (hasGlasses != gogglesMode) {
			gogglesMode = hasGlasses;
			cachedTooltips.clear();
		}

		String key = getTooltipTranslationKey(stack);
		if (cachedTooltips.containsKey(key))
			return cachedTooltips.get(key) != ItemDescription.MISSING;
		return findTooltip(stack);
	}

	public static ItemDescription getTooltip(ItemStack stack) {
		checkLocale();
		String key = getTooltipTranslationKey(stack);
		if (cachedTooltips.containsKey(key)) {
			ItemDescription itemDescription = cachedTooltips.get(key);
			if (itemDescription != ItemDescription.MISSING)
				return itemDescription;
		}
		return null;
	}

	private static boolean findTooltip(ItemStack stack) {
		String key = getTooltipTranslationKey(stack);
		if (I18n.hasTranslation(key)) {
			cachedTooltips.put(key, buildToolTip(key, stack));
			return true;
		}
		cachedTooltips.put(key, ItemDescription.MISSING);
		return false;
	}

	private static ItemDescription buildToolTip(String translationKey, ItemStack stack) {
		/**AllSections module = AllSections.of(stack); TODO FIX THIS WHOLE FUNCTION
		if (I18n.translate(translationKey)
			.equals("WIP"))
			return new WipScription(module.getTooltipPalette());*/

		ItemDescription tooltip = new ItemDescription(null); /**new ItemDescription(module.getTooltipPalette());*/
		String summaryKey = translationKey + ".summary";

		// Summary
		if (I18n.hasTranslation(summaryKey))
			tooltip = tooltip.withSummary(I18n.translate(summaryKey));

		// Requirements
		if (stack.getItem() instanceof BlockItem) {
			BlockItem item = (BlockItem) stack.getItem();
			if (item.getBlock() instanceof IRotate /**|| item.getBlock() instanceof EngineBlock*/) { // TODO EngineBlock CHECK
				tooltip = tooltip.withKineticStats(item.getBlock());
			}
		}

		// Behaviours
		for (int i = 1; i < 100; i++) {
			String conditionKey = translationKey + ".condition" + i;
			String behaviourKey = translationKey + ".behaviour" + i;
			if (!I18n.hasTranslation(conditionKey))
				break;
			tooltip.withBehaviour(I18n.translate(conditionKey), I18n.translate(behaviourKey));
		}

		// Controls
		for (int i = 1; i < 100; i++) {
			String controlKey = translationKey + ".control" + i;
			String actionKey = translationKey + ".action" + i;
			if (!I18n.hasTranslation(controlKey))
				break;
			tooltip.withControl(I18n.translate(controlKey), I18n.translate(actionKey));
		}

		return tooltip.createTabs();
	}

	public static String getTooltipTranslationKey(ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ToolItem) {
			ToolItem tieredItem = (ToolItem) item;
			if (tieredItem.getMaterial() instanceof AllToolTiers) {
				AllToolTiers allToolTiers = (AllToolTiers) tieredItem.getMaterial();
				return "tool.create." + Lang.asId(allToolTiers.name()) + ".tooltip";
			}
		}

		if (tooltipReferrals.containsKey(item))
			return tooltipReferrals.get(item).get() + ".tooltip";
		return item.getTranslationKey(stack) + ".tooltip";
	}

}
