package com.simibubi.create.foundation.block.render;

import java.util.function.Function;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class SpriteShiftEntry {
	protected Identifier originalTextureLocation;
	protected Identifier targetTextureLocation;
	protected Sprite original;
	protected Sprite target;

	public void set(Identifier originalTextureLocation, Identifier targetTextureLocation) {
		this.originalTextureLocation = originalTextureLocation;
		this.targetTextureLocation = targetTextureLocation;
	}

	protected void loadTextures() {
		Function<Identifier, Sprite> textureMap = MinecraftClient.getInstance()
			.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		original = textureMap.apply(originalTextureLocation);
		target = textureMap.apply(targetTextureLocation);
	}

	public Identifier getTargetResourceLocation() {
		return targetTextureLocation;
	}

	public Sprite getTarget() {
		if (target == null)
			loadTextures();
		return target;
	}

	public Sprite getOriginal() {
		if (original == null)
			loadTextures();
		return original;
	}
}