package com.simibubi.create.foundation.block.connected;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.block.render.SpriteShifter;
import net.minecraft.util.Identifier;

public class CTSpriteShifter extends SpriteShifter {

	public enum CTType {
		OMNIDIRECTIONAL, HORIZONTAL, VERTICAL, CROSS;
	}

	public static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
		return getCT(type, blockTextureName, blockTextureName);
	}

	public static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
		return getCT(type, new Identifier(Create.ID, "block/" + blockTextureName), connectedTextureName);
	}

	public static CTSpriteShiftEntry getCT(CTType type, Identifier blockTexture, String connectedTextureName) {
		String targetLocation = "block/" + connectedTextureName + "_connected";
		String key =
			type.name() + ":" + blockTexture.getNamespace() + ":" + blockTexture.getPath() + "->" + targetLocation;
		if (textures.containsKey(key))
			return (CTSpriteShiftEntry) textures.get(key);

		CTSpriteShiftEntry entry = create(type);
		Identifier targetTextureLocation = new Identifier(Create.ID, targetLocation);
		entry.set(blockTexture, targetTextureLocation);

		textures.put(key, entry);
		return entry;
	}

	private static CTSpriteShiftEntry create(CTType type) {
		switch (type) {
		case HORIZONTAL:
			return new CTSpriteShiftEntry.Horizontal();
		case OMNIDIRECTIONAL:
			return new CTSpriteShiftEntry.Omnidirectional();
		case VERTICAL:
			return new CTSpriteShiftEntry.Vertical();
		case CROSS:
			return new CTSpriteShiftEntry.Cross();
		default:
			return null;
		}
	}

}
