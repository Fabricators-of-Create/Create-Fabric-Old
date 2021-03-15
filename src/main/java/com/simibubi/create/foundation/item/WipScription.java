package com.simibubi.create.foundation.item;

import java.util.List;
import java.util.Random;

import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WipScription extends ItemDescription {

	public WipScription(Palette palette) {
		super(palette);
		add(getLines(), Lang.translate("tooltip.workInProgress").formatted(Formatting.RED));

		int descriptions = 0;
		while (I18n.hasTranslation("create.tooltip.randomWipDescription" + descriptions++))
			;

		if (--descriptions > 0) {
			int index = new Random().nextInt(descriptions);
			Text translate = Lang.translate("tooltip.randomWipDescription" + index);
			List<Text> lines = getLines();
			lines.addAll(TooltipHelper.cutTextComponent(translate, Formatting.DARK_RED, Formatting.DARK_RED));
		}
	}

	public static String decorateName(String name) {
		return Formatting.GRAY + "" + Formatting.STRIKETHROUGH + name + Formatting.GOLD + " "
			+ Lang.translate("tooltip.wip");
	}

	@Override
	public List<Text> addInformation(List<Text> tooltip) {
		tooltip.set(0, new LiteralText(decorateName(tooltip.get(0).toString())));
		tooltip.addAll(getLines());
		return tooltip;
	}

}
