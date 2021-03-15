package com.simibubi.create.foundation.networking;


import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.GlueEffectPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.ClientMotionPacket;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.LimbSwingUpdatePacket;
import com.simibubi.create.content.curiosities.symmetry.SymmetryEffectPacket;
import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.ScrollValueUpdatePacket;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.Packet;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.util.Identifier;

public enum AllPackets {

	// Client to Server
	@SuppressWarnings("deprecation")
	NBT(NbtPacket.class),
//	CONFIGURE_SCHEMATICANNON(ConfigureSchematicannonPacket.class, ConfigureSchematicannonPacket::new, PLAY_TO_SERVER),
//	CONFIGURE_FLEXCRATE(ConfigureFlexcratePacket.class, ConfigureFlexcratePacket::new, PLAY_TO_SERVER),
//	CONFIGURE_STOCKSWITCH(ConfigureStockswitchPacket.class, ConfigureStockswitchPacket::new, PLAY_TO_SERVER),
//	CONFIGURE_SEQUENCER(ConfigureSequencedGearshiftPacket.class, ConfigureSequencedGearshiftPacket::new, PLAY_TO_SERVER),
//	PLACE_SCHEMATIC(SchematicPlacePacket.class, SchematicPlacePacket::new, PLAY_TO_SERVER),
//	UPLOAD_SCHEMATIC(SchematicUploadPacket.class, SchematicUploadPacket::new, PLAY_TO_SERVER),
//	CONFIGURE_FILTER(FilterScreenPacket.class, FilterScreenPacket::new, PLAY_TO_SERVER),
//	CONFIGURE_FILTERING_AMOUNT(FilteringCountUpdatePacket.class, FilteringCountUpdatePacket::new, PLAY_TO_SERVER),
	CONFIGURE_SCROLLABLE(ScrollValueUpdatePacket.class),
//	EXTENDO_INTERACT(ExtendoGripInteractionPacket.class, ExtendoGripInteractionPacket::new, PLAY_TO_SERVER),
//	CONTRAPTION_INTERACT(ContraptionInteractionPacket.class, ContraptionInteractionPacket::new, PLAY_TO_SERVER),
	CLIENT_MOTION(ClientMotionPacket.class),
//	PLACE_ARM(ArmPlacementPacket.class, ArmPlacementPacket::new, PLAY_TO_SERVER),
//	MINECART_COUPLING_CREATION(CouplingCreationPacket.class, CouplingCreationPacket::new, PLAY_TO_SERVER),
//	INSTANT_SCHEMATIC(InstantSchematicPacket.class, InstantSchematicPacket::new, PLAY_TO_SERVER),
//	SYNC_SCHEMATIC(SchematicSyncPacket.class, SchematicSyncPacket::new, PLAY_TO_SERVER),
	LEFT_CLICK(LeftClickPacket.class),

	// Server to Client
	SYMMETRY_EFFECT(SymmetryEffectPacket.class),
//	SERVER_SPEED(ServerSpeedProvider.Packet.class, ServerSpeedProvider.Packet::new, PLAY_TO_CLIENT),
//	BEAM_EFFECT(ZapperBeamPacket.class, ZapperBeamPacket::new, PLAY_TO_CLIENT),
//	CONFIGURE_CONFIG(ConfigureConfigPacket.class, ConfigureConfigPacket::new, PLAY_TO_CLIENT),
//	CONTRAPTION_STALL(ContraptionStallPacket.class, ContraptionStallPacket::new, PLAY_TO_CLIENT),
//	CONTRAPTION_DISASSEMBLE(ContraptionDisassemblyPacket.class, ContraptionDisassemblyPacket::new, PLAY_TO_CLIENT),
    GLUE_EFFECT(GlueEffectPacket.class),
//	CONTRAPTION_SEAT_MAPPING(ContraptionSeatMappingPacket.class, ContraptionSeatMappingPacket::new, PLAY_TO_CLIENT),
	LIMBSWING_UPDATE(LimbSwingUpdatePacket.class),
//	MINECART_CONTROLLER(MinecartControllerUpdatePacket.class, MinecartControllerUpdatePacket::new, PLAY_TO_CLIENT),
//	FLUID_SPLASH(FluidSplashPacket.class, FluidSplashPacket::new, PLAY_TO_CLIENT),
//	CONTRAPTION_FLUID(ContraptionFluidPacket.class, ContraptionFluidPacket::new, PLAY_TO_CLIENT),
//	GANTRY_UPDATE(GantryContraptionUpdatePacket.class, GantryContraptionUpdatePacket::new, PLAY_TO_CLIENT),
//	BLOCK_HIGHLIGHT(HighlightPacket.class, HighlightPacket::new, PLAY_TO_CLIENT)

	;

	public static final Identifier CHANNEL_NAME = new Identifier(Create.ID, "network");
	public static final SimpleChannel CHANNEL = new SimpleChannel(CHANNEL_NAME);

	private Class<?> type;

	<T extends Packet> AllPackets(Class<T> type) {
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public static void registerPackets() {
		CHANNEL.initServerListener();
		int id = 0;
		for (AllPackets packet : values()) {
			boolean registered = false;
			if (C2SPacket.class.isAssignableFrom(packet.type)) {
				CHANNEL.registerC2SPacket((Class<C2SPacket>) packet.type, id++);
				registered = true;
			}
			if (S2CPacket.class.isAssignableFrom(packet.type)) {
				CHANNEL.registerS2CPacket((Class<S2CPacket>) packet.type, id++);
				registered = true;
			}
			if (!registered) {
				Create.logger.error("Could not register packet with type " + packet.type.getClass());
			}
		}
	}

	public static void clientInit() {
		CHANNEL.initClientListener();
	}
}
