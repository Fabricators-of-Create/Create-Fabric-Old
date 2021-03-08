package com.simibubi.create.content.contraptions.components.structureMovement.glue;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.MatrixStacker;
import com.simibubi.create.foundation.utility.VecHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Environment(EnvType.CLIENT)
public class SuperGlueRenderer extends EntityRenderer<SuperGlueEntity> {

	private Identifier regular = new Identifier(Create.ID, "textures/entity/super_glue/slime.png");

	private Vec3d[] quad1;
	private Vec3d[] quad2;
	private float[] u = { 0, 1, 1, 0 };
	private float[] v = { 0, 0, 1, 1 };

	public SuperGlueRenderer(EntityRenderDispatcher renderManager) {
		super(renderManager);
		initQuads();
	}

	@Override
	public Identifier getEntityTexture(SuperGlueEntity entity) {
		return regular;
	}

	@Override
	public void render(SuperGlueEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack ms,
		VertexConsumerProvider buffer, int light) {
		super.render(entity, p_225623_2_, p_225623_3_, ms, buffer, light);

		PlayerEntity player = MinecraftClient.getInstance().player;
		boolean visible = entity.isVisible();
		boolean holdingGlue = AllItems.SUPER_GLUE.isIn(player.getMainHandStack())
			|| AllItems.SUPER_GLUE.isIn(player.getOffHandStack());

		if (!visible && !holdingGlue)
			return;

		VertexConsumer builder = buffer.getBuffer(RenderLayer.getEntityCutout(getEntityTexture(entity)));
		light = getBrightnessForRender(entity);
		Direction face = entity.getFacingDirection();

		ms.push();
		MatrixStacker.of(ms)
			.rotateY(AngleHelper.horizontalAngle(face))
			.rotateX(AngleHelper.verticalAngle(face));
		Entry peek = ms.peek();

		Vec3d[][] quads = { quad1, quad2 };
		for (Vec3d[] quad : quads) {
			for (int i = 0; i < 4; i++) {
				Vec3d vertex = quad[i];
				builder.vertex(peek.getModel(), (float) vertex.x, (float) vertex.y, (float) vertex.z)
					.color(255, 255, 255, 255)
					.texture(u[i], v[i])
					.overlay(OverlayTexture.DEFAULT_UV)
					.light(light)
					.normal(peek.getNormal(), face.getOffsetX(), face.getOffsetY(), face.getOffsetZ())
					.next();
			}
			face = face.getOpposite();
		}
		ms.pop();
	}

	private void initQuads() {
		Vec3d diff = Vec3d.of(Direction.SOUTH.getVector());
		Vec3d extension = diff.normalize()
			.multiply(1 / 32f - 1 / 128f);

		Vec3d plane = VecHelper.axisAlingedPlaneOf(diff);
		Axis axis = Direction.getFacing(diff.x, diff.y, diff.z)
			.getAxis();

		Vec3d start = Vec3d.ZERO.subtract(extension);
		Vec3d end = Vec3d.ZERO.add(extension);

		plane = plane.multiply(1 / 2f);
		Vec3d a1 = plane.add(start);
		Vec3d b1 = plane.add(end);
		plane = VecHelper.rotate(plane, -90, axis);
		Vec3d a2 = plane.add(start);
		Vec3d b2 = plane.add(end);
		plane = VecHelper.rotate(plane, -90, axis);
		Vec3d a3 = plane.add(start);
		Vec3d b3 = plane.add(end);
		plane = VecHelper.rotate(plane, -90, axis);
		Vec3d a4 = plane.add(start);
		Vec3d b4 = plane.add(end);

		quad1 = new Vec3d[] { a2, a3, a4, a1 };
		quad2 = new Vec3d[] { b3, b2, b1, b4 };
	}

	private int getBrightnessForRender(SuperGlueEntity entity) {
		BlockPos blockpos = entity.getHangingPosition();
		BlockPos blockpos2 = blockpos.offset(entity.getFacingDirection()
			.getOpposite());

		World world = entity.getEntityWorld();
		int light = world.canSetBlock(blockpos) ? WorldRenderer.getLightmapCoordinates(world, blockpos) : 15;
		int light2 = world.canSetBlock(blockpos2) ? WorldRenderer.getLightmapCoordinates(world, blockpos2) : 15;
		return Math.max(light, light2);
	}

}
