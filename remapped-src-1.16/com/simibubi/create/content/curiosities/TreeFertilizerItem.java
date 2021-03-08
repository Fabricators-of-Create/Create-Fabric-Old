package com.simibubi.create.content.curiosities;

import com.simibubi.create.foundation.utility.worldWrappers.PlacementSimulationServerWorld;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SaplingBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class TreeFertilizerItem extends Item {

	public TreeFertilizerItem(Settings properties) {
		super(properties);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockState state = context.getWorld()
			.getBlockState(context.getBlockPos());
		Block block = state.getBlock();
		if (block instanceof SaplingBlock) {

			if (context.getWorld().isClient) {
				BoneMealItem.createParticles(context.getWorld(), context.getBlockPos(), 100);
				return ActionResult.SUCCESS;
			}

			TreesDreamWorld world = new TreesDreamWorld((ServerWorld) context.getWorld(), context.getBlockPos());
			BlockPos saplingPos = context.getBlockPos();

			for (BlockPos pos : BlockPos.iterate(-1, 0, -1, 1, 0, 1)) {
				if (context.getWorld()
					.getBlockState(saplingPos.add(pos))
					.getBlock() == block)
					world.setBlockState(pos.up(10), state.with(SaplingBlock.STAGE, 1));
			}

			((SaplingBlock) block).grow(world, world.getRandom(), BlockPos.ORIGIN.up(10),
				state.with(SaplingBlock.STAGE, 1));

			for (BlockPos pos : world.blocksAdded.keySet()) {
				BlockPos actualPos = pos.add(saplingPos)
					.down(10);

				// Don't replace Bedrock
				if (context.getWorld()
					.getBlockState(actualPos)
					.getHardness(context.getWorld(), actualPos) == -1)
					continue;
				// Don't replace solid blocks with leaves
				if (!world.getBlockState(pos)
					.isSolidBlock(world, pos)
					&& !context.getWorld()
						.getBlockState(actualPos)
						.getCollisionShape(context.getWorld(), actualPos)
						.isEmpty())
					continue;
				if (world.getBlockState(pos)
					.getBlock() == Blocks.GRASS_BLOCK
					|| world.getBlockState(pos)
						.getBlock() == Blocks.PODZOL)
					continue;

				context.getWorld()
					.setBlockState(actualPos, world.getBlockState(pos));
			}

			if (context.getPlayer() != null && !context.getPlayer()
				.isCreative())
				context.getStack()
					.decrement(1);
			return ActionResult.SUCCESS;

		}

		return super.useOnBlock(context);
	}

	private class TreesDreamWorld extends PlacementSimulationServerWorld {
		private final BlockPos saplingPos;

		protected TreesDreamWorld(ServerWorld wrapped, BlockPos saplingPos) {
			super(wrapped);
			this.saplingPos = saplingPos;
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			if (pos.getY() <= 9)
				return world.getBlockState(saplingPos.down());
			return super.getBlockState(pos);
		}

	}



}
