package com.simibubi.create.content.contraptions.processing.burner;

import java.util.Random;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.Lang;

import mcp.MethodsReturnNonnullByDefault;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlazeBurnerBlock extends Block implements ITE<BlazeBurnerTileEntity> {

	public static final Property<HeatLevel> HEAT_LEVEL = EnumProperty.of("blaze", HeatLevel.class);

	public BlazeBurnerBlock(Settings properties) {
		super(properties);
		setDefaultState(super.getDefaultState().with(HEAT_LEVEL, HeatLevel.NONE));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(HEAT_LEVEL);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
		if (world.isClient)
			return;
		BlockEntity tileEntity = world.getBlockEntity(pos.up());
		if (!(tileEntity instanceof BasinTileEntity))
			return;
		BasinTileEntity basin = (BasinTileEntity) tileEntity;
		basin.notifyChangeOfContents();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.get(HEAT_LEVEL)
			.isAtLeast(HeatLevel.SMOULDERING);
	}

	@Override
	public void addStacksForDisplay(ItemGroup p_149666_1_, DefaultedList<ItemStack> p_149666_2_) {
		p_149666_2_.add(AllItems.EMPTY_BLAZE_BURNER.asStack());
		super.addStacksForDisplay(p_149666_1_, p_149666_2_);
	}

	@Nullable
	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.HEATER.create();
	}

	@Override
	public Class<BlazeBurnerTileEntity> getTileEntityClass() {
		return BlazeBurnerTileEntity.class;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
		BlockHitResult blockRayTraceResult) {
		ItemStack heldItem = player.getStackInHand(hand);
		boolean dontConsume = player.isCreative();
		boolean forceOverflow = !(player instanceof FakePlayer);

		if (!state.hasTileEntity()) {
			if (heldItem.getItem() instanceof FlintAndSteelItem) {
				world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F,
					world.random.nextFloat() * 0.4F + 0.8F);
				if (world.isClient)
					return ActionResult.SUCCESS;
				heldItem.damage(1, player, p -> p.sendToolBreakStatus(hand));
				world.setBlockState(pos, AllBlocks.LIT_BLAZE_BURNER.getDefaultState());
				return ActionResult.SUCCESS;
			}
			return ActionResult.PASS;
		}

		TypedActionResult<ItemStack> res = tryInsert(state, world, pos, dontConsume ? heldItem.copy() : heldItem, forceOverflow, false);
		ItemStack leftover = res.getValue();
		if (!world.isClient && !dontConsume && !leftover.isEmpty()) {
			if (heldItem.isEmpty()) {
				player.setStackInHand(hand, leftover);
			} else if (!player.inventory.insertStack(leftover)) {
				player.dropItem(leftover, false);
			}
		}

		return res.getResult() == ActionResult.SUCCESS ? res.getResult() : ActionResult.PASS;
	}

	public static TypedActionResult<ItemStack> tryInsert(BlockState state, World world, BlockPos pos, ItemStack stack, boolean forceOverflow,
		boolean simulate) {
		if (!state.hasTileEntity())
			return TypedActionResult.fail(ItemStack.EMPTY);

		BlockEntity te = world.getBlockEntity(pos);
		if (!(te instanceof BlazeBurnerTileEntity))
			return TypedActionResult.fail(ItemStack.EMPTY);
		BlazeBurnerTileEntity burnerTE = (BlazeBurnerTileEntity) te;

		if (!burnerTE.tryUpdateFuel(stack, forceOverflow, simulate))
			return TypedActionResult.fail(ItemStack.EMPTY);
		
		ItemStack container = stack.getContainerItem();
		if (!simulate && !world.isClient) {
			world.playSound(null, pos, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.BLOCKS,
				.125f + world.random.nextFloat() * .125f, .75f - world.random.nextFloat() * .25f);
			stack.decrement(1);
		}
		if (!container.isEmpty()) {
			return TypedActionResult.success(container);
		}
		return TypedActionResult.success(ItemStack.EMPTY);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		ItemStack stack = context.getStack();
		Item item = stack.getItem();
		BlockState defaultState = getDefaultState();
		if (!(item instanceof BlazeBurnerBlockItem))
			return defaultState;
		HeatLevel initialHeat =
			((BlazeBurnerBlockItem) item).hasCapturedBlaze() ? HeatLevel.SMOULDERING : HeatLevel.NONE;
		return defaultState.with(HEAT_LEVEL, initialHeat);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView reader, BlockPos pos, ShapeContext context) {
		return AllShapes.HEATER_BLOCK_SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockView p_220071_2_, BlockPos p_220071_3_,
		ShapeContext p_220071_4_) {
		if (p_220071_4_ == ShapeContext.absent())
			return AllShapes.HEATER_BLOCK_SPECIAL_COLLISION_SHAPE;
		return getOutlineShape(p_220071_1_, p_220071_2_, p_220071_3_, p_220071_4_);
	}

	@Override
	public int getLightValue(BlockState state, BlockView world, BlockPos pos) {
		return MathHelper.clamp(state.get(HEAT_LEVEL)
			.ordinal() * 4 - 1, 0, 15);
	}

	public static HeatLevel getHeatLevelOf(BlockState blockState) {
		return blockState.contains(BlazeBurnerBlock.HEAT_LEVEL) ? blockState.get(BlazeBurnerBlock.HEAT_LEVEL)
			: HeatLevel.NONE;
	}

	public static LootTable.Builder buildLootTable() {
		net.minecraft.loot.condition.LootCondition.Builder survivesExplosion = SurvivesExplosionLootCondition.builder();
		BlazeBurnerBlock block = AllBlocks.BLAZE_BURNER.get();

		LootTable.Builder builder = LootTable.builder();
		LootPool.Builder poolBuilder = LootPool.builder();
		for (HeatLevel level : HeatLevel.values()) {
			ItemConvertible drop =
				level == HeatLevel.NONE ? AllItems.EMPTY_BLAZE_BURNER.get() : AllBlocks.BLAZE_BURNER.get();
			poolBuilder.with(ItemEntry.builder(drop)
				.conditionally(survivesExplosion)
				.conditionally(BlockStatePropertyLootCondition.builder(block)
					.properties(StatePredicate.Builder.create()
						.exactMatch(HEAT_LEVEL, level))));
		}
		builder.pool(poolBuilder.rolls(ConstantLootTableRange.create(1)));
		return builder;
	}
	
	@Override
	public boolean hasComparatorOutput(BlockState p_149740_1_) {
		return true;
	}
	
	@Override
	public int getComparatorOutput(BlockState state, World p_180641_2_, BlockPos p_180641_3_) {
		return Math.max(0, state.get(HEAT_LEVEL).ordinal() -1);
	}

	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(10) != 0)
			return;
		if (!state.get(HEAT_LEVEL)
			.isAtLeast(HeatLevel.SMOULDERING))
			return;
		world.playSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F),
			(double) ((float) pos.getZ() + 0.5F), SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS,
			0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.6F, false);
	}

	public enum HeatLevel implements StringIdentifiable {
		NONE, SMOULDERING, FADING, KINDLED, SEETHING,;

		public static HeatLevel byIndex(int index) {
			return values()[index];
		}

		@Override
		public String asString() {
			return Lang.asId(name());
		}

		public boolean isAtLeast(HeatLevel heatLevel) {
			return this.ordinal() >= heatLevel.ordinal();
		}
	}
}
