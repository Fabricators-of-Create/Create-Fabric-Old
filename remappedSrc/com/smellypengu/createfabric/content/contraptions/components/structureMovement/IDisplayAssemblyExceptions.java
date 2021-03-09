package com.smellypengu.createfabric.content.contraptions.components.structureMovement;

import static net.minecraft.util.Formatting.GRAY;

import java.util.Arrays;
import java.util.List;

import com.smellypengu.createfabric.content.contraptions.goggles.IHaveGoggleInformation;
import com.smellypengu.createfabric.foundation.item.TooltipHelper;
import com.smellypengu.createfabric.foundation.utility.Lang;
import net.minecraft.util.Formatting;

public interface IDisplayAssemblyExceptions {

	default boolean addExceptionToTooltip(List<String> tooltip) {
		AssemblyException e = getLastAssemblyException();
		if (e == null)
			return false;

		if (!tooltip.isEmpty())
			tooltip.add("");

		String spacing = IHaveGoggleInformation.spacing;
		tooltip.add(IHaveGoggleInformation.spacing + Formatting.GOLD + Lang.translate("gui.assembly.exception"));

		Arrays.stream(e.getFormattedText()
			.split("\n"))
			.forEach(l -> {
				List<String> cutString = TooltipHelper.cutString(spacing + l, GRAY, Formatting.WHITE);
				for (int i = 0; i < cutString.size(); i++)
					tooltip.add((i == 0 ? "" : spacing) + cutString.get(i));
			});

		return true;
	}

	AssemblyException getLastAssemblyException();
}
