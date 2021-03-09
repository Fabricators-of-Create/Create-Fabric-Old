package com.simibubi.create;

import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionEntityRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllEntityTypes {

	public static EntityType<OrientedContraptionEntity> ORIENTED_CONTRAPTION;

	public static EntityType<ControlledContraptionEntity> CONTROLLED_CONTRAPTION;


	/**public static final RegistryEntry<EntityType<GantryContraptionEntity>> GANTRY_CONTRAPTION =
		contraption("gantry_contraption", GantryContraptionEntity::new, 10, 40, false);

	public static final RegistryEntry<EntityType<SuperGlueEntity>> SUPER_GLUE = register("super_glue",
		SuperGlueEntity::new, EntityClassification.MISC, 10, Integer.MAX_VALUE, false, true, SuperGlueEntity::build);
	public static final RegistryEntry<EntityType<SeatEntity>> SEAT =
		register("seat", SeatEntity::new, EntityClassification.MISC, 0, Integer.MAX_VALUE, false, true, SeatEntity::build);*/

	//

	public static void register() {
		ORIENTED_CONTRAPTION = Registry.register(Registry.ENTITY_TYPE, new Identifier(Create.ID, "contraption"),
				FabricEntityTypeBuilder.create(SpawnGroup.MISC, OrientedContraptionEntity::new)
						.trackRangeBlocks(5).trackedUpdateRate(3).forceTrackedVelocityUpdates(false).fireImmune().build());

		CONTROLLED_CONTRAPTION = Registry.register(Registry.ENTITY_TYPE, new Identifier(Create.ID, "stationary_contraption"),
				FabricEntityTypeBuilder.create(SpawnGroup.MISC, ControlledContraptionEntity::new)
						.trackRangeBlocks(20).trackedUpdateRate(40).forceTrackedVelocityUpdates(false).fireImmune().build());
	}

	/**private static <T extends Entity> EntityType<T> contraption(String name, IFactory<T> factory,
		int range, int updateFrequency, boolean sendVelocity) {
		return register(name, factory, EntityClassification.MISC, range, updateFrequency, sendVelocity, true,
			AbstractContraptionEntity::build);
	}

	private static <T extends Entity> EntityType<T> register(String name, IFactory<T> factory,
		EntityClassification group, int range, int updateFrequency, boolean sendVelocity, boolean immuneToFire,
		NonNullConsumer<EntityType.Builder<T>> propertyBuilder) {
		String id = Lang.asId(name);
		return Create.com.smellypengu.registrate()
			.entity(id, factory, group)
			.properties(b -> b.setTrackingRange(range)
				.setUpdateInterval(updateFrequency)
				.setShouldReceiveVelocityUpdates(sendVelocity))
			.properties(propertyBuilder)
			.properties(b -> {
				if (immuneToFire)
					b.immuneToFire();
			})
			.register();
	}*/

	@Environment(EnvType.CLIENT)
	public static void registerRenderers() {
		EntityRendererRegistry.INSTANCE.register(ORIENTED_CONTRAPTION, OrientedContraptionEntityRenderer::new);
		EntityRendererRegistry.INSTANCE.register(CONTROLLED_CONTRAPTION, ContraptionEntityRenderer::new);

		/**RenderingRegistry.registerEntityRenderingHandler(GANTRY_CONTRAPTION.get(),
			ContraptionEntityRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SUPER_GLUE.get(), SuperGlueRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(SEAT.get(), SeatEntity.Render::new);*/
	}
}
