package com.simibubi.create;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.simibubi.create.content.contraptions.TorquePropagator;
import com.simibubi.create.content.palettes.AllPaletteBlocks;
import com.simibubi.create.events.CommonEvents;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.worldgen.AllWorldFeatures;

public class Create implements ModInitializer  {

    public static final String ID = "create";

    public static Logger logger = LogManager.getLogger();

    public static TorquePropagator torquePropagator;

    public static final ItemGroup baseCreativeTab = FabricItemGroupBuilder.build(new Identifier(ID, "base"), () -> new ItemStack(AllItems.ZINC_BLOCK));
    public static final ItemGroup palettesCreativeTab = FabricItemGroupBuilder.build(new Identifier(ID, "palettes"), () -> new ItemStack(AllItems.ZINC_BLOCK));

    public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(ID);

    @Override
    public void onInitialize() {
        AllBlocks.registerBlocks();
        AllItems.registerItems();
        AllFluids.register();
        AllTags.register();
        AllPaletteBlocks.registerBlocks();
        AllEntityTypes.register();
        AllMovementBehaviours.register();

        AllPackets.registerPackets();
        
        CommonEvents.register();

        AllWorldFeatures.reload();

        torquePropagator = new TorquePropagator();

        RRPCallback.EVENT.register(a -> a.add(RESOURCE_PACK));
    }

    public static Identifier asResource(String path) {
        return new Identifier(ID, path);
    }

}
