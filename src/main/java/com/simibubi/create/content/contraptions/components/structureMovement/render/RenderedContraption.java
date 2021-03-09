package com.simibubi.create.content.contraptions.components.structureMovement.render;

import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.base.KineticRenderMaterials;
import com.simibubi.create.content.contraptions.components.actors.ContraptionActorData;
import com.simibubi.create.content.contraptions.components.structureMovement.*;
import com.simibubi.create.foundation.render.backend.Backend;
import com.simibubi.create.foundation.render.backend.instancing.InstanceRendered;
import com.simibubi.create.foundation.render.backend.instancing.InstancedModel;
import com.simibubi.create.foundation.render.backend.instancing.RenderMaterial;
import com.simibubi.create.foundation.render.backend.light.GridAlignedBB;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.worldWrappers.PlacementSimulationWorld;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.LightingProvider;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class RenderedContraption {
	public final PlacementSimulationWorld renderWorld;
	public final ContraptionKineticRenderer kinetics;
	private final HashMap<RenderLayer, ContraptionModel> renderLayers = new HashMap<>();
	private final ContraptionLighter<?> lighter;
	public Contraption contraption;

	private Matrix4f model;
	private Box lightBox;

	public RenderedContraption(World world, Contraption contraption) {
		this.contraption = contraption;
		this.lighter = contraption.makeLighter();
		this.kinetics = new ContraptionKineticRenderer();
		this.renderWorld = setupRenderWorld(world, contraption);

		buildLayers();
		if (Backend.canUseInstancing()) {
			buildInstancedTiles();
			buildActors();
		}
	}

	private static ContraptionModel buildStructureModel(PlacementSimulationWorld renderWorld, Contraption c, RenderLayer layer) {
		BufferBuilder builder = buildStructure(renderWorld, c, layer);
		return new ContraptionModel(builder);
	}

	private static PlacementSimulationWorld setupRenderWorld(World world, Contraption c) {
		PlacementSimulationWorld renderWorld = new PlacementSimulationWorld(world);

		renderWorld.setBlockEntities(c.presentBlockEntities.values());

		for (Structure.StructureBlockInfo info : c.getBlocks()
			.values())
			renderWorld.setBlockState(info.pos, info.state);

		LightingProvider lighter = renderWorld.lighter;

		renderWorld.chunkProvider.getLightSources().forEach((pos) -> lighter.addLightSource(pos, renderWorld.getLuminance(pos)));

		lighter.doLightUpdates(Integer.MAX_VALUE, true, false);

		return renderWorld;
	}

	private static BufferBuilder buildStructure(PlacementSimulationWorld renderWorld, Contraption c, RenderLayer layer) {

		BlockRenderLayerMap.INSTANCE.putBlocks(layer); // TODO COULD BE WRONG VERY VERY IMPORTANT
		MatrixStack ms = new MatrixStack();
		BlockRenderManager dispatcher = MinecraftClient.getInstance()
			.getBlockRenderManager();
		BlockModelRenderer blockRenderer = dispatcher.getModelRenderer();
		Random random = new Random();
		BufferBuilder builder = new BufferBuilder(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeInteger());
		builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL); // TODO COULD BE WRONG DRAWMODE

		for (Structure.StructureBlockInfo info : c.getBlocks()
			.values()) {
			BlockState state = info.state;

			if (state.getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED)
				continue;
			/**if (!RenderLayers.canRenderInLayer(state, layer)) TODO canRenderInLayer CHECK
			 continue;*/

			BakedModel originalModel = dispatcher.getModel(state);
			ms.push();
			ms.translate(info.pos.getX(), info.pos.getY(), info.pos.getZ());
			blockRenderer.render(renderWorld, originalModel, state, info.pos, ms, builder, true, random, 42,
				OverlayTexture.DEFAULT_UV);
			ms.pop();
		}

		builder.end(); // TODO MIGHT BE WRONG
		return builder;
	}

	public int getEntityId() {
		return contraption.entity.getId();
	}

	public boolean isDead() {
		return !contraption.entity.isAlive();
	}

	public ContraptionLighter<?> getLighter() {
		return lighter;
	}

	public RenderMaterial<?, InstancedModel<ContraptionActorData>> getActorMaterial() {
		return kinetics.getMaterial(KineticRenderMaterials.ACTORS);
	}

	public void doRenderLayer(RenderLayer layer, ContraptionProgram shader) {
		ContraptionModel structure = renderLayers.get(layer);
		if (structure != null) {
			setup(shader);
			structure.render();
			teardown();
		}
	}

	public void beginFrame(double camX, double camY, double camZ) {
		AbstractContraptionEntity entity = contraption.entity;
		float pt = AnimationTickHolder.getPartialTicks();

		MatrixStack stack = new MatrixStack();

		double x = MathHelper.lerp(pt, entity.lastRenderX, entity.getX()) - camX;
		double y = MathHelper.lerp(pt, entity.lastRenderY, entity.getY()) - camY;
		double z = MathHelper.lerp(pt, entity.lastRenderZ, entity.getZ()) - camZ;
		stack.translate(x, y, z);

		entity.doLocalTransforms(pt, new MatrixStack[]{stack});

		model = stack.peek().getModel();

		Box lightBox = GridAlignedBB.toAABB(lighter.lightVolume.getTextureVolume());

		this.lightBox = lightBox.offset(-camX, -camY, -camZ);
	}

	void setup(ContraptionProgram shader) {
		if (model == null || lightBox == null) return;
		shader.bind(model, lightBox);
		lighter.lightVolume.bind();
	}

	void teardown() {
		lighter.lightVolume.unbind();
	}

	void invalidate() {
		for (ContraptionModel buffer : renderLayers.values()) {
			buffer.delete();
		}
		renderLayers.clear();

		lighter.lightVolume.delete();

		kinetics.invalidate();
	}

	private void buildLayers() {
		for (ContraptionModel buffer : renderLayers.values()) {
			buffer.delete();
		}

		renderLayers.clear();

		List<RenderLayer> blockLayers = RenderLayer.getBlockLayers();

		for (RenderLayer layer : blockLayers) {
			renderLayers.put(layer, buildStructureModel(renderWorld, contraption, layer));
		}
	}

	private void buildInstancedTiles() {
		Collection<BlockEntity> blockEntities = contraption.maybeInstancedBlockEntities;
		if (!blockEntities.isEmpty()) {
			for (BlockEntity be : blockEntities) {
				if (be instanceof InstanceRendered) {
					World world = be.getWorld();
					be.setWorld(renderWorld);
					kinetics.add(be);
					be.setWorld(world);
					// TODO setWorld IS PROBABLY THE CORRECT SOLUTION
				}
			}
		}
	}

	private void buildActors() {
		List<MutablePair<Structure.StructureBlockInfo, MovementContext>> actors = contraption.getActors();

		for (MutablePair<Structure.StructureBlockInfo, MovementContext> actor : actors) {
			Structure.StructureBlockInfo blockInfo = actor.left;
			MovementContext context = actor.right;

			MovementBehaviour movementBehaviour = AllMovementBehaviours.of(blockInfo.state);

			if (movementBehaviour != null) {
				movementBehaviour.addInstance(this, context);
			}
		}
	}
}
