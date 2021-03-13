package com.simibubi.create;

import com.simibubi.create.content.contraptions.components.clock.CuckooClockBlock;
import com.simibubi.create.content.contraptions.components.fan.NozzleBlock;
import com.simibubi.create.content.contraptions.components.motor.CreativeMotorBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.ClockworkBearingBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.MechanicalBearingBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.WindmillBearingBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.LinearChassisBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.RadialChassisBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.mounted.CartAssemblerBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.mounted.CartAssemblerBlockItem;
import com.simibubi.create.content.contraptions.components.waterwheel.WaterWheelBlock;
import com.simibubi.create.content.contraptions.relays.belt.BeltBlock;
import com.simibubi.create.content.contraptions.relays.elementary.CogWheelBlock;
import com.simibubi.create.content.contraptions.relays.elementary.CogwheelBlockItem;
import com.simibubi.create.content.contraptions.relays.elementary.ShaftBlock;
import com.simibubi.create.content.contraptions.relays.encased.ClutchBlock;
import com.simibubi.create.content.contraptions.relays.encased.GearshiftBlock;
import com.simibubi.create.content.contraptions.relays.gearbox.GearboxBlock;
import com.simibubi.create.content.logistics.block.redstone.AnalogLeverBlock;
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
            .of(Material.METAL)
            .strength(0.8f, 0.8f)
            .nonOpaque());

    public static final CreativeMotorBlock CREATIVE_MOTOR = new CreativeMotorBlock(Block.Settings
            .of(Material.METAL)
            .strength(0.8f, 0.8f)
            .nonOpaque());

	public static final CogWheelBlock COGWHEEL = new CogWheelBlock(false, Block.Settings
		.of(Material.METAL)
		.strength(0.8f, 0.8f)
		.sounds(BlockSoundGroup.WOOD)
		.nonOpaque());

	public static final CogWheelBlock LARGE_COGWHEEL = new CogWheelBlock(true, Block.Settings
		.of(Material.METAL)
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
		.of(Material.METAL)
		.strength(0.8f, 0.8f)
		.nonOpaque());

	public static final MechanicalBearingBlock MECHANICAL_BEARING = new MechanicalBearingBlock(Block.Settings
		.of(Material.METAL)
		.strength(0.8f, 0.8f));

	public static final WindmillBearingBlock WINDMILL_BEARING = new WindmillBearingBlock(Block.Settings
		.of(Material.METAL)
		.strength(0.8f, 0.8f));

	public static final ClockworkBearingBlock CLOCKWORK_BEARING = new ClockworkBearingBlock(Block.Settings
		.of(Material.METAL)
		.strength(0.8f, 0.8f));

	public static final AnalogLeverBlock ANALOG_LEVER = new AnalogLeverBlock(Block.Settings
		.of(Material.METAL)
		.strength(0.8f, 0.8f));

	public static final GearshiftBlock GEARSHIFT = new GearshiftBlock(Block.Settings
		.of(Material.METAL)
		.strength(0.8f, 0.8f));

	public static final ClutchBlock CLUTCH = new ClutchBlock(Block.Settings
		.of(Material.METAL)
		.strength(0.8f, 0.8f));

	public static final GearboxBlock GEARBOX = new GearboxBlock(Block.Settings
		.of(Material.METAL)
		.strength(0.8f, 0.8f));

	public static final CuckooClockBlock CUCKOO_CLOCK = CuckooClockBlock.regular(Block.Settings
		.of(Material.WOOD)
		.strength(0.8f, 0.8f));

	public static final CuckooClockBlock MYSTERIOUS_CUCKOO_CLOCK = CuckooClockBlock.mysterious(Block.Settings
		.of(Material.WOOD)
		.strength(0.8f, 0.8f));

	public static final CartAssemblerBlock CART_ASSEMBLER = new CartAssemblerBlock(Block.Settings
		.of(Material.METAL)
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

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "mechanical_bearing"), MECHANICAL_BEARING);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "mechanical_bearing"), new BlockItem(MECHANICAL_BEARING, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "windmill_bearing"), WINDMILL_BEARING);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "windmill_bearing"), new BlockItem(WINDMILL_BEARING, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "clockwork_bearing"), CLOCKWORK_BEARING);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "clockwork_bearing"), new BlockItem(CLOCKWORK_BEARING, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "analog_lever"), ANALOG_LEVER);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "analog_lever"), new BlockItem(ANALOG_LEVER, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "gearshift"), GEARSHIFT);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "gearshift"), new BlockItem(GEARSHIFT, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "clutch"), CLUTCH);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "clutch"), new BlockItem(CLUTCH, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "gearbox"), GEARBOX);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "gearbox"), new BlockItem(GEARBOX, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "cuckoo_clock"), CUCKOO_CLOCK);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "cuckoo_clock"), new BlockItem(CUCKOO_CLOCK, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "mysterious_cuckoo_clock"), MYSTERIOUS_CUCKOO_CLOCK);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "mysterious_cuckoo_clock"), new BlockItem(MYSTERIOUS_CUCKOO_CLOCK, new Item.Settings().group(Create.baseCreativeTab)));

		Registry.register(Registry.BLOCK, new Identifier(Create.ID, "cart_assembler"), CART_ASSEMBLER);
		Registry.register(Registry.ITEM, new Identifier(Create.ID, "cart_assembler"), new CartAssemblerBlockItem(CART_ASSEMBLER, new Item.Settings().group(Create.baseCreativeTab)));
    }

}
