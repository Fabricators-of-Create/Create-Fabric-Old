package com.simibubi.create.content.contraptions.relays.belt.item;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.relays.belt.BeltBlock;
import com.simibubi.create.content.contraptions.relays.belt.BeltPart;
import com.simibubi.create.content.contraptions.relays.belt.BeltSlope;
import com.simibubi.create.content.contraptions.relays.elementary.AbstractShaftBlock;
import com.simibubi.create.content.contraptions.relays.elementary.ShaftBlock;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.config.AllConfigs;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.world.World;

public class BeltConnectorItem extends BlockItem {

	public BeltConnectorItem(Settings properties) {
		super(AllBlocks.BELT.get(), properties);
	}

	@Override
	public String getTranslationKey() {
		return getOrCreateTranslationKey();
	}

	@Override
	public void appendStacks(ItemGroup p_150895_1_, DefaultedList<ItemStack> p_150895_2_) {
		if (p_150895_1_ == Create.baseCreativeTab)
			return;
		super.appendStacks(p_150895_1_, p_150895_2_);
	}

	@Nonnull
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		if (playerEntity != null && playerEntity.isSneaking()) {
			context.getStack()
				.setTag(null);
			return ActionResult.SUCCESS;
		}

		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		boolean validAxis = validateAxis(world, pos);

		if (world.isClient)
			return validAxis ? ActionResult.SUCCESS : ActionResult.FAIL;

		CompoundTag tag = context.getStack()
			.getOrCreateTag();
		BlockPos firstPulley = null;

		// Remove first if no longer existant or valid
		if (tag.contains("FirstPulley")) {
			firstPulley = NbtHelper.toBlockPos(tag.getCompound("FirstPulley"));
			if (!validateAxis(world, firstPulley) || !firstPulley.isWithinDistance(pos, maxLength() * 2)) {
				tag.remove("FirstPulley");
				context.getStack()
					.setTag(tag);
			}
		}

		if (!validAxis || playerEntity == null)
			return ActionResult.FAIL;

		if (tag.contains("FirstPulley")) {

			if (!canConnect(world, firstPulley, pos))
				return ActionResult.FAIL;

			if (firstPulley != null && !firstPulley.equals(pos)) {
				createBelts(world, firstPulley, pos);
				AllTriggers.triggerFor(AllTriggers.CONNECT_BELT, playerEntity);
				if (!playerEntity.isCreative())
					context.getStack()
						.decrement(1);
			}

			if (!context.getStack()
				.isEmpty()) {
				context.getStack()
					.setTag(null);
				playerEntity.getItemCooldownManager()
					.set(this, 5);
			}
			return ActionResult.SUCCESS;
		}

		tag.put("FirstPulley", NbtHelper.fromBlockPos(pos));
		context.getStack()
			.setTag(tag);
		playerEntity.getItemCooldownManager()
			.set(this, 5);
		return ActionResult.SUCCESS;
	}

	public static void createBelts(World world, BlockPos start, BlockPos end) {

		BeltSlope slope = getSlopeBetween(start, end);
		Direction facing = getFacingFromTo(start, end);

		BlockPos diff = end.subtract(start);
		if (diff.getX() == diff.getZ())
			facing = Direction.get(facing.getDirection(), world.getBlockState(start)
				.get(Properties.AXIS) == Axis.X ? Axis.Z : Axis.X);

		List<BlockPos> beltsToCreate = getBeltChainBetween(start, end, slope, facing);
		BlockState beltBlock = AllBlocks.BELT.getDefaultState();

		for (BlockPos pos : beltsToCreate) {
			BeltPart part = pos.equals(start) ? BeltPart.START : pos.equals(end) ? BeltPart.END : BeltPart.MIDDLE;
			BlockState shaftState = world.getBlockState(pos);
			boolean pulley = ShaftBlock.isShaft(shaftState);
			if (part == BeltPart.MIDDLE && pulley)
				part = BeltPart.PULLEY;
			if (pulley && shaftState.get(AbstractShaftBlock.AXIS) == Axis.Y)
				slope = BeltSlope.SIDEWAYS;
			world.setBlockState(pos, beltBlock.with(BeltBlock.SLOPE, slope)
				.with(BeltBlock.PART, part)
				.with(BeltBlock.HORIZONTAL_FACING, facing), 3);
		}
	}

	private static Direction getFacingFromTo(BlockPos start, BlockPos end) {
		Axis beltAxis = start.getX() == end.getX() ? Axis.Z : Axis.X;
		BlockPos diff = end.subtract(start);
		AxisDirection axisDirection = AxisDirection.POSITIVE;

		if (diff.getX() == 0 && diff.getZ() == 0)
			axisDirection = diff.getY() > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
		else
			axisDirection = beltAxis.choose(diff.getX(), 0, diff.getZ()) > 0 ? AxisDirection.POSITIVE
				: AxisDirection.NEGATIVE;

		return Direction.get(axisDirection, beltAxis);
	}

	private static BeltSlope getSlopeBetween(BlockPos start, BlockPos end) {
		BlockPos diff = end.subtract(start);

		if (diff.getY() != 0) {
			if (diff.getZ() != 0 || diff.getX() != 0)
				return diff.getY() > 0 ? BeltSlope.UPWARD : BeltSlope.DOWNWARD;
			return BeltSlope.VERTICAL;
		}
		return BeltSlope.HORIZONTAL;
	}

	private static List<BlockPos> getBeltChainBetween(BlockPos start, BlockPos end, BeltSlope slope, Direction direction) {
		List<BlockPos> positions = new LinkedList<>();
		int limit = 1000;
		BlockPos current = start;

		do {
			positions.add(current);

			if (slope == BeltSlope.VERTICAL) {
				current = current.up(direction.getDirection() == AxisDirection.POSITIVE ? 1 : -1);
				continue;
			}

			current = current.offset(direction);
			if (slope != BeltSlope.HORIZONTAL)
				current = current.up(slope == BeltSlope.UPWARD ? 1 : -1);

		} while (!current.equals(end) && limit-- > 0);

		positions.add(end);
		return positions;
	}

	public static boolean canConnect(World world, BlockPos first, BlockPos second) {
		if (!world.isAreaLoaded(first, 1))
			return false;
		if (!world.isAreaLoaded(second, 1))
			return false;
		if (!second.isWithinDistance(first, maxLength()))
			return false;

		BlockPos diff = second.subtract(first);
		Axis shaftAxis = world.getBlockState(first)
			.get(Properties.AXIS);

		int x = diff.getX();
		int y = diff.getY();
		int z = diff.getZ();
		int sames = ((Math.abs(x) == Math.abs(y)) ? 1 : 0) + ((Math.abs(y) == Math.abs(z)) ? 1 : 0)
			+ ((Math.abs(z) == Math.abs(x)) ? 1 : 0);

		if (shaftAxis.choose(x, y, z) != 0)
			return false;
		if (sames != 1)
			return false;
		if (shaftAxis != world.getBlockState(second)
			.get(Properties.AXIS))
			return false;
		if (shaftAxis == Axis.Y && x != 0 && z != 0)
			return false;

		BlockEntity tileEntity = world.getBlockEntity(first);
		BlockEntity tileEntity2 = world.getBlockEntity(second);

		if (!(tileEntity instanceof KineticTileEntity))
			return false;
		if (!(tileEntity2 instanceof KineticTileEntity))
			return false;

		float speed1 = ((KineticTileEntity) tileEntity).getTheoreticalSpeed();
		float speed2 = ((KineticTileEntity) tileEntity2).getTheoreticalSpeed();
		if (Math.signum(speed1) != Math.signum(speed2) && speed1 != 0 && speed2 != 0)
			return false;

		BlockPos step = new BlockPos(Math.signum(diff.getX()), Math.signum(diff.getY()), Math.signum(diff.getZ()));
		int limit = 1000;
		for (BlockPos currentPos = first.add(step); !currentPos.equals(second) && limit-- > 0; currentPos =
			currentPos.add(step)) {
			BlockState blockState = world.getBlockState(currentPos);
			if (ShaftBlock.isShaft(blockState) && blockState.get(AbstractShaftBlock.AXIS) == shaftAxis)
				continue;
			if (!blockState.getMaterial()
				.isReplaceable())
				return false;
		}

		return true;

	}

	protected static Integer maxLength() {
		return AllConfigs.SERVER.kinetics.maxBeltLength.get();
	}

	public static boolean validateAxis(World world, BlockPos pos) {
		if (!world.isAreaLoaded(pos, 1))
			return false;
		if (!ShaftBlock.isShaft(world.getBlockState(pos)))
			return false;
		return true;
	}

}
