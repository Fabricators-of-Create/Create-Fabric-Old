package com.simibubi.create.foundation.block.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.Identifier;
import com.simibubi.create.Create;

public class SpriteShifter {

	protected static Map<String, SpriteShiftEntry> textures = new HashMap<>();

	public static SpriteShiftEntry get(String originalLocation, String targetLocation) {
		String key = originalLocation + "->" + targetLocation;
		if (textures.containsKey(key))
			return textures.get(key);

		SpriteShiftEntry entry = new SpriteShiftEntry();
		entry.originalTextureLocation = new Identifier(Create.ID, originalLocation);
		entry.targetTextureLocation = new Identifier(Create.ID, targetLocation);
		textures.put(key, entry);
		return entry;
	}

	public static void reloadUVs() {
		textures.values().forEach(SpriteShiftEntry::loadTextures);
	}

	public static List<Identifier> getAllTargetSprites() {
		return textures.values().stream().map(SpriteShiftEntry::getTargetResourceLocation).collect(Collectors.toList());
	}

}
