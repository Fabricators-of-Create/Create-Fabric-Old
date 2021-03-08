package com.smellypengu.createfabric;

import com.smellypengu.createfabric.content.contraptions.TorquePropagator;
import com.smellypengu.createfabric.content.palettes.AllPaletteBlocks;
import com.smellypengu.createfabric.foundation.worldgen.AllWorldFeatures;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        AllWorldFeatures.reload();

        torquePropagator = new TorquePropagator();

        RRPCallback.EVENT.register(a -> a.add(RESOURCE_PACK));
    }

    public static Identifier asResource(String path) {
        return new Identifier(ID, path);
    }

}
