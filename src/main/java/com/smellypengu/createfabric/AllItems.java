package com.smellypengu.createfabric;

import com.smellypengu.createfabric.content.contraptions.relays.belt.item.BeltConnectorItem;
import com.smellypengu.createfabric.content.palettes.AllPaletteBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllItems {

    //Items
    public static final Item ZINC_NUGGET = new Item(new Item.Settings().group(Create.baseCreativeTab));
    public static final Item ZINC_INGOT = new Item(new Item.Settings().group(Create.baseCreativeTab));

    public static final Item REFINED_RADIANCE = new Item(new Item.Settings().group(Create.baseCreativeTab));

    public static final Item BELT_CONNECTOR = new BeltConnectorItem(new Item.Settings().group(Create.baseCreativeTab));

    //Block Items
    public static final BlockItem ZINC_ORE = new BlockItem(AllBlocks.ZINC_ORE, new Item.Settings().group(Create.baseCreativeTab));
    public static final BlockItem ZINC_BLOCK = new BlockItem(AllBlocks.ZINC_BLOCK, new Item.Settings().group(Create.baseCreativeTab));
    public static final BlockItem TILED_GLASS = new BlockItem(AllPaletteBlocks.TILED_GLASS, new Item.Settings().group(Create.palettesCreativeTab));

    public static void registerItems() {
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_nugget"), ZINC_NUGGET);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_ingot"), ZINC_INGOT);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_ore"), ZINC_ORE);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_block"), ZINC_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "tiled_glass"), TILED_GLASS);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "belt_connector"), BELT_CONNECTOR);
    }

}
