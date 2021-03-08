package com.simibubi.create.content.contraptions.components.tracks;

import static net.minecraft.block.enums.RailShape.NORTH_SOUTH;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.VecHelper;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ControllerRailBlock extends AbstractRailBlock implements IWrenchable {

	public static final EnumProperty<RailShape> SHAPE = Properties.STRAIGHT_RAIL_SHAPE;
	public static final BooleanProperty BACKWARDS = BooleanProperty.of("backwards");
	public static final IntProperty POWER = Properties.POWER;

	public ControllerRailBlock(Settings properties) {
		super(true, properties);
		this.setDefaultState(this.stateManager.getDefaultState()
			.with(POWER, 0)
			.with(BACKWARDS, false)
			.with(SHAPE, NORTH_SOUTH));
	}

	public static Vec3i getAccelerationVector(BlockState state) {
		Direction pointingTo = getPointingTowards(state);
		return (isStateBackwards(state) ? pointingTo.getOpposite() : pointingTo).getVector();
	}

	private static Direction getPointingTowards(BlockState state) {
		switch (state.get(SHAPE)) {
		case ASCENDING_WEST:
		case EAST_WEST:
			return Direction.WEST;
		case ASCENDING_EAST:
			return Direction.EAST;
		case ASCENDING_SOUTH:
			return Direction.SOUTH;
		default:
			return Direction.NORTH;
		}
	}

	@Override
	protected BlockState updateBlockState(World world, BlockPos pos, BlockState state, boolean p_208489_4_) {
		BlockState updatedState = super.updateBlockState(world, pos, state, p_208489_4_);
		if (updatedState.get(SHAPE) == state.get(SHAPE))
			return updatedState;
		BlockState reversedUpdatedState = updatedState;

		// Rails snapping to others at 90 degrees should follow their direction
		if (getPointingTowards(state).getAxis() != getPointingTowards(updatedState).getAxis()) {
			for (boolean opposite : Iterate.trueAndFalse) {
				Direction offset = getPointingTowards(updatedState);
				if (opposite)
					offset = offset.getOpposite();
				for (BlockPos adjPos : Iterate.hereBelowAndAbove(pos.offset(offset))) {
					BlockState adjState = world.getBlockState(adjPos);
					if (!AllBlocks.CONTROLLER_RAIL.has(adjState))
						continue;
					if (getPointingTowards(adjState).getAxis() != offset.getAxis())
						continue;
					if (adjState.get(BACKWARDS) != reversedUpdatedState.get(BACKWARDS))
						reversedUpdatedState = reversedUpdatedState.cycle(BACKWARDS);
				}
			}
		}

		// Replace if changed
		if (reversedUpdatedState != updatedState)
			world.setBlockState(pos, reversedUpdatedState);
		return reversedUpdatedState;
	}

	private static void decelerateCart(BlockPos pos, AbstractMinecartEntity cart) {
		Vec3d diff = VecHelper.getCenterOf(pos)
			.subtract(cart.getPos());
		cart.setVelocity(diff.x / 16f, 0, diff.z / 16f);

		if (cart instanceof FurnaceMinecartEntity) {
			FurnaceMinecartEntity fme = (FurnaceMinecartEntity) cart;
			fme.pushX = fme.pushZ = 0;
		}
	}

	private static boolean isStableWith(BlockState testState, BlockView world, BlockPos pos) {
		return hasTopRim(world, pos.down()) && (!testState.get(SHAPE)
			.isAscending() || hasTopRim(world, pos.offset(getPointingTowards(testState))));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext p_196258_1_) {
		Direction direction = p_196258_1_.getPlayerFacing();
		BlockState base = super.getPlacementState(p_196258_1_);
		return (base == null ? getDefaultState() : base).with(BACKWARDS,
			direction.getDirection() == AxisDirection.POSITIVE);
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> p_206840_1_) {
		p_206840_1_.add(SHAPE, POWER, BACKWARDS);
	}

	@Override
	public void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
		if (world.isClient)
			return;
		Vec3d accelerationVec = Vec3d.of(getAccelerationVector(state));
		double targetSpeed = cart.getMaxSpeedWithRail() * state.get(POWER) / 15f;

		if (cart instanceof FurnaceMinecartEntity) {
			FurnaceMinecartEntity fme = (FurnaceMinecartEntity) cart;
			fme.pushX = accelerationVec.x;
			fme.pushZ = accelerationVec.z;
		}

		Vec3d motion = cart.getVelocity();
		if ((motion.dotProduct(accelerationVec) >= 0 || motion.lengthSquared() < 0.0001) && targetSpeed > 0)
			cart.setVelocity(accelerationVec.multiply(targetSpeed));
		else
			decelerateCart(pos, cart);
	}

	@Override
	protected void updateBlockState(BlockState state, World world, BlockPos pos, Block block) {
		int newPower = calculatePower(world, pos);
		if (state.get(POWER) != newPower)
			placeAndNotify(state.with(POWER, newPower), pos, world);
	}

	private int calculatePower(World world, BlockPos pos) {
		int newPower = world.getReceivedRedstonePower(pos);
		if (newPower != 0)
			return newPower;

		int forwardDistance = 0;
		int backwardsDistance = 0;
		BlockPos lastForwardRail = pos;
		BlockPos lastBackwardsRail = pos;
		int forwardPower = 0;
		int backwardsPower = 0;

		for (int i = 0; i < 15; i++) {
			BlockPos testPos = findNextRail(lastForwardRail, world, false);
			if (testPos == null)
				break;
			forwardDistance++;
			lastForwardRail = testPos;
			forwardPower = world.getReceivedRedstonePower(testPos);
			if (forwardPower != 0)
				break;
		}
		for (int i = 0; i < 15; i++) {
			BlockPos testPos = findNextRail(lastBackwardsRail, world, true);
			if (testPos == null)
				break;
			backwardsDistance++;
			lastBackwardsRail = testPos;
			backwardsPower = world.getReceivedRedstonePower(testPos);
			if (backwardsPower != 0)
				break;
		}

		if (forwardDistance > 8 && backwardsDistance > 8)
			return 0;
		if (backwardsPower == 0 && forwardDistance <= 8)
			return forwardPower;
		if (forwardPower == 0 && backwardsDistance <= 8)
			return backwardsPower;
		if (backwardsPower != 0 && forwardPower != 0)
			return MathHelper.ceil((backwardsPower * forwardDistance + forwardPower * backwardsDistance)
				/ (double) (forwardDistance + backwardsDistance));
		return 0;
	}

	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		World world = context.getWorld();
		if (world.isClient)
			return ActionResult.SUCCESS;
		BlockPos pos = context.getBlockPos();
		for (BlockRotation testRotation : new BlockRotation[] { BlockRotation.CLOCKWISE_90, BlockRotation.CLOCKWISE_180,
			BlockRotation.COUNTERCLOCKWISE_90 }) {
			BlockState testState = rotate(state, testRotation);
			if (isStableWith(testState, world, pos)) {
				placeAndNotify(testState, pos, world);
				break;
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public ActionResult onSneakWrenched(BlockState state, ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState testState = state.with(BACKWARDS, !state.get(BACKWARDS));
		if (isStableWith(testState, world, pos))
			placeAndNotify(testState, pos, world);
		return ActionResult.SUCCESS;
	}

	private void placeAndNotify(BlockState state, BlockPos pos, World world) {
		world.setBlockState(pos, state, 3);
		world.updateNeighborsAlways(pos.down(), this);
		if (state.get(SHAPE)
			.isAscending())
			world.updateNeighborsAlways(pos.up(), this);
	}

	@Nullable
	private BlockPos findNextRail(BlockPos from, BlockView world, boolean reversed) {
		BlockState current = world.getBlockState(from);
		if (!(current.getBlock() instanceof ControllerRailBlock))
			return null;
		Vec3i accelerationVec = getAccelerationVector(current);
		BlockPos baseTestPos = reversed ? from.subtract(accelerationVec) : from.add(accelerationVec);
		for (BlockPos testPos : Iterate.hereBelowAndAbove(baseTestPos)) {
			if (testPos.getY() > from.getY() && !current.get(SHAPE)
				.isAscending())
				continue;
			BlockState testState = world.getBlockState(testPos);
			if (testState.getBlock() instanceof ControllerRailBlock
				&& getAccelerationVector(testState).equals(accelerationVec))
				return testPos;
		}
		return null;
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return state.get(POWER);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		if (rotation == BlockRotation.NONE)
			return state;

		RailShape railshape = Blocks.POWERED_RAIL.getDefaultState()
			.with(SHAPE, state.get(SHAPE))
			.rotate(rotation)
			.get(SHAPE);
		state = state.with(SHAPE, railshape);

		if (rotation == BlockRotation.CLOCKWISE_180
			|| (getPointingTowards(state).getAxis() == Axis.Z) == (rotation == BlockRotation.COUNTERCLOCKWISE_90))
			return state.cycle(BACKWARDS);

		return state;
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		if (mirror == BlockMirror.NONE)
			return state;

		RailShape railshape = Blocks.POWERED_RAIL.getDefaultState()
			.with(SHAPE, state.get(SHAPE))
			.mirror(mirror)
			.get(SHAPE);
		state = state.with(SHAPE, railshape);

		if ((getPointingTowards(state).getAxis() == Axis.Z) == (mirror == BlockMirror.LEFT_RIGHT))
			return state.cycle(BACKWARDS);

		return state;
	}

	public static boolean isStateBackwards(BlockState state) {
		return state.get(BACKWARDS) ^ isReversedSlope(state);
	}

	public static boolean isReversedSlope(BlockState state) {
		return state.get(SHAPE) == RailShape.ASCENDING_SOUTH || state.get(SHAPE) == RailShape.ASCENDING_EAST;
	}
}
