package com.simibubi.create.content.contraptions.particle;

import com.simibubi.create.content.contraptions.goggles.GogglesItem;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.ColorHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public class RotationIndicatorParticle extends AnimatedParticle {

	protected float radius;
	protected float radius1;
	protected float radius2;
	protected float speed;
	protected Axis axis;
	protected Vec3d origin;
	protected Vec3d offset;
	protected boolean isVisible;

	private RotationIndicatorParticle(ClientWorld world, double x, double y, double z, int color, float radius1,
									  float radius2, float speed, Axis axis, int lifeSpan, boolean isVisible, SpriteProvider sprite) {
		super(world, x, y, z, sprite, 0);
		this.velocityX = 0;
		this.velocityY = 0;
		this.velocityZ = 0;
		this.origin = new Vec3d(x, y, z);
		this.scale *= 0.75F;
		this.maxAge = lifeSpan + this.random.nextInt(32);
		this.setTargetColor(color);
		this.setColor(ColorHelper.mixColors(color, 0xFFFFFF, .5f));
		this.setSpriteForAge(sprite);
		this.radius1 = radius1;
		this.radius = radius1;
		this.radius2 = radius2;
		this.speed = speed;
		this.axis = axis;
		this.isVisible = isVisible;
		this.offset = axis.isHorizontal() ? new Vec3d(0, 1, 0) : new Vec3d(1, 0, 0);
		move(0, 0, 0);
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
	}

	@Override
	public void tick() {
		super.tick();
		radius += (radius2 - radius) * .1f;
	}
	
	@Override
	public void buildGeometry(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		if (!isVisible)
			return;
		super.buildGeometry(buffer, renderInfo, partialTicks);
	}

	public void move(double x, double y, double z) {
		float time = AnimationTickHolder.getTicks();
		float angle = (float) ((time * speed) % 360) - (speed / 2 * age * (((float) age) / maxAge));
		Vec3d position = VecHelper.rotate(this.offset.multiply(radius), angle, axis).add(origin);
		x = position.x;
		y = position.y;
		z = position.z;
	}

	public static class Factory implements ParticleFactory<RotationIndicatorParticleData> {
		private final SpriteProvider spriteSet;

		public Factory(SpriteProvider animatedSprite) {
			this.spriteSet = animatedSprite;
		}

		public Particle makeParticle(RotationIndicatorParticleData data, ClientWorld worldIn, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed) {
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			boolean visible = player != null && GogglesItem.canSeeParticles(player);
			return new RotationIndicatorParticle(worldIn, x, y, z, data.color, data.radius1, data.radius2, data.speed,
					data.getAxis(), data.lifeSpan, visible, this.spriteSet);
		}
	}

}
