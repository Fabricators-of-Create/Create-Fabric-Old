package com.simibubi.create.content.contraptions.base;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.KineticDebugger;
import com.simibubi.create.content.contraptions.relays.elementary.CogWheelBlock;
import com.simibubi.create.foundation.block.entity.render.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.Compartment;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.render.backend.FastRenderDispatcher;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.ColorHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class KineticBlockEntityRenderer extends SafeBlockEntityRenderer<KineticBlockEntity> {

	public static final Compartment<BlockState> KINETIC_TILE = new Compartment<>();
	public static boolean rainbowMode = false;

	public KineticBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	public static void renderRotatingKineticBlock(KineticBlockEntity te, BlockState renderedState, MatrixStack ms,
												  VertexConsumer buffer, int light) {
		SuperByteBuffer superByteBuffer = CreateClient.bufferCache.renderBlockIn(KINETIC_TILE, renderedState);
		renderRotatingBuffer(te, superByteBuffer, ms, buffer, light);
	}

	public static void renderRotatingBuffer(KineticBlockEntity te, SuperByteBuffer superBuffer, MatrixStack ms,
											VertexConsumer buffer, int light) {
		standardKineticRotationTransform(superBuffer, te, light).renderInto(ms, buffer);
	}

	public static float getAngleForTe(KineticBlockEntity te, final BlockPos pos, Direction.Axis axis) {
		float time = AnimationTickHolder.getRenderTick();
		float offset = getRotationOffsetForPosition(te, pos, axis);
		float angle = ((time * te.getSpeed() * 3f / 10 + offset) % 360) / 180 * (float) Math.PI;
		return angle;
	}

	public static SuperByteBuffer standardKineticRotationTransform(SuperByteBuffer buffer, KineticBlockEntity te,
																   int light) {
		final BlockPos pos = te.getPos();
		Direction.Axis axis = ((Rotating) te.getCachedState()
			.getBlock()).getRotationAxis(te.getCachedState());
		return kineticRotationTransform(buffer, te, axis, getAngleForTe(te, pos, axis), light);
	}

	public static SuperByteBuffer kineticRotationTransform(SuperByteBuffer buffer, KineticBlockEntity te, Direction.Axis axis,
														   float angle, int light) {
		buffer.light(light);
		buffer.rotateCentered(Direction.get(Direction.AxisDirection.POSITIVE, axis), angle);

		int white = 0xFFFFFF;
		if (KineticDebugger.isActive()) {
			rainbowMode = true;
			buffer.color(te.hasNetwork() ? ColorHelper.colorFromLong(te.network) : white);
		} else {
			float overStressedEffect = te.effects.overStressedEffect;
			if (overStressedEffect != 0)
				if (overStressedEffect > 0)
					buffer.color(ColorHelper.mixColors(white, 0xFF0000, overStressedEffect));
				else
					buffer.color(ColorHelper.mixColors(white, 0x00FFBB, -overStressedEffect));
			else
				buffer.color(white);
		}

		return buffer;
	}

	protected static float getRotationOffsetForPosition(KineticBlockEntity te, final BlockPos pos, final Direction.Axis axis) {
		float offset = CogWheelBlock.isLargeCog(te.getCachedState()) ? 11.25f : 0;
		double d = (((axis == Direction.Axis.X) ? 0 : pos.getX()) + ((axis == Direction.Axis.Y) ? 0 : pos.getY())
			+ ((axis == Direction.Axis.Z) ? 0 : pos.getZ())) % 2;
		if (d == 0) {
			offset = 22.5f;
		}
		return offset;
	}

	public static BlockState shaft(Direction.Axis axis) {
		return AllBlocks.SHAFT.getDefaultState()
			.with(Properties.AXIS, axis);
	}

	public static Direction.Axis getRotationAxisOf(KineticBlockEntity te) {
		return ((Rotating) te.getCachedState()
			.getBlock()).getRotationAxis(te.getCachedState());
	}

	@Override
	protected void renderSafe(KineticBlockEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
		if (FastRenderDispatcher.available(te.getWorld())) return;

		RenderLayer type = RenderLayers.getBlockLayer(te.getCachedState());
		renderRotatingBuffer(te, getRotatedModel(te), ms, buffer.getBuffer(type), light);
	}

	protected BlockState getRenderedBlockState(KineticBlockEntity te) {
		return te.getCachedState();
	}

	protected SuperByteBuffer getRotatedModel(KineticBlockEntity te) {
		return CreateClient.bufferCache.renderBlockIn(KINETIC_TILE, getRenderedBlockState(te));
	}

}
