package com.simibubi.create.content.contraptions.goggles;

import java.util.List;
import net.minecraft.text.Text;

/*
* Implement this Interface in the TileEntity class that wants to add info to the screen
* */
public interface IHaveHoveringInformation {

	default boolean addToTooltip(List<Text> tooltip, boolean isPlayerSneaking){
		return false;
	}

}
