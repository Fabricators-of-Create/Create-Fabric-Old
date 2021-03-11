package com.simibubi.create;

import com.simibubi.create.content.contraptions.base.BackHalfShaftInstance;
import com.simibubi.create.content.contraptions.base.HalfShaftInstance;
import com.simibubi.create.content.contraptions.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.contraptions.base.SingleRotatingInstance;
import com.simibubi.create.content.contraptions.components.crank.HandCrankBlockEntity;
import com.simibubi.create.content.contraptions.components.crank.HandCrankRenderer;
import com.simibubi.create.content.contraptions.components.fan.NozzleBlockEntity;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorBlockEntity;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.BearingRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.ClockworkBearingBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.WindmillBearingBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.ChassisBlockEntity;
import com.simibubi.create.content.contraptions.components.waterwheel.WaterWheelBlockEntity;
import com.simibubi.create.content.contraptions.relays.belt.BeltBlockEntity;
import com.simibubi.create.content.contraptions.relays.belt.BeltRenderer;
import com.simibubi.create.content.contraptions.relays.elementary.SimpleKineticBlockEntity;
import com.simibubi.create.content.logistics.block.redstone.AnalogLeverBlockEntity;
import com.simibubi.create.content.logistics.block.redstone.AnalogLeverRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

public class AllBlockEntities {
	public static final BlockEntityType<BeltBlockEntity> BELT = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "belt"), BlockEntityType.Builder.create(BeltBlockEntity::new, AllBlocks.BELT).build(null));
	public static final BlockEntityType<SimpleKineticBlockEntity> SIMPLE_KINETIC = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "simple_kinetic"), BlockEntityType.Builder.create(SimpleKineticBlockEntity::new, AllBlocks.SHAFT, AllBlocks.COGWHEEL, AllBlocks.LARGE_COGWHEEL).build(null));
	public static final BlockEntityType<CreativeMotorBlockEntity> MOTOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "motor"), BlockEntityType.Builder.create(CreativeMotorBlockEntity::new, AllBlocks.CREATIVE_MOTOR).build(null));
	public static final BlockEntityType<WaterWheelBlockEntity> WATER_WHEEL = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "water_wheel"), BlockEntityType.Builder.create(WaterWheelBlockEntity::new, AllBlocks.WATER_WHEEL).build(null));
	public static final BlockEntityType<HandCrankBlockEntity> HAND_CRANK = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "hand_crank"), BlockEntityType.Builder.create(HandCrankBlockEntity::new, AllBlocks.HAND_CRANK).build(null));
	public static final BlockEntityType<ChassisBlockEntity> CHASSIS = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "chassis"), BlockEntityType.Builder.create(ChassisBlockEntity::new, AllBlocks.RADIAL_CHASSIS, AllBlocks.LINEAR_CHASSIS, AllBlocks.SECONDARY_LINEAR_CHASSIS).build(null));
	public static final BlockEntityType<NozzleBlockEntity> NOZZLE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "nozzle"), BlockEntityType.Builder.create(NozzleBlockEntity::new, AllBlocks.NOZZLE).build(null));
	public static final BlockEntityType<MechanicalBearingBlockEntity> MECHANICAL_BEARING = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "mechanical_bearing"), BlockEntityType.Builder.create(MechanicalBearingBlockEntity::new, AllBlocks.MECHANICAL_BEARING).build(null));
	public static final BlockEntityType<WindmillBearingBlockEntity> WINDMILL_BEARING = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "windmill_bearing"), BlockEntityType.Builder.create(WindmillBearingBlockEntity::new, AllBlocks.WINDMILL_BEARING).build(null));
	public static final BlockEntityType<ClockworkBearingBlockEntity> CLOCKWORK_BEARING = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "clockwork_bearing"), BlockEntityType.Builder.create(ClockworkBearingBlockEntity::new, AllBlocks.CLOCKWORK_BEARING).build(null));
	public static final BlockEntityType<AnalogLeverBlockEntity> ANALOG_LEVER = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "analog_lever"), BlockEntityType.Builder.create(AnalogLeverBlockEntity::new, AllBlocks.ANALOG_LEVER).build(null));

	public static void registerRenderers() {
		registerRenderer(BELT, BeltRenderer::new);
		registerRenderer(WATER_WHEEL, KineticBlockEntityRenderer::new);
		registerRenderer(HAND_CRANK, HandCrankRenderer::new);
		registerRenderer(MOTOR, CreativeMotorRenderer::new);
		registerRenderer(SIMPLE_KINETIC, KineticBlockEntityRenderer::new);
		registerRenderer(MECHANICAL_BEARING, BearingRenderer::new);
		registerRenderer(WINDMILL_BEARING, BearingRenderer::new);
		registerRenderer(CLOCKWORK_BEARING, BearingRenderer::new);
		registerRenderer(ANALOG_LEVER, AnalogLeverRenderer::new);

		HalfShaftInstance.register(MOTOR);
		SingleRotatingInstance.register(SIMPLE_KINETIC);
		SingleRotatingInstance.register(WATER_WHEEL);
		SingleRotatingInstance.register(HAND_CRANK);
		BackHalfShaftInstance.register(MECHANICAL_BEARING);
		BackHalfShaftInstance.register(WINDMILL_BEARING);
		BackHalfShaftInstance.register(CLOCKWORK_BEARING);
	}

	@SuppressWarnings("unchecked")
	private static void registerRenderer(BlockEntityType<?> type, Function<BlockEntityRenderDispatcher, BlockEntityRenderer<?>> factory) {
		BlockEntityRendererRegistry.INSTANCE.register((BlockEntityType<BlockEntity>) type, (Function<BlockEntityRenderDispatcher, BlockEntityRenderer<BlockEntity>>) (Object) factory);
	}
}
