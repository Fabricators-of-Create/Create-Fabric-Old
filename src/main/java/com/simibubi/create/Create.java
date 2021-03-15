package com.simibubi.create;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.MixinEnvironment;

import com.simibubi.create.content.contraptions.TorquePropagator;
import com.simibubi.create.content.palettes.AllPaletteBlocks;
import com.simibubi.create.events.CommonEvents;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.resource.TranslationsHolder;
import com.simibubi.create.foundation.worldgen.AllWorldFeatures;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Random;

public class Create implements ModInitializer  {
	public static final String ID = "create";
	public static final String NAME = "Create";

	public static Logger logger = LogManager.getLogger();
	public static ItemGroup baseCreativeTab = FabricItemGroupBuilder.build(id("base"), () -> new ItemStack(AllBlocks.COGWHEEL));
	public static ItemGroup palettesCreativeTab = FabricItemGroupBuilder.build(id("palettes"), () -> new ItemStack(AllBlocks.ZINC_BLOCK));

	public static TorquePropagator torquePropagator;
	public static Random random;


	@Override
	public void onInitialize() {
		AllBlocks.register();
		AllItems.register();
		AllFluids.register();
		AllTags.register();
		AllPaletteBlocks.registerBlocks();
		AllEntityTypes.register();
		AllBlockEntities.register();
		AllMovementBehaviours.register();
		AllConfigs.register();

		random = new Random();

		torquePropagator = new TorquePropagator();

		AllPackets.registerPackets();

		CommonEvents.register();

		AllWorldFeatures.reload();

		TranslationsHolder.initialize();

		if (SharedConstants.isDevelopment) MixinEnvironment.getCurrentEnvironment().audit();

		AllTriggers.register();
	}

	public static Identifier id(String path) {
		return new Identifier(ID, path);
	}
	public static AllConfigs getConfig() {
		AllConfigs config = AutoConfig.getConfigHolder(AllConfigs.class).getConfig();
		try {
			config.validatePostLoad(); // The best way to validate :)
		} catch (ConfigData.ValidationException e) {
			throw new RuntimeException(e);
		}
		return config;
	}
}
