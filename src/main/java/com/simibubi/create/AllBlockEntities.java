package com.simibubi.create;

import com.simibubi.create.content.contraptions.base.HalfShaftInstance;
import com.simibubi.create.content.contraptions.base.SingleRotatingInstance;
import com.simibubi.create.content.contraptions.components.crank.HandCrankBlockEntity;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorBlockEntity;
import com.simibubi.create.content.contraptions.components.waterwheel.WaterWheelBlockEntity;
import com.simibubi.create.content.contraptions.relays.belt.BeltBlockEntity;
import com.simibubi.create.content.contraptions.relays.belt.BeltRenderer;
import com.simibubi.create.content.contraptions.relays.elementary.SimpleKineticBlockEntity;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllBlockEntities {
	public static BlockEntityType<BeltBlockEntity> BELT = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "belt"), BlockEntityType.Builder.create(BeltBlockEntity::new, AllBlocks.BELT).build(null));

	public static BlockEntityType<SimpleKineticBlockEntity> SIMPLE_KINETIC = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "simple_kinetic"), BlockEntityType.Builder.create(SimpleKineticBlockEntity::new, AllBlocks.SHAFT, AllBlocks.COGWHEEL, AllBlocks.LARGE_COGWHEEL).build(null));

	public static BlockEntityType<CreativeMotorBlockEntity> MOTOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "motor"), BlockEntityType.Builder.create(CreativeMotorBlockEntity::new, AllBlocks.CREATIVE_MOTOR).build(null));

	public static BlockEntityType<WaterWheelBlockEntity> WATER_WHEEL = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "water_wheel"), BlockEntityType.Builder.create(WaterWheelBlockEntity::new, AllBlocks.WATER_WHEEL).build(null));

	public static BlockEntityType<HandCrankBlockEntity> HAND_CRANK = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "hand_crank"), BlockEntityType.Builder.create(HandCrankBlockEntity::new, AllBlocks.HAND_CRANK).build(null));

	public static void registerRenderers() {
		BlockEntityRendererRegistry.INSTANCE.register(BELT, BeltRenderer::new);
		//BlockEntityRendererRegistry.INSTANCE.register(WATER_WHEEL, KineticBlockEntityRenderer::new);
		//BlockEntityRendererRegistry.INSTANCE.register(HAND_CRANK, HandCrankRenderer::new);

		HalfShaftInstance.register(MOTOR);
		SingleRotatingInstance.register(SIMPLE_KINETIC);
		SingleRotatingInstance.register(WATER_WHEEL);
		SingleRotatingInstance.register(HAND_CRANK);
		//BlockEntityRendererRegistry.INSTANCE.register(MOTOR, (BlockEntityRendererFactory) CreativeMotorRenderer::new);

		//BlockEntityRendererRegistry.INSTANCE.register(SIMPLE_KINETIC, (BlockEntityRendererFactory) KineticTileEntityRenderer::new);
	}
}
