package com.simibubi.create.content.contraptions.components.structureMovement;

import java.util.Arrays;
import java.util.List;

import com.simibubi.create.content.contraptions.goggles.GoggleInformationProvider;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public interface DisplayAssemblyExceptionsProvider {

	default boolean addExceptionToTooltip(List<Text> tooltip) {
		AssemblyException e = getLastAssemblyException();
		if (e == null)
			return false;

		if (!tooltip.isEmpty())
			tooltip.add(LiteralText.EMPTY);

		tooltip.add(GoggleInformationProvider.componentSpacing.copy().append(Lang.translate("gui.assembly.exception").formatted(Formatting.GOLD)));
		String text = TooltipHelper.getUnformattedDeepText(e.component);
		Arrays.stream(text.split("\n")).forEach(l -> tooltip.add(GoggleInformationProvider.componentSpacing.copy().append(new LiteralText(l).setStyle(e.component.getStyle()).formatted(Formatting.GRAY))));

		return true;
	}

	AssemblyException getLastAssemblyException();
}