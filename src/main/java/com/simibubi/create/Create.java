package com.simibubi.create;

import com.simibubi.create.content.contraptions.TorquePropagator;
import com.simibubi.create.content.palettes.AllPaletteBlocks;
import com.simibubi.create.events.CommonEvents;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.worldgen.AllWorldFeatures;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class Create implements ModInitializer  {
	public static final String ID = "create";
	public static final String NAME = "Create";

	public static Logger logger = LogManager.getLogger();
	public static ItemGroup baseCreativeTab = FabricItemGroupBuilder.build(new Identifier(ID, "base"), () -> new ItemStack(AllBlocks.COGWHEEL));
	public static ItemGroup palettesCreativeTab = FabricItemGroupBuilder.build(new Identifier(ID, "palettes"), () -> new ItemStack(AllItems.ZINC_BLOCK));

	public static TorquePropagator torquePropagator;

	public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(ID);

	@Override
	public void onInitialize() {
		AllBlocks.registerBlocks();
		AllItems.registerItems();
		AllFluids.register();
		AllTags.register();
		AllPaletteBlocks.registerBlocks();
		AllEntityTypes.register();
		AllBlockEntities.register();
		AllMovementBehaviours.register();
		AllConfigs.register();

		torquePropagator = new TorquePropagator();

		AllPackets.registerPackets();

		CommonEvents.register();

		AllWorldFeatures.reload();

		RRPCallback.EVENT.register(a -> a.add(RESOURCE_PACK));

		if (SharedConstants.isDevelopment) MixinEnvironment.getCurrentEnvironment().audit();

		AllTriggers.register();
	}

	public static Identifier asResource(String path) {
		return new Identifier(ID, path);
	}
}
