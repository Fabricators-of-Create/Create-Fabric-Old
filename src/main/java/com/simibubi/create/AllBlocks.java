package com.simibubi.create;

import com.simibubi.create.content.contraptions.components.fan.NozzleBlock;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.LinearChassisBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.RadialChassisBlock;
import com.simibubi.create.content.contraptions.components.waterwheel.WaterWheelBlock;
import com.simibubi.create.content.contraptions.relays.belt.BeltBlock;
import com.simibubi.create.content.contraptions.relays.elementary.CogWheelBlock;
import com.simibubi.create.content.contraptions.relays.elementary.CogwheelBlockItem;
import com.simibubi.create.content.contraptions.relays.elementary.ShaftBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AllBlocks {

    public static final Block ZINC_ORE = new Block(FabricBlockSettings
            .of(Material.METAL)
            .breakByTool(FabricToolTags.PICKAXES)
            .requiresTool()
            .strength(5.0f, 30.0f)
            .sounds(BlockSoundGroup.STONE));

    public static final Block ZINC_BLOCK = new Block(FabricBlockSettings
            .of(Material.METAL)
            .breakByTool(FabricToolTags.PICKAXES, 2)
            .requiresTool()
            .strength(5.0f, 30.0f)
            .sounds(BlockSoundGroup.METAL));

    public static final BeltBlock BELT = new BeltBlock(Block.Settings
            .of(Material.WOOL)
            .strength(0.8f, 0.8f));

    public static final ShaftBlock SHAFT = new ShaftBlock(Block.Settings
            .of(Material.STONE)
            .strength(0.8f, 0.8f)
            .nonOpaque());

    public static final CreativeMotorBlock CREATIVE_MOTOR = new CreativeMotorBlock(Block.Settings
            .of(Material.STONE)
            .strength(0.8f, 0.8f)
            .nonOpaque());

	public static final CogWheelBlock COGWHEEL = new CogWheelBlock(false, Block.Settings
		.of(Material.STONE)
		.strength(0.8f, 0.8f)
		.sounds(BlockSoundGroup.WOOD)
		.nonOpaque());

	public static final CogWheelBlock LARGE_COGWHEEL = new CogWheelBlock(true, Block.Settings
		.of(Material.STONE)
		.strength(0.8f, 0.8f)
		.sounds(BlockSoundGroup.WOOD)
		.nonOpaque());

	public static final WaterWheelBlock WATER_WHEEL = new WaterWheelBlock(Block.Settings
		.of(Material.WOOD)
		.strength(0.8f, 0.8f)
		.nonOpaque());

	public static final WaterWheelBlock HAND_CRANK = new WaterWheelBlock(Block.Settings
		.of(Material.WOOD)
		.strength(0.8f, 0.8f)
		.nonOpaque());

	public static final LinearChassisBlock LINEAR_CHASSIS = new LinearChassisBlock(Block.Settings
		.of(Material.WOOD)
		.strength(0.8f, 0.8f));

	public static final LinearChassisBlock SECONDARY_LINEAR_CHASSIS = new LinearChassisBlock(Block.Settings
		.of(Material.WOOD)
		.strength(0.8f, 0.8f));

	public static final RadialChassisBlock RADIAL_CHASSIS = new RadialChassisBlock(Block.Settings
		.of(Material.WOOD)
		.strength(0.8f, 0.8f));

	public static final NozzleBlock NOZZLE = new NozzleBlock(Block.Settings
		.of(Material.WOOD)
		.strength(0.8f, 0.8f)
		.nonOpaque());

	public static void registerBlocks() {
        Registry.register(Registry.BLOCK, new Identifier(Create.ID, "zinc_ore"), ZINC_ORE);
        Registry.register(Registry.BLOCK, new Identifier(Create.ID, "zinc_block"), ZINC_BLOCK);

        Registry.register(Registry.BLOCK, new Identifier(Create.ID, "belt"), BELT);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "belt"), new BlockItem(BELT, new Item.Settings().group(Create.baseCreativeTab)));

        Registry.register(Registry.BLOCK, new Identifier(Create.ID, "shaft"), SHAFT);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "shaft"), new BlockItem(SHAFT, new Item.Settings().group(Create.baseCreativeTab)));

        Registry.register(Registry.BLOCK, new Identifier(Create.ID, "creative_motor"), CREATIVE_MOTOR);
        Registry.register(Registry.ITEM, new Identifier(Create.ID, "creative_motor"), new BlockItem(CREATIVE_MOTOR, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "cogwheel"), COGWHEEL);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "cogwheel"), new CogwheelBlockItem(COGWHEEL, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "large_cogwheel"), LARGE_COGWHEEL);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "large_cogwheel"), new CogwheelBlockItem(LARGE_COGWHEEL, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "water_wheel"), WATER_WHEEL);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "water_wheel"), new BlockItem(WATER_WHEEL, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "hand_crank"), HAND_CRANK);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "hand_crank"), new BlockItem(HAND_CRANK, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "linear_chassis"), LINEAR_CHASSIS);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "linear_chassis"), new BlockItem(LINEAR_CHASSIS, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "secondary_linear_chassis"), SECONDARY_LINEAR_CHASSIS);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "secondary_linear_chassis"), new BlockItem(SECONDARY_LINEAR_CHASSIS, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "radial_chassis"), RADIAL_CHASSIS);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "radial_chassis"), new BlockItem(RADIAL_CHASSIS, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "nozzle"), NOZZLE);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "nozzle"), new BlockItem(NOZZLE, new Item.Settings().group(Create.baseCreativeTab)));
    }

}
