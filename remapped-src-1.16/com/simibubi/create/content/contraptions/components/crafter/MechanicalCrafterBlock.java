package com.simibubi.create.content.contraptions.components.crafter;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.HorizontalKineticBlock;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.components.crafter.ConnectedInputHandler.ConnectedInput;
import com.simibubi.create.content.contraptions.components.crafter.MechanicalCrafterTileEntity.Phase;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Pointing;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class MechanicalCrafterBlock extends HorizontalKineticBlock implements ITE<MechanicalCrafterTileEntity> {

	public static final EnumProperty<Pointing> POINTING = EnumProperty.of("pointing", Pointing.class);

	public MechanicalCrafterBlock(Settings properties) {
		super(properties);
		setDefaultState(getDefaultState().with(POINTING, Pointing.UP));
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(POINTING));
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.MECHANICAL_CRAFTER.create();
	}

	@Override
	public boolean hasIntegratedCogwheel(WorldView world, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(HORIZONTAL_FACING)
			.getAxis();
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		Direction face = context.getSide();
		BlockPos placedOnPos = context.getBlockPos()
			.offset(face.getOpposite());
		BlockState blockState = context.getWorld()
			.getBlockState(placedOnPos);

		if ((blockState.getBlock() != this) || (context.getPlayer() != null && context.getPlayer()
			.isSneaking())) {
			BlockState stateForPlacement = super.getPlacementState(context);
			Direction direction = stateForPlacement.get(HORIZONTAL_FACING);
			if (direction != face)
				stateForPlacement = stateForPlacement.with(POINTING, pointingFromFacing(face, direction));
			return stateForPlacement;
		}

		Direction otherFacing = blockState.get(HORIZONTAL_FACING);
		Pointing pointing = pointingFromFacing(face, otherFacing);
		return getDefaultState().with(HORIZONTAL_FACING, otherFacing)
			.with(POINTING, pointing);
	}

	@Override
	public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() == newState.getBlock()) {
			if (getTargetDirection(state) != getTargetDirection(newState)) {
				MechanicalCrafterTileEntity crafter = CrafterHelper.getCrafter(worldIn, pos);
				if (crafter != null)
					crafter.blockChanged();
			}
		}

		if (state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
			MechanicalCrafterTileEntity crafter = CrafterHelper.getCrafter(worldIn, pos);
			if (crafter != null) {
				if (crafter.covered)
					Block.dropStack(worldIn, pos, AllItems.CRAFTER_SLOT_COVER.asStack());
				crafter.ejectWholeGrid();
			}

			for (Direction direction : Iterate.directions) {
				if (direction.getAxis() == state.get(HORIZONTAL_FACING)
					.getAxis())
					continue;

				BlockPos otherPos = pos.offset(direction);
				ConnectedInput thisInput = CrafterHelper.getInput(worldIn, pos);
				ConnectedInput otherInput = CrafterHelper.getInput(worldIn, otherPos);

				if (thisInput == null || otherInput == null)
					continue;
				if (!pos.add(thisInput.data.get(0))
					.equals(otherPos.add(otherInput.data.get(0))))
					continue;

				ConnectedInputHandler.toggleConnection(worldIn, pos, otherPos);
			}

			worldIn.removeBlockEntity(pos);
		}
	}

	public static Pointing pointingFromFacing(Direction pointingFace, Direction blockFacing) {
		boolean positive = blockFacing.getDirection() == AxisDirection.POSITIVE;

		Pointing pointing = pointingFace == Direction.DOWN ? Pointing.UP : Pointing.DOWN;
		if (pointingFace == Direction.EAST)
			pointing = positive ? Pointing.LEFT : Pointing.RIGHT;
		if (pointingFace == Direction.WEST)
			pointing = positive ? Pointing.RIGHT : Pointing.LEFT;
		if (pointingFace == Direction.NORTH)
			pointing = positive ? Pointing.LEFT : Pointing.RIGHT;
		if (pointingFace == Direction.SOUTH)
			pointing = positive ? Pointing.RIGHT : Pointing.LEFT;
		return pointing;
	}

	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		if (context.getSide() == state.get(HORIZONTAL_FACING)) {
			if (!context.getWorld().isClient)
				KineticTileEntity.switchToBlockState(context.getWorld(), context.getBlockPos(), state.cycle(POINTING));
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	@Override
	public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
		BlockHitResult hit) {
		ItemStack heldItem = player.getStackInHand(handIn);
		boolean isHand = heldItem.isEmpty() && handIn == Hand.MAIN_HAND;

		BlockEntity te = worldIn.getBlockEntity(pos);
		if (!(te instanceof MechanicalCrafterTileEntity))
			return ActionResult.PASS;
		MechanicalCrafterTileEntity crafter = (MechanicalCrafterTileEntity) te;
		boolean wrenched = AllItems.WRENCH.isIn(heldItem);

		if (AllBlocks.MECHANICAL_ARM.isIn(heldItem))
			return ActionResult.PASS;
		
		if (hit.getSide() == state.get(HORIZONTAL_FACING)) {

			if (crafter.phase != Phase.IDLE && !wrenched) {
				crafter.ejectWholeGrid();
				return ActionResult.SUCCESS;
			}

			if (crafter.phase == Phase.IDLE && !isHand && !wrenched) {
				if (worldIn.isClient)
					return ActionResult.SUCCESS;

				if (AllItems.CRAFTER_SLOT_COVER.isIn(heldItem)) {
					if (crafter.covered)
						return ActionResult.PASS;
					if (!crafter.inventory.isEmpty())
						return ActionResult.PASS;
					crafter.covered = true;
					crafter.markDirty();
					crafter.sendData();
					if (!player.isCreative())
						heldItem.decrement(1);
					return ActionResult.SUCCESS;
				}

				LazyOptional<IItemHandler> capability =
					crafter.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
				if (!capability.isPresent())
					return ActionResult.PASS;
				ItemStack remainder =
					ItemHandlerHelper.insertItem(capability.orElse(new ItemStackHandler()), heldItem.copy(), false);
				if (remainder.getCount() != heldItem.getCount())
					player.setStackInHand(handIn, remainder);
				return ActionResult.SUCCESS;
			}

			ItemStack inSlot = crafter.getInventory().getStack(0);
			if (inSlot.isEmpty()) {
				if (crafter.covered && !wrenched) {
					if (worldIn.isClient)
						return ActionResult.SUCCESS;
					crafter.covered = false;
					crafter.markDirty();
					crafter.sendData();
					if (!player.isCreative())
						player.inventory.offerOrDrop(worldIn, AllItems.CRAFTER_SLOT_COVER.asStack());
					return ActionResult.SUCCESS;
				}
				return ActionResult.PASS;
			}
			if (!isHand && !ItemHandlerHelper.canItemStacksStack(heldItem, inSlot))
				return ActionResult.PASS;
			if (worldIn.isClient)
				return ActionResult.SUCCESS;
			player.inventory.offerOrDrop(worldIn, inSlot);
			crafter.getInventory().setStackInSlot(0, ItemStack.EMPTY);
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	@Override
	public float getParticleTargetRadius() {
		return .85f;
	}

	@Override
	public float getParticleInitialRadius() {
		return .75f;
	}

	public static Direction getTargetDirection(BlockState state) {
		if (!AllBlocks.MECHANICAL_CRAFTER.has(state))
			return Direction.UP;
		Direction facing = state.get(HORIZONTAL_FACING);
		Pointing point = state.get(POINTING);
		Vec3d targetVec = new Vec3d(0, 1, 0);
		targetVec = VecHelper.rotate(targetVec, -point.getXRotation(), Axis.Z);
		targetVec = VecHelper.rotate(targetVec, AngleHelper.horizontalAngle(facing), Axis.Y);
		return Direction.getFacing(targetVec.x, targetVec.y, targetVec.z);
	}

	public static boolean isValidTarget(World world, BlockPos targetPos, BlockState crafterState) {
		BlockState targetState = world.getBlockState(targetPos);
		if (!world.canSetBlock(targetPos))
			return false;
		if (!AllBlocks.MECHANICAL_CRAFTER.has(targetState))
			return false;
		if (crafterState.get(HORIZONTAL_FACING) != targetState.get(HORIZONTAL_FACING))
			return false;
		if (Math.abs(crafterState.get(POINTING)
			.getXRotation()
			- targetState.get(POINTING)
				.getXRotation()) == 180)
			return false;
		return true;
	}

	@Override
	public Class<MechanicalCrafterTileEntity> getTileEntityClass() {
		return MechanicalCrafterTileEntity.class;
	}

}
