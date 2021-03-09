package com.smellypengu.createfabric.content.contraptions.base;

import com.smellypengu.createfabric.AllBlocks;
import com.smellypengu.createfabric.CreateClient;
import com.smellypengu.createfabric.content.contraptions.KineticDebugger;
import com.smellypengu.createfabric.foundation.render.Compartment;
import com.smellypengu.createfabric.foundation.render.SuperByteBuffer;
import com.smellypengu.createfabric.foundation.render.backend.FastRenderDispatcher;
import com.smellypengu.createfabric.foundation.tileEntity.render.SafeTileEntityRenderer;
import com.smellypengu.createfabric.foundation.utility.AnimationTickHolder;
import com.smellypengu.createfabric.foundation.utility.ColorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class KineticTileEntityRenderer extends SafeTileEntityRenderer<KineticTileEntity> {

	public KineticTileEntityRenderer(BlockEntityRendererFactory.Context ctx) {
	}

	public static final Compartment<BlockState> KINETIC_TILE = new Compartment<>();
	public static boolean rainbowMode = false;

	@Override
	protected void renderSafe(KineticTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
		if (FastRenderDispatcher.available(te.getWorld())) return;

		for (RenderLayer type : RenderLayer.getBlockLayers())
			//if (RenderLayers.canRenderInLayer(te.getCachedState(), type)) TODO DONT KNOW WHAT TO DO ABOUT THIS RenderLayers CHECK
				renderRotatingBuffer(te, getRotatedModel(te), ms, buffer.getBuffer(type), light);
	}

	public static void renderRotatingKineticBlock(KineticTileEntity te, BlockState renderedState, MatrixStack ms,
												  VertexConsumer buffer, int light) {
		SuperByteBuffer superByteBuffer = CreateClient.bufferCache.renderBlockIn(KINETIC_TILE, renderedState);
		renderRotatingBuffer(te, superByteBuffer, ms, buffer, light);
	}

	public static void renderRotatingBuffer(KineticTileEntity te, SuperByteBuffer superBuffer, MatrixStack ms,
											VertexConsumer buffer, int light) {
		standardKineticRotationTransform(superBuffer, te, light).renderInto(ms, buffer);
	}

	public static float getAngleForTe(KineticTileEntity te, final BlockPos pos, Direction.Axis axis) {
		float time = AnimationTickHolder.getRenderTick();
		float offset = 0; //getRotationOffsetForPosition(te, pos, axis); TODO FIX THIS
		float angle = ((time * te.getSpeed() * 3f / 10 + offset) % 360) / 180 * (float) Math.PI;
		return angle;
	}

	public static SuperByteBuffer standardKineticRotationTransform(SuperByteBuffer buffer, KineticTileEntity te,
		int light) {
		final BlockPos pos = te.getPos();
		Direction.Axis axis = ((IRotate) te.getCachedState()
			.getBlock()).getRotationAxis(te.getCachedState());
		return kineticRotationTransform(buffer, te, axis, getAngleForTe(te, pos, axis), light);
	}

	public static SuperByteBuffer kineticRotationTransform(SuperByteBuffer buffer, KineticTileEntity te, Direction.Axis axis,
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

	/**protected static float getRotationOffsetForPosition(KineticTileEntity te, final BlockPos pos, final Direction.Axis axis) { TODO CogWheelBlock CHECK
		float offset = CogWheelBlock.isLargeCog(te.getCachedState()) ? 11.25f : 0;
		double d = (((axis == Direction.Axis.X) ? 0 : pos.getX()) + ((axis == Direction.Axis.Y) ? 0 : pos.getY())
			+ ((axis == Direction.Axis.Z) ? 0 : pos.getZ())) % 2;
		if (d == 0) {
			offset = 22.5f;
		}
		return offset;
	}*/

	public static BlockState shaft(Direction.Axis axis) {
		return AllBlocks.SHAFT.getDefaultState()
			.with(Properties.AXIS, axis);
	}

	public static Direction.Axis getRotationAxisOf(KineticTileEntity te) {
		return ((IRotate) te.getCachedState()
			.getBlock()).getRotationAxis(te.getCachedState());
	}

	protected BlockState getRenderedBlockState(KineticTileEntity te) {
		return te.getCachedState();
	}

	protected SuperByteBuffer getRotatedModel(KineticTileEntity te) {
		return CreateClient.bufferCache.renderBlockIn(KINETIC_TILE, getRenderedBlockState(te));
	}

}
