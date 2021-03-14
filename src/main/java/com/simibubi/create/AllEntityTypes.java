package com.simibubi.create;

import java.util.function.Consumer;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionEntityRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntityRenderer;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueRenderer;
import com.simibubi.create.foundation.mixinterface.EntityTypeExtension;
import com.simibubi.create.foundation.utility.Lang;

import me.pepperbell.reghelper.EntityTypeRegBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.EntityFactory;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;

public class AllEntityTypes {
	public static final EntityType<OrientedContraptionEntity> ORIENTED_CONTRAPTION =
		contraption("contraption", OrientedContraptionEntity::new, 5, 3, true);
	public static final EntityType<ControlledContraptionEntity> CONTROLLED_CONTRAPTION =
		contraption("stationary_contraption", ControlledContraptionEntity::new, 20, 40, false);
//	public static final EntityType<GantryContraptionEntity> GANTRY_CONTRAPTION =
//		contraption("gantry_contraption", GantryContraptionEntity::new, 10, 40, false);
	public static final EntityType<SuperGlueEntity> SUPER_GLUE = register("super_glue",
		SuperGlueEntity::new, SpawnGroup.MISC, 10, Integer.MAX_VALUE, false, SuperGlueEntity::build);
//	public static final RegistryEntry<EntityType<SeatEntity>> SEAT =
//		register("seat", SeatEntity::new, SpawnGroup.MISC, 0, Integer.MAX_VALUE, false, SeatEntity::build);

	//

	private static <T extends Entity> EntityType<T> contraption(String name, EntityFactory<T> factory,
		int range, int updateFrequency, boolean sendVelocity) {
		return register(name, factory, SpawnGroup.MISC, range, updateFrequency, sendVelocity,
			AbstractContraptionEntity::build);
	}

	private static <T extends Entity> EntityType<T> register(String name, EntityFactory<T> factory,
		SpawnGroup group, int range, int updateFrequency, boolean sendVelocity,
		Consumer<EntityType.Builder<T>> propertyBuilder) {
		String id = Lang.asId(name);
		EntityType<T> type = createBuilder(id, factory, group)
			.properties(b -> b.maxTrackingRange(range)
				.trackingTickInterval(updateFrequency))
			.properties(propertyBuilder)
			.register();
		((EntityTypeExtension) type).setAlwaysUpdateVelocity(TriState.of(sendVelocity));
		return type;
	}

	private static <T extends Entity> EntityTypeRegBuilder<T> createBuilder(String id, EntityFactory<T> factory, SpawnGroup spawnGroup) {
		return EntityTypeRegBuilder.create(new Identifier(Create.ID, id), factory, spawnGroup);
	}

	public static void register() {}

	@Environment(EnvType.CLIENT)
	public static void registerRenderers() {
		EntityRendererRegistry.INSTANCE.register(CONTROLLED_CONTRAPTION, ContraptionEntityRenderer::new);
		EntityRendererRegistry.INSTANCE.register(ORIENTED_CONTRAPTION, OrientedContraptionEntityRenderer::new);
//		EntityRendererRegistry.INSTANCE.register(GANTRY_CONTRAPTION, ContraptionEntityRenderer::new);
		EntityRendererRegistry.INSTANCE.register(SUPER_GLUE, SuperGlueRenderer::new);
//		EntityRendererRegistry.INSTANCE.register(SEAT, SeatEntity.Render::new);
	}
}

