package com.simibubi.create;

import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueItem;
import com.simibubi.create.content.contraptions.components.structureMovement.mounted.MinecartContraptionItem;
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
    public static final Item REFINED_RADIANCE = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
    public static final Item BELT_CONNECTOR = new BeltConnectorItem(new FabricItemSettings().group(Create.baseCreativeTab));
    public static final GogglesItem GOGGLES = new GogglesItem(new FabricItemSettings().maxCount(1).equipmentSlot(itemStack -> EquipmentSlot.HEAD).group(Create.baseCreativeTab));
	public static final Item WRENCH = new WrenchItem(new FabricItemSettings().maxCount(1).group(Create.baseCreativeTab));
	public static final Item SUPER_GLUE = new SuperGlueItem(new FabricItemSettings().maxCount(1).group(Create.baseCreativeTab));
	public static final Item BUILDERS_TEA = new BuildersTeaItem(new FabricItemSettings().maxCount(16).group(Create.baseCreativeTab));
	public static final Item DEFORESTER = new WrenchItem(new FabricItemSettings().maxCount(1).group(Create.baseCreativeTab));
	public static final Item VERTICAL_GEARBOX = new VerticalGearboxItem(new FabricItemSettings().group(Create.baseCreativeTab));

	public static final Item ZINC_NUGGET = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item BRASS_NUGGET = new Item(new FabricItemSettings().group(Create.baseCreativeTab));

	public static final Item COPPER_SHEET = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item BRASS_SHEET = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item IRON_SHEET = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item GOLDEN_SHEET = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item LAPIS_SHEET = new Item(new FabricItemSettings().group(Create.baseCreativeTab));

	public static final Item CRUSHED_IRON = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item CRUSHED_GOLD = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item CRUSHED_COPPER = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item CRUSHED_ZINC = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item CRUSHED_BRASS = new Item(new FabricItemSettings().group(Create.baseCreativeTab));

	public static final Item ANDESITE_ALLOY = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item COPPER_INGOT = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item ZINC_INGOT = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item BRASS_INGOT = new Item(new FabricItemSettings().group(Create.baseCreativeTab));

	public static final Item WHEAT_FLOUR = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item DOUGH = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item CINDER_FLOUR = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item POWDERED_OBSIDIAN = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item ROSE_QUARTZ = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item POLISHED_ROSE_QUARTZ = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item PROPELLER = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item WHISK = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item BRASS_HAND = new Item(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item CRAFTER_SLOT_COVER = new Item(new FabricItemSettings().group(Create.baseCreativeTab));

	public static final Item MINECART_CONTRAPTION = MinecartContraptionItem.rideable(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item FURNACE_MINECART_CONTRAPTION = MinecartContraptionItem.furnace(new FabricItemSettings().group(Create.baseCreativeTab));
	public static final Item CHEST_MINECART_CONTRAPTION = MinecartContraptionItem.chest(new FabricItemSettings().group(Create.baseCreativeTab));

    //Block Items
    public static final BlockItem ZINC_ORE = new BlockItem(AllBlocks.ZINC_ORE, new FabricItemSettings().group(Create.baseCreativeTab));
    public static final BlockItem ZINC_BLOCK = new BlockItem(AllBlocks.ZINC_BLOCK, new FabricItemSettings().group(Create.baseCreativeTab));
    public static final BlockItem TILED_GLASS = new BlockItem(AllPaletteBlocks.TILED_GLASS, new FabricItemSettings().group(Create.palettesCreativeTab));

    public static void registerItems() {
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_ore"), ZINC_ORE);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_block"), ZINC_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "tiled_glass"), TILED_GLASS);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "belt_connector"), BELT_CONNECTOR);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "goggles"), GOGGLES);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "wrench"), WRENCH);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "super_glue"), SUPER_GLUE);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "builders_tea"), BUILDERS_TEA);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "deforester"), DEFORESTER);

		Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_nugget"), ZINC_NUGGET);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "brass_nugget"), BRASS_NUGGET);

		Registry.register(Registry.ITEM, new Identifier(Create.ID, "copper_sheet"), COPPER_SHEET);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "brass_sheet"), BRASS_SHEET);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "iron_sheet"), IRON_SHEET);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "golden_sheet"), GOLDEN_SHEET);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "lapis_sheet"), LAPIS_SHEET);

		Registry.register(Registry.ITEM, new Identifier(Create.ID, "crushed_iron_ore"), CRUSHED_IRON);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "crushed_gold_ore"), CRUSHED_GOLD);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "crushed_copper_ore"), CRUSHED_COPPER);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "crushed_zinc_ore"), CRUSHED_ZINC);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "crushed_brass"), CRUSHED_BRASS);

		Registry.register(Registry.ITEM, new Identifier(Create.ID, "andesite_alloy"), ANDESITE_ALLOY);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "copper_ingot"), COPPER_INGOT);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "zinc_ingot"), ZINC_INGOT);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "brass_ingot"), BRASS_INGOT);

		Registry.register(Registry.ITEM, new Identifier(Create.ID, "wheat_flour"), WHEAT_FLOUR);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "dough"), DOUGH);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "cinder_flour"), CINDER_FLOUR);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "powdered_obsidian"), POWDERED_OBSIDIAN);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "rose_quartz"), ROSE_QUARTZ);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "polished_rose_quartz"), POLISHED_ROSE_QUARTZ);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "propeller"), PROPELLER);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "whisk"), WHISK);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "brass_hand"), BRASS_HAND);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "crafter_slot_cover"), CRAFTER_SLOT_COVER);

		Registry.register(Registry.ITEM, new Identifier(Create.ID, "minecart_contraption"), MINECART_CONTRAPTION);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "furnace_minecart_contraption"), FURNACE_MINECART_CONTRAPTION);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "chest_minecart_contraption"), CHEST_MINECART_CONTRAPTION);

		//BuiltinItemRendererRegistry.INSTANCE.register(WRENCH, WrenchItemRenderer::render);
		//BuiltinItemRendererRegistry.INSTANCE.register(DEFORESTER, DeforesterItemRenderer::render);
    }

}
