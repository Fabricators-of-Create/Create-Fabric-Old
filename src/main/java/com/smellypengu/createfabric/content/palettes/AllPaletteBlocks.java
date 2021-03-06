package com.smellypengu.createfabric.content.palettes;

import com.smellypengu.createfabric.Create;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllPaletteBlocks {

    public static final Block TILED_GLASS = new GlassBlock(FabricBlockSettings
            .of(Material.GLASS)
            .strength(0.3f, 0.3f)
            .nonOpaque()
            .sounds(BlockSoundGroup.GLASS));

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(Create.ID, "tiled_glass"), TILED_GLASS);
    }

}
