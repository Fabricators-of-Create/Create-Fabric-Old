package com.simibubi.create.content.logistics;

import static com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock.getHeatLevelOf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.contraptions.components.fan.SplashingRecipe;
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.foundation.utility.ColorHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.SmokerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class InWorldProcessing {

	public static class SplashingInv extends RecipeWrapper {
		public SplashingInv() {
			super(new ItemStackHandler(1));
		}
	}

	public static SplashingInv splashingInv = new SplashingInv();

	public enum Type {
		SMOKING, BLASTING, SPLASHING, NONE

		;

		public static Type byBlock(BlockView reader, BlockPos pos) {
			BlockState blockState = reader.getBlockState(pos);
			FluidState fluidState = reader.getFluidState(pos);
			if (fluidState.getFluid() == Fluids.WATER || fluidState.getFluid() == Fluids.FLOWING_WATER)
				return Type.SPLASHING;
			Block block = blockState.getBlock();
			if (block == Blocks.FIRE || AllBlocks.LIT_BLAZE_BURNER.has(blockState)
				|| (block == Blocks.CAMPFIRE && blockState.get(CampfireBlock.LIT))
				|| getHeatLevelOf(blockState) == BlazeBurnerBlock.HeatLevel.SMOULDERING)
				return Type.SMOKING;
			if (block == Blocks.LAVA || getHeatLevelOf(blockState).isAtLeast(BlazeBurnerBlock.HeatLevel.FADING))
				return Type.BLASTING;
			return Type.NONE;
		}
	}

	public static boolean canProcess(ItemEntity entity, Type type) {
		if (entity.getPersistentData()
			.contains("CreateData")) {
			CompoundTag compound = entity.getPersistentData()
				.getCompound("CreateData");
			if (compound.contains("Processing")) {
				CompoundTag processing = compound.getCompound("Processing");

				if (Type.valueOf(processing.getString("Type")) != type) {
					boolean canProcess = canProcess(entity.getStack(), type, entity.world);
					processing.putString("Type", type.name());
					if (!canProcess)
						processing.putInt("Time", -1);
					return canProcess;
				} else if (processing.getInt("Time") >= 0)
					return true;
				else if (processing.getInt("Time") == -1)
					return false;
			}
		}
		return canProcess(entity.getStack(), type, entity.world);
	}

	private static boolean canProcess(ItemStack stack, Type type, World world) {
		if (type == Type.BLASTING) {
			return true;
		}

		if (type == Type.SMOKING) {
			// FIXME this does not need to be a TE
			SmokerBlockEntity smoker = new SmokerBlockEntity();
			smoker.setLocation(world, BlockPos.ORIGIN);
			smoker.setStack(0, stack);
			Optional<SmokingRecipe> recipe = world.getRecipeManager()
				.getFirstMatch(RecipeType.SMOKING, smoker, world);
			return recipe.isPresent();
		}

		if (type == Type.SPLASHING)
			return isWashable(stack, world);

		return false;
	}

	public static boolean isWashable(ItemStack stack, World world) {
		splashingInv.setStack(0, stack);
		Optional<SplashingRecipe> recipe = AllRecipeTypes.SPLASHING.find(splashingInv, world);
		return recipe.isPresent();
	}

	public static void applyProcessing(ItemEntity entity, Type type) {
		if (decrementProcessingTime(entity, type) != 0)
			return;
		List<ItemStack> stacks = process(entity.getStack(), type, entity.world);
		if (stacks == null)
			return;
		if (stacks.isEmpty()) {
			entity.remove();
			return;
		}
		entity.setStack(stacks.remove(0));
		for (ItemStack additional : stacks) {
			ItemEntity entityIn = new ItemEntity(entity.world, entity.getX(), entity.getY(), entity.getZ(), additional);
			entityIn.setVelocity(entity.getVelocity());
			entity.world.spawnEntity(entityIn);
		}
	}

	public static TransportedResult applyProcessing(TransportedItemStack transported, World world, Type type) {
		TransportedResult ignore = TransportedResult.doNothing();
		if (transported.processedBy != type) {
			transported.processedBy = type;
			int timeModifierForStackSize = ((transported.stack.getCount() - 1) / 16) + 1;
			int processingTime =
				(int) (AllConfigs.SERVER.kinetics.inWorldProcessingTime.get() * timeModifierForStackSize) + 1;
			transported.processingTime = processingTime;
			if (!canProcess(transported.stack, type, world))
				transported.processingTime = -1;
			return ignore;
		}
		if (transported.processingTime == -1)
			return ignore;
		if (transported.processingTime-- > 0)
			return ignore;

		List<ItemStack> stacks = process(transported.stack, type, world);
		if (stacks == null)
			return ignore;

		List<TransportedItemStack> transportedStacks = new ArrayList<>();
		for (ItemStack additional : stacks) {
			TransportedItemStack newTransported = transported.getSimilar();
			newTransported.stack = additional.copy();
			transportedStacks.add(newTransported);
		}
		return TransportedResult.convertTo(transportedStacks);
	}

	private static List<ItemStack> process(ItemStack stack, Type type, World world) {
		if (type == Type.SPLASHING) {
			splashingInv.setStack(0, stack);
			Optional<SplashingRecipe> recipe = AllRecipeTypes.SPLASHING.find(splashingInv, world);
			if (recipe.isPresent())
				return applyRecipeOn(stack, recipe.get());
			return null;
		}

		// FIXME this does not need to be a TE
		SmokerBlockEntity smoker = new SmokerBlockEntity();
		smoker.setLocation(world, BlockPos.ORIGIN);
		smoker.setStack(0, stack);
		Optional<SmokingRecipe> smokingRecipe = world.getRecipeManager()
			.getFirstMatch(RecipeType.SMOKING, smoker, world);

		if (type == Type.BLASTING) {
			// FIXME this does not need to be a TE
			FurnaceBlockEntity furnace = new FurnaceBlockEntity();
			furnace.setLocation(world, BlockPos.ORIGIN);
			furnace.setStack(0, stack);
			Optional<SmeltingRecipe> smeltingRecipe = world.getRecipeManager()
				.getFirstMatch(RecipeType.SMELTING, furnace, world);

			if (!smokingRecipe.isPresent()) {
				if (smeltingRecipe.isPresent())
					return applyRecipeOn(stack, smeltingRecipe.get());

				// FIXME this does not need to be a TE
				BlastFurnaceBlockEntity blastFurnace = new BlastFurnaceBlockEntity();
				blastFurnace.setLocation(world, BlockPos.ORIGIN);
				blastFurnace.setStack(0, stack);
				Optional<BlastingRecipe> blastingRecipe = world.getRecipeManager()
					.getFirstMatch(RecipeType.BLASTING, blastFurnace, world);

				if (blastingRecipe.isPresent())
					return applyRecipeOn(stack, blastingRecipe.get());
			}

			return Collections.emptyList();
		}

		if (type == Type.SMOKING && smokingRecipe.isPresent())
			return applyRecipeOn(stack, smokingRecipe.get());

		return null;
	}

	private static int decrementProcessingTime(ItemEntity entity, Type type) {
		CompoundTag nbt = entity.getPersistentData();

		if (!nbt.contains("CreateData"))
			nbt.put("CreateData", new CompoundTag());
		CompoundTag createData = nbt.getCompound("CreateData");

		if (!createData.contains("Processing"))
			createData.put("Processing", new CompoundTag());
		CompoundTag processing = createData.getCompound("Processing");

		if (!processing.contains("Type") || Type.valueOf(processing.getString("Type")) != type) {
			processing.putString("Type", type.name());
			int timeModifierForStackSize = ((entity.getStack()
				.getCount() - 1) / 16) + 1;
			int processingTime =
				(int) (AllConfigs.SERVER.kinetics.inWorldProcessingTime.get() * timeModifierForStackSize) + 1;
			processing.putInt("Time", processingTime);
		}

		int value = processing.getInt("Time") - 1;
		processing.putInt("Time", value);
		return value;
	}

	public static void applyRecipeOn(ItemEntity entity, Recipe<?> recipe) {
		List<ItemStack> stacks = applyRecipeOn(entity.getStack(), recipe);
		if (stacks == null)
			return;
		if (stacks.isEmpty()) {
			entity.remove();
			return;
		}
		entity.setStack(stacks.remove(0));
		for (ItemStack additional : stacks) {
			ItemEntity entityIn = new ItemEntity(entity.world, entity.getX(), entity.getY(), entity.getZ(), additional);
			entityIn.setVelocity(entity.getVelocity());
			entity.world.spawnEntity(entityIn);
		}
	}

	private static List<ItemStack> applyRecipeOn(ItemStack stackIn, Recipe<?> recipe) {
		List<ItemStack> stacks;

		if (recipe instanceof ProcessingRecipe) {
			stacks = new ArrayList<>();
			for (int i = 0; i < stackIn.getCount(); i++) {
				List<ItemStack> rollResults = ((ProcessingRecipe<?>) recipe).rollResults();
				for (ItemStack stack : rollResults) {
					for (ItemStack previouslyRolled : stacks) {
						if (stack.isEmpty())
							continue;
						if (!ItemHandlerHelper.canItemStacksStack(stack, previouslyRolled))
							continue;
						int amount = Math.min(previouslyRolled.getMaxCount() - previouslyRolled.getCount(),
							stack.getCount());
						previouslyRolled.increment(amount);
						stack.decrement(amount);
					}

					if (stack.isEmpty())
						continue;

					stacks.add(stack);
				}
			}
		} else {
			ItemStack out = recipe.getOutput()
				.copy();
			stacks = ItemHelper.multipliedOutput(stackIn, out);
		}

		return stacks;
	}
	public static void spawnParticlesForProcessing(@Nullable World world, Vec3d vec, Type type) {
		if (world == null || !world.isClient)
			return;
		if (world.random.nextInt(8) != 0)
			return;

		switch (type) {
		case BLASTING:
			world.addParticle(ParticleTypes.LARGE_SMOKE, vec.x, vec.y + .25f, vec.z, 0, 1 / 16f, 0);
			break;
		case SMOKING:
			world.addParticle(ParticleTypes.POOF, vec.x, vec.y + .25f, vec.z, 0, 1 / 16f, 0);
			break;
		case SPLASHING:
			Vec3d color = ColorHelper.getRGB(0x0055FF);
			world.addParticle(new DustParticleEffect((float) color.x, (float) color.y, (float) color.z, 1),
				vec.x + (world.random.nextFloat() - .5f) * .5f, vec.y + .5f, vec.z + (world.random.nextFloat() - .5f) * .5f,
				0, 1 / 8f, 0);
			world.addParticle(ParticleTypes.SPIT, vec.x + (world.random.nextFloat() - .5f) * .5f, vec.y + .5f,
				vec.z + (world.random.nextFloat() - .5f) * .5f, 0, 1 / 8f, 0);
			break;
		default:
			break;
		}
	}

}
