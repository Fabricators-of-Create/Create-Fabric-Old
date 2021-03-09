package com.smellypengu.createfabric;

import com.smellypengu.createfabric.content.contraptions.base.HalfShaftInstance;
import com.smellypengu.createfabric.content.contraptions.components.motor.CreativeMotorBlockEntity;
import com.smellypengu.createfabric.content.contraptions.relays.belt.BeltRenderer;
import com.smellypengu.createfabric.content.contraptions.relays.belt.BeltBlockEntity;
import com.smellypengu.createfabric.content.contraptions.relays.elementary.SimpleKineticBlockEntity;
import com.smellypengu.createfabric.content.contraptions.relays.encased.ShaftInstance;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllBlockEntities {
	public static BlockEntityType<BeltBlockEntity> BELT = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "belt"), BlockEntityType.Builder.create(BeltBlockEntity::new, AllBlocks.BELT).build(null));

	public static BlockEntityType<SimpleKineticBlockEntity> SIMPLE_KINETIC = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "simple_kinetic"), BlockEntityType.Builder.create(SimpleKineticBlockEntity::new, AllBlocks.SHAFT).build(null));

	public static BlockEntityType<CreativeMotorBlockEntity> MOTOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "motor"), BlockEntityType.Builder.create(CreativeMotorBlockEntity::new, AllBlocks.CREATIVE_MOTOR).build(null));

	public static void registerRenderers() {
		BlockEntityRendererRegistry.INSTANCE.register(BELT, BeltRenderer::new);

		HalfShaftInstance.register(MOTOR);
		ShaftInstance.register(SIMPLE_KINETIC);
		//BlockEntityRendererRegistry.INSTANCE.register(MOTOR, (BlockEntityRendererFactory) CreativeMotorRenderer::new);

		//BlockEntityRendererRegistry.INSTANCE.register(SIMPLE_KINETIC, (BlockEntityRendererFactory) KineticTileEntityRenderer::new);
	}
}
