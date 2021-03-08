package com.simibubi.create.content.logistics.block.redstone;

import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NixieTubeBlock extends HorizontalFacingBlock implements ITE<NixieTubeTileEntity> {

	public static final BooleanProperty CEILING = BooleanProperty.of("ceiling");

	public NixieTubeBlock(Settings properties) {
		super(properties);
		setDefaultState(getDefaultState().with(CEILING, false));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
		BlockHitResult ray) {
		try {

			ItemStack heldItem = player.getStackInHand(hand);
			NixieTubeTileEntity nixie = getTileEntity(world, pos);

			if (player.isSneaking())
				return ActionResult.PASS;

			if (heldItem.isEmpty()) {
				if (nixie.reactsToRedstone())
					return ActionResult.PASS;
				nixie.clearCustomText();
				updateDisplayedRedstoneValue(state, world, pos);
				return ActionResult.SUCCESS;
			}

			if (heldItem.getItem() == Items.NAME_TAG && heldItem.hasCustomName()) {
				Direction left = state.get(FACING)
					.rotateYClockwise();
				Direction right = left.getOpposite();

				if (world.isClient)
					return ActionResult.SUCCESS;

				BlockPos currentPos = pos;
				while (true) {
					BlockPos nextPos = currentPos.offset(left);
					if (world.getBlockState(nextPos) != state)
						break;
					currentPos = nextPos;
				}

				int index = 0;

				while (true) {
					final int rowPosition = index;
					withTileEntityDo(world, currentPos, te -> te.displayCustomNameOf(heldItem, rowPosition));
					BlockPos nextPos = currentPos.offset(right);
					if (world.getBlockState(nextPos) != state)
						break;
					currentPos = nextPos;
					index++;
				}
			}

		} catch (TileEntityException e) {
		}

		return ActionResult.PASS;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(CEILING, FACING));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView p_220053_2_, BlockPos p_220053_3_,
		ShapeContext p_220053_4_) {
		return (state.get(CEILING) ? AllShapes.NIXIE_TUBE_CEILING : AllShapes.NIXIE_TUBE)
			.get(state.get(FACING)
				.getAxis());
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockPos pos = context.getBlockPos();
		boolean ceiling = context.getSide() == Direction.DOWN;
		Vec3d hitVec = context.getHitPos();
		if (hitVec != null)
			ceiling = hitVec.y - pos.getY() > .5f;
		return getDefaultState().with(FACING, context.getPlayerFacing()
			.getOpposite())
			.with(CEILING, ceiling);
	}

	@Override
	public void neighborUpdate(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_,
		BlockPos p_220069_5_, boolean p_220069_6_) {
		updateDisplayedRedstoneValue(p_220069_1_, p_220069_2_, p_220069_3_);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		updateDisplayedRedstoneValue(state, worldIn, pos);
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return new NixieTubeTileEntity(AllTileEntities.NIXIE_TUBE.get());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	private void updateDisplayedRedstoneValue(BlockState state, World worldIn, BlockPos pos) {
		if (worldIn.isClient)
			return;
		withTileEntityDo(worldIn, pos, te -> {
			if (te.reactsToRedstone())
				te.displayRedstoneStrength(getPower(worldIn, pos));
		});
	}

	static boolean isValidBlock(BlockView world, BlockPos pos, boolean above) {
		BlockState state = world.getBlockState(pos.up(above ? 1 : -1));
		return !state.getOutlineShape(world, pos)
			.isEmpty();
	}

	private int getPower(World worldIn, BlockPos pos) {
		int power = 0;
		for (Direction direction : Iterate.directions)
			power = Math.max(worldIn.getEmittedRedstonePower(pos.offset(direction), direction), power);
		for (Direction direction : Iterate.directions)
			power = Math.max(worldIn.getEmittedRedstonePower(pos.offset(direction), Direction.UP), power);
		return power;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockView world, BlockPos pos, Direction side) {
		return side != null;
	}

	@Override
	public Class<NixieTubeTileEntity> getTileEntityClass() {
		return NixieTubeTileEntity.class;
	}

}
