package com.simibubi.create.foundation.item;

import java.util.List;
import java.util.Random;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.simibubi.create.foundation.utility.Lang;

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
	
	@Override
	public List<Text> addInformation(List<Text> tooltip) {
		tooltip.set(0, decorateName(tooltip.get(0)));
		tooltip.addAll(getLines());
		return tooltip;
	}

	public static Text decorateName(Text name) {
		return LiteralText.EMPTY.copy().append(name.copy().formatted(Formatting.GRAY, Formatting.STRIKETHROUGH)).append(" ").append(Lang.translate("tooltip.wip").formatted(Formatting.GOLD));
	}

}
