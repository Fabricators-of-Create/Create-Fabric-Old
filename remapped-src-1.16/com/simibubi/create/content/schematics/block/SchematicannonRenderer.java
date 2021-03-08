package com.simibubi.create.content.schematics.block;

import java.util.Random;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.schematics.block.LaunchedItem.ForBlockState;
import com.simibubi.create.content.schematics.block.LaunchedItem.ForEntity;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.data.EmptyModelData;

public class SchematicannonRenderer extends SafeTileEntityRenderer<SchematicannonTileEntity> {

	public SchematicannonRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}
	
	@Override
	public boolean isGlobalRenderer(SchematicannonTileEntity p_188185_1_) {
		return true;
	}

	@Override
	protected void renderSafe(SchematicannonTileEntity tileEntityIn, float partialTicks, MatrixStack ms,
			VertexConsumerProvider buffer, int light, int overlay) {

		double yaw = 0;
		double pitch = 40;
		double recoil = 0;

		BlockPos pos = tileEntityIn.getPos();
		if (tileEntityIn.target != null) {

			// Calculate Angle of Cannon
			Vec3d diff = Vec3d.of(tileEntityIn.target.subtract(pos));
			if (tileEntityIn.previousTarget != null) {
				diff = (Vec3d.of(tileEntityIn.previousTarget)
						.add(Vec3d.of(tileEntityIn.target.subtract(tileEntityIn.previousTarget)).multiply(partialTicks)))
								.subtract(Vec3d.of(pos));
			}

			double diffX = diff.getX();
			double diffZ = diff.getZ();
			yaw = MathHelper.atan2(diffX, diffZ);
			yaw = yaw / Math.PI * 180;

			float distance = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
			double yOffset = 0 + distance * 2f;
			pitch = MathHelper.atan2(distance, diff.getY() * 3 + yOffset);
			pitch = pitch / Math.PI * 180 + 10;

		}

		if (!tileEntityIn.flyingBlocks.isEmpty()) {
			for (LaunchedItem launched : tileEntityIn.flyingBlocks) {

				if (launched.ticksRemaining == 0)
					continue;

				// Calculate position of flying block
				Vec3d start = Vec3d.of(tileEntityIn.getPos().add(.5f, 1, .5f));
				Vec3d target = Vec3d.of(launched.target).add(-.5, 0, 1);
				Vec3d distance = target.subtract(start);

				double targetY = target.y - start.y;
				double throwHeight = Math.sqrt(distance.lengthSquared()) * .6f + targetY;
				Vec3d cannonOffset = distance.add(0, throwHeight, 0).normalize().multiply(2);
				start = start.add(cannonOffset);

				float progress =
					((float) launched.totalTicks - (launched.ticksRemaining + 1 - partialTicks)) / launched.totalTicks;
				Vec3d blockLocationXZ = new Vec3d(.5, .5, .5).add(target.subtract(start).multiply(progress).multiply(1, 0, 1));

				// Height is determined through a bezier curve
				float t = progress;
				double yOffset = 2 * (1 - t) * t * throwHeight + t * t * targetY;
				Vec3d blockLocation = blockLocationXZ.add(0, yOffset + 1, 0).add(cannonOffset);

				// Offset to position
				ms.push();
				ms.translate(blockLocation.x, blockLocation.y, blockLocation.z);

				ms.multiply(new Vector3f(0, 1, 0).getDegreesQuaternion(360 * t * 2));
				ms.multiply(new Vector3f(1, 0, 0).getDegreesQuaternion(360 * t * 2));

				// Render the Block
				if (launched instanceof ForBlockState) {
					float scale = .3f;
					ms.scale(scale, scale, scale);
					MinecraftClient.getInstance().getBlockRenderManager().renderBlock(((ForBlockState) launched).state,
							ms, buffer, light, overlay, EmptyModelData.INSTANCE);
				}

				// Render the item
				if (launched instanceof ForEntity) {
					float scale = 1.2f;
					ms.scale(scale, scale, scale);
					MinecraftClient.getInstance().getItemRenderer().renderItem(launched.stack, Mode.GROUND, light,
							overlay, ms, buffer);
				}

				ms.pop();

				// Apply Recoil if block was just launched
				if ((launched.ticksRemaining + 1 - partialTicks) > launched.totalTicks - 10) 
					recoil = Math.max(recoil, (launched.ticksRemaining + 1 - partialTicks) - launched.totalTicks + 10);

				// Render particles for launch
				if (launched.ticksRemaining == launched.totalTicks && tileEntityIn.firstRenderTick) {
					tileEntityIn.firstRenderTick = false;
					for (int i = 0; i < 10; i++) {
						Random r = tileEntityIn.getWorld().getRandom();
						double sX = cannonOffset.x * .01f;
						double sY = (cannonOffset.y + 1) * .01f;
						double sZ = cannonOffset.z * .01f;
						double rX = r.nextFloat() - sX * 40;
						double rY = r.nextFloat() - sY * 40;
						double rZ = r.nextFloat() - sZ * 40;
						tileEntityIn.getWorld().addParticle(ParticleTypes.CLOUD, start.x + rX, start.y + rY,
								start.z + rZ, sX, sY, sZ);
					}
				}

			}
		}

		ms.push();
		BlockState state = tileEntityIn.getCachedState();
		int lightCoords = WorldRenderer.getLightmapCoordinates(tileEntityIn.getWorld(), pos);

		VertexConsumer vb = buffer.getBuffer(RenderLayer.getSolid());

		SuperByteBuffer connector = AllBlockPartials.SCHEMATICANNON_CONNECTOR.renderOn(state);
		connector.translate(.5f, 0, .5f);
		connector.rotate(Direction.UP, (float) ((yaw + 90) / 180 * Math.PI));
		connector.translate(-.5f, 0, -.5f);
		connector.light(lightCoords).renderInto(ms, vb);

		SuperByteBuffer pipe = AllBlockPartials.SCHEMATICANNON_PIPE.renderOn(state);
		pipe.translate(.5f, 15 / 16f, .5f);
		pipe.rotate(Direction.UP, (float) ((yaw + 90) / 180 * Math.PI));
		pipe.rotate(Direction.SOUTH, (float) (pitch / 180 * Math.PI));
		pipe.translate(-.5f, -15 / 16f, -.5f);
		pipe.translate(0, -recoil / 100, 0);
		pipe.light(lightCoords).renderInto(ms, vb);

		ms.pop();
	}

}
