package com.smellypengu.createfabric;

import com.smellypengu.createfabric.content.contraptions.base.KineticTileEntityRenderer;
import com.smellypengu.createfabric.content.contraptions.components.structureMovement.render.ContraptionRenderDispatcher;
import com.smellypengu.createfabric.content.contraptions.goggles.GoggleOverlayRenderer;
import com.smellypengu.createfabric.content.palettes.AllPaletteBlocks;
import com.smellypengu.createfabric.events.ClientEvents;
import com.smellypengu.createfabric.foundation.ResourceReloadHandler;
import com.smellypengu.createfabric.foundation.block.render.CustomBlockModels;
import com.smellypengu.createfabric.foundation.block.render.SpriteShifter;
import com.smellypengu.createfabric.foundation.item.CustomItemModels;
import com.smellypengu.createfabric.foundation.item.CustomRenderedItems;
import com.smellypengu.createfabric.foundation.render.KineticRenderer;
import com.smellypengu.createfabric.foundation.render.SuperByteBufferCache;
import com.smellypengu.createfabric.foundation.render.backend.Backend;
import com.smellypengu.createfabric.foundation.utility.ghost.GhostBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;

public class CreateClient implements ClientModInitializer {
    public static KineticRenderer kineticRenderer;
    public static SuperByteBufferCache bufferCache;
    //public static final Outliner outliner = new Outliner();
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

        ResourceManager resourceManager = MinecraftClient.getInstance()
                .getResourceManager();
        if (resourceManager instanceof ReloadableResourceManager)
            ((ReloadableResourceManager) resourceManager).registerListener(new ResourceReloadHandler());


        ClientTickEvents.END_CLIENT_TICK.register(ClientEvents::onTick);
        WorldRenderEvents.END.register(ClientEvents::onRenderWorld);
        HudRenderCallback.EVENT.register(GoggleOverlayRenderer::lookingAtBlocksThroughGogglesShowsTooltip);

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlas, registry) -> {
            SpriteShifter.getAllTargetSprites().forEach(registry::register);
        });



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
