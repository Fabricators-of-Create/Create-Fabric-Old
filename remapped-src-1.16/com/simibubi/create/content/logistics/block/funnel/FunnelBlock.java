package com.simibubi.create.content.logistics.block.funnel;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class FunnelBlock extends AbstractFunnelBlock {

	public static final EnumProperty<WallMountLocation> FACE = Properties.WALL_MOUNT_LOCATION;
	public static final BooleanProperty EXTRACTING = BooleanProperty.of("extracting");

	public FunnelBlock(Settings p_i48415_1_) {
		super(p_i48415_1_);
		setDefaultState(getDefaultState().with(FACE, WallMountLocation.WALL)
			.with(EXTRACTING, false));
	}

	public abstract BlockState getEquivalentBeltFunnel(BlockView world, BlockPos pos, BlockState state);

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState state = super.getPlacementState(context);

		boolean sneak = context.getPlayer() != null && context.getPlayer()
			.isSneaking();
		state = state.with(EXTRACTING, !sneak);

		for (Direction direction : context.getPlacementDirections()) {
			BlockState blockstate;
			if (direction.getAxis() == Direction.Axis.Y)
				blockstate = state.with(FACE, direction == Direction.UP ? WallMountLocation.CEILING : WallMountLocation.FLOOR)
					.with(FACING, context.getPlayerFacing());
			else
				blockstate = state.with(FACE, WallMountLocation.WALL)
					.with(FACING, direction.getOpposite());
			if (blockstate.canPlaceAt(context.getWorld(), context.getBlockPos()))
				return blockstate.with(POWERED, state.get(POWERED));
		}

		return state;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACE, EXTRACTING));
	}

	@Override
	public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
		BlockHitResult hit) {

		ItemStack heldItem = player.getStackInHand(handIn);
		boolean shouldntInsertItem = AllBlocks.MECHANICAL_ARM.isIn(heldItem) || !canInsertIntoFunnel(state);

		if (AllItems.WRENCH.isIn(heldItem))
			return ActionResult.PASS;

		if (hit.getSide() == getFunnelFacing(state) && !shouldntInsertItem) {
			if (!worldIn.isClient)
				withTileEntityDo(worldIn, pos, te -> {
					ItemStack toInsert = heldItem.copy();
					ItemStack remainder = tryInsert(worldIn, pos, toInsert, false);
					if (!ItemStack.areEqual(remainder, toInsert))
						player.setStackInHand(handIn, remainder);
				});
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		World world = context.getWorld();
		if (!world.isClient)
			world.setBlockState(context.getBlockPos(), state.cycle(EXTRACTING));
		return ActionResult.SUCCESS;
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (worldIn.isClient)
			return;
		if (!(entityIn instanceof ItemEntity))
			return;
		if (!canInsertIntoFunnel(state))
			return;
		if (!entityIn.isAlive())
			return;
		ItemEntity itemEntity = (ItemEntity) entityIn;

		Direction direction = getFunnelFacing(state);
		Vec3d diff = entityIn.getPos()
			.subtract(VecHelper.getCenterOf(pos));
		double projectedDiff = direction.getAxis()
			.choose(diff.x, diff.y, diff.z);
		if (projectedDiff < 0 == (direction.getDirection() == AxisDirection.POSITIVE))
			return;

		ItemStack toInsert = itemEntity.getStack();
		ItemStack remainder = tryInsert(worldIn, pos, toInsert, false);

		if (remainder.isEmpty())
			itemEntity.remove();
		if (remainder.getCount() < toInsert.getCount())
			itemEntity.setStack(remainder);
	}

	protected boolean canInsertIntoFunnel(BlockState state) {
		return !state.get(POWERED) && !state.get(EXTRACTING);
	}

	@Override
	protected Direction getFacing(BlockState state) {
		if (state.get(FACE) == WallMountLocation.CEILING)
			return Direction.DOWN;
		if (state.get(FACE) == WallMountLocation.FLOOR)
			return Direction.UP;
		return super.getFacing(state);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		WallMountLocation attachFace = state.get(FACE);
		return attachFace == WallMountLocation.CEILING ? AllShapes.FUNNEL_CEILING
			: attachFace == WallMountLocation.FLOOR ? AllShapes.FUNNEL_FLOOR
				: AllShapes.FUNNEL.get(state.get(FACING));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		if (context.getEntity() instanceof ItemEntity && state.get(FACE) == WallMountLocation.WALL)
			return AllShapes.FUNNEL_COLLISION.get(getFacing(state));
		return getOutlineShape(state, world, pos, context);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState p_196271_3_, WorldAccess world,
		BlockPos pos, BlockPos p_196271_6_) {
		if (state.get(FACE) != WallMountLocation.WALL || direction != Direction.DOWN)
			return state;
		BlockState equivalentFunnel = getEquivalentBeltFunnel(null, null, state);
		if (BeltFunnelBlock.isOnValidBelt(equivalentFunnel, world, pos))
			return equivalentFunnel.with(BeltFunnelBlock.SHAPE,
				BeltFunnelBlock.getShapeForPosition(world, pos, getFacing(state), state.get(EXTRACTING)));
		return state;
	}

}
