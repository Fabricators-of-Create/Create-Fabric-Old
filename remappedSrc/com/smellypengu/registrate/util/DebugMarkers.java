package com.smellypengu.registrate.util;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@SuppressWarnings("null")
public class DebugMarkers {

	private static final String PREFIX = "REGISTRATE.";
	public static final Marker REGISTER = marker("REGISTER");
	public static final Marker DATA = marker("DATA");

	private static final Marker marker(String name) {
		return MarkerManager.getMarker(PREFIX + name);
	}
}