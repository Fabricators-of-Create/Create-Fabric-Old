package com.simibubi.create.content.contraptions.particle;

import java.util.Locale;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleManager.SpriteAwareFactory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AirFlowParticleData implements ParticleEffect, ICustomParticleDataWithSprite<AirFlowParticleData> {
	
	public static final Codec<AirFlowParticleData> CODEC = RecordCodecBuilder.create(i -> 
		i.group(
			Codec.INT.fieldOf("x").forGetter(p -> p.posX),
			Codec.INT.fieldOf("y").forGetter(p -> p.posY),
			Codec.INT.fieldOf("z").forGetter(p -> p.posZ))
		.apply(i, AirFlowParticleData::new));

	public static final ParticleEffect.Factory<AirFlowParticleData> DESERIALIZER = new ParticleEffect.Factory<AirFlowParticleData>() {
		public AirFlowParticleData read(ParticleType<AirFlowParticleData> particleTypeIn, StringReader reader)
				throws CommandSyntaxException {
			reader.expect(' ');
			int x = reader.readInt();
			reader.expect(' ');
			int y = reader.readInt();
			reader.expect(' ');
			int z = reader.readInt();
			return new AirFlowParticleData(x, y, z);
		}

		public AirFlowParticleData read(ParticleType<AirFlowParticleData> particleTypeIn, PacketByteBuf buffer) {
			return new AirFlowParticleData(buffer.readInt(), buffer.readInt(), buffer.readInt());
		}
	};

	final int posX;
	final int posY;
	final int posZ;

	public AirFlowParticleData(Vec3i pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}

	public AirFlowParticleData(int posX, int posY, int posZ) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	public AirFlowParticleData() {
		this(0, 0, 0);
	}

	@Override
	public ParticleType<?> getType() {
		return AllParticleTypes.AIR_FLOW.get();
	}

	@Override
	public void write(PacketByteBuf buffer) {
		buffer.writeInt(posX);
		buffer.writeInt(posY);
		buffer.writeInt(posZ);
	}

	@Override
	public String asString() {
		return String.format(Locale.ROOT, "%s %d %d %d", AllParticleTypes.AIR_FLOW.parameter(), posX, posY, posZ);
	}

	@Override
	public Factory<AirFlowParticleData> getDeserializer() {
		return DESERIALIZER;
	}
	
	@Override
	public Codec<AirFlowParticleData> getCodec(ParticleType<AirFlowParticleData> type) {
		return CODEC;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public SpriteAwareFactory<AirFlowParticleData> getMetaFactory() {
		return AirFlowParticle.Factory::new;
	}

}