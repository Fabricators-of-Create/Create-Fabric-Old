package com.simibubi.create.foundation.item;

import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GRAY;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import com.google.common.base.Strings;
import com.mojang.bridge.game.Language;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.components.flywheel.engine.EngineBlock;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.curiosities.tools.AllToolTiers;
import com.simibubi.create.foundation.item.ItemDescription.Palette;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraftforge.client.MinecraftForgeClient;

public class TooltipHelper {

	public static final int maxWidthPerLine = 200;
	public static final Map<String, ItemDescription> cachedTooltips = new HashMap<>();
	public static Language cachedLanguage;
	private static boolean gogglesMode;
	private static final Map<Item, Supplier<String>> tooltipReferrals = new HashMap<>();

	public static MutableText holdShift(Palette color, boolean highlighted) {
		Formatting colorFormat = highlighted ? color.hColor : color.color;
		return Lang.translate("tooltip.holdKey", Lang.translate("tooltip.keyShift")
			.formatted(colorFormat)).formatted(Formatting.DARK_GRAY);
	}

	public static void addHint(List<Text> tooltip, String hintKey, Object... messageParams) {
		Text spacing = IHaveGoggleInformation.componentSpacing;
		tooltip.add(spacing.copy().append(Lang.translate(hintKey + ".title")).formatted(GOLD));
		Text hint = Lang.translate(hintKey);
		List<Text> cutComponent = TooltipHelper.cutTextComponent(hint, GRAY, Formatting.WHITE);
		for (Text component : cutComponent) tooltip.add(spacing.copy().append(component));
	}
	
	public static void referTo(ItemConvertible item, Supplier<? extends ItemConvertible> itemWithTooltip) {
		tooltipReferrals.put(item.asItem(), () -> itemWithTooltip.get()
			.asItem()
			.getTranslationKey());
	}

	public static void referTo(ItemConvertible item, String string) {
		tooltipReferrals.put(item.asItem(), () -> string);
	}

	@Deprecated
	public static List<String> cutString(Text s, Formatting defaultColor, Formatting highlightColor) {
		return cutString(s.asString(), defaultColor, highlightColor, 0);
	}

	@Deprecated
	public static List<String> cutString(String s, Formatting defaultColor, Formatting highlightColor,
		int indent) {
		// Apply markup
		String markedUp = s.replaceAll("_([^_]+)_", highlightColor + "$1" + defaultColor);

		// Split words
		List<String> words = new LinkedList<>();
		BreakIterator iterator = BreakIterator.getLineInstance(MinecraftForgeClient.getLocale());
		iterator.setText(markedUp);
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			String word = markedUp.substring(start, end);
			words.add(word);
		}

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
//			format = TextFormatting.getFormatString(formattedLine);
		}
		return formattedLines;
	}

	public static List<Text> cutStringTextComponent(String c, Formatting defaultColor,
		Formatting highlightColor) {
		return cutTextComponent(new LiteralText(c), defaultColor, highlightColor, 0);
	}

	public static List<Text> cutTextComponent(Text c, Formatting defaultColor,
		Formatting highlightColor) {
		return cutTextComponent(c, defaultColor, highlightColor, 0);
	}

	public static List<Text> cutStringTextComponent(String c, Formatting defaultColor,
		Formatting highlightColor, int indent) {
		return cutTextComponent(new LiteralText(c), defaultColor, highlightColor, indent);
	}

	public static List<Text> cutTextComponent(Text c, Formatting defaultColor,
		Formatting highlightColor, int indent) {
		String s = getUnformattedDeepText(c);
		
		// Apply markup
		String markedUp = s;//.replaceAll("_([^_]+)_", highlightColor + "$1" + defaultColor);

		// Split words
		List<String> words = new LinkedList<>();
		BreakIterator iterator = BreakIterator.getLineInstance(MinecraftForgeClient.getLocale());
		iterator.setText(markedUp);
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			String word = markedUp.substring(start, end);
			words.add(word);
		}

		// Apply hard wrap
		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		List<String> lines = new LinkedList<>();
		StringBuilder currentLine = new StringBuilder();
		int width = 0;
		for (String word : words) {
			int newWidth = font.getWidth(word.replaceAll("_", ""));
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
		MutableText lineStart = new LiteralText(Strings.repeat(" ", indent));
		lineStart.formatted(defaultColor);
		List<Text> formattedLines = new ArrayList<>(lines.size());
		Couple<Formatting> f = Couple.create(highlightColor, defaultColor);

		boolean currentlyHighlighted = false;
		for (String string : lines) {
			MutableText currentComponent = lineStart.copy();
			String[] split = string.split("_");
			for (String part : split) {
				currentComponent.append(new LiteralText(part).formatted(f.get(currentlyHighlighted)));
				currentlyHighlighted = !currentlyHighlighted;
			}
			
			formattedLines.add(currentComponent);
			currentlyHighlighted = !currentlyHighlighted;
		}
		
		
		return formattedLines;
	}
	
//	public static List<ITextComponent> cutTextComponentOld(ITextComponent c, TextFormatting defaultColor,
//		TextFormatting highlightColor, int indent) {
//		IFormattableTextComponent lineStart = StringTextComponent.EMPTY.copy();
//		for (int i = 0; i < indent; i++)
//			lineStart.append(" ");
//		lineStart.formatted(defaultColor);
//
//		List<ITextComponent> lines = new ArrayList<>();
//		String rawText = getUnformattedDeepText(c);
//		String[] words = rawText.split(" ");
//		String word;
//		IFormattableTextComponent currentLine = lineStart.copy();
//
//		boolean firstWord = true;
//		boolean lastWord;
//
//		// Apply hard wrap
//		for (int i = 0; i < words.length; i++) {
//			word = words[i];
//			lastWord = i == words.length - 1;
//
//			if (!lastWord && !firstWord && getComponentLength(currentLine) + word.length() > maxCharsPerLine) {
//				lines.add(currentLine);
//				currentLine = lineStart.copy();
//				firstWord = true;
//			}
//
//			currentLine.append(new StringTextComponent((firstWord ? "" : " ") + word.replace("_", ""))
//				.formatted(word.matches("_([^_]+)_") ? highlightColor : defaultColor));
//			firstWord = false;
//		}
//
//		if (!firstWord) {
//			lines.add(currentLine);
//		}
//
//		return lines;
//	}

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

		boolean hasGlasses = AllItems.GOGGLES.isIn(player.getEquippedStack(EquipmentSlot.HEAD));

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
		AllSections module = AllSections.of(stack);
		if (I18n.translate(translationKey)
			.equals("WIP"))
			return new WipScription(module.getTooltipPalette());

		ItemDescription tooltip = new ItemDescription(module.getTooltipPalette());
		String summaryKey = translationKey + ".summary";

		// Summary
		if (I18n.hasTranslation(summaryKey))
			tooltip = tooltip.withSummary(new LiteralText(I18n.translate(summaryKey)));

		// Requirements
		if (stack.getItem() instanceof BlockItem) {
			BlockItem item = (BlockItem) stack.getItem();
			if (item.getBlock() instanceof IRotate || item.getBlock() instanceof EngineBlock) {
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

	private static int getComponentLength(Text component) {
		AtomicInteger l = new AtomicInteger();
		TextVisitFactory.visitFormatted(component, Style.EMPTY, (s, style, charConsumer) -> {
			l.getAndIncrement();
			return true;
		});
		return l.get();
	}

	public static String getUnformattedDeepText(Text component) {
		StringBuilder b = new StringBuilder();
		b.append(component.getString());
		component.getSiblings()
			.forEach(c -> b.append(getUnformattedDeepText(c)));
		return b.toString();
	}

}
