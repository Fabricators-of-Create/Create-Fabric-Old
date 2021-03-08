package com.simibubi.create.content.contraptions.components.structureMovement;

import java.util.Arrays;
import java.util.List;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Lang;

public interface IDisplayAssemblyExceptions {

	default boolean addExceptionToTooltip(List<Text> tooltip) {
		AssemblyException e = getLastAssemblyException();
		if (e == null)
			return false;

		if (!tooltip.isEmpty())
			tooltip.add(LiteralText.EMPTY);

		tooltip.add(IHaveGoggleInformation.componentSpacing.copy().append(Lang.translate("gui.assembly.exception").formatted(Formatting.GOLD)));
		String text = TooltipHelper.getUnformattedDeepText(e.component);
		Arrays.stream(text.split("\n")).forEach(l -> tooltip.add(IHaveGoggleInformation.componentSpacing.copy().append(new LiteralText(l).setStyle(e.component.getStyle()).formatted(Formatting.GRAY))));

		return true;
	}

	AssemblyException getLastAssemblyException();
}
