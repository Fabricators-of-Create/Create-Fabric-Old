package com.simibubi.create.content.contraptions.components.actors;

import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.DyeHelper;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SeatBlock extends Block {

	private final boolean inCreativeTab;

	public SeatBlock(Settings p_i48440_1_, boolean inCreativeTab) {
		super(p_i48440_1_);
		this.inCreativeTab = inCreativeTab;
	}

	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> p_149666_2_) {
		if (group != ItemGroup.SEARCH && !inCreativeTab)
			return;
		super.addStacksForDisplay(group, p_149666_2_);
	}

	@Override
	public void onLandedUpon(World p_180658_1_, BlockPos p_180658_2_, Entity p_180658_3_, float p_180658_4_) {
		super.onLandedUpon(p_180658_1_, p_180658_2_, p_180658_3_, p_180658_4_ * 0.5F);
	}

	@Override
	public void onEntityLand(BlockView reader, Entity entity) {
		BlockPos pos = entity.getBlockPos();
		if (entity instanceof PlayerEntity || !(entity instanceof LivingEntity) || !canBePickedUp(entity) || isSeatOccupied(entity.world, pos)) {
			Blocks.PINK_BED.onEntityLand(reader, entity);
			return;
		}
		if (reader.getBlockState(pos)
			.getBlock() != this)
			return;
		sitDown(entity.world, pos, entity);
	}

	@Override
	public PathNodeType getAiPathNodeType(BlockState state, BlockView world, BlockPos pos,
		@Nullable MobEntity entity) {
		return PathNodeType.RAIL;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState p_220053_1_, BlockView p_220053_2_, BlockPos p_220053_3_,
		ShapeContext p_220053_4_) {
		return AllShapes.SEAT;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState p_220071_1_, BlockView p_220071_2_, BlockPos p_220071_3_,
		ShapeContext p_220071_4_) {
		return AllShapes.SEAT_COLLISION;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
		BlockHitResult p_225533_6_) {
		if (player.isSneaking())
			return ActionResult.PASS;

		ItemStack heldItem = player.getStackInHand(hand);
		for (DyeColor color : DyeColor.values()) {
			if (!heldItem.getItem()
				.isIn(DyeHelper.getTagOfDye(color)))
				continue;
			if (world.isClient)
				return ActionResult.SUCCESS;

			BlockState newState = AllBlocks.SEATS[color.ordinal()].getDefaultState();
			if (newState != state)
				world.setBlockState(pos, newState);
			return ActionResult.SUCCESS;
		}

		List<SeatEntity> seats = world.getNonSpectatingEntities(SeatEntity.class, new Box(pos));
		if (!seats.isEmpty()) {
			SeatEntity seatEntity = seats.get(0);
			List<Entity> passengers = seatEntity.getPassengerList();
			if (!passengers.isEmpty() && passengers.get(0) instanceof PlayerEntity)
				return ActionResult.PASS;
			if (!world.isClient) {
				seatEntity.removeAllPassengers();
				player.startRiding(seatEntity);
			}
			return ActionResult.SUCCESS;
		}

		if (world.isClient)
			return ActionResult.SUCCESS;
		sitDown(world, pos, player);
		return ActionResult.SUCCESS;
	}

	public static boolean isSeatOccupied(World world, BlockPos pos) {
		return !world.getNonSpectatingEntities(SeatEntity.class, new Box(pos))
			.isEmpty();
	}

	public static boolean canBePickedUp(Entity passenger) {
		return !(passenger instanceof PlayerEntity) && (passenger instanceof LivingEntity);
	}

	public static void sitDown(World world, BlockPos pos, Entity entity) {
		if (world.isClient)
			return;
		SeatEntity seat = new SeatEntity(world, pos);
		seat.setPos(pos.getX() + .5f, pos.getY(), pos.getZ() + .5f);
		world.spawnEntity(seat);
		entity.startRiding(seat, true);
	}

}
