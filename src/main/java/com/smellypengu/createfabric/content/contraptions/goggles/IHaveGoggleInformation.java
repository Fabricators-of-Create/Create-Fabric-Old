package com.smellypengu.createfabric.content.contraptions.goggles;

import java.text.DecimalFormat;
import java.util.List;

/*
 * Implement this Interface in the TileEntity class that wants to add info to the screen
 * */
public interface IHaveGoggleInformation {

    DecimalFormat decimalFormat = new DecimalFormat("#.##");
    String spacing = "    ";

    static String format(double d) {
        return decimalFormat.format(d);
    }

    /**
     * this method will be called when looking at a TileEntity that implemented this interface
     *
     * @return {@code true} if the tooltip creation was successful and should be displayed,
     * or {@code false} if the overlay should not be displayed
     */
    default boolean addToGoggleTooltip(List<String> tooltip, boolean isPlayerSneaking) {
        return false;
    }

}
