package com.simibubi.create.content.contraptions.components.structureMovement;

import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.util.Formatting.GRAY;

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
