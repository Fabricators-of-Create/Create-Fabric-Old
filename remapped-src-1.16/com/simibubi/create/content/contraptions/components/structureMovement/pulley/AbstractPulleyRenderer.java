package com.simibubi.create.content.contraptions.components.structureMovement.pulley;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class AbstractPulleyRenderer extends KineticTileEntityRenderer {

	private AllBlockPartials halfRope;
	private AllBlockPartials halfMagnet;

	public AbstractPulleyRenderer(BlockEntityRenderDispatcher dispatcher, AllBlockPartials halfRope,
		AllBlockPartials halfMagnet) {
		super(dispatcher);
		this.halfRope = halfRope;
		this.halfMagnet = halfMagnet;
	}

	@Override
	public boolean isGlobalRenderer(KineticTileEntity p_188185_1_) {
		return true;
	}

	@Override
	protected void renderSafe(KineticTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
		int light, int overlay) {
		super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
		float offset = getOffset(te, partialTicks);
		boolean running = isRunning(te);

		Axis rotationAxis = ((IRotate) te.getCachedState()
			.getBlock()).getRotationAxis(te.getCachedState());
		kineticRotationTransform(getRotatedCoil(te), te, rotationAxis, AngleHelper.rad(offset * 180), light)
			.renderInto(ms, buffer.getBuffer(RenderLayer.getSolid()));

		World world = te.getWorld();
		BlockState blockState = te.getCachedState();
		BlockPos pos = te.getPos();

		SuperByteBuffer halfMagnet = this.halfMagnet.renderOn(blockState);
		SuperByteBuffer halfRope = this.halfRope.renderOn(blockState);
		SuperByteBuffer magnet = renderMagnet(te);
		SuperByteBuffer rope = renderRope(te);

		VertexConsumer vb = buffer.getBuffer(RenderLayer.getSolid());
		if (running || offset == 0)
			renderAt(world, offset > .25f ? magnet : halfMagnet, offset, pos, ms, vb);

		float f = offset % 1;
		if (offset > .75f && (f < .25f || f > .75f))
			renderAt(world, halfRope, f > .75f ? f - 1 : f, pos, ms, vb);

		if (!running)
			return;

		for (int i = 0; i < offset - 1.25f; i++)
			renderAt(world, rope, offset - i - 1, pos, ms, vb);
	}

	private void renderAt(WorldAccess world, SuperByteBuffer partial, float offset, BlockPos pulleyPos, MatrixStack ms,
		VertexConsumer buffer) {
		BlockPos actualPos = pulleyPos.down((int) offset);
		int light = WorldRenderer.getLightmapCoordinates(world, world.getBlockState(actualPos), actualPos);
		partial.translate(0, -offset, 0)
			.light(light)
			.renderInto(ms, buffer);
	}

	protected abstract Axis getShaftAxis(KineticTileEntity te);

	protected abstract AllBlockPartials getCoil();

	protected abstract SuperByteBuffer renderRope(KineticTileEntity te);

	protected abstract SuperByteBuffer renderMagnet(KineticTileEntity te);

	protected abstract float getOffset(KineticTileEntity te, float partialTicks);

	protected abstract boolean isRunning(KineticTileEntity te);

	@Override
	protected BlockState getRenderedBlockState(KineticTileEntity te) {
		return shaft(getShaftAxis(te));
	}

	protected SuperByteBuffer getRotatedCoil(KineticTileEntity te) {
		BlockState blockState = te.getCachedState();
		return getCoil().renderOnDirectionalSouth(blockState,
			Direction.get(AxisDirection.POSITIVE, getShaftAxis(te)));
	}

}
