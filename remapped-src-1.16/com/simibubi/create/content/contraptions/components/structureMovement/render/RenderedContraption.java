package com.simibubi.create.content.contraptions.components.structureMovement.render;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.MutablePair;
import org.lwjgl.opengl.GL11;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.base.KineticRenderMaterials;
import com.simibubi.create.content.contraptions.components.actors.ContraptionActorData;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionLighter;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.foundation.render.backend.Backend;
import com.simibubi.create.foundation.render.backend.instancing.IInstanceRendered;
import com.simibubi.create.foundation.render.backend.instancing.InstancedModel;
import com.simibubi.create.foundation.render.backend.instancing.RenderMaterial;
import com.simibubi.create.foundation.render.backend.light.GridAlignedBB;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.worldWrappers.PlacementSimulationWorld;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

public class RenderedContraption {
    private final HashMap<RenderLayer, ContraptionModel> renderLayers = new HashMap<>();

    public final PlacementSimulationWorld renderWorld;

    private final ContraptionLighter<?> lighter;

    public final ContraptionKineticRenderer kinetics;

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

    public int getEntityId() {
        return contraption.entity.getEntityId();
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

        entity.doLocalTransforms(pt, new MatrixStack[]{ stack });

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
        Collection<BlockEntity> tileEntities = contraption.maybeInstancedTileEntities;
        if (!tileEntities.isEmpty()) {
            for (BlockEntity te : tileEntities) {
                if (te instanceof IInstanceRendered) {
                    World world = te.getWorld();
                    BlockPos pos = te.getPos();
                    te.setLocation(renderWorld, pos);
                    kinetics.add(te);
                    te.setLocation(world, pos);
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

    private static ContraptionModel buildStructureModel(PlacementSimulationWorld renderWorld, Contraption c, RenderLayer layer) {
        BufferBuilder builder = buildStructure(renderWorld, c, layer);
        return new ContraptionModel(builder);
    }

    private static PlacementSimulationWorld setupRenderWorld(World world, Contraption c) {
        PlacementSimulationWorld renderWorld = new PlacementSimulationWorld(world);

        renderWorld.setTileEntities(c.presentTileEntities.values());

        for (Structure.StructureBlockInfo info : c.getBlocks()
                                        .values())
            renderWorld.setBlockState(info.pos, info.state);

        LightingProvider lighter = renderWorld.lighter;

        renderWorld.chunkProvider.getLightSources().forEach((pos) -> lighter.addLightSource(pos, renderWorld.getLuminance(pos)));

        lighter.doLightUpdates(Integer.MAX_VALUE, true, false);

        return renderWorld;
    }

    private static BufferBuilder buildStructure(PlacementSimulationWorld renderWorld, Contraption c, RenderLayer layer) {

        ForgeHooksClient.setRenderLayer(layer);
        MatrixStack ms = new MatrixStack();
        BlockRenderManager dispatcher = MinecraftClient.getInstance()
                                                      .getBlockRenderManager();
        BlockModelRenderer blockRenderer = dispatcher.getModelRenderer();
        Random random = new Random();
        BufferBuilder builder = new BufferBuilder(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeInteger());
        builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);

        for (Structure.StructureBlockInfo info : c.getBlocks()
                                        .values()) {
            BlockState state = info.state;

            if (state.getRenderType() == BlockRenderType.ENTITYBLOCK_ANIMATED)
                continue;
            if (!RenderLayers.canRenderInLayer(state, layer))
                continue;

            BakedModel originalModel = dispatcher.getModel(state);
            ms.push();
            ms.translate(info.pos.getX(), info.pos.getY(), info.pos.getZ());
            blockRenderer.renderModel(renderWorld, originalModel, state, info.pos, ms, builder, true, random, 42,
                                      OverlayTexture.DEFAULT_UV, EmptyModelData.INSTANCE);
            ms.pop();
        }

        builder.end();
        return builder;
    }
}
