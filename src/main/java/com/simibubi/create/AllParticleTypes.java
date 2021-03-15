package com.simibubi.create;

import com.simibubi.create.content.contraptions.particle.AirFlowParticleData;
import com.simibubi.create.content.contraptions.particle.CubeParticleData;
import com.simibubi.create.content.contraptions.particle.CustomParticleData;
import com.simibubi.create.content.contraptions.particle.RotationIndicatorParticleData;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

import java.util.function.Supplier;

public enum AllParticleTypes {

	ROTATION_INDICATOR(RotationIndicatorParticleData::new),
	AIR_FLOW(AirFlowParticleData::new),
	/*AIR(AirParticleData::new),
	HEATER_PARTICLE(HeaterParticleData::new),*/
	CUBE(CubeParticleData::new),
	/*FLUID_PARTICLE(FluidParticleData::new),
	BASIN_FLUID(FluidParticleData::new),
	FLUID_DRIP(FluidParticleData::new)*/

	;

	/**private ParticleEntry<?> entry;*/

	<D extends ParticleEffect> AllParticleTypes(Supplier<? extends CustomParticleData<D>> typeFactory) {
		String asId = Lang.asId(this.name());
		//entry = new ParticleEntry<>(new Identifier(Create.ID, asId), typeFactory);
	}

	/*public static void register(RegistryEvent.Register<ParticleType<?>> event) {
		for (AllParticleTypes particle : values())
			particle.entry.register(event.getRegistry());
	}

	@Environment(EnvType.CLIENT)
	public static void registerFactories(ParticleFactoryRegisterEvent event) {
		ParticleManager particles = MinecraftClient.getInstance().particleManager;
		for (AllParticleTypes particle : values())
			particle.entry.registerFactory(particles);
	}*/

	public ParticleType<?> get() {
		return null; /**entry.getOrCreateType()*/
	}

	public String parameter() {
		return Lang.asId(name());
	}

	/*private class ParticleEntry<D extends ParticleEffect> {
		Supplier<? extends ICustomParticleData<D>> typeFactory;
		ParticleType<D> type;
		Identifier id;

		public ParticleEntry(Identifier id, Supplier<? extends ICustomParticleData<D>> typeFactory) {
			this.id = id;
			this.typeFactory = typeFactory;
		}

		void register(IForgeRegistry<ParticleType<?>> registry) {
			registry.register(getOrCreateType());
		}

		ParticleType<D> getOrCreateType() {
			if (type != null)
				return type;
			type = typeFactory.get()
				.createType();
			type.setRegistryName(id);
			return type;
		}

		@Environment(EnvType.CLIENT)
		void registerFactory(ParticleManager particles) {
			typeFactory.get()
				.register(getOrCreateType(), particles);
		}

	}*/

}
