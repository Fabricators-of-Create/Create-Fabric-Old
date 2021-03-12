package com.simibubi.create;

import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueItem;
import com.simibubi.create.content.contraptions.goggles.GogglesItem;
import com.simibubi.create.content.contraptions.relays.belt.item.BeltConnectorItem;
import com.simibubi.create.content.contraptions.relays.gearbox.VerticalGearboxItem;
import com.simibubi.create.content.contraptions.wrench.WrenchItem;
import com.simibubi.create.content.curiosities.BuildersTeaItem;
import com.simibubi.create.content.palettes.AllPaletteBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllItems {

    //Items
    public static final Item ZINC_NUGGET = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
    public static final Item ZINC_INGOT = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
    public static final Item REFINED_RADIANCE = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
    public static final Item BELT_CONNECTOR = new BeltConnectorItem(new FabricItemSettings().group(Create.baseCreativeTab));
    public static final GogglesItem GOGGLES = new GogglesItem(new FabricItemSettings().maxCount(1).equipmentSlot(itemStack -> EquipmentSlot.HEAD).group(Create.baseCreativeTab));
	public static final Item WRENCH = new WrenchItem(new FabricItemSettings().maxCount(1).group(Create.baseCreativeTab));
	public static final Item SUPER_GLUE = new SuperGlueItem(new FabricItemSettings().maxCount(1).group(Create.baseCreativeTab));
	public static final Item BUILDERS_TEA = new BuildersTeaItem(new FabricItemSettings().maxCount(16).group(Create.baseCreativeTab));
	public static final Item DEFORESTER = new WrenchItem(new FabricItemSettings().maxCount(1).group(Create.baseCreativeTab));
	public static final Item VERTICAL_GEARBOX = new VerticalGearboxItem(new FabricItemSettings().maxCount(1).group(Create.baseCreativeTab));

    //Block Items
    public static final BlockItem ZINC_ORE = new BlockItem(AllBlocks.ZINC_ORE, new FabricItemSettings().group(Create.baseCreativeTab));
    public static final BlockItem ZINC_BLOCK = new BlockItem(AllBlocks.ZINC_BLOCK, new FabricItemSettings().group(Create.baseCreativeTab));
    public static final BlockItem TILED_GLASS = new BlockItem(AllPaletteBlocks.TILED_GLASS, new FabricItemSettings().group(Create.palettesCreativeTab));

    public static void registerItems() {
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_nugget"), ZINC_NUGGET);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_ingot"), ZINC_INGOT);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_ore"), ZINC_ORE);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_block"), ZINC_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "tiled_glass"), TILED_GLASS);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "belt_connector"), BELT_CONNECTOR);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "goggles"), GOGGLES);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "wrench"), WRENCH);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "super_glue"), SUPER_GLUE);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "builders_tea"), BUILDERS_TEA);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "deforester"), DEFORESTER);
		//BuiltinItemRendererRegistry.INSTANCE.register(WRENCH, WrenchItemRenderer::render);
		//BuiltinItemRendererRegistry.INSTANCE.register(DEFORESTER, DeforesterItemRenderer::render);
    }

}
