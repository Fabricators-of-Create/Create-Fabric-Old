package com.simibubi.create.content.contraptions.particle;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AirParticle extends AnimatedParticle {

	private float originX, originY, originZ;
	private float targetX, targetY, targetZ;
	private float drag;

	private float twirlRadius, twirlAngleOffset;
	private Axis twirlAxis;

	protected AirParticle(ClientWorld world, AirParticleData data, double x, double y, double z, double dx, double dy,
						  double dz, SpriteProvider sprite) {
		super(world, x, y, z, sprite, world.random.nextFloat() * .5f);
		scale *= 0.75F;
		collidesWithWorld = false;

		setPos(x, y, z);
		originX = (float) (prevPosX = x);
		originY = (float) (prevPosY = y);
		originZ = (float) (prevPosZ = z);
		targetX = (float) (x + dx);
		targetY = (float) (y + dy);
		targetZ = (float) (z + dz);
		drag = data.drag;

		twirlRadius = Create.random.nextFloat() / 6;
		twirlAngleOffset = Create.random.nextFloat() * 360;
		twirlAxis = Create.random.nextBoolean() ? Axis.X : Axis.Z;

		// speed in m/ticks
		maxAge = Math.min((int) (new Vec3d(dx, dy, dz).length() / data.speed), 60);
		selectSprite(7);
		setColorAlpha(.25f);
	}

	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
			return;
		}

		float progress = (float) Math.pow(((float) age) / maxAge, drag);
		float angle = (progress * 2 * 360 + twirlAngleOffset) % 360;
		Vec3d twirl = VecHelper.rotate(new Vec3d(0, twirlRadius, 0), angle, twirlAxis);
		
		float x = (float) (MathHelper.lerp(progress, originX, targetX) + twirl.x);
		float y = (float) (MathHelper.lerp(progress, originY, targetY) + twirl.y);
		float z = (float) (MathHelper.lerp(progress, originZ, targetZ) + twirl.z);
		
		velocityX = x - x;
		velocityY = y - y;
		velocityZ = z - z;

		setSpriteForAge(spriteProvider);
		this.move(this.velocityX, this.velocityY, this.velocityZ);
	}

	public int getColorMultiplier(float partialTick) {
		BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
		return this.world.canSetBlock(blockpos) ? WorldRenderer.getLightmapCoordinates(world, blockpos) : 0;
	}

	private void selectSprite(int index) {
		setSprite(spriteProvider.getSprite(index, 8));
	}

	public static class Factory implements ParticleFactory<AirParticleData> {
		private final SpriteProvider spriteSet;

		public Factory(SpriteProvider animatedSprite) {
			this.spriteSet = animatedSprite;
		}

		public Particle makeParticle(AirParticleData data, ClientWorld worldIn, double x, double y, double z, double xSpeed,
			double ySpeed, double zSpeed) {
			return new AirParticle(worldIn, data, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

}
