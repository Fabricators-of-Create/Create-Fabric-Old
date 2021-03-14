package com.simibubi.create.foundation.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.simibubi.create.foundation.utility.extensions.ParticleManagerExtensions;

import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

@Mixin(ParticleManager.class)
public abstract class ParticleManagerMixin implements ParticleManagerExtensions {
	@Shadow
	protected abstract <T extends ParticleEffect> void registerFactory(ParticleType<T> particleType, ParticleManager.SpriteAwareFactory<T> spriteAwareFactory);

	@Shadow
	protected abstract <T extends ParticleEffect> void registerFactory(ParticleType<T> type, ParticleFactory<T> factory);

	@Override
	public <T extends ParticleEffect> void create$registerFactory0(ParticleType<T> particleType, ParticleManager.SpriteAwareFactory<T> spriteAwareFactory) {
		registerFactory(particleType, spriteAwareFactory);
	}

	@Override
	public <T extends ParticleEffect> void create$registerFactory1(ParticleType<T> type, ParticleFactory<T> factory) {
		registerFactory(type, factory);
	}
}
