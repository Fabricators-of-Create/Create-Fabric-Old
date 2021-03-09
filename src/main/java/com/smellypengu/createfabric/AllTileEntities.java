package com.smellypengu.createfabric;

import com.smellypengu.createfabric.content.contraptions.base.HalfShaftInstance;
import com.smellypengu.createfabric.content.contraptions.components.motor.CreativeMotorTileEntity;
import com.smellypengu.createfabric.content.contraptions.relays.belt.BeltRenderer;
import com.smellypengu.createfabric.content.contraptions.relays.belt.BeltTileEntity;
import com.smellypengu.createfabric.content.contraptions.relays.elementary.SimpleKineticTileEntity;
import com.smellypengu.createfabric.content.contraptions.relays.encased.ShaftInstance;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllTileEntities {

	public static BlockEntityType<BeltTileEntity> BELT = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "belt"), BlockEntityType.Builder.create(BeltTileEntity::new, AllBlocks.BELT).build(null));

	public static BlockEntityType<SimpleKineticTileEntity> SIMPLE_KINETIC = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "simple_kinetic"), BlockEntityType.Builder.create(SimpleKineticTileEntity::new, AllBlocks.SHAFT).build(null));

	public static BlockEntityType<CreativeMotorTileEntity> MOTOR = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "motor"), BlockEntityType.Builder.create(CreativeMotorTileEntity::new, AllBlocks.CREATIVE_MOTOR).build(null));

	public static void registerRenderers() {
		BlockEntityRendererRegistry.INSTANCE.register(BELT, BeltRenderer::new);

		HalfShaftInstance.register(MOTOR);
		ShaftInstance.register(SIMPLE_KINETIC);
		//BlockEntityRendererRegistry.INSTANCE.register(MOTOR, (BlockEntityRendererFactory) CreativeMotorRenderer::new);

		//BlockEntityRendererRegistry.INSTANCE.register(SIMPLE_KINETIC, (BlockEntityRendererFactory) KineticTileEntityRenderer::new);
	}
}
