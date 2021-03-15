package com.simibubi.create;

import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.contraptions.components.clock.CuckooClockBlock;
import com.simibubi.create.content.contraptions.components.crank.HandCrankBlock;
import com.simibubi.create.content.contraptions.components.crank.ValveHandleBlock;
import com.simibubi.create.content.contraptions.components.fan.EncasedFanBlock;
import com.simibubi.create.content.contraptions.components.fan.NozzleBlock;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.ClockworkBearingBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.MechanicalBearingBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.WindmillBearingBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.LinearChassisBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.RadialChassisBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.mounted.CartAssemblerBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.mounted.CartAssemblerBlockItem;
import com.simibubi.create.content.contraptions.components.structureMovement.pulley.PulleyBlock;
import com.simibubi.create.content.contraptions.components.waterwheel.WaterWheelBlock;
import com.simibubi.create.content.contraptions.relays.belt.BeltBlock;
import com.simibubi.create.content.contraptions.relays.elementary.CogWheelBlock;
import com.simibubi.create.content.contraptions.relays.elementary.CogwheelBlockItem;
import com.simibubi.create.content.contraptions.relays.elementary.ShaftBlock;
import com.simibubi.create.content.contraptions.relays.encased.ClutchBlock;
import com.simibubi.create.content.contraptions.relays.encased.GearshiftBlock;
import com.simibubi.create.content.contraptions.relays.gearbox.GearboxBlock;
import com.simibubi.create.content.logistics.block.diodes.AdjustableRepeaterBlock;
import com.simibubi.create.content.logistics.block.diodes.PoweredLatchBlock;
import com.simibubi.create.content.logistics.block.diodes.PulseRepeaterBlock;
import com.simibubi.create.content.logistics.block.diodes.ToggleLatchBlock;
import com.simibubi.create.content.logistics.block.redstone.AnalogLeverBlock;
import com.simibubi.create.foundation.config.StressConfigDefaults;
import com.simibubi.create.foundation.data.BuilderConsumers;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.item.TooltipHelper;
import me.pepperbell.reghelper.BlockRegBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class AllBlocks {
	private static AllSections currentSection;
	private static ItemGroup itemGroup = Create.baseCreativeTab;

	// Schematics

	static {
		currentSection = AllSections.SCHEMATICS;
	}

	/*public static final SchematicannonBlock SCHEMATICANNON = createBuilder("schematicannon", SchematicannonBlock::new)
		.initialProperties(() -> Blocks.DISPENSER)
		.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), AssetLookup.partialBaseModel(ctx, prov)))
		.item()
		.transform(customItemModel())
		.register();

	public static final SchematicTableBlock SCHEMATIC_TABLE = createBuilder("schematic_table", SchematicTableBlock::new)
		.initialProperties(() -> Blocks.LECTERN)
		.blockstate((ctx, prov) -> prov.horizontalBlock(ctx.getEntry(), prov.models()
			.getExistingFile(ctx.getId()), 0))
		.simpleItem()
		.register();*/

	// Kinetics

	static {
		currentSection = AllSections.KINETICS;
	}

	public static final ShaftBlock SHAFT = createBuilder("shaft", ShaftBlock::new)
		.initialProperties(SharedProperties::stone)
		.consume(StressConfigDefaults.noImpactConsumer())
//		.blockstate(BlockStateGen.axisBlockProvider(false))
//		.onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
		.simpleItem()
		.register();

	public static final CogWheelBlock COGWHEEL = createBuilder("cogwheel", CogWheelBlock::small)
		.initialProperties(SharedProperties::stone)
		.consume(StressConfigDefaults.noImpactConsumer())
		.properties(p -> p.sounds(BlockSoundGroup.WOOD))
//		.blockstate(BlockStateGen.axisBlockProvider(false))
//		.onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
		.item(CogwheelBlockItem::new)
		.build()
		.register();

	public static final CogWheelBlock LARGE_COGWHEEL = createBuilder("large_cogwheel", CogWheelBlock::large)
		.initialProperties(SharedProperties::stone)
		.properties(p -> p.sounds(BlockSoundGroup.WOOD))
		.consume(StressConfigDefaults.noImpactConsumer())
//		.blockstate(BlockStateGen.axisBlockProvider(false))
//		.onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
		.item(CogwheelBlockItem::new)
		.build()
		.register();

	/*public static final EncasedShaftBlock ANDESITE_ENCASED_SHAFT = createBuilder("andesite_encased_shaft", EncasedShaftBlock::andesite)
		.transform(BuilderTransformers.encasedShaft("andesite", AllSpriteShifts.ANDESITE_CASING))
		.register();

	public static final EncasedShaftBlock BRASS_ENCASED_SHAFT = createBuilder("brass_encased_shaft", EncasedShaftBlock::brass)
		.transform(BuilderTransformers.encasedShaft("brass", AllSpriteShifts.BRASS_CASING))
		.register();*/

	public static final GearboxBlock GEARBOX = createBuilder("gearbox", GearboxBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(AbstractBlock.Settings::nonOpaque)
		.consume(StressConfigDefaults.noImpactConsumer())
//		.onRegister(CreateRegistrate.connectedTextures(new EncasedCTBehaviour(AllSpriteShifts.ANDESITE_CASING))) TODO
//		.onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.ANDESITE_CASING, TODO
//			(s, f) -> f.getAxis() == s.get(GearboxBlock.AXIS))))
//		.blockstate((c, p) -> axisBlock(c, p, $ -> AssetLookup.partialBaseModel(c, p), true))
		.item()
//		.transform(customItemModel())
		.build()
		.register();

	public static final ClutchBlock CLUTCH = createBuilder("clutch", ClutchBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(AbstractBlock.Settings::nonOpaque)
		.consume(StressConfigDefaults.noImpactConsumer())
//		.blockstate((c, p) -> BlockStateGen.axisBlock(c, p, AssetLookup.forPowered(c, p)))
		.item()
//		.transform(customItemModel())
		.build()
		.register();

	public static final GearshiftBlock GEARSHIFT = createBuilder("gearshift", GearshiftBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(AbstractBlock.Settings::nonOpaque)
		.consume(StressConfigDefaults.noImpactConsumer())
//		.blockstate((c, p) -> BlockStateGen.axisBlock(c, p, AssetLookup.forPowered(c, p)))
		.item()
//		.transform(customItemModel())
		.build()
		.register();

	/*public static final EncasedBeltBlock ENCASED_CHAIN_DRIVE = createBuilder("encased_chain_drive", EncasedBeltBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(AbstractBlock.Settings::nonOpaque)
		.transform(StressConfigDefaults.setNoImpact())
		.blockstate((c, p) -> new EncasedBeltGenerator((state, suffix) -> p.models()
			.getExistingFile(p.modLoc("block/" + c.getName() + "/" + suffix))).generate(c, p))
		.item()
		.transform(customItemModel())
		.register();

	public static final AdjustablePulleyBlock ADJUSTABLE_CHAIN_GEARSHIFT = createBuilder("adjustable_chain_gearshift", AdjustablePulleyBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(AbstractBlock.Settings::nonOpaque)
		.transform(StressConfigDefaults.setNoImpact())
		.blockstate((c, p) -> new EncasedBeltGenerator((state, suffix) -> {
			String powered = state.get(AdjustablePulleyBlock.POWERED) ? "_powered" : "";
			return p.models()
				.withExistingParent(c.getName() + "_" + suffix + powered,
				p.modLoc("block/encased_chain_drive/" + suffix))
				.texture("side", p.modLoc("block/" + c.getName() + powered));
		}).generate(c, p))
		.item()
		.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/encased_chain_drive/item"))
			.texture("side", p.modLoc("block/" + c.getName())))
		.build()
		.register();*/

	public static final BeltBlock BELT = createBuilder("belt", BeltBlock::new)
		.initialProperties(SharedProperties.beltMaterial, MaterialColor.GRAY)
		.properties(p -> p.sounds(BlockSoundGroup.WOOL))
		.properties(p -> p.strength(0.8F))
//		.blockstate(new BeltGenerator()::generate)
		.consume(StressConfigDefaults.impactConsumer(1.0))
//		.onRegister(CreateRegistrate.blockModel(() -> BeltModel::new))
		.register();

	public static final CreativeMotorBlock CREATIVE_MOTOR = createBuilder("creative_motor", CreativeMotorBlock::new)
		.initialProperties(SharedProperties::stone)
//		.tag(AllBlockTags.SAFE_NBT.tag)
//		.blockstate(new CreativeMotorGenerator()::generate)
		.consume(StressConfigDefaults.capacityConsumer(16384.0))
		.item()
//		.transform(customItemModel())
		.build()
		.register();

	public static final WaterWheelBlock WATER_WHEEL = createBuilder("water_wheel", WaterWheelBlock::new)
		.initialProperties(SharedProperties::wooden)
		.properties(AbstractBlock.Settings::nonOpaque)
//		.blockstate(BlockStateGen.horizontalWheelProvider(false))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.consume(StressConfigDefaults.capacityConsumer(16.0))
		.simpleItem()
		.register();

	public static final EncasedFanBlock ENCASED_FAN = createBuilder("encased_fan", EncasedFanBlock::new)
		.initialProperties(SharedProperties::stone)
//		.blockstate(BlockStateGen.directionalBlockProvider(true))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.consume(StressConfigDefaults.capacityConsumer(16.0))
		.consume(StressConfigDefaults.impactConsumer(2.0))
		.item()
//		.transform(customItemModel())
		.build()
		.register();

	public static final NozzleBlock NOZZLE = createBuilder("nozzle", NozzleBlock::new)
		.initialProperties(SharedProperties::stone)
//		.tag(AllBlockTags.BRITTLE.tag)
//		.blockstate(BlockStateGen.directionalBlockProvider(true))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.item()
//		.transform(customItemModel())
		.build()
		.register();

	/*public static final TurntableBlock TURNTABLE = createBuilder("turntable", TurntableBlock::new)
		.initialProperties(SharedProperties::wooden)
		.blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.standardModel(c, p)))
		.transform(StressConfigDefaults.setImpact(4.0))
		.simpleItem()
		.register();*/

	public static final HandCrankBlock HAND_CRANK = createBuilder("hand_crank", HandCrankBlock::new)
		.initialProperties(SharedProperties::wooden)
//		.blockstate(BlockStateGen.directionalBlockProvider(true))
		.consume(StressConfigDefaults.capacityConsumer(8.0))
//		.tag(AllBlockTags.BRITTLE.tag)
		.item()
//		.transform(customItemModel())
		.build()
		.register();

	public static final CuckooClockBlock CUCKOO_CLOCK = createBuilder("cuckoo_clock", CuckooClockBlock::regular)
		.consume(BuilderConsumers.cuckooClock())
		.register();

	public static final CuckooClockBlock MYSTERIOUS_CUCKOO_CLOCK = createBuilder("mysterious_cuckoo_clock", CuckooClockBlock::mysterious)
		.consume(BuilderConsumers.cuckooClock())
//		.lang("Cuckoo Clock")
		.onRegisterItem(item -> TooltipHelper.referTo(item, () -> CUCKOO_CLOCK))
		.register();

	/*public static final MillstoneBlock MILLSTONE = createBuilder("millstone", MillstoneBlock::new)
		.initialProperties(SharedProperties::stone)
		.blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
		.transform(StressConfigDefaults.setImpact(4.0))
		.item()
		.transform(customItemModel())
		.register();

	public static final CrushingWheelBlock CRUSHING_WHEEL = createBuilder("crushing_wheel", CrushingWheelBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(AbstractBlock.Settings::nonOpaque)
		.blockstate(BlockStateGen.axisBlockProvider(false))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.transform(StressConfigDefaults.setImpact(8.0))
		.simpleItem()
		.register();

	public static final CrushingWheelControllerBlock CRUSHING_WHEEL_CONTROLLER = createBuilder("crushing_wheel_controller", CrushingWheelControllerBlock::new)
		.initialProperties(() -> Blocks.AIR)
		.blockstate((c, p) -> p.getVariantBuilder(c.get())
			.forAllStatesExcept(state -> ConfiguredModel.builder()
				.modelFile(p.models()
				.getExistingFile(p.mcLoc("block/air")))
				.build(), CrushingWheelControllerBlock.FACING))
		.register();

	public static final MechanicalPressBlock MECHANICAL_PRESS = createBuilder("mechanical_press", MechanicalPressBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(AbstractBlock.Settings::nonOpaque)
		.blockstate(BlockStateGen.horizontalBlockProvider(true))
		.transform(StressConfigDefaults.setImpact(8.0))
		.item(BasinOperatorBlockItem::new)
		.transform(customItemModel())
		.register();

	public static final MechanicalMixerBlock MECHANICAL_MIXER = createBuilder("mechanical_mixer", MechanicalMixerBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(AbstractBlock.Settings::nonOpaque)
		.blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.transform(StressConfigDefaults.setImpact(4.0))
		.item(BasinOperatorBlockItem::new)
		.transform(customItemModel())
		.register();

	public static final BasinBlock BASIN = createBuilder("basin", BasinBlock::new)
		.initialProperties(SharedProperties::stone)
		.blockstate(new BasinGenerator()::generate)
		.onRegister(addMovementBehaviour(new BasinMovementBehaviour()))
		.item()
		.transform(customItemModel("_", "block"))
		.register();

	public static final BlazeBurnerBlock BLAZE_BURNER = createBuilder("blaze_burner", BlazeBurnerBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.properties(p -> p.luminance($ -> 12))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.tag(AllBlockTags.FAN_TRANSPARENT.tag, AllBlockTags.FAN_HEATERS.tag)
		.loot((lt, block) -> lt.addDrop(block, BlazeBurnerBlock.buildLootTable()))
		.blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
		.item(BlazeBurnerBlockItem::withBlaze)
		.model(AssetLookup.<BlazeBurnerBlockItem>customItemModel("blaze_burner", "block_with_blaze"))
		.build()
		.register();

	public static final LitBlazeBurnerBlock LIT_BLAZE_BURNER = createBuilder("lit_blaze_burner", LitBlazeBurnerBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.properties(p -> p.luminance($ -> 12))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.tag(AllBlockTags.FAN_TRANSPARENT.tag, AllBlockTags.FAN_HEATERS.tag)
		.loot((lt, block) -> lt.addDrop(block, AllItems.EMPTY_BLAZE_BURNER.get()))
		.blockstate((c, p) -> p.simpleBlock(c.getEntry(), p.models()
			.getExistingFile(p.modLoc("block/blaze_burner/block_with_fire"))))
		.register();

	public static final DepotBlock DEPOT = createBuilder("depot", DepotBlock::new)
		.initialProperties(SharedProperties::stone)
		.blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
		.item()
		.transform(customItemModel("_", "block"))
		.register();

	public static final ChuteBlock CHUTE = createBuilder("chute", ChuteBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.blockstate(new ChuteGenerator()::generate)
		.item(ChuteItem::new)
		.transform(customItemModel("_", "block"))
		.register();

	public static final SmartChuteBlock SMART_CHUTE = createBuilder("smart_chute", SmartChuteBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.blockstate((c, p) -> BlockStateGen.simpleBlock(c, p, AssetLookup.forPowered(c, p)))
		.item()
		.transform(customItemModel("_", "block"))
		.register();

	public static final GaugeBlock SPEEDOMETER = createBuilder("speedometer", GaugeBlock::speed)
		.initialProperties(SharedProperties::wooden)
		.transform(StressConfigDefaults.setNoImpact())
		.blockstate(new GaugeGenerator()::generate)
		.item()
		.transform(ModelGen.customItemModel("gauge", "_", "item"))
		.register();

	public static final GaugeBlock STRESSOMETER = createBuilder("stressometer", GaugeBlock::stress)
		.initialProperties(SharedProperties::wooden)
		.transform(StressConfigDefaults.setNoImpact())
		.blockstate(new GaugeGenerator()::generate)
		.item()
		.transform(ModelGen.customItemModel("gauge", "_", "item"))
		.register();

	public static final BracketBlock WOODEN_BRACKET = createBuilder("wooden_bracket", BracketBlock::new)
		.blockstate(new BracketGenerator("wooden")::generate)
		.item(BracketBlockItem::new)
		.transform(BracketGenerator.itemModel("wooden"))
		.register();

	public static final BracketBlock METAL_BRACKET = createBuilder("metal_bracket", BracketBlock::new)
		.blockstate(new BracketGenerator("metal")::generate)
		.item(BracketBlockItem::new)
		.transform(BracketGenerator.itemModel("metal"))
		.register();*/

	// Fluids

	/*public static final FluidPipeBlock FLUID_PIPE = createBuilder("fluid_pipe", FluidPipeBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.blockstate(BlockStateGen.pipe())
		.onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::new))
		.item()
		.transform(customItemModel())
		.register();

	public static final EncasedPipeBlock ENCASED_FLUID_PIPE = createBuilder("encased_fluid_pipe", EncasedPipeBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.properties(Block.Properties::nonOpaque)
		.blockstate(BlockStateGen.encasedPipe())
		.onRegister(CreateRegistrate.connectedTextures(new EncasedCTBehaviour(AllSpriteShifts.COPPER_CASING)))
		.onRegister(CreateRegistrate.casingConnectivity((block, cc) -> cc.make(block, AllSpriteShifts.COPPER_CASING,
			(s, f) -> !s.get(EncasedPipeBlock.FACING_TO_PROPERTY_MAP.get(f)))))
		.onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::new))
		.loot((p, b) -> p.addDrop(b, FLUID_PIPE.get()))
		.register();

	public static final GlassFluidPipeBlock GLASS_FLUID_PIPE = createBuilder("glass_fluid_pipe", GlassFluidPipeBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.blockstate((c, p) -> BlockStateGen.axisBlock(c, p, s -> p.models()
			.getExistingFile(p.modLoc("block/fluid_pipe/window" + (s.get(GlassFluidPipeBlock.ALT) ? "_alt" : "")))))
		.onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::new))
		.loot((p, b) -> p.addDrop(b, FLUID_PIPE.get()))
		.register();

	public static final PumpBlock MECHANICAL_PUMP = createBuilder("mechanical_pump", PumpBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.blockstate(BlockStateGen.directionalBlockProviderIgnoresWaterlogged(true))
		.onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::new))
		.transform(StressConfigDefaults.setImpact(4.0))
		.item()
		.transform(customItemModel())
		.register();

	public static final SmartFluidPipeBlock SMART_FLUID_PIPE = createBuilder("smart_fluid_pipe", SmartFluidPipeBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.blockstate(new SmartFluidPipeGenerator()::generate)
		.onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::new))
		.item()
		.transform(customItemModel())
		.register();

	public static final FluidValveBlock FLUID_VALVE = createBuilder("fluid_valve", FluidValveBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.blockstate((c, p) -> BlockStateGen.directionalAxisBlock(c, p,
		(state, vertical) -> AssetLookup.partialBaseModel(c, p, vertical ? "vertical" : "horizontal",
			state.get(FluidValveBlock.ENABLED) ? "open" : "closed")))
		.onRegister(CreateRegistrate.blockModel(() -> PipeAttachmentModel::new))
		.item()
		.transform(customItemModel())
		.register();*/

	public static final ValveHandleBlock COPPER_VALVE_HANDLE = createBuilder("copper_valve_handle", ValveHandleBlock::copper)
		.consume(BuilderConsumers.valveHandle(null))
		.register();

	public static final ValveHandleBlock[] DYED_VALVE_HANDLES = new ValveHandleBlock[DyeColor.values().length];

	static {
		for (DyeColor colour : DyeColor.values()) {
			String colourName = colour.asString();
			DYED_VALVE_HANDLES[colour.ordinal()] = createBuilder(colourName + "_valve_handle", ValveHandleBlock::dyed)
				.consume(BuilderConsumers.valveHandle(colour))
//				.recipe((c, p) -> ShapedRecipeJsonFactory.create(c.get())
//				.pattern("#")
//				.pattern("-")
//				.input('#', DyeHelper.getTagOfDye(colour))
//				.input('-', AllItemTags.VALVE_HANDLES.tag)
//				.criterion("has_valve", RegistrateRecipeProvider.conditionsFromTag(AllItemTags.VALVE_HANDLES.tag))
//				.offerTo(p, Create.asResource("crafting/kinetics/" + c.getName() + "_from_other_valve_handle")))
				.register();
		}
	}

	/*public static final FluidTankBlock FLUID_TANK = createBuilder("fluid_tank", FluidTankBlock::regular)
		.initialProperties(SharedProperties::softMetal)
		.properties(AbstractBlock.Settings::nonOpaque)
		.blockstate(new FluidTankGenerator()::generate)
		.onRegister(CreateRegistrate.blockModel(() -> FluidTankModel::standard))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.item(FluidTankItem::new)
		.model(AssetLookup.<FluidTankItem>customItemModel("_", "block_single_window"))
		.build()
		.register();

	public static final FluidTankBlock CREATIVE_FLUID_TANK = createBuilder("creative_fluid_tank", FluidTankBlock::creative)
		.initialProperties(SharedProperties::softMetal)
		.tag(AllBlockTags.SAFE_NBT.tag)
		.properties(Block.Properties::nonOpaque)
		.blockstate(new FluidTankGenerator("creative_")::generate)
		.onRegister(CreateRegistrate.blockModel(() -> FluidTankModel::creative))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.item(FluidTankItem::new)
		.model((c, p) -> p.withExistingParent(c.getName(), p.modLoc("block/fluid_tank/block_single_window"))
			.texture("5", p.modLoc("block/creative_fluid_tank_window_single"))
			.texture("1", p.modLoc("block/creative_fluid_tank"))
			.texture("0", p.modLoc("block/creative_casing")))
		.build()
		.register();

	public static final HosePulleyBlock HOSE_PULLEY = createBuilder("hose_pulley", HosePulleyBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.blockstate(BlockStateGen.horizontalBlockProvider(true))
		.transform(StressConfigDefaults.setImpact(4.0))
		.item()
		.transform(customItemModel())
		.register();

	public static final ItemDrainBlock ITEM_DRAIN = createBuilder("item_drain", ItemDrainBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.blockstate((c, p) -> p.simpleBlock(c.get(), AssetLookup.standardModel(c, p)))
		.simpleItem()
		.register();

	public static final SpoutBlock SPOUT = createBuilder("spout", SpoutBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(), AssetLookup.partialBaseModel(ctx, prov)))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.item(BasinOperatorBlockItem::new)
		.transform(customItemModel())
		.register();

	public static final PortableStorageInterfaceBlock PORTABLE_FLUID_INTERFACE = createBuilder("portable_fluid_interface", PortableStorageInterfaceBlock::forFluids)
		.initialProperties(SharedProperties::softMetal)
		.blockstate((c, p) -> p.directionalBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
		.onRegister(addMovementBehaviour(new PortableStorageInterfaceMovement()))
		.item()
		.transform(customItemModel())
		.register();*/

	// Contraptions

	/*public static final MechanicalPistonBlock MECHANICAL_PISTON = createBuilder("mechanical_piston", MechanicalPistonBlock::normal)
		.transform(BuilderTransformers.mechanicalPiston(PistonType.DEFAULT))
		.tag(AllBlockTags.SAFE_NBT.tag)
		.register();

	public static final MechanicalPistonBlock STICKY_MECHANICAL_PISTON = createBuilder("sticky_mechanical_piston", MechanicalPistonBlock::sticky)
		.transform(BuilderTransformers.mechanicalPiston(PistonType.STICKY))
		.tag(AllBlockTags.SAFE_NBT.tag)
		.register();

	public static final PistonExtensionPoleBlock PISTON_EXTENSION_POLE = createBuilder("piston_extension_pole", PistonExtensionPoleBlock::new)
		.initialProperties(() -> Blocks.PISTON_HEAD)
		.blockstate(BlockStateGen.directionalBlockProviderIgnoresWaterlogged(false))
		.simpleItem()
		.register();

	public static final MechanicalPistonHeadBlock MECHANICAL_PISTON_HEAD = createBuilder("mechanical_piston_head", MechanicalPistonHeadBlock::new)
		.initialProperties(() -> Blocks.PISTON_HEAD)
		.loot((p, b) -> p.addDrop(b, PISTON_EXTENSION_POLE.get()))
		.blockstate((c, p) -> BlockStateGen.directionalBlockIgnoresWaterlogged(c, p, state -> p.models()
			.getExistingFile(p.modLoc("block/mechanical_piston/" + state.get(MechanicalPistonHeadBlock.TYPE)
				.asString() + "/head"))))
		.register();

	public static final GantryPinionBlock GANTRY_PINION = createBuilder("gantry_pinion", GantryPinionBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(Block.Properties::nonOpaque)
		.blockstate(BlockStateGen.directionalAxisBlockProvider())
		.item()
		.transform(customItemModel())
		.register();

	public static final GantryShaftBlock GANTRY_SHAFT = createBuilder("gantry_shaft", GantryShaftBlock::new)
		.initialProperties(SharedProperties::stone)
		.blockstate((c, p) -> p.directionalBlock(c.get(), s -> {
			boolean isPowered = s.get(GantryShaftBlock.POWERED);
			boolean isFlipped = s.get(GantryShaftBlock.FACING)
				.getDirection() == AxisDirection.NEGATIVE;
			String partName = s.get(GantryShaftBlock.PART)
				.asString();
			String flipped = isFlipped ? "_flipped" : "";
			String powered = isPowered ? "_powered" : "";
			ModelFile existing = AssetLookup.partialBaseModel(c, p, partName);
			if (!isPowered && !isFlipped)
				return existing;
			return p.models()
				.withExistingParent("block/" + c.getName() + "_" + partName + powered + flipped,
				existing.getLocation())
				.texture("2", p.modLoc("block/" + c.getName() + powered + flipped));
		}))
		.item()
		.transform(customItemModel("_", "block_single"))
		.register();*/

	public static final WindmillBearingBlock WINDMILL_BEARING = createBuilder("windmill_bearing", WindmillBearingBlock::new)
		.consume(BuilderConsumers.bearing("windmill", "gearbox", true))
		.consume(StressConfigDefaults.capacityConsumer(512.0))
//		.tag(AllBlockTags.SAFE_NBT.tag)
		.register();

	public static final MechanicalBearingBlock MECHANICAL_BEARING = createBuilder("mechanical_bearing", MechanicalBearingBlock::new)
		.consume(BuilderConsumers.bearing("mechanical", "gearbox", false))
		.consume(StressConfigDefaults.impactConsumer(4.0))
//		.tag(AllBlockTags.SAFE_NBT.tag)
//		.onRegister(addMovementBehaviour(new StabilizedBearingMovementBehaviour())) TODO
		.register();

	public static final ClockworkBearingBlock CLOCKWORK_BEARING = createBuilder("clockwork_bearing", ClockworkBearingBlock::new)
		.consume(BuilderConsumers.bearing("clockwork", "brass_gearbox", false))
		.consume(StressConfigDefaults.impactConsumer(4.0))
//		.tag(AllBlockTags.SAFE_NBT.tag)
		.register();

	public static final PulleyBlock ROPE_PULLEY = createBuilder("rope_pulley", PulleyBlock::new)
		.initialProperties(SharedProperties::stone)
//		.tag(AllBlockTags.SAFE_NBT.tag)
//		.blockstate(BlockStateGen.horizontalAxisBlockProvider(true))
		.consume(StressConfigDefaults.impactConsumer(4.0))
		.item()
//		.transform(customItemModel())
		.build()
		.register();

	public static final PulleyBlock.RopeBlock ROPE = createBuilder("rope", PulleyBlock.RopeBlock::new)
		.initialProperties(SharedProperties.beltMaterial, MaterialColor.BROWN)
//		.tag(AllBlockTags.BRITTLE.tag)
		.properties(p -> p.sounds(BlockSoundGroup.WOOL))
//		.blockstate((c, p) -> p.simpleBlock(c.get(), p.models()
//		.getExistingFile(p.modLoc("block/rope_pulley/" + c.getName()))))
		.register();

	public static final PulleyBlock.MagnetBlock PULLEY_MAGNET = createBuilder("pulley_magnet", PulleyBlock.MagnetBlock::new)
		.initialProperties(SharedProperties::stone)
//		.tag(AllBlockTags.BRITTLE.tag)
//		.blockstate((c, p) -> p.simpleBlock(c.get(), p.models()
//			.getExistingFile(p.modLoc("block/rope_pulley/" + c.getName()))))
		.register();

	public static final CartAssemblerBlock CART_ASSEMBLER = createBuilder("cart_assembler", CartAssemblerBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(AbstractBlock.Settings::nonOpaque)
//		.blockstate(BlockStateGen.cartAssembler())
		.addLayer(() -> RenderLayer::getCutoutMipped)
//		.tag(BlockTags.RAILS, AllBlockTags.SAFE_NBT.tag)
		.item(CartAssemblerBlockItem::new)
//		.transform(customItemModel())
		.build()
		.register();

	/*public static final ReinforcedRailBlock REINFORCED_RAIL = createBuilder("reinforced_rail", ReinforcedRailBlock::new)
		.initialProperties(SharedProperties::stone)
		.properties(AbstractBlock.Settings::nonOpaque)
		.blockstate(BlockStateGen.reinforcedRail())
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.tag(BlockTags.RAILS)
		.item()
		.model((c, p) -> p.blockItem(() -> c.getEntry()
			.getBlock(), "/block"))
		.build()
		.register();

	public static final ControllerRailBlock CONTROLLER_RAIL = createBuilder("controller_rail", ControllerRailBlock::new)
		.initialProperties(() -> Blocks.POWERED_RAIL)
		.blockstate(new ControllerRailGenerator()::generate)
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.onRegister(CreateRegistrate.blockColors(() -> AllColorHandlers::getRedstonePower))
		.tag(BlockTags.RAILS)
		.item()
		.model((c, p) -> p.generated(c, Create.asResource("block/" + c.getName())))
		.build()
		.register();

	public static final MinecartAnchorBlock MINECART_ANCHOR = createBuilder("minecart_anchor", MinecartAnchorBlock::new)
		.initialProperties(SharedProperties::stone)
		.blockstate((c, p) -> p.simpleBlock(c.get(), p.models()
			.getExistingFile(p.modLoc("block/cart_assembler/" + c.getName()))))
		.register();*/

	public static final LinearChassisBlock LINEAR_CHASSIS = createBuilder("linear_chassis", LinearChassisBlock::new)
		.initialProperties(SharedProperties::wooden)
//		.tag(AllBlockTags.SAFE_NBT.tag)
//		.blockstate(BlockStateGen.linearChassis())
//		.onRegister(connectedTextures(new ChassisCTBehaviour())) TODO
//		.lang("Linear Chassis")
		.simpleItem()
		.register();

	public static final LinearChassisBlock SECONDARY_LINEAR_CHASSIS = createBuilder("secondary_linear_chassis", LinearChassisBlock::new)
		.initialProperties(SharedProperties::wooden)
//		.tag(AllBlockTags.SAFE_NBT.tag)
//		.blockstate(BlockStateGen.linearChassis())
//		.onRegister(connectedTextures(new ChassisCTBehaviour())) TODO
		.simpleItem()
		.register();

	public static final RadialChassisBlock RADIAL_CHASSIS = createBuilder("radial_chassis", RadialChassisBlock::new)
		.initialProperties(SharedProperties::wooden)
//		.tag(AllBlockTags.SAFE_NBT.tag)
//		.blockstate(BlockStateGen.radialChassis())
		.item()
//		.model((c, p) -> {
//			String path = "block/" + c.getName();
//			p.cubeColumn(c.getName(), p.modLoc(path + "_side"), p.modLoc(path + "_end"));
//		})
		.build()
		.register();

	/*public static final DrillBlock MECHANICAL_DRILL = createBuilder("mechanical_drill", DrillBlock::new)
		.initialProperties(SharedProperties::stone)
		.blockstate(BlockStateGen.directionalBlockProvider(true))
		.transform(StressConfigDefaults.setImpact(4.0))
		.onRegister(addMovementBehaviour(new DrillMovementBehaviour()))
		.item()
		.transform(customItemModel())
		.register();

	public static final SawBlock MECHANICAL_SAW = createBuilder("mechanical_saw", SawBlock::new)
		.initialProperties(SharedProperties::stone)
		.blockstate(new SawGenerator()::generate)
		.transform(StressConfigDefaults.setImpact(4.0))
		.onRegister(addMovementBehaviour(new SawMovementBehaviour()))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.item()
		.transform(customItemModel())
		.register();

	public static final DeployerBlock DEPLOYER = createBuilder("deployer", DeployerBlock::new)
		.initialProperties(SharedProperties::stone)
		.blockstate(BlockStateGen.directionalAxisBlockProvider())
		.transform(StressConfigDefaults.setImpact(4.0))
		.onRegister(addMovementBehaviour(new DeployerMovementBehaviour()))
		.item()
		.transform(customItemModel())
		.register();

	public static final PortableStorageInterfaceBlock PORTABLE_STORAGE_INTERFACE = createBuilder("portable_storage_interface", PortableStorageInterfaceBlock::forItems)
		.initialProperties(SharedProperties::stone)
		.blockstate((c, p) -> p.directionalBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
		.onRegister(addMovementBehaviour(new PortableStorageInterfaceMovement()))
		.item()
		.transform(customItemModel())
		.register();

	public static final HarvesterBlock MECHANICAL_HARVESTER = createBuilder("mechanical_harvester", HarvesterBlock::new)
		.initialProperties(SharedProperties::stone)
		.onRegister(addMovementBehaviour(new HarvesterMovementBehaviour()))
		.blockstate(BlockStateGen.horizontalBlockProvider(true))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.item()
		.transform(customItemModel())
		.register();

	public static final PloughBlock MECHANICAL_PLOUGH = createBuilder("mechanical_plough", PloughBlock::new)
		.initialProperties(SharedProperties::stone)
		.onRegister(addMovementBehaviour(new PloughMovementBehaviour()))
		.blockstate(BlockStateGen.horizontalBlockProvider(false))
		.simpleItem()
		.register();

	public static final SeatBlock[] SEATS = new SeatBlock[DyeColor.values().length];

	static {
		// SEATS
		for (DyeColor colour : DyeColor.values()) {
			String colourName = colour.asString();
			SeatMovementBehaviour movementBehaviour = new SeatMovementBehaviour();
			SEATS[colour.ordinal()] = createBuilder(colourName + "_seat", p -> new SeatBlock(p, colour == DyeColor.RED))
				.initialProperties(SharedProperties::wooden)
				.onRegister(addMovementBehaviour(movementBehaviour))
				.blockstate((c, p) -> {
				p.simpleBlock(c.get(), p.models()
					.withExistingParent(colourName + "_seat", p.modLoc("block/seat"))
					.texture("1", p.modLoc("block/seat/top_" + colourName))
					.texture("2", p.modLoc("block/seat/side_" + colourName)));
				})
				.recipe((c, p) -> {
				ShapedRecipeJsonFactory.create(c.get())
					.pattern("#")
					.pattern("-")
					.input('#', DyeHelper.getWoolOfDye(colour))
					.input('-', ItemTags.WOODEN_SLABS)
					.criterion("has_wool", RegistrateRecipeProvider.conditionsFromTag(ItemTags.WOOL))
					.offerTo(p, Create.asResource("crafting/kinetics/" + c.getName()));
				ShapedRecipeJsonFactory.create(c.get())
					.pattern("#")
					.pattern("-")
					.input('#', DyeHelper.getTagOfDye(colour))
					.input('-', AllItemTags.SEATS.tag)
					.criterion("has_seat", RegistrateRecipeProvider.conditionsFromTag(AllItemTags.SEATS.tag))
					.offerTo(p, Create.asResource("crafting/kinetics/" + c.getName() + "_from_other_seat"));
				})
				.onRegisterAfter(Item.class, v -> TooltipHelper.referTo(v, "block.create.seat"))
				.tag(AllBlockTags.SEATS.tag)
				.item()
				.tag(AllItemTags.SEATS.tag)
				.build()
				.register();
		}
	}

	public static final SailBlock SAIL_FRAME = createBuilder("sail_frame", p -> SailBlock.frame(p))
		.initialProperties(SharedProperties::wooden)
		.properties(Block.Properties::nonOpaque)
		.blockstate(BlockStateGen.directionalBlockProvider(false))
		.tag(AllBlockTags.WINDMILL_SAILS.tag)
		.tag(AllBlockTags.FAN_TRANSPARENT.tag)
		.simpleItem()
		.register();

	public static final SailBlock[] DYED_SAILS = new SailBlock[DyeColor.values().length];

	public static final SailBlock SAIL = createBuilder("white_sail", p -> SailBlock.withCanvas(p))
		.initialProperties(SharedProperties::wooden)
		.properties(Block.Properties::nonOpaque)
		.blockstate(BlockStateGen.directionalBlockProvider(false))
		.tag(AllBlockTags.WINDMILL_SAILS.tag)
		.simpleItem()
		.register();

	static {
		// DYED SAILS
		for (DyeColor colour : DyeColor.values()) {
			if (colour == DyeColor.WHITE) {
				DYED_SAILS[colour.ordinal()] = SAIL;
				continue;
			}
			String colourName = colour.asString();
			DYED_SAILS[colour.ordinal()] = createBuilder(colourName + "_sail", p -> SailBlock.withCanvas(p))
				.properties(Block.Properties::nonOpaque)
				.initialProperties(SharedProperties::wooden)
				.blockstate((c, p) -> p.directionalBlock(c.get(), p.models()
					.withExistingParent(colourName + "_sail", p.modLoc("block/white_sail"))
					.texture("0", p.modLoc("block/sail/canvas_" + colourName))))
				.tag(AllBlockTags.WINDMILL_SAILS.tag)
				.tag(AllBlockTags.SAILS.tag)
				.loot((p, b) -> p.addDrop(b, SAIL.get()))
				.register();
		}
	}

	public static final CasingBlock ANDESITE_CASING = createBuilder("andesite_casing", CasingBlock::new)
		.transform(BuilderTransformers.casing(AllSpriteShifts.ANDESITE_CASING))
		.register();

	public static final CasingBlock BRASS_CASING = createBuilder("brass_casing", CasingBlock::new)
		.transform(BuilderTransformers.casing(AllSpriteShifts.BRASS_CASING))
		.register();

	public static final CasingBlock COPPER_CASING = createBuilder("copper_casing", CasingBlock::new)
		.transform(BuilderTransformers.casing(AllSpriteShifts.COPPER_CASING))
		.register();

	public static final CasingBlock SHADOW_STEEL_CASING = createBuilder("shadow_steel_casing", CasingBlock::new)
		.transform(BuilderTransformers.casing(AllSpriteShifts.SHADOW_STEEL_CASING))
		.lang("Shadow Casing")
		.register();

	public static final CasingBlock REFINED_RADIANCE_CASING = createBuilder("refined_radiance_casing", CasingBlock::new)
		.transform(BuilderTransformers.casing(AllSpriteShifts.REFINED_RADIANCE_CASING))
		.properties(p -> p.luminance($ -> 12))
		.lang("Radiant Casing")
		.register();

	public static final MechanicalCrafterBlock MECHANICAL_CRAFTER = createBuilder("mechanical_crafter", MechanicalCrafterBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.properties(AbstractBlock.Settings::nonOpaque)
		.blockstate(BlockStateGen.horizontalBlockProvider(true))
		.transform(StressConfigDefaults.setImpact(2.0))
		.onRegister(CreateRegistrate.connectedTextures(new CrafterCTBehaviour()))
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.item()
		.transform(customItemModel())
		.register();

	public static final SequencedGearshiftBlock SEQUENCED_GEARSHIFT = createBuilder("sequenced_gearshift", SequencedGearshiftBlock::new)
		.initialProperties(SharedProperties::stone)
		.tag(AllBlockTags.SAFE_NBT.tag)
		.properties(AbstractBlock.Settings::nonOpaque)
		.transform(StressConfigDefaults.setNoImpact())
		.blockstate(new SequencedGearshiftGenerator()::generate)
		.item()
		.transform(customItemModel())
		.register();

	public static final FlywheelBlock FLYWHEEL = createBuilder("flywheel", FlywheelBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.properties(AbstractBlock.Settings::nonOpaque)
		.transform(StressConfigDefaults.setNoImpact())
		.blockstate(new FlywheelGenerator()::generate)
		.item()
		.transform(customItemModel())
		.register();

	public static final FurnaceEngineBlock FURNACE_ENGINE = createBuilder("furnace_engine", FurnaceEngineBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.tag(AllBlockTags.BRITTLE.tag)
		.blockstate(BlockStateGen.horizontalBlockProvider(true))
		.transform(StressConfigDefaults.setCapacity(1024.0))
		.item()
		.transform(customItemModel())
		.register();

	public static final SpeedControllerBlock ROTATION_SPEED_CONTROLLER = createBuilder("rotation_speed_controller", SpeedControllerBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.tag(AllBlockTags.SAFE_NBT.tag)
		.transform(StressConfigDefaults.setNoImpact())
		.blockstate(BlockStateGen.horizontalAxisBlockProvider(true))
		.item()
		.transform(customItemModel())
		.register();*/

	// Logistics

	static {
		currentSection = AllSections.LOGISTICS;
	}

	/*public static final ArmBlock MECHANICAL_ARM = createBuilder("mechanical_arm", ArmBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.blockstate((c, p) -> p.getVariantBuilder(c.get())
		.forAllStates(s -> ConfiguredModel.builder()
			.modelFile(AssetLookup.partialBaseModel(c, p))
			.rotationX(s.get(ArmBlock.CEILING) ? 180 : 0)
			.build()))
		.transform(StressConfigDefaults.setImpact(8.0))
		.item(ArmItem::new)
		.transform(customItemModel())
		.register();

	public static final AndesiteFunnelBlock ANDESITE_FUNNEL = createBuilder("andesite_funnel", AndesiteFunnelBlock::new)
		.initialProperties(SharedProperties::stone)
		.tag(AllBlockTags.SAFE_NBT.tag)
		.onRegister(addMovementBehaviour(FunnelMovementBehaviour.andesite()))
		.transform(BuilderTransformers.funnel("andesite", Create.asResource("block/andesite_casing")))
		.register();

	public static final BeltFunnelBlock ANDESITE_BELT_FUNNEL = createBuilder("andesite_belt_funnel", p -> new BeltFunnelBlock(AllBlocks.ANDESITE_FUNNEL, p))
		.initialProperties(SharedProperties::stone)
		.tag(AllBlockTags.SAFE_NBT.tag)
		.blockstate(new BeltFunnelGenerator("andesite", new Identifier("block/polished_andesite"))::generate)
		.loot((p, b) -> p.addDrop(b, ANDESITE_FUNNEL.get()))
		.register();

	public static final BrassFunnelBlock BRASS_FUNNEL = createBuilder("brass_funnel", BrassFunnelBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.tag(AllBlockTags.SAFE_NBT.tag)
		.onRegister(addMovementBehaviour(FunnelMovementBehaviour.brass()))
		.transform(BuilderTransformers.funnel("brass", Create.asResource("block/brass_casing")))
		.register();

	public static final BeltFunnelBlock BRASS_BELT_FUNNEL = createBuilder("brass_belt_funnel", p -> new BeltFunnelBlock(AllBlocks.BRASS_FUNNEL, p))
		.initialProperties(SharedProperties::softMetal)
		.tag(AllBlockTags.SAFE_NBT.tag)
		.blockstate(new BeltFunnelGenerator("brass", Create.asResource("block/brass_block"))::generate)
		.loot((p, b) -> p.addDrop(b, BRASS_FUNNEL.get()))
		.register();

	public static final BeltTunnelBlock ANDESITE_TUNNEL = createBuilder("andesite_tunnel", BeltTunnelBlock::new)
		.transform(BuilderTransformers.beltTunnel("andesite", new Identifier("block/polished_andesite")))
		.register();

	public static final BrassTunnelBlock BRASS_TUNNEL = createBuilder("brass_tunnel", BrassTunnelBlock::new)
		.transform(BuilderTransformers.beltTunnel("brass", Create.asResource("block/brass_block")))
		.onRegister(connectedTextures(new BrassTunnelCTBehaviour()))
		.register();

	public static final RedstoneContactBlock REDSTONE_CONTACT = createBuilder("redstone_contact", RedstoneContactBlock::new)
		.initialProperties(SharedProperties::stone)
		.onRegister(addMovementBehaviour(new ContactMovementBehaviour()))
		.blockstate((c, p) -> p.directionalBlock(c.get(), AssetLookup.forPowered(c, p)))
		.item()
		.transform(customItemModel("_", "block"))
		.register();

	public static final ContentObserverBlock CONTENT_OBSERVER = createBuilder("content_observer", ContentObserverBlock::new)
		.initialProperties(SharedProperties::stone)
		.blockstate((c, p) -> p.horizontalBlock(c.get(), AssetLookup.forPowered(c, p)))
		.item()
		.transform(customItemModel("_", "block"))
		.register();

	public static final StockpileSwitchBlock STOCKPILE_SWITCH = createBuilder("stockpile_switch", StockpileSwitchBlock::new)
		.initialProperties(SharedProperties::stone)
		.blockstate((c, p) -> p.horizontalBlock(c.get(),
			AssetLookup.withIndicator(c, p, $ -> AssetLookup.standardModel(c, p), StockpileSwitchBlock.INDICATOR)))
		.simpleItem()
		.register();

	public static final AdjustableCrateBlock ADJUSTABLE_CRATE = createBuilder("adjustable_crate", AdjustableCrateBlock::new)
		.transform(BuilderTransformers.crate("brass"))
		.register();

	public static final CreativeCrateBlock CREATIVE_CRATE = createBuilder("creative_crate", CreativeCrateBlock::new)
		.transform(BuilderTransformers.crate("creative"))
		.tag(AllBlockTags.SAFE_NBT.tag)
		.register();

	public static final NixieTubeBlock NIXIE_TUBE = createBuilder("nixie_tube", NixieTubeBlock::new)
		.initialProperties(SharedProperties::softMetal)
		.properties(p -> p.luminance($ -> 5))
		.blockstate(new NixieTubeGenerator()::generate)
		.addLayer(() -> RenderLayer::getTranslucent)
		.item()
		.transform(customItemModel())
		.register();

	public static final RedstoneLinkBlock REDSTONE_LINK = createBuilder("redstone_link", RedstoneLinkBlock::new)
		.initialProperties(SharedProperties::wooden)
		.tag(AllBlockTags.BRITTLE.tag, AllBlockTags.SAFE_NBT.tag)
		.blockstate(new RedstoneLinkGenerator()::generate)
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.item()
		.transform(customItemModel("_", "transmitter"))
		.register();*/

	public static final AnalogLeverBlock ANALOG_LEVER = createBuilder("analog_lever", AnalogLeverBlock::new)
		.initialProperties(() -> Blocks.LEVER)
//		.tag(AllBlockTags.SAFE_NBT.tag)
//		.blockstate((c, p) -> p.horizontalFaceBlock(c.get(), AssetLookup.partialBaseModel(c, p)))
		.item()
//		.transform(customItemModel())
		.build()
		.register();

	public static final PulseRepeaterBlock PULSE_REPEATER = createBuilder("pulse_repeater", PulseRepeaterBlock::new)
		.initialProperties(() -> Blocks.REPEATER)
//		.blockstate(new PulseRepeaterGenerator()::generate)
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.item()
//		.transform(customItemModel("diodes", "pulse_repeater"))
		.build()
		.register();

	public static final AdjustableRepeaterBlock ADJUSTABLE_REPEATER = createBuilder("adjustable_repeater", AdjustableRepeaterBlock::new)
		.initialProperties(() -> Blocks.REPEATER)
//		.tag(AllBlockTags.SAFE_NBT.tag)
//		.blockstate(new AdjustableRepeaterGenerator()::generate)
		.item()
//		.model(AbstractDiodeGenerator.diodeItemModel(true))
		.build()
		.register();

	public static final AdjustableRepeaterBlock ADJUSTABLE_PULSE_REPEATER = createBuilder("adjustable_pulse_repeater", AdjustableRepeaterBlock::new)
		.initialProperties(() -> Blocks.REPEATER)
//		.tag(AllBlockTags.SAFE_NBT.tag)
//		.blockstate(new AdjustableRepeaterGenerator()::generate)
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.item()
//		.model(AbstractDiodeGenerator.diodeItemModel(true))
		.build()
		.register();

	public static final PoweredLatchBlock POWERED_LATCH = createBuilder("powered_latch", PoweredLatchBlock::new)
		.initialProperties(() -> Blocks.REPEATER)
//		.blockstate(new PoweredLatchGenerator()::generate)
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.simpleItem()
		.register();

	public static final ToggleLatchBlock POWERED_TOGGLE_LATCH = createBuilder("powered_toggle_latch", ToggleLatchBlock::new)
		.initialProperties(() -> Blocks.REPEATER)
//		.blockstate(new ToggleLatchGenerator()::generate)
		.addLayer(() -> RenderLayer::getCutoutMipped)
		.item()
//		.transform(customItemModel("diodes", "latch_off"))
		.build()
		.register();

	// Materials

	static {
		currentSection = AllSections.MATERIALS;
	}

	/*public static final OxidizingBlock COPPER_ORE = createBuilder("copper_ore", p -> new OxidizingBlock(p, 1))
		.initialProperties(() -> Blocks.IRON_ORE)
		.transform(oxidizedBlockstate())
		.tag(Tags.Blocks.ORES)
		.transform(tagBlockAndItem("ores/copper"))
		.tag(Tags.Items.ORES)
		.transform(oxidizedItemModel())
		.register();*/

	public static final Block ZINC_ORE = createBuilder("zinc_ore", Block::new)
		.initialProperties(() -> Blocks.GOLD_BLOCK)
		.properties(p -> p.breakByTool(FabricToolTags.PICKAXES, 2)
			.sounds(BlockSoundGroup.STONE))
//		.tag(Tags.Blocks.ORES)
//		.transform(tagBlockAndItem("ores/zinc"))
		.simpleItem()
//		.tag(Tags.Items.ORES)
//		.build()
		.register();

	/*public static final OxidizingBlock COPPER_BLOCK = createBuilder("copper_block", p -> new OxidizingBlock(p, 1 / 32f))
		.initialProperties(() -> Blocks.IRON_BLOCK)
		.tag(Tags.Blocks.STORAGE_BLOCKS)
		.tag(AllBlockTags.BEACON_BASE_BLOCKS.tag)
		.transform(tagBlockAndItem("storage_blocks/copper"))
		.tag(Tags.Items.STORAGE_BLOCKS)
		.transform(oxidizedItemModel())
		.transform(oxidizedBlockstate())
		.register();

	public static final OxidizingBlock COPPER_SHINGLES = createBuilder("copper_shingles", p -> new OxidizingBlock(p, 1 / 32f))
		.initialProperties(() -> Blocks.IRON_BLOCK)
		.item()
		.transform(oxidizedItemModel())
		.transform(oxidizedBlockstate())
		.register();

	public static final OxidizingBlock COPPER_TILES = createBuilder("copper_tiles", p -> new OxidizingBlock(p, 1 / 32f))
		.initialProperties(() -> Blocks.IRON_BLOCK)
		.item()
		.transform(oxidizedItemModel())
		.transform(oxidizedBlockstate())
		.register();*/

	public static final Block ZINC_BLOCK = createBuilder("zinc_block", p -> new Block(p))
		.initialProperties(() -> Blocks.IRON_BLOCK)
//		.tag(Tags.Blocks.STORAGE_BLOCKS)
//		.tag(AllBlockTags.BEACON_BASE_BLOCKS.tag)
//		.transform(tagBlockAndItem("storage_blocks/zinc"))
		.simpleItem()
//		.tag(Tags.Items.STORAGE_BLOCKS)
//		.build()
		.register();

	public static final Block BRASS_BLOCK = createBuilder("brass_block", p -> new Block(p))
		.initialProperties(() -> Blocks.IRON_BLOCK)
//		.blockstate((c, p) -> p.simpleBlock(c.get(), p.models()
//			.cubeAll(c.getName(), p.modLoc("block/brass_storage_block"))))
//		.tag(Tags.Blocks.STORAGE_BLOCKS)
//		.tag(AllBlockTags.BEACON_BASE_BLOCKS.tag)
//		.transform(tagBlockAndItem("storage_blocks/brass"))
		.simpleItem()
//		.tag(Tags.Items.STORAGE_BLOCKS)
//		.build()
		.register();

	private static <T extends Block> BlockRegBuilder<T> createBuilder(String id, Function<FabricBlockSettings, T> function) {
		BlockRegBuilder<T> builder = BlockRegBuilder.create(new Identifier(Create.ID, id), function);
		builder.onRegister(block -> AllSections.addToSection(block, currentSection));
		builder.onRegisterItem(item -> AllSections.addToSection(item, currentSection));
		builder.beforeRegisterItem(builder1 -> builder1.properties(settings -> settings.group(itemGroup)));
		return builder;
	}

	// Load this class

	public static void register() {}
}
