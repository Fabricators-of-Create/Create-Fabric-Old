package com.simibubi.create.content.contraptions.particle;

import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.utility.extensions.ParticleManagerUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleManager.SpriteAwareFactory;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleEffect.Factory;
import net.minecraft.particle.ParticleType;

public interface CustomParticleDataWithSprite<T extends ParticleEffect> extends CustomParticleData<T> {
	Factory<T> getDeserializer();

	default ParticleType<T> createType() {
		return new ParticleType<T>(false, getDeserializer()) {

			@Override
			public Codec<T> getCodec() {
				return CustomParticleDataWithSprite.this.getCodec(this);
			}
		};
	}

	@Override
	@Environment(EnvType.CLIENT)
	default ParticleFactory<T> getFactory() {
		throw new IllegalAccessError("This particle type uses a metaFactory!");
	}

	@Environment(EnvType.CLIENT)
	SpriteAwareFactory<T> getMetaFactory();

	@Override
	@Environment(EnvType.CLIENT)
	default void register(ParticleType<T> type, ParticleManager particles) {
		ParticleManagerUtils.registerFactory(particles, type, getMetaFactory());
	}
}
