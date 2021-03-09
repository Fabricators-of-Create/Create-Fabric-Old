package com.smellypengu.createfabric.foundation.render;

import com.smellypengu.createfabric.Create;
import com.smellypengu.createfabric.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.smellypengu.createfabric.foundation.utility.AnimationTickHolder;
import com.smellypengu.createfabric.foundation.utility.MatrixStacker;
import com.smellypengu.createfabric.foundation.utility.worldWrappers.PlacementSimulationWorld;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;

import java.util.Iterator;

public class TileEntityRenderHelper {

	public static void renderTileEntities(World world, Iterable<BlockEntity> customRenderTEs, MatrixStack ms,
										  MatrixStack localTransform, VertexConsumerProvider buffer) {

		renderTileEntities(world, null, customRenderTEs, ms, localTransform, buffer);
	}

	public static void renderTileEntities(World world, PlacementSimulationWorld renderWorld, Iterable<BlockEntity> customRenderTEs, MatrixStack ms,
										  MatrixStack localTransform, VertexConsumerProvider buffer) {
		float pt = AnimationTickHolder.getPartialTicks();
		Matrix4f matrix = localTransform.peek()
			.getModel();

		for (Iterator<BlockEntity> iterator = customRenderTEs.iterator(); iterator.hasNext();) {
			BlockEntity tileEntity = iterator.next();
			//if (tileEntity instanceof IInstanceRendered) continue; // TODO: some things still need to render

			BlockEntityRenderer<BlockEntity> renderer = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(tileEntity); // TODO I THINK THIS IS RIGHT
			if (renderer == null) {
				iterator.remove();
				continue;
			}

			try {
				BlockPos pos = tileEntity.getPos();
				ms.push();
				MatrixStacker.of(ms)
					.translate(pos);

				Vector4f vec = new Vector4f(pos.getX() + .5f, pos.getY() + .5f, pos.getZ() + .5f, 1);
				vec.transform(matrix);
				BlockPos lightPos = new BlockPos(vec.getX(), vec.getY(), vec.getZ());
				int worldLight = ContraptionRenderDispatcher.getLightOnContraption(world, renderWorld, pos, lightPos);

				renderer.render(tileEntity, pt, ms, buffer, worldLight,
								OverlayTexture.DEFAULT_UV);
				ms.pop();

			} catch (Exception e) {
				iterator.remove();
				
				String message = "TileEntity " + tileEntity.getType()
					.toString() + " didn't want to render while moved.\n";
				/**if (AllConfigs.CLIENT.explainRenderErrors.get()) { TODO FIX CONFIG
					Create.logger.error(message, e);
					continue;
				}*/
				
				Create.logger.error(message, e); /** SHOULD BE Create.logger.error(message);*/
				continue;
			}
		}
	}

}
