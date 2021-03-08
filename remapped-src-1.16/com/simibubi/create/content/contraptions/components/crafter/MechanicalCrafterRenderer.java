package com.simibubi.create.content.contraptions.components.crafter;

import static com.simibubi.create.content.contraptions.base.HorizontalKineticBlock.HORIZONTAL_FACING;
import static com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer.standardKineticRotationTransform;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.contraptions.components.crafter.MechanicalCrafterTileEntity.Phase;
import com.simibubi.create.content.contraptions.components.crafter.RecipeGridHandler.GroupedItems;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.render.backend.FastRenderDispatcher;
import com.simibubi.create.foundation.tileEntity.renderer.SafeTileEntityRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.MatrixStacker;

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
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MechanicalCrafterRenderer extends SafeTileEntityRenderer<MechanicalCrafterTileEntity> {

	public MechanicalCrafterRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected void renderSafe(MechanicalCrafterTileEntity te, float partialTicks, MatrixStack ms,
		VertexConsumerProvider buffer, int light, int overlay) {
		ms.push();
		Direction facing = te.getCachedState()
			.get(HORIZONTAL_FACING);
		Vec3d vec = Vec3d.of(facing.getVector()).multiply(.58)
			.add(.5, .5, .5);

		if (te.phase == Phase.EXPORTING) {
			Direction targetDirection = MechanicalCrafterBlock.getTargetDirection(te.getCachedState());
			float progress =
				MathHelper.clamp((1000 - te.countDown + te.getCountDownSpeed() * partialTicks) / 1000f, 0, 1);
			vec = vec.add(Vec3d.of(targetDirection.getVector()).multiply(progress * .75f));
		}

		ms.translate(vec.x, vec.y, vec.z);
		ms.scale(1 / 2f, 1 / 2f, 1 / 2f);
		float yRot = AngleHelper.horizontalAngle(facing);
		ms.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(yRot));
		renderItems(te, partialTicks, ms, buffer, light, overlay);
		ms.pop();

		renderFast(te, partialTicks, ms, buffer, light);
	}

	public void renderItems(MechanicalCrafterTileEntity te, float partialTicks, MatrixStack ms,
		VertexConsumerProvider buffer, int light, int overlay) {
		if (te.phase == Phase.IDLE) {
			ItemStack stack = te.getInventory().getStack(0);
			if (!stack.isEmpty()) {
				ms.push();
				ms.translate(0, 0, -1 / 256f);
				MinecraftClient.getInstance()
					.getItemRenderer()
					.renderItem(stack, Mode.FIXED, light, overlay, ms, buffer);
				ms.pop();
			}
		} else {
			// render grouped items
			GroupedItems items = te.groupedItems;
			float distance = .5f;

			ms.push();

			if (te.phase == Phase.CRAFTING) {
				items = te.groupedItemsBeforeCraft;
				items.calcStats();
				float progress =
					MathHelper.clamp((2000 - te.countDown + te.getCountDownSpeed() * partialTicks) / 1000f, 0, 1);
				float earlyProgress = MathHelper.clamp(progress * 2, 0, 1);
				float lateProgress = MathHelper.clamp(progress * 2 - 1, 0, 1);

				ms.scale(1 - lateProgress, 1 - lateProgress, 1 - lateProgress);
				Vec3d centering =
					new Vec3d(-items.minX + (-items.width + 1) / 2f, -items.minY + (-items.height + 1) / 2f, 0)
						.multiply(earlyProgress);
				ms.translate(centering.x * .5f, centering.y * .5f, 0);
				distance += (-4 * (progress - .5f) * (progress - .5f) + 1) * .25f;
			}

			boolean onlyRenderFirst = te.phase == Phase.INSERTING || te.phase == Phase.CRAFTING && te.countDown < 1000;
			final float spacing = distance;
			items.grid.forEach((pair, stack) -> {
				if (onlyRenderFirst && (pair.getLeft()
					.intValue() != 0
					|| pair.getRight()
						.intValue() != 0))
					return;

				ms.push();
				Integer x = pair.getKey();
				Integer y = pair.getValue();
				ms.translate(x * spacing, y * spacing, 0);
				MatrixStacker.of(ms)
					.nudge(x * 13 + y + te.getPos()
						.hashCode());
				MinecraftClient.getInstance()
					.getItemRenderer()
					.renderItem(stack, Mode.FIXED, light, overlay, ms, buffer);
				ms.pop();
			});

			ms.pop();

			if (te.phase == Phase.CRAFTING) {
				items = te.groupedItems;
				float progress =
					MathHelper.clamp((1000 - te.countDown + te.getCountDownSpeed() * partialTicks) / 1000f, 0, 1);
				float earlyProgress = MathHelper.clamp(progress * 2, 0, 1);
				float lateProgress = MathHelper.clamp(progress * 2 - 1, 0, 1);

				ms.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(earlyProgress * 2 * 360));
				float upScaling = earlyProgress * 1.125f;
				float downScaling = 1 + (1 - lateProgress) * .125f;
				ms.scale(upScaling, upScaling, upScaling);
				ms.scale(downScaling, downScaling, downScaling);

				items.grid.forEach((pair, stack) -> {
					if (pair.getLeft()
						.intValue() != 0
						|| pair.getRight()
							.intValue() != 0)
						return;
					MinecraftClient.getInstance()
						.getItemRenderer()
						.renderItem(stack, Mode.FIXED, light, overlay, ms, buffer);
				});
			}

		}
	}

	public void renderFast(MechanicalCrafterTileEntity te, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
		int light) {
		BlockState blockState = te.getCachedState();
		VertexConsumer vb = buffer.getBuffer(RenderLayer.getSolid());

		if (!FastRenderDispatcher.available(te.getWorld())) {
			SuperByteBuffer superBuffer = AllBlockPartials.SHAFTLESS_COGWHEEL.renderOn(blockState);
			standardKineticRotationTransform(superBuffer, te, light);
			superBuffer.rotateCentered(Direction.UP, (float) (blockState.get(HORIZONTAL_FACING).getAxis() != Direction.Axis.X ? 0 : Math.PI / 2));
			superBuffer.rotateCentered(Direction.EAST, (float) (Math.PI / 2));
			superBuffer.renderInto(ms, vb);
		}

		Direction targetDirection = MechanicalCrafterBlock.getTargetDirection(blockState);
		BlockPos pos = te.getPos();

		if ((te.covered || te.phase != Phase.IDLE) && te.phase != Phase.CRAFTING && te.phase != Phase.INSERTING) {
			SuperByteBuffer lidBuffer =
				renderAndTransform(te, AllBlockPartials.MECHANICAL_CRAFTER_LID, blockState, pos);
			lidBuffer.renderInto(ms, vb);
		}

		if (MechanicalCrafterBlock.isValidTarget(te.getWorld(), pos.offset(targetDirection), blockState)) {
			SuperByteBuffer beltBuffer =
				renderAndTransform(te, AllBlockPartials.MECHANICAL_CRAFTER_BELT, blockState, pos);
			SuperByteBuffer beltFrameBuffer =
				renderAndTransform(te, AllBlockPartials.MECHANICAL_CRAFTER_BELT_FRAME, blockState, pos);

			if (te.phase == Phase.EXPORTING) {
				int textureIndex = (int) ((te.getCountDownSpeed() / 128f * AnimationTickHolder.getTicks()));
				beltBuffer.shiftUVtoSheet(AllSpriteShifts.CRAFTER_THINGIES, (textureIndex % 4) / 4f, 0, 1);
			} 

			beltBuffer.renderInto(ms, vb);
			beltFrameBuffer.renderInto(ms, vb);

		} else {
			SuperByteBuffer arrowBuffer =
				renderAndTransform(te, AllBlockPartials.MECHANICAL_CRAFTER_ARROW, blockState, pos);
			arrowBuffer.renderInto(ms, vb);
		}

	}

	private SuperByteBuffer renderAndTransform(MechanicalCrafterTileEntity te, AllBlockPartials renderBlock,
		BlockState crafterState, BlockPos pos) {
		SuperByteBuffer buffer = renderBlock.renderOn(crafterState);
		float xRot = crafterState.get(MechanicalCrafterBlock.POINTING)
			.getXRotation();
		float yRot = AngleHelper.horizontalAngle(crafterState.get(HORIZONTAL_FACING));
		buffer.rotateCentered(Direction.UP, (float) ((yRot + 90) / 180 * Math.PI));
		buffer.rotateCentered(Direction.EAST, (float) ((xRot) / 180 * Math.PI));
		buffer.light(WorldRenderer.getLightmapCoordinates(te.getWorld(), crafterState, pos));
		return buffer;
	}

}
