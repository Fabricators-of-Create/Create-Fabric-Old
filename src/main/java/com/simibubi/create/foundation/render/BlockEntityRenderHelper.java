package com.simibubi.create.foundation.render;

import java.util.Iterator;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.MatrixStacker;
import com.simibubi.create.foundation.utility.worldWrappers.PlacementSimulationWorld;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

public class BlockEntityRenderHelper {

	public static void renderBlockEntities(World world, Iterable<BlockEntity> customRenderTEs, MatrixStack ms,
										   MatrixStack localTransform, VertexConsumerProvider buffer) {

		renderBlockEntities(world, null, customRenderTEs, ms, localTransform, buffer);
	}

	public static void renderBlockEntities(World world, PlacementSimulationWorld renderWorld, Iterable<BlockEntity> customRenderTEs, MatrixStack ms,
										   MatrixStack localTransform, VertexConsumerProvider buffer) {
		float pt = AnimationTickHolder.getPartialTicks();
		Matrix4f matrix = localTransform.peek()
			.getModel();

		for (Iterator<BlockEntity> iterator = customRenderTEs.iterator(); iterator.hasNext();) {
			BlockEntity blockEntity = iterator.next();
			//if (blockEntity instanceof IInstanceRendered) continue; // TODO: some things still need to render

			BlockEntityRenderer<BlockEntity> renderer = BlockEntityRenderDispatcher.INSTANCE.get(blockEntity);
			if (renderer == null) {
				iterator.remove();
				continue;
			}

			try {
				BlockPos pos = blockEntity.getPos();
				ms.push();
				MatrixStacker.of(ms)
					.translate(pos);

				Vector4f vec = new Vector4f(pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, 1);
				vec.transform(matrix);
				BlockPos lightPos = new BlockPos(vec.getX(), vec.getY(), vec.getZ());
				int worldLight = ContraptionRenderDispatcher.getLightOnContraption(world, renderWorld, pos, lightPos);

				renderer.render(blockEntity, pt, ms, buffer, worldLight,
								OverlayTexture.DEFAULT_UV);
				ms.pop();

			} catch (Exception e) {
				iterator.remove();
				
				String message = "BlockEntity " + BlockEntityType.getId(blockEntity.getType()).toString()
						+ " didn't want to render while moved.\n";
//				if (AllConfigs.CLIENT.explainRenderErrors.get()) { TODO FIX CONFIG
//					Create.logger.error(message, e);
//					continue;
//				}
				
				Create.logger.error(message);
				continue;
			}
		}
	}

}
