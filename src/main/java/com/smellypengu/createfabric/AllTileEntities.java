package com.smellypengu.createfabric;

import com.smellypengu.createfabric.content.contraptions.relays.belt.BeltModelProvider;
import com.smellypengu.createfabric.content.contraptions.relays.belt.BeltRenderer;
import com.smellypengu.createfabric.content.contraptions.relays.belt.BeltTileEntity;
import com.smellypengu.createfabric.content.contraptions.relays.elementary.SimpleKineticTileEntity;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllTileEntities {

	public static BlockEntityType<BeltTileEntity> BELT = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "belt"), BlockEntityType.Builder.create(BeltTileEntity::new, AllBlocks.BELT).build(null));

	public static BlockEntityType<SimpleKineticTileEntity> SIMPLE_KINETIC = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(Create.ID, "simple_kinetic"), BlockEntityType.Builder.create(SimpleKineticTileEntity::new, AllBlocks.SHAFT).build(null));

	public static void registerRenderers() {
		BlockEntityRendererRegistry.INSTANCE.register(BELT, BeltRenderer::new);
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new BeltModelProvider());
		//Create.RESOURCE_PACK.addBlockState(JState.state(JState.variant(JState.model(Create.ID + ":block/belt"))), new Identifier("belt"));

		//BlockEntityRendererRegistry.INSTANCE.register(SIMPLE_KINETIC, KineticTileEntityRenderer::new);
	}
}
