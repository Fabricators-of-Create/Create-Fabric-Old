package com.simibubi.create.content.contraptions.processing.burner;

import java.util.Random;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LitBlazeBurnerBlock extends Block {

// 	1.16: add a soul fire variant

//	public enum FlameType implements IStringSerializable {
//		REGULAR, SOULFIRE;
//
//		@Override
//		public String getName() {
//			return Lang.asId(name());
//		}
//
//	}

	public LitBlazeBurnerBlock(Settings p_i48440_1_) {
		super(p_i48440_1_);
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
		BlockHitResult blockRayTraceResult) {
		ItemStack heldItem = player.getStackInHand(hand);

		// Check for 'Shovels'
		if (!heldItem.isEffectiveOn(Blocks.SNOW.getDefaultState()))
			return ActionResult.PASS;

		world.playSound(player, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, .5f, 2);

		if (world.isClient)
			return ActionResult.SUCCESS;
		if (!player.isCreative())
			heldItem.damage(1, player, p -> p.sendToolBreakStatus(hand));

		world.setBlockState(pos, AllBlocks.BLAZE_BURNER.getDefaultState());
		return ActionResult.SUCCESS;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView reader, BlockPos pos, ShapeContext context) {
		return AllBlocks.BLAZE_BURNER.get()
			.getOutlineShape(state, reader, pos, context);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockView world, BlockPos pos,
		PlayerEntity player) {
		return AllItems.EMPTY_BLAZE_BURNER.asStack();
	}

	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState p_180655_1_, World world, BlockPos pos, Random random) {
		world.addImportantParticle(ParticleTypes.LARGE_SMOKE, true,
			(double) pos.getX() + 0.5D + random.nextDouble() / 3.0D * (double) (random.nextBoolean() ? 1 : -1),
			(double) pos.getY() + random.nextDouble() + random.nextDouble(),
			(double) pos.getZ() + 0.5D + random.nextDouble() / 3.0D * (double) (random.nextBoolean() ? 1 : -1), 0.0D,
			0.07D, 0.0D);

		if (random.nextInt(10) == 0) {
			world.playSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F),
				(double) ((float) pos.getZ() + 0.5F), SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS,
				0.25F + random.nextFloat() * .25f, random.nextFloat() * 0.7F + 0.6F, false);
		}

		if (random.nextInt(5) == 0) {
			for (int i = 0; i < random.nextInt(1) + 1; ++i) {
				world.addParticle(ParticleTypes.LAVA, (double) ((float) pos.getX() + 0.5F),
					(double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F),
					(double) (random.nextFloat() / 2.0F), 5.0E-5D, (double) (random.nextFloat() / 2.0F));
			}
		}
	}
	
	@Override
	public boolean hasComparatorOutput(BlockState p_149740_1_) {
		return true;
	}
	
	@Override
	public int getComparatorOutput(BlockState state, World p_180641_2_, BlockPos p_180641_3_) {
		return 1;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView reader, BlockPos pos,
		ShapeContext context) {
		return AllBlocks.BLAZE_BURNER.get()
			.getCollisionShape(state, reader, pos, context);
	}

}
