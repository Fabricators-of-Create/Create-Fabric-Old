package com.simibubi.create;

import static com.simibubi.create.content.AllSections.CURIOSITIES;
import static com.simibubi.create.content.AllSections.KINETICS;
import static com.simibubi.create.content.AllSections.LOGISTICS;
import static com.simibubi.create.content.AllSections.MATERIALS;
import static com.simibubi.create.content.AllSections.SCHEMATICS;

import java.util.function.Function;

import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueItem;
import com.simibubi.create.content.contraptions.components.structureMovement.mounted.MinecartContraptionItem;
import com.simibubi.create.content.contraptions.goggles.GogglesItem;
import com.simibubi.create.content.contraptions.relays.belt.item.BeltConnectorItem;
import com.simibubi.create.content.contraptions.relays.gearbox.VerticalGearboxItem;
import com.simibubi.create.content.contraptions.wrench.WrenchItem;
import com.simibubi.create.content.curiosities.BuildersTeaItem;
import com.simibubi.create.content.curiosities.RefinedRadianceItem;
import com.simibubi.create.content.curiosities.TreeFertilizerItem;
import com.simibubi.create.content.curiosities.symmetry.SymmetryWandItem;
import com.simibubi.create.content.curiosities.tools.DeforesterItem;
import com.simibubi.create.foundation.item.HiddenIngredientItem;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.simibubi.create.foundation.item.TooltipHelper;

import me.pepperbell.reghelper.ItemRegBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class AllItems {
	private static AllSections currentSection;
	private static ItemGroup itemGroup = Create.baseCreativeTab;

	// Schematics

	static {
		currentSection = MATERIALS;
	}

	public static final Item COPPER_NUGGET =
		taggedIngredient("copper_nugget"/*, forgeItemTag("nuggets/copper"), NUGGETS.tag*/),
		ZINC_NUGGET = taggedIngredient("zinc_nugget"/*, forgeItemTag("nuggets/zinc"), NUGGETS.tag*/),
		BRASS_NUGGET = taggedIngredient("brass_nugget"/*, forgeItemTag("nuggets/brass"), NUGGETS.tag*/),

		COPPER_SHEET = taggedIngredient("copper_sheet"/*, forgeItemTag("plates/copper"), PLATES.tag*/),
		BRASS_SHEET = taggedIngredient("brass_sheet"/*, forgeItemTag("plates/brass"), PLATES.tag*/),
		IRON_SHEET = taggedIngredient("iron_sheet"/*, forgeItemTag("plates/iron"), PLATES.tag*/),
		GOLDEN_SHEET = taggedIngredient("golden_sheet"/*, forgeItemTag("plates/gold"), PLATES.tag*/),
		LAPIS_SHEET = taggedIngredient("lapis_sheet"/*, forgeItemTag("plates/lapis_lazuli"), PLATES.tag*/),

		CRUSHED_IRON = taggedIngredient("crushed_iron_ore"/*, CRUSHED_ORES.tag*/),
		CRUSHED_GOLD = taggedIngredient("crushed_gold_ore"/*, CRUSHED_ORES.tag*/),
		CRUSHED_COPPER = taggedIngredient("crushed_copper_ore"/*, CRUSHED_ORES.tag*/),
		CRUSHED_ZINC = taggedIngredient("crushed_zinc_ore"/*, CRUSHED_ORES.tag*/),
		CRUSHED_BRASS = taggedIngredient("crushed_brass"/*, CRUSHED_ORES.tag*/);

	public static final TagDependentIngredientItem CRUSHED_OSMIUM = compatCrushedOre("osmium"),
		CRUSHED_PLATINUM = compatCrushedOre("platinum"), CRUSHED_SILVER = compatCrushedOre("silver"),
		CRUSHED_TIN = compatCrushedOre("tin"), CRUSHED_LEAD = compatCrushedOre("lead"),
		CRUSHED_QUICKSILVER = compatCrushedOre("quicksilver"), CRUSHED_BAUXITE = compatCrushedOre("aluminum"),
		CRUSHED_URANIUM = compatCrushedOre("uranium"), CRUSHED_NICKEL = compatCrushedOre("nickel");

	public static final Item ANDESITE_ALLOY = ingredient("andesite_alloy"),
		COPPER_INGOT = taggedIngredient("copper_ingot"/*, forgeItemTag("ingots/copper"), CREATE_INGOTS.tag*/),
		ZINC_INGOT = taggedIngredient("zinc_ingot"/*, forgeItemTag("ingots/zinc"), CREATE_INGOTS.tag*/),
		BRASS_INGOT = taggedIngredient("brass_ingot"/*, forgeItemTag("ingots/brass"), CREATE_INGOTS.tag*/),

		WHEAT_FLOUR = ingredient("wheat_flour"), DOUGH = ingredient("dough"), CINDER_FLOUR = ingredient("cinder_flour"),
		POWDERED_OBSIDIAN = ingredient("powdered_obsidian"), ROSE_QUARTZ = ingredient("rose_quartz"),
		POLISHED_ROSE_QUARTZ = ingredient("polished_rose_quartz"), PROPELLER = ingredient("propeller"),
		WHISK = ingredient("whisk"), BRASS_HAND = ingredient("brass_hand"),
		CRAFTER_SLOT_COVER = ingredient("crafter_slot_cover");

	public static final HiddenIngredientItem BLAZE_CAKE_BASE = createBuilder("blaze_cake_base", HiddenIngredientItem::new)
//		.tag(AllItemTags.UPRIGHT_ON_BELT.tag)
		.register();

//	public static final CombustibleItem BLAZE_CAKE = createBuilder("blaze_cake", CombustibleItem::new)
////		.tag(AllItemTags.UPRIGHT_ON_BELT.tag)
//		.register();

	public static final Item BAR_OF_CHOCOLATE = createBuilder("bar_of_chocolate", Item::new)
		.properties(p -> p.food(new FoodComponent.Builder().hunger(5)
			.saturationModifier(0.6F)
			.build()))
//		.lang("Bar of Chocolate")
		.register();

	public static final BuildersTeaItem BUILDERS_TEA = createBuilder("builders_tea", BuildersTeaItem::new)
//		.tag(AllItemTags.UPRIGHT_ON_BELT.tag)
		.properties(p -> p.maxCount(16))
//		.lang("Builder's Tea")
		.register();

//	public static final ChromaticCompoundItem CHROMATIC_COMPOUND = createBuilder("chromatic_compound", ChromaticCompoundItem::new)
//		.properties(p -> p.rarity(Rarity.UNCOMMON))
////		.model(AssetLookup.existingItemModel())
//		.onRegister(CreateRegistrate.itemColors(() -> ChromaticCompoundColor::new))
//		.register();

//	public static final ShadowSteelItem SHADOW_STEEL = createBuilder("shadow_steel", ShadowSteelItem::new)
//		.properties(p -> p.rarity(Rarity.UNCOMMON))
//		.register();

	public static final RefinedRadianceItem REFINED_RADIANCE = createBuilder("refined_radiance", RefinedRadianceItem::new)
		.properties(p -> p.rarity(Rarity.UNCOMMON))
		.register();

	public static final Item ELECTRON_TUBE = ingredient("electron_tube"),
		INTEGRATED_CIRCUIT = ingredient("integrated_circuit");

	// Kinetics

	static {
		currentSection = KINETICS;
	}

	public static final BeltConnectorItem BELT_CONNECTOR = createBuilder("belt_connector", BeltConnectorItem::new)
//		.lang("Mechanical Belt")
		.register();

	public static final VerticalGearboxItem VERTICAL_GEARBOX = createBuilder("vertical_gearbox", VerticalGearboxItem::new)
//		.model(AssetLookup.<VerticalGearboxItem>customItemModel("gearbox", "item_vertical"))
		.onRegister(item -> TooltipHelper.referTo(item, () -> AllBlocks.GEARBOX))
		.register();

//	public static final BlazeBurnerBlockItem EMPTY_BLAZE_BURNER = createBuilder("empty_blaze_burner", BlazeBurnerBlockItem::empty)
////		.model(AssetLookup.<BlazeBurnerBlockItem>customItemModel("blaze_burner", "block"))
//		.register();

	public static final SuperGlueItem SUPER_GLUE = createBuilder("super_glue", SuperGlueItem::new)
		.register();

//	public static final MinecartCouplingItem MINECART_COUPLING = createBuilder("minecart_coupling", MinecartCouplingItem::new)
//		.register();

//	public static final SandPaperItem SAND_PAPER = createBuilder("sand_paper", SandPaperItem::new)
//		.transform(CreateRegistrate.customRenderedItem(() -> SandPaperModel::new))
//		.register();

//	public static final SandPaperItem RED_SAND_PAPER = createBuilder("red_sand_paper", SandPaperItem::new)
//		.transform(CreateRegistrate.customRenderedItem(() -> SandPaperModel::new))
//		.onRegister(s -> TooltipHelper.referTo(s, SAND_PAPER))
//		.register();

	public static final WrenchItem WRENCH = createBuilder("wrench", WrenchItem::new)
		.properties(p -> p.maxCount(1))
//		.transform(CreateRegistrate.customRenderedItem(() -> WrenchModel::new)) TODO
//		.model(AssetLookup.itemModelWithPartials())
		.register();

	public static final GogglesItem GOGGLES = createBuilder("goggles", GogglesItem::new)
		.properties(p -> p.maxCount(1))
//		.model(AssetLookup.existingItemModel())
//		.lang("Engineer's Goggles")
		.register();

	public static final MinecartContraptionItem MINECART_CONTRAPTION = createBuilder("minecart_contraption", MinecartContraptionItem::rideable)
		.register();

	public static final MinecartContraptionItem FURNACE_MINECART_CONTRAPTION = createBuilder("furnace_minecart_contraption", MinecartContraptionItem::furnace)
		.register();

	public static final MinecartContraptionItem CHEST_MINECART_CONTRAPTION = createBuilder("chest_minecart_contraption", MinecartContraptionItem::chest)
		.register();

	// Logistics

	static {
		currentSection = LOGISTICS;
	}

//	public static final FilterItem FILTER = createBuilder("filter", FilterItem::regular)
////		.model(AssetLookup.existingItemModel())
//		.register();

//	public static final FilterItem ATTRIBUTE_FILTER = createBuilder("attribute_filter", FilterItem::attribute)
////		.model(AssetLookup.existingItemModel())
//		.register();

	// Curiosities

	static {
		currentSection = CURIOSITIES;
	}

	public static final TreeFertilizerItem TREE_FERTILIZER = createBuilder("tree_fertilizer", TreeFertilizerItem::new)
		.register();

//	public static final BlockzapperItem BLOCKZAPPER = createBuilder("handheld_blockzapper", BlockzapperItem::new)
//		.transform(CreateRegistrate.customRenderedItem(() -> BlockzapperModel::new))
////		.model(AssetLookup.itemModelWithPartials())
//		.register();

//	public static final WorldshaperItem WORLDSHAPER = createBuilder("handheld_worldshaper", WorldshaperItem::new)
//		.transform(CreateRegistrate.customRenderedItem(() -> WorldshaperModel::new))
////		.model(AssetLookup.itemModelWithPartials())
//		.register();

	public static final DeforesterItem DEFORESTER = createBuilder("deforester", DeforesterItem::new)
//		.transform(CreateRegistrate.customRenderedItem(() -> DeforesterModel::new)) TODO
//		.model(AssetLookup.itemModelWithPartials())
		.register();

	public static final SymmetryWandItem WAND_OF_SYMMETRY = createBuilder("wand_of_symmetry", SymmetryWandItem::new)
//		.transform(CreateRegistrate.customRenderedItem(() -> SymmetryWandModel::new))
//		.model(AssetLookup.itemModelWithPartials())
		.register();

//	public static final ExtendoGripItem EXTENDO_GRIP = createBuilder("extendo_grip", ExtendoGripItem::new)
//		.transform(CreateRegistrate.customRenderedItem(() -> ExtendoGripModel::new))
////		.model(AssetLookup.itemModelWithPartials())
//		.register();

	// Schematics

	static {
		currentSection = SCHEMATICS;
	}

	public static final Item EMPTY_SCHEMATIC = createBuilder("empty_schematic", Item::new)
		.properties(p -> p.maxCount(1))
		.register();

//	public static final SchematicAndQuillItem SCHEMATIC_AND_QUILL = createBuilder("schematic_and_quill", SchematicAndQuillItem::new)
//		.properties(p -> p.maxCount(1))
//		.register();

//	public static final SchematicItem SCHEMATIC = createBuilder("schematic", SchematicItem::new)
//		.properties(p -> p.maxCount(1))
//		.register();

	// Shortcuts

	private static Item ingredient(String name) {
		return createBuilder(name, Item::new)
			.register();
	}

	@SuppressWarnings("unused")
	private static HiddenIngredientItem hiddenIngredient(String name) {
		return createBuilder(name, HiddenIngredientItem::new)
			.register();
	}

	@SafeVarargs
	private static Item taggedIngredient(String name, Tag.Identified<Item>... tags) {
		return createBuilder(name, Item::new)
//			.tag(tags)
			.register();
	}

	private static TagDependentIngredientItem compatCrushedOre(String metalName) {
		return createBuilder("crushed_" + metalName + "_ore",
				props -> new TagDependentIngredientItem(props, new Identifier("forge", "ores/" + metalName)))
//			.tag(AllItemTags.CRUSHED_ORES.tag)
			.register();
	}

	private static <T extends Item> ItemRegBuilder<T> createBuilder(String id, Function<FabricItemSettings, T> function) {
		ItemRegBuilder<T> builder = ItemRegBuilder.create(new Identifier(Create.ID, id), function);
		builder.properties(p -> p.group(itemGroup));
		builder.onRegister(item -> AllSections.addToSection(item, currentSection));
		return builder;
	}

	// Load this class

	public static void register() {}
}
