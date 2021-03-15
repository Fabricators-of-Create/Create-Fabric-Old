package com.simibubi.create;

import com.simibubi.create.content.contraptions.base.*;
import com.simibubi.create.content.contraptions.components.clock.CuckooClockBlockEntity;
import com.simibubi.create.content.contraptions.components.clock.CuckooClockRenderer;
import com.simibubi.create.content.contraptions.components.crank.HandCrankBlockEntity;
import com.simibubi.create.content.contraptions.components.crank.HandCrankRenderer;
import com.simibubi.create.content.contraptions.components.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.contraptions.components.fan.EncasedFanRenderer;
import com.simibubi.create.content.contraptions.components.fan.FanInstance;
import com.simibubi.create.content.contraptions.components.fan.NozzleBlockEntity;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorBlockEntity;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.BearingRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.ClockworkBearingBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.ChassisBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.mounted.CartAssemblerBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.pulley.PulleyBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.pulley.PulleyRenderer;
import com.simibubi.create.content.contraptions.components.waterwheel.WaterWheelBlockEntity;
import com.simibubi.create.content.contraptions.relays.belt.BeltBlockEntity;
import com.simibubi.create.content.contraptions.relays.belt.BeltInstance;
import com.simibubi.create.content.contraptions.relays.belt.BeltRenderer;
import com.simibubi.create.content.contraptions.relays.elementary.SimpleKineticBlockEntity;
import com.simibubi.create.content.contraptions.relays.encased.ClutchBlockEntity;
import com.simibubi.create.content.contraptions.relays.encased.ShaftInstance;
import com.simibubi.create.content.contraptions.relays.encased.SplitShaftInstance;
import com.simibubi.create.content.contraptions.relays.encased.SplitShaftRenderer;
import com.simibubi.create.content.contraptions.relays.gearbox.GearboxBlockEntity;
import com.simibubi.create.content.contraptions.relays.gearbox.GearboxInstance;
import com.simibubi.create.content.contraptions.relays.gearbox.GearboxRenderer;
import com.simibubi.create.content.contraptions.relays.gearbox.GearshiftBlockEntity;
import com.simibubi.create.content.logistics.block.diodes.AdjustablePulseRepeaterBlockEntity;
import com.simibubi.create.content.logistics.block.diodes.AdjustableRepeaterBlockEntity;
import com.simibubi.create.content.logistics.block.diodes.AdjustableRepeaterRenderer;
import com.simibubi.create.content.logistics.block.redstone.AnalogLeverBlockEntity;
import com.simibubi.create.content.logistics.block.redstone.AnalogLeverRenderer;
import me.pepperbell.reghelper.BlockEntityTypeRegBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class AllBlockEntities {
	// Schematics
	/*public static final BlockEntityType<SchematicannonBlockEntity> SCHEMATICANNON = createBuilder("schematicannon", SchematicannonBlockEntity::new)
		.validBlocks(AllBlocks.SCHEMATICANNON)
		.renderer(() -> SchematicannonRenderer::new)
		.register();

	public static final BlockEntityType<SchematicTableBlockEntity> SCHEMATIC_TABLE = createBuilder("schematic_table", SchematicTableBlockEntity::new)
		.validBlocks(AllBlocks.SCHEMATIC_TABLE)
		.register();*/

	// Kinetics
	public static final BlockEntityType<SimpleKineticBlockEntity> SIMPLE_KINETIC = createBuilder("simple_kinetic", SimpleKineticBlockEntity::create)
		.validBlocks(AllBlocks.SHAFT, AllBlocks.COGWHEEL, AllBlocks.LARGE_COGWHEEL)
		.renderer(() -> KineticBlockEntityRenderer::new)
		.onRegister(SingleRotatingInstance::register)
		.register();

	public static final BlockEntityType<CreativeMotorBlockEntity> MOTOR = createBuilder("motor", CreativeMotorBlockEntity::new)
		.validBlocks(AllBlocks.CREATIVE_MOTOR)
		.renderer(() -> CreativeMotorRenderer::new)
		.onRegister(HalfShaftInstance::register)
		.register();

	public static final BlockEntityType<GearboxBlockEntity> GEARBOX = createBuilder("gearbox", GearboxBlockEntity::new)
		.validBlocks(AllBlocks.GEARBOX)
		.renderer(() -> GearboxRenderer::new)
		.onRegister(GearboxInstance::register)
		.register();

	/*public static final BlockEntityType<EncasedShaftBlockEntity> ENCASED_SHAFT = createBuilder("encased_shaft", EncasedShaftBlockEntity::new)
		.validBlocks(AllBlocks.ANDESITE_ENCASED_SHAFT, AllBlocks.BRASS_ENCASED_SHAFT, AllBlocks.ENCASED_CHAIN_DRIVE)
		.renderer(() -> EncasedShaftRenderer::new)
		.onRegister(ShaftInstance::register)
		.register();

	public static final BlockEntityType<AdjustablePulleyBlockEntity> ADJUSTABLE_PULLEY = createBuilder("adjustable_pulley", AdjustablePulleyBlockEntity::new)
		.validBlocks(AllBlocks.ADJUSTABLE_CHAIN_GEARSHIFT)
		.renderer(() -> EncasedShaftRenderer::new)
		.onRegister(ShaftInstance::register)
		.register();*/

	public static final BlockEntityType<EncasedFanBlockEntity> ENCASED_FAN = createBuilder("encased_fan", EncasedFanBlockEntity::new)
		.validBlocks(AllBlocks.ENCASED_FAN)
		.renderer(() -> EncasedFanRenderer::new)
		.onRegister(FanInstance::register)
		.register();

	public static final BlockEntityType<NozzleBlockEntity> NOZZLE = createBuilder("nozzle", NozzleBlockEntity::new)
		.validBlocks(AllBlocks.NOZZLE)
		// .renderer(() -> renderer)
		.register();

	public static final BlockEntityType<ClutchBlockEntity> CLUTCH = createBuilder("clutch", ClutchBlockEntity::new)
		.validBlocks(AllBlocks.CLUTCH)
		.renderer(() -> SplitShaftRenderer::new)
		.onRegister(SplitShaftInstance::register)
		.register();

	public static final BlockEntityType<GearshiftBlockEntity> GEARSHIFT = createBuilder("gearshift", GearshiftBlockEntity::new)
		.validBlocks(AllBlocks.GEARSHIFT)
		.renderer(() -> SplitShaftRenderer::new)
		.onRegister(SplitShaftInstance::register)
		.register();

	/*public static final BlockEntityType<TurntableBlockEntity> TURNTABLE = createBuilder("turntable", TurntableBlockEntity::new)
		.validBlocks(AllBlocks.TURNTABLE)
		.renderer(() -> KineticBlockEntityRenderer::new)
		.onRegister(SingleRotatingInstance::register)
		.register();*/

	public static final BlockEntityType<HandCrankBlockEntity> HAND_CRANK = createBuilder("hand_crank", HandCrankBlockEntity::new)
		.validBlocks(AllBlocks.HAND_CRANK, AllBlocks.COPPER_VALVE_HANDLE)
		.validBlocks(AllBlocks.DYED_VALVE_HANDLES)
		.renderer(() -> HandCrankRenderer::new)
		.onRegister(SingleRotatingInstance::register)
		.register();

	public static final BlockEntityType<CuckooClockBlockEntity> CUCKOO_CLOCK = createBuilder("cuckoo_clock", CuckooClockBlockEntity::new)
		.validBlocks(AllBlocks.CUCKOO_CLOCK, AllBlocks.MYSTERIOUS_CUCKOO_CLOCK)
		.renderer(() -> CuckooClockRenderer::new)
		.onRegister(HorizontalHalfShaftInstance::register)
		.register();
	
	/*public static final BlockEntityType<GantryShaftBlockEntity> GANTRY_SHAFT = createBuilder("gantry_shaft", GantryShaftBlockEntity::new)
		.validBlocks(AllBlocks.GANTRY_SHAFT)
		.renderer(() -> KineticBlockEntityRenderer::new)
		.onRegister(SingleRotatingInstance::register)
		.register();
	
	public static final BlockEntityType<GantryPinionBlockEntity> GANTRY_PINION = createBuilder("gantry_pinion", GantryPinionBlockEntity::new)
		.validBlocks(AllBlocks.GANTRY_PINION)
		.renderer(() -> GantryPinionRenderer::new)
		.onRegister(ShaftInstance::register)
		.register();

	public static final BlockEntityType<PumpBlockEntity> MECHANICAL_PUMP = createBuilder("mechanical_pump", PumpBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_PUMP)
		.renderer(() -> PumpRenderer::new)
		.onRegister(PumpCogInstance::register)
		.register();

	public static final BlockEntityType<SmartFluidPipeBlockEntity> SMART_FLUID_PIPE = createBuilder("smart_fluid_pipe", SmartFluidPipeBlockEntity::new)
		.validBlocks(AllBlocks.SMART_FLUID_PIPE)
		.renderer(() -> SmartBlockEntityRenderer::new)
		.register();

	public static final BlockEntityType<FluidPipeBlockEntity> FLUID_PIPE = createBuilder("fluid_pipe", FluidPipeBlockEntity::new)
		.validBlocks(AllBlocks.FLUID_PIPE)
		.register();

	public static final BlockEntityType<FluidPipeBlockEntity> ENCASED_FLUID_PIPE = createBuilder("encased_fluid_pipe", FluidPipeBlockEntity::new)
		.validBlocks(AllBlocks.ENCASED_FLUID_PIPE)
		.register();

	public static final BlockEntityType<StraightPipeBlockEntity> GLASS_FLUID_PIPE = createBuilder("glass_fluid_pipe", StraightPipeBlockEntity::new)
		.validBlocks(AllBlocks.GLASS_FLUID_PIPE)
		.renderer(() -> TransparentStraightPipeRenderer::new)
		.register();

	public static final BlockEntityType<FluidValveBlockEntity> FLUID_VALVE = createBuilder("fluid_valve", FluidValveBlockEntity::new)
		.validBlocks(AllBlocks.FLUID_VALVE)
		.renderer(() -> FluidValveRenderer::new)
		.onRegister(ShaftInstance::register)
		.register();

	public static final BlockEntityType<FluidTankBlockEntity> FLUID_TANK = createBuilder("fluid_tank", FluidTankBlockEntity::new)
		.validBlocks(AllBlocks.FLUID_TANK)
		.renderer(() -> FluidTankRenderer::new)
		.register();

	public static final BlockEntityType<CreativeFluidTankBlockEntity> CREATIVE_FLUID_TANK = createBuilder("creative_fluid_tank", CreativeFluidTankBlockEntity::new)
		.validBlocks(AllBlocks.CREATIVE_FLUID_TANK)
		.renderer(() -> FluidTankRenderer::new)
		.register();

	public static final BlockEntityType<HosePulleyBlockEntity> HOSE_PULLEY = createBuilder("hose_pulley", HosePulleyBlockEntity::new)
		.validBlocks(AllBlocks.HOSE_PULLEY)
		.renderer(() -> HosePulleyRenderer::new)
		.onRegister(ShaftInstance::register)
		.register();

	public static final BlockEntityType<SpoutBlockEntity> SPOUT = createBuilder("spout", SpoutBlockEntity::new)
		.validBlocks(AllBlocks.SPOUT)
		.renderer(() -> SpoutRenderer::new)
		.register();

	public static final BlockEntityType<ItemDrainBlockEntity> ITEM_DRAIN = createBuilder("item_drain", ItemDrainBlockEntity::new)
		.validBlocks(AllBlocks.ITEM_DRAIN)
		.renderer(() -> ItemDrainRenderer::new)
		.register();*/

	public static final BlockEntityType<BeltBlockEntity> BELT = createBuilder("belt", BeltBlockEntity::new)
		.validBlocks(AllBlocks.BELT)
		.renderer(() -> BeltRenderer::new)
		.onRegister(BeltInstance::register)
		.register();

	/*public static final BlockEntityType<ChuteBlockEntity> CHUTE = createBuilder("chute", ChuteBlockEntity::new)
		.validBlocks(AllBlocks.CHUTE)
		.renderer(() -> ChuteRenderer::new)
		.register();

	public static final BlockEntityType<SmartChuteBlockEntity> SMART_CHUTE = createBuilder("smart_chute", SmartChuteBlockEntity::new)
		.validBlocks(AllBlocks.SMART_CHUTE)
		.renderer(() -> SmartChuteRenderer::new)
		.register();

	public static final BlockEntityType<BeltTunnelBlockEntity> ANDESITE_TUNNEL = createBuilder("andesite_tunnel", BeltTunnelBlockEntity::new)
		.validBlocks(AllBlocks.ANDESITE_TUNNEL)
		.renderer(() -> BeltTunnelRenderer::new)
		.register();

	public static final BlockEntityType<BrassTunnelBlockEntity> BRASS_TUNNEL = createBuilder("brass_tunnel", BrassTunnelBlockEntity::new)
		.validBlocks(AllBlocks.BRASS_TUNNEL)
		.renderer(() -> BeltTunnelRenderer::new)
		.register();

	public static final BlockEntityType<ArmBlockEntity> MECHANICAL_ARM = createBuilder("mechanical_arm", ArmBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_ARM)
		.renderer(() -> ArmRenderer::new)
		.onRegister(ArmInstance::register)
		.register();

	public static final BlockEntityType<MechanicalPistonBlockEntity> MECHANICAL_PISTON = createBuilder("mechanical_piston", MechanicalPistonBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_PISTON, AllBlocks.STICKY_MECHANICAL_PISTON)
		.renderer(() -> MechanicalPistonRenderer::new)
		.onRegister(ShaftInstance::register)
		.register();*/

	public static final BlockEntityType<WindmillBearingBlockEntity> WINDMILL_BEARING = createBuilder("windmill_bearing", WindmillBearingBlockEntity::new)
		.validBlocks(AllBlocks.WINDMILL_BEARING)
		.renderer(() -> BearingRenderer::new)
		.onRegister(BackHalfShaftInstance::register)
		.register();

	public static final BlockEntityType<MechanicalBearingBlockEntity> MECHANICAL_BEARING = createBuilder("mechanical_bearing", MechanicalBearingBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_BEARING)
		.renderer(() -> BearingRenderer::new)
		.onRegister(BackHalfShaftInstance::register)
		.register();

	public static final BlockEntityType<ClockworkBearingBlockEntity> CLOCKWORK_BEARING = createBuilder("clockwork_bearing", ClockworkBearingBlockEntity::new)
		.validBlocks(AllBlocks.CLOCKWORK_BEARING)
		.renderer(() -> BearingRenderer::new)
		.onRegister(BackHalfShaftInstance::register)
		.register();

	public static final BlockEntityType<PulleyBlockEntity> ROPE_PULLEY = createBuilder("rope_pulley", PulleyBlockEntity::new)
		.validBlocks(AllBlocks.ROPE_PULLEY)
		.renderer(() -> PulleyRenderer::new)
		.onRegister(ShaftInstance::register)
		.register();

	public static final BlockEntityType<ChassisBlockEntity> CHASSIS = createBuilder("chassis", ChassisBlockEntity::new)
		.validBlocks(AllBlocks.RADIAL_CHASSIS, AllBlocks.LINEAR_CHASSIS, AllBlocks.SECONDARY_LINEAR_CHASSIS)
		// .renderer(() -> renderer)
		.register();

	/*public static final BlockEntityType<DrillBlockEntity> DRILL = createBuilder("drill", DrillBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_DRILL)
		.renderer(() -> DrillRenderer::new)
		.onRegister(DrillInstance::register)
		.register();

	public static final BlockEntityType<SawBlockEntity> SAW = createBuilder("saw", SawBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_SAW)
		.renderer(() -> SawRenderer::new)
		.onRegister(SawInstance::register)
		.register();

	public static final BlockEntityType<HarvesterBlockEntity> HARVESTER = createBuilder("harvester", HarvesterBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_HARVESTER)
		.renderer(() -> HarvesterRenderer::new)
		.register();

	public static final BlockEntityType<PortableItemInterfaceBlockEntity> PORTABLE_STORAGE_INTERFACE = createBuilder("portable_storage_interface", PortableItemInterfaceBlockEntity::new)
		.validBlocks(AllBlocks.PORTABLE_STORAGE_INTERFACE)
		.renderer(() -> PortableStorageInterfaceRenderer::new)
		.register();

	public static final BlockEntityType<PortableFluidInterfaceBlockEntity> PORTABLE_FLUID_INTERFACE = createBuilder("portable_fluid_interface", PortableFluidInterfaceBlockEntity::new)
		.validBlocks(AllBlocks.PORTABLE_FLUID_INTERFACE)
		.renderer(() -> PortableStorageInterfaceRenderer::new)
		.register();

	public static final BlockEntityType<FlywheelBlockEntity> FLYWHEEL = createBuilder("flywheel", FlywheelBlockEntity::new)
		.validBlocks(AllBlocks.FLYWHEEL)
		.renderer(() -> FlywheelRenderer::new)
		.onRegister(FlyWheelInstance::register)
		.register();

	public static final BlockEntityType<FurnaceEngineBlockEntity> FURNACE_ENGINE = createBuilder("furnace_engine", FurnaceEngineBlockEntity::new)
		.validBlocks(AllBlocks.FURNACE_ENGINE)
		.renderer(() -> EngineRenderer::new)
		.register();

	public static final BlockEntityType<MillstoneBlockEntity> MILLSTONE = createBuilder("millstone", MillstoneBlockEntity::new)
		.validBlocks(AllBlocks.MILLSTONE)
		.renderer(() -> MillstoneRenderer::new)
		.onRegister(MillStoneCogInstance::register)
		.register();

	public static final BlockEntityType<CrushingWheelBlockEntity> CRUSHING_WHEEL = createBuilder("crushing_wheel", CrushingWheelBlockEntity::new)
		.validBlocks(AllBlocks.CRUSHING_WHEEL)
		.renderer(() -> KineticBlockEntityRenderer::new)
		.onRegister(SingleRotatingInstance::register)
		.register();

	public static final BlockEntityType<CrushingWheelControllerBlockEntity> CRUSHING_WHEEL_CONTROLLER = createBuilder("crushing_wheel_controller", CrushingWheelControllerBlockEntity::new)
		.validBlocks(AllBlocks.CRUSHING_WHEEL_CONTROLLER)
		// .renderer(() -> renderer)
		.register();*/

	public static final BlockEntityType<WaterWheelBlockEntity> WATER_WHEEL = createBuilder("water_wheel", WaterWheelBlockEntity::new)
		.validBlocks(AllBlocks.WATER_WHEEL)
		.renderer(() -> KineticBlockEntityRenderer::new)
		.onRegister(SingleRotatingInstance::register)
		.register();

	/*public static final BlockEntityType<MechanicalPressBlockEntity> MECHANICAL_PRESS = createBuilder("mechanical_press", MechanicalPressBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_PRESS)
		.renderer(() -> MechanicalPressRenderer::new)
		.onRegister(ShaftInstance::register)
		.register();

	public static final BlockEntityType<MechanicalMixerBlockEntity> MECHANICAL_MIXER = createBuilder("mechanical_mixer", MechanicalMixerBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_MIXER)
		.renderer(() -> MechanicalMixerRenderer::new)
		.onRegister(ShaftlessCogInstance::register)
		.register();

	public static final BlockEntityType<DeployerBlockEntity> DEPLOYER = createBuilder("deployer", DeployerBlockEntity::new)
		.validBlocks(AllBlocks.DEPLOYER)
		.renderer(() -> DeployerRenderer::new)
		.onRegister(ShaftInstance::register)
		.register();

	public static final BlockEntityType<BasinBlockEntity> BASIN = createBuilder("basin", BasinBlockEntity::new)
		.validBlocks(AllBlocks.BASIN)
		.renderer(() -> BasinRenderer::new)
		.register();

	public static final BlockEntityType<BlazeBurnerBlockEntity> HEATER = createBuilder("blaze_heater", BlazeBurnerBlockEntity::new)
		.validBlocks(AllBlocks.BLAZE_BURNER)
		.renderer(() -> BlazeBurnerRenderer::new)
		.register();

	public static final BlockEntityType<MechanicalCrafterBlockEntity> MECHANICAL_CRAFTER = createBuilder("mechanical_crafter", MechanicalCrafterBlockEntity::new)
		.validBlocks(AllBlocks.MECHANICAL_CRAFTER)
		.renderer(() -> MechanicalCrafterRenderer::new)
		.onRegister(MechanicalCrafterInstance::register)
		.register();

	public static final BlockEntityType<SequencedGearshiftBlockEntity> SEQUENCED_GEARSHIFT = createBuilder("sequenced_gearshift", SequencedGearshiftBlockEntity::new)
		.validBlocks(AllBlocks.SEQUENCED_GEARSHIFT)
		.renderer(() -> SplitShaftRenderer::new)
		.onRegister(SplitShaftInstance::register)
		.register();

	public static final BlockEntityType<SpeedControllerBlockEntity> ROTATION_SPEED_CONTROLLER = createBuilder("rotation_speed_controller", SpeedControllerBlockEntity::new)
		.validBlocks(AllBlocks.ROTATION_SPEED_CONTROLLER)
		.renderer(() -> SpeedControllerRenderer::new)
		.onRegister(ShaftInstance::register)
		.register();

	public static final BlockEntityType<SpeedGaugeBlockEntity> SPEEDOMETER = createBuilder("speedometer", SpeedGaugeBlockEntity::new)
		.validBlocks(AllBlocks.SPEEDOMETER)
		.renderer(() -> GaugeRenderer::speed)
		.onRegister(ShaftInstance::register)
		.register();

	public static final BlockEntityType<StressGaugeBlockEntity> STRESSOMETER = createBuilder("stressometer", StressGaugeBlockEntity::new)
		.validBlocks(AllBlocks.STRESSOMETER)
		.renderer(() -> GaugeRenderer::stress)
		.onRegister(ShaftInstance::register)
		.register();*/

	public static final BlockEntityType<AnalogLeverBlockEntity> ANALOG_LEVER = createBuilder("analog_lever", AnalogLeverBlockEntity::new)
		.validBlocks(AllBlocks.ANALOG_LEVER)
		.renderer(() -> AnalogLeverRenderer::new)
		.register();

	public static final BlockEntityType<CartAssemblerBlockEntity> CART_ASSEMBLER = createBuilder("cart_assembler", CartAssemblerBlockEntity::new)
		.validBlocks(AllBlocks.CART_ASSEMBLER)
		// .renderer(() -> renderer)
		.register();

	// Logistics
	/*public static final BlockEntityType<RedstoneLinkBlockEntity> REDSTONE_LINK = createBuilder("redstone_link", RedstoneLinkBlockEntity::new)
		.validBlocks(AllBlocks.REDSTONE_LINK)
		.renderer(() -> SmartBlockEntityRenderer::new)
		.register();

	public static final BlockEntityType<NixieTubeBlockEntity> NIXIE_TUBE = createBuilder("nixie_tube", NixieTubeBlockEntity::new)
		.validBlocks(AllBlocks.NIXIE_TUBE)
		.renderer(() -> NixieTubeRenderer::new)
		.register();

	public static final BlockEntityType<StockpileSwitchBlockEntity> STOCKPILE_SWITCH = createBuilder("stockpile_switch", StockpileSwitchBlockEntity::new)
		.validBlocks(AllBlocks.STOCKPILE_SWITCH)
		.renderer(() -> SmartBlockEntityRenderer::new)
		.register();

	public static final BlockEntityType<AdjustableCrateBlockEntity> ADJUSTABLE_CRATE = createBuilder("adjustable_crate", AdjustableCrateBlockEntity::new)
		.validBlocks(AllBlocks.ADJUSTABLE_CRATE)
		// .renderer(() -> renderer)
		.register();

	public static final BlockEntityType<CreativeCrateBlockEntity> CREATIVE_CRATE = createBuilder("creative_crate", CreativeCrateBlockEntity::new)
		.validBlocks(AllBlocks.CREATIVE_CRATE)
		.renderer(() -> SmartBlockEntityRenderer::new)
		.register();

	public static final BlockEntityType<DepotBlockEntity> DEPOT = createBuilder("depot", DepotBlockEntity::new)
		.validBlocks(AllBlocks.DEPOT)
		.renderer(() -> DepotRenderer::new)
		.register();

	public static final BlockEntityType<FunnelBlockEntity> FUNNEL = createBuilder("funnel", FunnelBlockEntity::new)
		.validBlocks(AllBlocks.BRASS_FUNNEL, AllBlocks.BRASS_BELT_FUNNEL, AllBlocks.ANDESITE_FUNNEL,
			AllBlocks.ANDESITE_BELT_FUNNEL)
		.renderer(() -> FunnelRenderer::new)
		.register();

	public static final BlockEntityType<ContentObserverBlockEntity> CONTENT_OBSERVER = createBuilder("content_observer", ContentObserverBlockEntity::new)
		.validBlocks(AllBlocks.CONTENT_OBSERVER)
		.renderer(() -> SmartBlockEntityRenderer::new)
		.register();*/

	public static final BlockEntityType<AdjustableRepeaterBlockEntity> ADJUSTABLE_REPEATER = createBuilder("adjustable_repeater", AdjustableRepeaterBlockEntity::new)
		.validBlocks(AllBlocks.ADJUSTABLE_REPEATER)
		.renderer(() -> AdjustableRepeaterRenderer::new)
		.register();

	public static final BlockEntityType<AdjustablePulseRepeaterBlockEntity> ADJUSTABLE_PULSE_REPEATER = createBuilder("adjustable_pulse_repeater", AdjustablePulseRepeaterBlockEntity::new)
		.validBlocks(AllBlocks.ADJUSTABLE_PULSE_REPEATER)
		.renderer(() -> AdjustableRepeaterRenderer::new)
		.register();

	private static <T extends BlockEntity> BlockEntityTypeRegBuilder<T> createBuilder(String id, Supplier<T> supplier) {
		return BlockEntityTypeRegBuilder.create(new Identifier(Create.ID, id), supplier);
	}

	public static void register() {}
}
