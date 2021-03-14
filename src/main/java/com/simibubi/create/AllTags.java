package com.simibubi.create;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;

// TODO FIX THIS WHOLE CLASS TAGS
public class AllTags {
	/*private static final CreateRegistrate REGISTRATE = Create.com.simibubi.create.registrate()
		.itemGroup(() -> Create.baseCreativeTab);*/

	/*public static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockAndItem(
		String tagName) {
		return b -> b.tag(forgeBlockTag(tagName))
			.item()
			.tag(forgeItemTag(tagName));
	}*/

	/*public static net.minecraft.tag.Tag.Identified<Block> forgeBlockTag(String name) {
		return forgeTag(BlockTags.getTagGroup(), name);
	}*/

	/*public static Tag<Item> forgeItemTag(String name) {
		return forgeTag(ItemTags.getCollection(), name);
	}

	public static Tag<Fluid> forgeFluidTag(String name) {
		return forgeTag(FluidTags.getContainer(), name);
	}*/

	/*public static <T> net.minecraft.tag.Tag.Identified<T> forgeTag(TagGroup<T> collection, String name) {
		return tag(collection, "forge", name);
	}*/

	/*public static <T> net.minecraft.tag.Tag.Identified<T> tag(TagGroup<T> collection, String domain, String name) {
		return collection.getOrCreate(new Identifier(domain, name));
	}*/

	public static enum NameSpace {

		MOD(Create.ID), FORGE("forge"), MC("minecraft")

		;
		String id;

		private NameSpace(String id) {
			this.id = id;
		}
	}

	/*public static enum AllItemTags {
		CRUSHED_ORES(NameSpace.MOD),
		SEATS(NameSpace.MOD),
		VALVE_HANDLES(NameSpace.MOD),
		UPRIGHT_ON_BELT(NameSpace.MOD),
		CREATE_INGOTS(NameSpace.MOD),
		BEACON_PAYMENT(NameSpace.FORGE),
		INGOTS(NameSpace.FORGE),
		NUGGETS(NameSpace.FORGE),
		PLATES(NameSpace.FORGE),
		COBBLESTONE(NameSpace.FORGE)

		;

		public Tag<Item> tag;

		private AllItemTags(NameSpace namespace) {
			this(namespace, "");
		}

		private AllItemTags(NameSpace namespace, String path) {
			tag = new ItemTags.Wrapper(
				new Identifier(namespace.id, (path.isEmpty() ? "" : path + "/") + Lang.asId(name())));
		}

		public boolean matches(ItemStack stack) {
			return tag.contains(stack.getItem());
		}

		public void add(Item... values) {
			REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.getBuilder(tag)
				.add(values));
		}

		public void includeIn(AllItemTags parent) {
			REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, prov -> prov.getBuilder(parent.tag)
				.add(tag));
		}
	}
	
	public static enum AllFluidTags {
		NO_INFINITE_DRAINING
		
		;
		public Tag<Fluid> tag;
		
		private AllFluidTags() {
			this(NameSpace.MOD, "");
		}

		private AllFluidTags(NameSpace namespace) {
			this(namespace, "");
		}

		private AllFluidTags(NameSpace namespace, String path) {
			tag = new FluidTags.Wrapper(
				new Identifier(namespace.id, (path.isEmpty() ? "" : path + "/") + Lang.asId(name())));
		}
		
		public boolean matches(Fluid fluid) {
			return fluid != null && fluid.isIn(tag);
		}
	}*/

	public static enum AllBlockTags {
		WINDMILL_SAILS, FAN_HEATERS, WINDOWABLE, NON_MOVABLE, BRITTLE, SEATS, SAILS, VALVE_HANDLES, FAN_TRANSPARENT, SAFE_NBT

		;

		public net.minecraft.tag.Tag.Identified<Block> tag;

		private AllBlockTags() {
			this(NameSpace.MOD, "");
		}

		private AllBlockTags(NameSpace namespace) {
			this(namespace, "");
		}

		private AllBlockTags(NameSpace namespace, String path) {
			/*tag = new BlockTags.Wrapper(
				new Identifier(namespace.id, (path.isEmpty() ? "" : path + "/") + Lang.asId(name())));*/
		}

		public boolean matches(BlockState block) {
			return tag.contains(block.getBlock());
		}

		/*public void includeIn(AllBlockTags parent) {
			REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.getBuilder(parent.tag)
				.add(tag));
		}*/

		public void includeAll(net.minecraft.tag.Tag.Identified<Block> child) {
			/*REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.getBuilder(tag)
				.add(child));*/
		}

		public void add(Block... values) {
			/*REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, prov -> prov.getBuilder(tag)
				.add(values));*/
		}
	}

	public static void register() {
		/*AllItemTags.CREATE_INGOTS.includeIn(AllItemTags.BEACON_PAYMENT);
		AllItemTags.CREATE_INGOTS.includeIn(AllItemTags.INGOTS);

		AllItemTags.UPRIGHT_ON_BELT.add(Items.GLASS_BOTTLE, Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION);*/

		AllBlockTags.WINDMILL_SAILS.includeAll(BlockTags.WOOL);
		
		AllBlockTags.BRITTLE.includeAll(BlockTags.DOORS);
		AllBlockTags.BRITTLE.add(Blocks.FLOWER_POT, Blocks.BELL, Blocks.COCOA);

		AllBlockTags.FAN_TRANSPARENT.includeAll(BlockTags.FENCES);
		AllBlockTags.FAN_TRANSPARENT.add(Blocks.IRON_BARS);

		AllBlockTags.FAN_HEATERS.add(Blocks.MAGMA_BLOCK, Blocks.CAMPFIRE, Blocks.LAVA, Blocks.FIRE);

		AllBlockTags.SAFE_NBT.includeAll(BlockTags.SIGNS);
	}
}
