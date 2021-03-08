package com.simibubi.create.content.contraptions.components.structureMovement.render;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL40;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementBehaviour;
import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;
import com.simibubi.create.foundation.render.AllProgramSpecs;
import com.simibubi.create.foundation.render.Compartment;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.render.SuperByteBufferCache;
import com.simibubi.create.foundation.render.TileEntityRenderHelper;
import com.simibubi.create.foundation.render.backend.Backend;
import com.simibubi.create.foundation.render.backend.FastRenderDispatcher;
import com.simibubi.create.foundation.utility.MatrixStacker;
import com.simibubi.create.foundation.utility.worldWrappers.PlacementSimulationWorld;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

public class ContraptionRenderDispatcher {
    public static final Int2ObjectMap<RenderedContraption> renderers = new Int2ObjectOpenHashMap<>();
    public static final Compartment<Pair<Contraption, Integer>> CONTRAPTION = new Compartment<>();
    protected static PlacementSimulationWorld renderWorld;

    private static boolean firstLayer = true;

    public static void notifyLightUpdate(BlockRenderView world, LightType type, ChunkSectionPos pos) {
        for (RenderedContraption renderer : renderers.values()) {
            renderer.getLighter().lightVolume.notifyLightUpdate(world, type, pos);
        }
    }

    public static void renderTick() {
        firstLayer = true;
    }

    public static void renderTileEntities(World world, Contraption c, MatrixStack ms, MatrixStack msLocal,
                                             VertexConsumerProvider buffer) {
        PlacementSimulationWorld renderWorld = null;
        if (Backend.canUseVBOs()) {
            RenderedContraption renderer = getRenderer(world, c);

            renderWorld = renderer.renderWorld;
        }
        TileEntityRenderHelper.renderTileEntities(world, renderWorld, c.specialRenderedTileEntities, ms, msLocal, buffer);

    }

    public static void tick() {
        for (RenderedContraption contraption : renderers.values()) {
            contraption.getLighter().tick(contraption);
        }
    }

    private static RenderedContraption getRenderer(World world, Contraption c) {
        int entityId = c.entity.getEntityId();
        RenderedContraption contraption = renderers.get(entityId);

        if (contraption == null) {
            contraption = new RenderedContraption(world, c);
            renderers.put(entityId, contraption);
        }

        return contraption;
    }

    public static void renderLayer(RenderLayer layer, Matrix4f viewProjection, double camX, double camY, double camZ) {
        removeDeadContraptions();

        if (renderers.isEmpty()) return;

        if (firstLayer) {

            for (RenderedContraption renderer : renderers.values()) {
                renderer.beginFrame(camX, camY, camZ);
            }

            firstLayer = false;
        }

        layer.startDrawing();
        GL11.glEnable(GL13.GL_TEXTURE_3D);
        GL13.glActiveTexture(GL40.GL_TEXTURE4); // the shaders expect light volumes to be in texture 4

        if (Backend.canUseVBOs()) {
            ContraptionProgram structureShader = Backend.getProgram(AllProgramSpecs.CONTRAPTION_STRUCTURE);
            structureShader.bind(viewProjection, camX, camY, camZ, FastRenderDispatcher.getDebugMode());
            for (RenderedContraption renderer : renderers.values()) {
                renderer.doRenderLayer(layer, structureShader);
            }
        }

        if (Backend.canUseInstancing()) {
            for (RenderedContraption renderer : renderers.values()) {
                renderer.kinetics.render(layer, viewProjection, camX, camY, camZ, renderer::setup);
                renderer.teardown();
            }
        }

        layer.endDrawing();
        GL11.glDisable(GL13.GL_TEXTURE_3D);
        GL13.glActiveTexture(GL40.GL_TEXTURE0);
    }

    public static void removeDeadContraptions() {
        renderers.values().removeIf(renderer -> {
            if (renderer.isDead()) {
                renderer.invalidate();
                return true;
            }
            return false;
        });
    }

    public static void invalidateAll() {
        for (RenderedContraption renderer : renderers.values()) {
            renderer.invalidate();
        }

        renderers.clear();
    }

    public static void render(AbstractContraptionEntity entity, MatrixStack ms, VertexConsumerProvider buffers,
                              MatrixStack msLocal, Contraption contraption) {
        if (Backend.canUseVBOs()) {
            ContraptionRenderDispatcher.renderDynamic(entity.world, contraption, ms, msLocal, buffers);
        } else {
            ContraptionRenderDispatcher.renderDynamic(entity.world, contraption, ms, msLocal, buffers);
            ContraptionRenderDispatcher.renderStructure(entity.world, contraption, ms, msLocal, buffers);
        }
    }

    public static void renderDynamic(World world, Contraption c, MatrixStack ms, MatrixStack msLocal,
                                     VertexConsumerProvider buffer) {
        renderTileEntities(world, c, ms, msLocal, buffer);
        if (buffer instanceof VertexConsumerProvider.Immediate)
            ((VertexConsumerProvider.Immediate) buffer).draw();
        renderActors(world, c, ms, msLocal, buffer);
    }

    public static void renderStructure(World world, Contraption c, MatrixStack ms, MatrixStack msLocal,
                                          VertexConsumerProvider buffer) {
        SuperByteBufferCache bufferCache = CreateClient.bufferCache;
        List<RenderLayer> blockLayers = RenderLayer.getBlockLayers();

        buffer.getBuffer(RenderLayer.getSolid());
        for (int i = 0; i < blockLayers.size(); i++) {
            RenderLayer layer = blockLayers.get(i);
            Pair<Contraption, Integer> key = Pair.of(c, i);
            SuperByteBuffer contraptionBuffer = bufferCache.get(CONTRAPTION, key, () -> buildStructureBuffer(c, layer));
            if (contraptionBuffer.isEmpty())
                continue;
            Matrix4f model = msLocal.peek()
                .getModel();
            contraptionBuffer.light(model)
                .renderInto(ms, buffer.getBuffer(layer));
        }
    }

    private static SuperByteBuffer buildStructureBuffer(Contraption c, RenderLayer layer) {
        BufferBuilder builder = buildStructure(c, layer);
        return new SuperByteBuffer(builder);
    }

    public static BufferBuilder buildStructure(Contraption c, RenderLayer layer) {
        if (renderWorld == null || renderWorld.getWorld() != MinecraftClient.getInstance().world)
            renderWorld = new PlacementSimulationWorld(MinecraftClient.getInstance().world);

        ForgeHooksClient.setRenderLayer(layer);
        MatrixStack ms = new MatrixStack();
        BlockRenderManager dispatcher = MinecraftClient.getInstance()
                                                      .getBlockRenderManager();
        BlockModelRenderer blockRenderer = dispatcher.getModelRenderer();
        Random random = new Random();
        BufferBuilder builder = new BufferBuilder(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeInteger());
        builder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
        renderWorld.setTileEntities(c.presentTileEntities.values());

        for (Structure.StructureBlockInfo info : c.getBlocks()
                                        .values())
            renderWorld.setBlockState(info.pos, info.state);

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
        renderWorld.clear();
        renderWorld = null;
        return builder;
    }

    protected static void renderActors(World world, Contraption c, MatrixStack ms, MatrixStack msLocal,
                                       VertexConsumerProvider buffer) {
        MatrixStack[] matrixStacks = new MatrixStack[] { ms, msLocal };
        for (Pair<Structure.StructureBlockInfo, MovementContext> actor : c.getActors()) {
            MovementContext context = actor.getRight();
            if (context == null)
                continue;
            if (context.world == null)
                context.world = world;
            Structure.StructureBlockInfo blockInfo = actor.getLeft();
            for (MatrixStack m : matrixStacks) {
                m.push();
                MatrixStacker.of(m)
                             .translate(blockInfo.pos);
            }

            MovementBehaviour movementBehaviour = AllMovementBehaviours.of(blockInfo.state);
            if (movementBehaviour != null)
                movementBehaviour.renderInContraption(context, ms, msLocal, buffer);

            for (MatrixStack m : matrixStacks)
                m.pop();
        }
    }

    public static int getLight(World world, float lx, float ly, float lz) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        float sky = 0, block = 0;
        float offset = 1 / 8f;

        for (float zOffset = offset; zOffset >= -offset; zOffset -= 2 * offset)
            for (float yOffset = offset; yOffset >= -offset; yOffset -= 2 * offset)
                for (float xOffset = offset; xOffset >= -offset; xOffset -= 2 * offset) {
                    pos.set(lx + xOffset, ly + yOffset, lz + zOffset);
                    sky += world.getLightLevel(LightType.SKY, pos) / 8f;
                    block += world.getLightLevel(LightType.BLOCK, pos) / 8f;
                }

        return ((int) sky) << 20 | ((int) block) << 4;
    }

    public static int getLightOnContraption(World world, PlacementSimulationWorld renderWorld, BlockPos pos, BlockPos lightPos) {
        int worldLight = WorldRenderer.getLightmapCoordinates(world, lightPos);

        if (renderWorld != null)
            return getMaxBlockLight(worldLight, renderWorld.getLightLevel(LightType.BLOCK, pos));

        return worldLight;
    }

    public static int getMaxBlockLight(int packedLight, int blockLightValue) {
        int unpackedBlockLight = LightmapTextureManager.getBlockLightCoordinates(packedLight);

        if (blockLightValue > unpackedBlockLight) {
            packedLight = (packedLight & 0xFFFF0000) | (blockLightValue << 4);
        }

        return packedLight;
    }

    public static int getLightOnContraption(MovementContext context) {
        int entityId = context.contraption.entity.getEntityId();

        RenderedContraption renderedContraption = renderers.get(entityId);
        if (renderedContraption != null) {
            return renderedContraption.renderWorld.getLightLevel(LightType.BLOCK, context.localPos);
        } else {
            return -1;
        }
    }
}
