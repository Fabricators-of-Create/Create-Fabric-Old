package com.simibubi.create;

import java.util.HashMap;
import java.util.Map;
import com.simibubi.create.AllColorHandlers.ItemColor.Function;
import com.simibubi.create.foundation.block.IBlockVertexColor;
import com.simibubi.create.foundation.block.render.ColoredVertexModel;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class AllColorHandlers {

	private final Map<Block, IBlockVertexColor> coloredVertexBlocks = new HashMap<>();
	private final Map<Block, BlockColorProvider> coloredBlocks = new HashMap<>();
	private final Map<ItemConvertible, ItemColorProvider> coloredItems = new HashMap<>();

	//

	public static BlockColorProvider getGrassyBlock() {
		return new BlockColor(
			(state, world, pos, layer) -> pos != null && world != null ? BiomeColors.getGrassColor(world, pos)
				: GrassColors.getColor(0.5D, 1.0D));
	}

	public static ItemColorProvider getGrassyItem() {
		return new ItemColor((stack, layer) -> GrassColors.getColor(0.5D, 1.0D));
	}

	public static BlockColorProvider getRedstonePower() {
		return new BlockColor(
				(state, world, pos, layer) -> RedstoneWireBlock.getWireColor(pos != null && world != null ? state.get(Properties.POWER) : 0)
		);
	}

	//

	public void register(Block block, BlockColorProvider color) {
		coloredBlocks.put(block, color);
	}

	public void register(Block block, IBlockVertexColor color) {
		coloredVertexBlocks.put(block, color);
	}

	public void register(ItemConvertible item, ItemColorProvider color) {
		coloredItems.put(item, color);
	}

	public void init() {
		BlockColors blockColors = MinecraftClient.getInstance()
			.getBlockColors();
		ItemColors itemColors = MinecraftClient.getInstance()
			.getItemColors();

		coloredBlocks.forEach((block, color) -> blockColors.registerColorProvider(color, block));
		coloredItems.forEach((item, color) -> itemColors.register(color, item));
		coloredVertexBlocks.forEach((block, color) -> CreateClient.getCustomBlockModels()
			.register(() -> block, model -> new ColoredVertexModel(model, color)));
	}

	//

	private static class ItemColor implements ItemColorProvider {

		private Function function;

		@FunctionalInterface
		interface Function {
			int apply(ItemStack stack, int layer);
		}

		public ItemColor(Function function) {
			this.function = function;
		}

		@Override
		public int getColor(ItemStack stack, int layer) {
			return function.apply(stack, layer);
		}

	}

	private static class BlockColor implements BlockColorProvider {

		private com.simibubi.create.AllColorHandlers.BlockColor.Function function;

		@FunctionalInterface
		interface Function {
			int apply(BlockState state, BlockRenderView world, BlockPos pos, int layer);
		}

		public BlockColor(com.simibubi.create.AllColorHandlers.BlockColor.Function function) {
			this.function = function;
		}

		@Override
		public int getColor(BlockState state, BlockRenderView world, BlockPos pos, int layer) {
			return function.apply(state, world, pos, layer);
		}

	}

}
