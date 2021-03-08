package com.simibubi.create.content.contraptions.particle;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CubeParticle extends Particle {

	public static final Vec3d[] CUBE = {
		// TOP
		new Vec3d(1, 1, -1), new Vec3d(1, 1, 1), new Vec3d(-1, 1, 1), new Vec3d(-1, 1, -1),

		// BOTTOM
		new Vec3d(-1, -1, -1), new Vec3d(-1, -1, 1), new Vec3d(1, -1, 1), new Vec3d(1, -1, -1),

		// FRONT
		new Vec3d(-1, -1, 1), new Vec3d(-1, 1, 1), new Vec3d(1, 1, 1), new Vec3d(1, -1, 1),

		// BACK
		new Vec3d(1, -1, -1), new Vec3d(1, 1, -1), new Vec3d(-1, 1, -1), new Vec3d(-1, -1, -1),

		// LEFT
		new Vec3d(-1, -1, -1), new Vec3d(-1, 1, -1), new Vec3d(-1, 1, 1), new Vec3d(-1, -1, 1),

		// RIGHT
		new Vec3d(1, -1, 1), new Vec3d(1, 1, 1), new Vec3d(1, 1, -1), new Vec3d(1, -1, -1) };

	public static final Vec3d[] CUBE_NORMALS = {
		// modified normals for the sides
		new Vec3d(0, 1, 0), new Vec3d(0, -1, 0), new Vec3d(0, 0, 1), new Vec3d(0, 0, 1), new Vec3d(0, 0, 1),
		new Vec3d(0, 0, 1),

		/*
		 * new Vector3d(0, 1, 0), new Vector3d(0, -1, 0), new Vector3d(0, 0, 1), new Vector3d(0, 0,
		 * -1), new Vector3d(-1, 0, 0), new Vector3d(1, 0, 0)
		 */
	};

	private static final ParticleTextureSheet renderType = new ParticleTextureSheet() {
		@Override
		public void begin(BufferBuilder builder, TextureManager textureManager) {
			RenderSystem.disableTexture();

			// transparent, additive blending
			RenderSystem.depthMask(false);
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
			RenderSystem.enableLighting();
			RenderSystem.enableColorMaterial();

			// opaque
//			RenderSystem.depthMask(true);
//			RenderSystem.disableBlend();
//			RenderSystem.enableLighting();

			builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		}

		@Override
		public void draw(Tessellator tessellator) {
			tessellator.draw();
			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA,
				GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.disableLighting();
			RenderSystem.enableTexture();
		}
	};

	protected float scale;
	protected boolean hot;

	public CubeParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ) {
		super(world, x, y, z);
		this.velocityX = motionX;
		this.velocityY = motionY;
		this.velocityZ = motionZ;

		setScale(0.2F);
	}

	public void setScale(float scale) {
		this.scale = scale;
		this.setBoundingBoxSpacing(scale * 0.5f, scale * 0.5f);
	}

	public void averageAge(int age) {
		this.maxAge = (int) (age + (random.nextDouble() * 2D - 1D) * 8);
	}
	
	public void setHot(boolean hot) {
		this.hot = hot;
	}
	
	private boolean billowing = false;
	
	@Override
	public void tick() {
		if (this.hot && this.age > 0) {
			if (this.prevPosY == this.y) {
				billowing = true;
				field_21507 = false; // Prevent motion being ignored due to vertical collision
				if (this.velocityX == 0 && this.velocityZ == 0) {
					Vec3d diff = Vec3d.of(new BlockPos(x, y, z)).add(0.5, 0.5, 0.5).subtract(x, y, z);
					this.velocityX = -diff.x * 0.1;
					this.velocityZ = -diff.z * 0.1;
				}
				this.velocityX *= 1.1;
				this.velocityY *= 0.9;
				this.velocityZ *= 1.1;
			} else if (billowing) {
				this.velocityY *= 1.2;
			}
		}
		super.tick();
	}

	@Override
	public void buildGeometry(VertexConsumer builder, Camera renderInfo, float p_225606_3_) {
		Vec3d projectedView = renderInfo.getPos();
		float lerpedX = (float) (MathHelper.lerp(p_225606_3_, this.prevPosX, this.x) - projectedView.getX());
		float lerpedY = (float) (MathHelper.lerp(p_225606_3_, this.prevPosY, this.y) - projectedView.getY());
		float lerpedZ = (float) (MathHelper.lerp(p_225606_3_, this.prevPosZ, this.z) - projectedView.getZ());

		// int light = getBrightnessForRender(p_225606_3_);
		int light = 15728880;// 15<<20 && 15<<4
		double ageMultiplier = 1 - Math.pow(age, 3) / Math.pow(maxAge, 3);

		for (int i = 0; i < 6; i++) {
			// 6 faces to a cube
			for (int j = 0; j < 4; j++) {
				Vec3d vec = CUBE[i * 4 + j];
				vec = vec
					/* .rotate(?) */
					.multiply(scale * ageMultiplier)
					.add(lerpedX, lerpedY, lerpedZ);

				Vec3d normal = CUBE_NORMALS[i];
				builder.vertex(vec.x, vec.y, vec.z)
					.color(colorRed, colorGreen, colorBlue, colorAlpha)
					.texture(0, 0)
					.light(light)
					.normal((float) normal.x, (float) normal.y, (float) normal.z)
					.next();
			}
		}
	}

	@Override
	public ParticleTextureSheet getType() {
		return renderType;
	}

	public static class Factory implements ParticleFactory<CubeParticleData> {

		public Factory() {}

		@Override
		public Particle makeParticle(CubeParticleData data, ClientWorld world, double x, double y, double z, double motionX,
			double motionY, double motionZ) {
			CubeParticle particle = new CubeParticle(world, x, y, z, motionX, motionY, motionZ);
			particle.setColor(data.r, data.g, data.b);
			particle.setScale(data.scale);
			particle.averageAge(data.avgAge);
			particle.setHot(data.hot);
			return particle;
		}
	}
}
