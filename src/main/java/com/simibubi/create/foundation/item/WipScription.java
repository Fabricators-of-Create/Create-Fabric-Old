package com.simibubi.create.foundation.item;

import java.awt.*;
import java.util.List;
import java.util.Random;

import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class WipScription extends ItemDescription {

	public WipScription(Palette palette) {
		super(palette);
		add(getLines(), Formatting.RED + Lang.translate("tooltip.workInProgress"));

		int descriptions = 0;
		while (I18n.hasTranslation("create.tooltip.randomWipDescription" + descriptions++))
			;

		if (--descriptions > 0) {
			int index = new Random().nextInt(descriptions);
			String translate = Lang.translate("tooltip.randomWipDescription" + index);
			add(getLines(), TooltipHelper.cutString(translate, Formatting.DARK_RED, Formatting.DARK_RED));
		}
	}
	
	@Override
	public List<LiteralText> addInformation(List<LiteralText> tooltip) {
		tooltip.set(0, new LiteralText(decorateName(tooltip.get(0).toString())));
		tooltip.addAll(getLines());
		return tooltip;
	}

	public static String decorateName(String name) {
		return Formatting.GRAY + "" + Formatting.STRIKETHROUGH + name + Formatting.GOLD + " "
				+ Lang.translate("tooltip.wip");
	}

}
