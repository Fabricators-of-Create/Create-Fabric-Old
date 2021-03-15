package com.simibubi.create.foundation.utility.extensions;

import com.simibubi.create.foundation.utility.MixinHelper;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public final class ParticleManagerUtils {
	private ParticleManagerUtils() {}

	public static <T extends ParticleEffect> void registerFactory(ParticleManager $this, ParticleType<T> type, ParticleManager.SpriteAwareFactory<T> factory) {
		get($this).create$registerFactory0(type, factory);
	}

	public static <T extends ParticleEffect> void registerFactory(ParticleManager $this, ParticleType<T> type, ParticleFactory<T> factory) {
		get($this).create$registerFactory1(type, factory);
	}

	private static ParticleManagerExtensions get(ParticleManager manager) {
		return MixinHelper.cast(manager);
	}
}
