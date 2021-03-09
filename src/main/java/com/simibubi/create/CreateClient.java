package com.simibubi.create;

import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.simibubi.create.content.contraptions.goggles.GoggleOverlayRenderer;
import com.simibubi.create.content.palettes.AllPaletteBlocks;
import com.simibubi.create.events.ClientEvents;
import com.simibubi.create.foundation.ResourceReloadHandler;
import com.simibubi.create.foundation.block.render.CustomBlockModels;
import com.simibubi.create.foundation.block.render.SpriteShifter;
import com.simibubi.create.foundation.item.CustomItemModels;
import com.simibubi.create.foundation.item.CustomRenderedItems;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.render.KineticRenderer;
import com.simibubi.create.foundation.render.SuperByteBufferCache;
import com.simibubi.create.foundation.render.backend.Backend;
import com.simibubi.create.foundation.utility.ghost.GhostBlocks;
import com.simibubi.create.foundation.utility.outliner.Outliner;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;

public class CreateClient implements ClientModInitializer {
    public static KineticRenderer kineticRenderer;
    public static SuperByteBufferCache bufferCache;
    public static final Outliner outliner = new Outliner();
    public static GhostBlocks ghostBlocks;

    public static CustomBlockModels customBlockModels;
    public static CustomItemModels customItemModels;
    public static CustomRenderedItems customRenderedItems;

    @Override
    public void onInitializeClient() {
        kineticRenderer = new KineticRenderer();

        bufferCache = new SuperByteBufferCache();
        bufferCache.registerCompartment(KineticTileEntityRenderer.KINETIC_TILE);
        bufferCache.registerCompartment(ContraptionRenderDispatcher.CONTRAPTION, 20);

        ghostBlocks = new GhostBlocks();

        Backend.init();

        AllKeys.register();
        AllTileEntities.registerRenderers();
        AllEntityTypes.registerRenderers();
        AllFluids.registerRenderers();

        AllPackets.clientInit();

        ResourceManager resourceManager = MinecraftClient.getInstance()
                .getResourceManager();
        if (resourceManager instanceof ReloadableResourceManager)
            ((ReloadableResourceManager) resourceManager).registerListener(new ResourceReloadHandler());


        ClientEvents.register();
        HudRenderCallback.EVENT.register(GoggleOverlayRenderer::lookingAtBlocksThroughGogglesShowsTooltip);


        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlas, registry) -> SpriteShifter.getAllTargetSprites().forEach(registry::register));

        BlockRenderLayerMap.INSTANCE.putBlock(AllPaletteBlocks.TILED_GLASS, RenderLayer.getCutout());
    }

    public static CustomBlockModels getCustomBlockModels() {
        if (customBlockModels == null)
            customBlockModels = new CustomBlockModels();
        return customBlockModels;
    }

    public static CustomItemModels getCustomItemModels() {
        if (customItemModels == null)
            customItemModels = new CustomItemModels();
        return customItemModels;
    }

    public static CustomRenderedItems getCustomRenderedItems() {
        if (customRenderedItems == null)
            customRenderedItems = new CustomRenderedItems();
        return customRenderedItems;
    }

    public static void invalidateRenderers() {
        CreateClient.bufferCache.invalidate();
        CreateClient.kineticRenderer.invalidate();
        ContraptionRenderDispatcher.invalidateAll();
    }
}
