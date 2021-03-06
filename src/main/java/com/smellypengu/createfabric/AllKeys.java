package com.smellypengu.createfabric;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;

public enum AllKeys {

	TOOL_MENU("toolmenu", GLFW.GLFW_KEY_LEFT_ALT), 
	ACTIVATE_TOOL("", GLFW.GLFW_KEY_LEFT_CONTROL),

	;

	private KeyBinding keybind;
	private String description;
	private int key;
	private boolean modifiable;

	private AllKeys(String description, int defaultKey) {
		this.description = Create.ID + ".keyinfo." + description;
		this.key = defaultKey;
		this.modifiable = !description.isEmpty();
	}

	public static void register() {
		for (AllKeys key : values()) {
			key.keybind = new KeyBinding(key.description, key.key, Create.ID);
			if (!key.modifiable)
				continue;

			//TODO: FIX THIS PLS | ClientRegistry.registerKeyBinding(key.keybind);
		}
	}

	public KeyBinding getKeybind() {
		return keybind;
	}

	public boolean isPressed() {
		if (!modifiable)
			return isKeyDown(key);
		return keybind.isPressed();
	}

	public String getBoundKey() {
		return keybind.getBoundKeyLocalizedText().toString().toUpperCase();
	}

	public int getBoundCode() {
		return keybind.getDefaultKey().getCode();
	}

	public static boolean isKeyDown(int key) {
		return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), key) != 0;
	}

	public static boolean ctrlDown() {
		return Screen.hasControlDown();
	}

	public static boolean shiftDown() {
		return Screen.hasShiftDown();
	}

	public static boolean altDown() {
		return Screen.hasAltDown();
	}

}
