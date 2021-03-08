package com.simibubi.create.content.contraptions.particle;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HeaterParticle extends AnimatedParticle {

	private final SpriteProvider animatedSprite;

	public HeaterParticle(ClientWorld worldIn, float r, float g, float b, double x, double y, double z, double vx, double vy,
						  double vz, SpriteProvider spriteSet) {
		super(worldIn, x, y, z, spriteSet, worldIn.random.nextFloat() * .5f);

		this.animatedSprite = spriteSet;

		this.velocityX = this.velocityX * (double) 0.01F + vx;
		this.velocityY = this.velocityY * (double) 0.01F + vy;
		this.velocityZ = this.velocityZ * (double) 0.01F + vz;

		this.colorRed = r;
		this.colorGreen = g;
		this.colorBlue = b;

		this.x += (this.random.nextFloat() - this.random.nextFloat()) * 0.05F;
		this.y += (this.random.nextFloat() - this.random.nextFloat()) * 0.05F;
		this.z += (this.random.nextFloat() - this.random.nextFloat()) * 0.05F;

		this.maxAge = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
		this.scale *= 1.875F;
		this.setSpriteForAge(animatedSprite);

	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_LIT;
	}

	@Override
	public float getSize(float p_217561_1_) {
		float f = ((float) this.age + p_217561_1_) / (float) this.maxAge;
		return this.scale * (1.0F - f * f * 0.5F);
	}

	@Override
	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox()
			.offset(x, y, z));
		this.repositionFromBoundingBox();
	}

	@Override
	public int getColorMultiplier(float p_189214_1_) {
		float f = ((float) this.age + p_189214_1_) / (float) this.maxAge;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		int i = super.getColorMultiplier(p_189214_1_);
		int j = i & 255;
		int k = i >> 16 & 255;
		j = j + (int) (f * 15.0F * 16.0F);
		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.setSpriteForAge(animatedSprite);
			this.move(this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= (double) 0.96F;
			this.velocityY *= (double) 0.96F;
			this.velocityZ *= (double) 0.96F;
			if (this.onGround) {
				this.velocityX *= (double) 0.7F;
				this.velocityZ *= (double) 0.7F;
			}
		}
	}

	public static class Factory implements ParticleFactory<HeaterParticleData> {
		private final SpriteProvider spriteSet;

		public Factory(SpriteProvider animatedSprite) {
			this.spriteSet = animatedSprite;
		}

		@Override
		public Particle makeParticle(HeaterParticleData data, ClientWorld worldIn, double x, double y, double z, double vx,
			double vy, double vz) {
			return new HeaterParticle(worldIn, data.r, data.g, data.b, x, y, z, vx, vy, vz, this.spriteSet);
		}
	}
}
