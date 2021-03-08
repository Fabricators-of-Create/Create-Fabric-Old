package com.simibubi.create.content.contraptions.particle;

import javax.annotation.Nonnull;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.components.fan.IAirCurrentSource;
import com.simibubi.create.content.logistics.InWorldProcessing;
import com.simibubi.create.foundation.utility.ColorHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AirFlowParticle extends AnimatedParticle {

	private final IAirCurrentSource source;

	protected AirFlowParticle(ClientWorld world, IAirCurrentSource source, double x, double y, double z,
							  SpriteProvider sprite) {
		super(world, x, y, z, sprite, world.random.nextFloat() * .5f);
		this.source = source;
		this.scale *= 0.75F;
		this.maxAge = 40;
		collidesWithWorld = false;
		selectSprite(7);
		Vec3d offset = VecHelper.offsetRandomly(Vec3d.ZERO, Create.random, .25f);
		this.setPos(x + offset.x, y + offset.y, z + offset.z);
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
		setColorAlpha(.25f);
	}

	@Nonnull
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		if (source == null || source.isSourceRemoved()) {
			dissipate();
			return;
		}
		this.prevPosX = this.x;
		this.prevPosY = this.y;
		this.prevPosZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			if (source.getAirCurrent() == null || !source.getAirCurrent().bounds.expand(.25f).contains(x, y, z)) {
				dissipate();
				return;
			}

			Vec3d directionVec = Vec3d.of(source.getAirCurrent().direction.getVector());
			Vec3d motion = directionVec.multiply(1 / 8f);
			if (!source.getAirCurrent().pushing)
				motion = motion.multiply(-1);

			double distance = new Vec3d(x, y, z).subtract(VecHelper.getCenterOf(source.getAirCurrentPos()))
					.multiply(directionVec).length() - .5f;
			if (distance > source.getAirCurrent().maxDistance + 1 || distance < -.25f) {
				dissipate();
				return;
			}
			motion = motion.multiply(source.getAirCurrent().maxDistance - (distance - 1f)).multiply(.5f);
			selectSprite((int) MathHelper.clamp((distance / source.getAirCurrent().maxDistance) * 8 + world.random.nextInt(4),
					0, 7));

			morphType(distance);

			velocityX = motion.x;
			velocityY = motion.y;
			velocityZ = motion.z;

			if (this.onGround) {
				this.velocityX *= 0.7;
				this.velocityZ *= 0.7;
			}
			this.move(this.velocityX, this.velocityY, this.velocityZ);

		}

	}

	public void morphType(double distance) {
		if(source.getAirCurrent() == null)
			return;
		InWorldProcessing.Type type = source.getAirCurrent().getSegmentAt((float) distance);

		if (type == InWorldProcessing.Type.SPLASHING) {
			setColor(ColorHelper.mixColors(0x4499FF, 0x2277FF, world.random.nextFloat()));
			setColorAlpha(1f);
			selectSprite(world.random.nextInt(3));
			if (world.random.nextFloat() < 1 / 32f)
				world.addParticle(ParticleTypes.BUBBLE, x, y, z, velocityX * .125f, velocityY * .125f,
						velocityZ * .125f);
			if (world.random.nextFloat() < 1 / 32f)
				world.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, velocityX * .125f, velocityY * .125f,
						velocityZ * .125f);
		}

		if (type == InWorldProcessing.Type.SMOKING) {
			setColor(ColorHelper.mixColors(0x0, 0x555555, world.random.nextFloat()));
			setColorAlpha(1f);
			selectSprite(world.random.nextInt(3));
			if (world.random.nextFloat() < 1 / 32f)
				world.addParticle(ParticleTypes.SMOKE, x, y, z, velocityX * .125f, velocityY * .125f,
						velocityZ * .125f);
			if (world.random.nextFloat() < 1 / 32f)
				world.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, velocityX * .125f, velocityY * .125f,
						velocityZ * .125f);
		}

		if (type == InWorldProcessing.Type.BLASTING) {
			setColor(ColorHelper.mixColors(0xFF4400, 0xFF8855, world.random.nextFloat()));
			setColorAlpha(.5f);
			selectSprite(world.random.nextInt(3));
			if (world.random.nextFloat() < 1 / 32f)
				world.addParticle(ParticleTypes.FLAME, x, y, z, velocityX * .25f, velocityY * .25f,
						velocityZ * .25f);
			if (world.random.nextFloat() < 1 / 16f)
				world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.LAVA.getDefaultState()), x, y,
						z, velocityX * .25f, velocityY * .25f, velocityZ * .25f);
		}

		if (type == null) {
			setColor(0xEEEEEE);
			setColorAlpha(.25f);
			setBoundingBoxSpacing(.2f, .2f);
		}
	}

	private void dissipate() {
		markDead();
	}

	public int getColorMultiplier(float partialTick) {
		BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
		return this.world.canSetBlock(blockpos) ? WorldRenderer.getLightmapCoordinates(world, blockpos) : 0;
	}

	private void selectSprite(int index) {
		setSprite(spriteProvider.getSprite(index, 8));
	}

	public static class Factory implements ParticleFactory<AirFlowParticleData> {
		private final SpriteProvider spriteSet;

		public Factory(SpriteProvider animatedSprite) {
			this.spriteSet = animatedSprite;
		}

		public Particle makeParticle(AirFlowParticleData data, ClientWorld worldIn, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed) {
			BlockEntity te = worldIn.getBlockEntity(new BlockPos(data.posX, data.posY, data.posZ));
			if (!(te instanceof IAirCurrentSource))
				te = null;
			return new AirFlowParticle(worldIn, (IAirCurrentSource) te, x, y, z, this.spriteSet);
		}
	}

}
