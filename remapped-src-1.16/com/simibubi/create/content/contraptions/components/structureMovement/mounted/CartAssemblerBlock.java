package com.simibubi.create.content.contraptions.components.structureMovement.mounted;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.mounted.CartAssemblerTileEntity.CartMovementMode;
import com.simibubi.create.content.contraptions.components.structureMovement.train.CouplingHandler;
import com.simibubi.create.content.contraptions.components.structureMovement.train.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.components.structureMovement.train.capability.MinecartController;
import com.simibubi.create.content.contraptions.components.tracks.ControllerRailBlock;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.content.schematics.ISpecialBlockItemRequirement;
import com.simibubi.create.content.schematics.ItemRequirement;
import com.simibubi.create.content.schematics.ItemRequirement.ItemUseType;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraftforge.common.util.LazyOptional;

public class CartAssemblerBlock extends AbstractRailBlock
	implements ITE<CartAssemblerTileEntity>, IWrenchable, ISpecialBlockItemRequirement {

	public static final Property<RailShape> RAIL_SHAPE =
		EnumProperty.of("shape", RailShape.class, RailShape.EAST_WEST, RailShape.NORTH_SOUTH);
	public static final Property<CartAssembleRailType> RAIL_TYPE =
		EnumProperty.of("rail_type", CartAssembleRailType.class);
	public static final BooleanProperty POWERED = Properties.POWERED;

	public CartAssemblerBlock(Settings properties) {
		super(true, properties);
		setDefaultState(getDefaultState().with(POWERED, false)
			.with(RAIL_TYPE, CartAssembleRailType.POWERED_RAIL));
	}

	public static BlockState createAnchor(BlockState state) {
		Axis axis = state.get(RAIL_SHAPE) == RailShape.NORTH_SOUTH ? Axis.Z : Axis.X;
		return AllBlocks.MINECART_ANCHOR.getDefaultState()
			.with(Properties.HORIZONTAL_AXIS, axis);
	}

	private static Item getRailItem(BlockState state) {
		return state.get(RAIL_TYPE).getItem();
	}

	public static BlockState getRailBlock(BlockState state) {
		AbstractRailBlock railBlock = (AbstractRailBlock) state.get(RAIL_TYPE).getBlock();
		BlockState railState = railBlock.getDefaultState()
			.with(railBlock.getShapeProperty(), state.get(RAIL_SHAPE));
		if (railState.contains(ControllerRailBlock.BACKWARDS)) {
			railState = railState.with(ControllerRailBlock.BACKWARDS, state.get(RAIL_TYPE) == CartAssembleRailType.CONTROLLER_RAIL_BACKWARDS);
		}
		return railState;
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(RAIL_SHAPE, POWERED, RAIL_TYPE);
		super.appendProperties(builder);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.CART_ASSEMBLER.create();
	}

	@Override
	public boolean canMakeSlopes(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos) {
		return false;
	}

	@Override
	public void onMinecartPass(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos,
		AbstractMinecartEntity cart) {
		if (!canAssembleTo(cart))
			return;
		if (world.isClient)
			return;

		withTileEntityDo(world, pos, te -> {
			/*
		}
<<<<<<< HEAD
			if (te.isMinecartUpdateValid()) {
				switch (state.get(RAIL_TYPE)) {
				case POWERED_RAIL:
					if (state.get(POWERED)) {
						assemble(world, pos, cart);
						Direction facing = cart.getAdjustedHorizontalFacing();
						float speed = getRailMaxSpeed(state, world, pos, cart);
						cart.setMotion(facing.getXOffset() * speed, facing.getYOffset() * speed,
							facing.getZOffset() * speed);
					} else {
						disassemble(world, pos, cart);
						Vector3d diff = VecHelper.getCenterOf(pos)
							.subtract(cart.getPositionVec());
						cart.setMotion(diff.x / 16f, 0, diff.z / 16f);
					}
					break;
				case REGULAR:
					if (state.get(POWERED)) {
						assemble(world, pos, cart);
					} else {
						disassemble(world, pos, cart);
					}
					break;
				case ACTIVATOR_RAIL:
					if (state.get(POWERED)) {
						disassemble(world, pos, cart);
					}
					break;
				case DETECTOR_RAIL:
					if (cart.getPassengers()
						.isEmpty()) {
						assemble(world, pos, cart);
						Direction facing = cart.getAdjustedHorizontalFacing();
						float speed = getRailMaxSpeed(state, world, pos, cart);
						cart.setMotion(facing.getXOffset() * speed, facing.getYOffset() * speed,
							facing.getZOffset() * speed);
					} else {
						disassemble(world, pos, cart);
					}
					break;
				default:
					break;
				}
				te.resetTicksSinceMinecartUpdate();
=======*/
			if (!te.isMinecartUpdateValid())
				return;

			CartAssemblerAction action = getActionForCart(state, cart);
			if (action.shouldAssemble())
				assemble(world, pos, cart);
			if (action.shouldDisassemble())
				disassemble(world, pos, cart);
			if (action == CartAssemblerAction.ASSEMBLE_ACCELERATE) {
				Direction facing = cart.getMovementDirection();
				float speed = getRailMaxSpeed(state, world, pos, cart);
				cart.setVelocity(facing.getOffsetX() * speed, facing.getOffsetY() * speed, facing.getOffsetZ() * speed);
			}
			if (action == CartAssemblerAction.ASSEMBLE_ACCELERATE_DIRECTIONAL) {
				Vec3i accelerationVector = ControllerRailBlock.getAccelerationVector(AllBlocks.CONTROLLER_RAIL.getDefaultState().with(ControllerRailBlock.SHAPE, state.get(RAIL_SHAPE)).with(ControllerRailBlock.BACKWARDS, state.get(RAIL_TYPE) == CartAssembleRailType.CONTROLLER_RAIL_BACKWARDS));
				float speed = getRailMaxSpeed(state, world, pos, cart);
				cart.setVelocity(Vec3d.of(accelerationVector).multiply(speed));
			}
			if (action == CartAssemblerAction.DISASSEMBLE_BRAKE) {
				Vec3d diff = VecHelper.getCenterOf(pos)
					.subtract(cart.getPos());
				cart.setVelocity(diff.x / 16f, 0, diff.z / 16f);
			}

		});
	}

	public enum CartAssemblerAction {
		ASSEMBLE, DISASSEMBLE, ASSEMBLE_ACCELERATE, DISASSEMBLE_BRAKE, ASSEMBLE_ACCELERATE_DIRECTIONAL, PASS;

		public boolean shouldAssemble() {
			return this == ASSEMBLE || this == ASSEMBLE_ACCELERATE || this == ASSEMBLE_ACCELERATE_DIRECTIONAL;
		}

		public boolean shouldDisassemble() {
			return this == DISASSEMBLE || this == DISASSEMBLE_BRAKE;
		}
	}

	public static CartAssemblerAction getActionForCart(BlockState state, AbstractMinecartEntity cart) {
		CartAssembleRailType type = state.get(RAIL_TYPE);
		boolean powered = state.get(POWERED);

		if (type == CartAssembleRailType.REGULAR)
			return powered ? CartAssemblerAction.ASSEMBLE : CartAssemblerAction.DISASSEMBLE;

		if (type == CartAssembleRailType.ACTIVATOR_RAIL)
			return powered ? CartAssemblerAction.DISASSEMBLE : CartAssemblerAction.PASS;

		if (type == CartAssembleRailType.POWERED_RAIL)
			return powered ? CartAssemblerAction.ASSEMBLE_ACCELERATE : CartAssemblerAction.DISASSEMBLE_BRAKE;

		if (type == CartAssembleRailType.DETECTOR_RAIL)
			return cart.getPassengerList()
				.isEmpty() ? CartAssemblerAction.ASSEMBLE_ACCELERATE : CartAssemblerAction.DISASSEMBLE;

		if (type == CartAssembleRailType.CONTROLLER_RAIL || type == CartAssembleRailType.CONTROLLER_RAIL_BACKWARDS)
			return powered ? CartAssemblerAction.ASSEMBLE_ACCELERATE_DIRECTIONAL : CartAssemblerAction.DISASSEMBLE_BRAKE;

		return CartAssemblerAction.PASS;
	}

	public static boolean canAssembleTo(AbstractMinecartEntity cart) {
		return cart.canBeRidden() || cart instanceof FurnaceMinecartEntity || cart instanceof ChestMinecartEntity;
	}

	@Override
	@Nonnull
	public ActionResult onUse(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos,
		PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockHitResult blockRayTraceResult) {

		ItemStack itemStack = player.getStackInHand(hand);
		Item previousItem = getRailItem(state);
		Item heldItem = itemStack.getItem();
		if (heldItem != previousItem) {

			CartAssembleRailType newType = null;
			for (CartAssembleRailType type : CartAssembleRailType.values())
				if (heldItem == type.getItem())
					newType = type;
			if (newType == null)
				return ActionResult.PASS;
			world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1, 1);
			world.setBlockState(pos, state.with(RAIL_TYPE, newType));

			if (!player.isCreative()) {
				itemStack.decrement(1);
				player.inventory.offerOrDrop(world, new ItemStack(previousItem));
			}
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	protected void assemble(World world, BlockPos pos, AbstractMinecartEntity cart) {
		if (!cart.getPassengerList()
			.isEmpty())
			return;

		LazyOptional<MinecartController> optional =
			cart.getCapability(CapabilityMinecartController.MINECART_CONTROLLER_CAPABILITY);
		if (optional.isPresent() && optional.orElse(null)
			.isCoupledThroughContraption())
			return;

		
		Optional<CartAssemblerTileEntity> assembler = getTileEntityOptional(world, pos);
		CartMovementMode mode = assembler.map(te -> CartMovementMode.values()[te.movementMode.value])
				.orElse(CartMovementMode.ROTATE);

		MountedContraption contraption = new MountedContraption(mode);
		try {
			if (!contraption.assemble(world, pos))
				return;

			assembler.ifPresent(te -> {
				te.lastException = null;
				te.sendData();
			});
		} catch (AssemblyException e) {
			assembler.ifPresent(te -> {
				te.lastException = e;
				te.sendData();
			});
			return;
		}

		boolean couplingFound = contraption.connectedCart != null;
		Optional<Direction> initialOrientation = cart.getVelocity()
			.length() < 1 / 512f ? Optional.empty() : Optional.of(cart.getMovementDirection());

		if (couplingFound) {
			cart.updatePosition(pos.getX() + .5f, pos.getY(), pos.getZ() + .5f);
			if (!CouplingHandler.tryToCoupleCarts(null, world, cart.getEntityId(),
				contraption.connectedCart.getEntityId()))
				return;
		}

		contraption.removeBlocksFromWorld(world, BlockPos.ORIGIN);
		contraption.startMoving(world);
		contraption.expandBoundsAroundAxis(Axis.Y);

		if (couplingFound) {
			Vec3d diff = contraption.connectedCart.getPos()
				.subtract(cart.getPos());
			initialOrientation = Optional.of(Direction.fromRotation(MathHelper.atan2(diff.z, diff.x) * 180 / Math.PI));
		}

		OrientedContraptionEntity entity = OrientedContraptionEntity.create(world, contraption, initialOrientation);
		if (couplingFound)
			entity.setCouplingId(cart.getUuid());
		entity.updatePosition(pos.getX(), pos.getY(), pos.getZ());
		world.spawnEntity(entity);
		entity.startRiding(cart);

		if (cart instanceof FurnaceMinecartEntity) {
			CompoundTag nbt = cart.serializeNBT();
			nbt.putDouble("PushZ", 0);
			nbt.putDouble("PushX", 0);
			cart.deserializeNBT(nbt);
		}
	}

	protected void disassemble(World world, BlockPos pos, AbstractMinecartEntity cart) {
		if (cart.getPassengerList()
			.isEmpty())
			return;
		Entity entity = cart.getPassengerList()
			.get(0);
		if (!(entity instanceof OrientedContraptionEntity))
			return;
		OrientedContraptionEntity contraption = (OrientedContraptionEntity) entity;
		UUID couplingId = contraption.getCouplingId();

		if (couplingId == null) {
			disassembleCart(cart);
			return;
		}

		Couple<MinecartController> coupledCarts = contraption.getCoupledCartsIfPresent();
		if (coupledCarts == null)
			return;

		// Make sure connected cart is present and being disassembled
		for (boolean current : Iterate.trueAndFalse) {
			MinecartController minecartController = coupledCarts.get(current);
			if (minecartController.cart() == cart)
				continue;
			BlockPos otherPos = minecartController.cart()
				.getBlockPos();
			BlockState blockState = world.getBlockState(otherPos);
			if (!AllBlocks.CART_ASSEMBLER.has(blockState))
				return;
			if (!getActionForCart(blockState, minecartController.cart()).shouldDisassemble())
				return;
		}

		for (boolean current : Iterate.trueAndFalse)
			coupledCarts.get(current)
				.removeConnection(current);
		disassembleCart(cart);
	}

	protected void disassembleCart(AbstractMinecartEntity cart) {
		cart.removeAllPassengers();
		if (cart instanceof FurnaceMinecartEntity) {
			CompoundTag nbt = cart.serializeNBT();
			nbt.putDouble("PushZ", cart.getVelocity().x);
			nbt.putDouble("PushX", cart.getVelocity().z);
			cart.deserializeNBT(nbt);
		}
	}

	@Override
	public void neighborUpdate(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos,
		@Nonnull Block blockIn, @Nonnull BlockPos fromPos, boolean isMoving) {
		super.neighborUpdate(state, worldIn, pos, blockIn, fromPos, isMoving);

		if (worldIn.isClient)
			return;

		boolean previouslyPowered = state.get(POWERED);
		if (previouslyPowered != worldIn.isReceivingRedstonePower(pos)) {
			worldIn.setBlockState(pos, state.cycle(POWERED), 2);
		}
	}

	@Override
	@Nonnull
	public Property<RailShape> getShapeProperty() {
		return RAIL_SHAPE;
	}

	@Override
	@Nonnull
	public VoxelShape getOutlineShape(BlockState state, @Nonnull BlockView worldIn, @Nonnull BlockPos pos,
		@Nonnull ShapeContext context) {
		return AllShapes.CART_ASSEMBLER.get(getRailAxis(state));
	}

	protected Axis getRailAxis(BlockState state) {
		return state.get(RAIL_SHAPE) == RailShape.NORTH_SOUTH ? Direction.Axis.Z : Direction.Axis.X;
	}

	@Override
	@Nonnull
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockView worldIn, @Nonnull BlockPos pos,
		ShapeContext context) {
		Entity entity = context.getEntity();
		if (entity instanceof AbstractMinecartEntity)
			return VoxelShapes.empty();
		if (entity instanceof PlayerEntity)
			return AllShapes.CART_ASSEMBLER_PLAYER_COLLISION.get(getRailAxis(state));
		return VoxelShapes.fullCube();
	}

	@Override
	@Nonnull
	public PistonBehavior getPistonBehavior(@Nonnull BlockState state) {
		return PistonBehavior.BLOCK;
	}

	/* FIXME: Is there a 1.16 equivalent to be used? Or is this just removed?
	@Override
	public boolean isNormalCube(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos) {
		return false;
	}
	 */

	@Override
	public Class<CartAssemblerTileEntity> getTileEntityClass() {
		return CartAssemblerTileEntity.class;
	}

	@Override
	public boolean canPlaceAt(@Nonnull BlockState state, @Nonnull WorldView world, @Nonnull BlockPos pos) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	@Nonnull
	public List<ItemStack> getDroppedStacks(@Nonnull BlockState state,
		@Nonnull net.minecraft.loot.context.LootContext.Builder builder) {
		List<ItemStack> drops = super.getDroppedStacks(state, builder);
		drops.addAll(getRailBlock(state).getDroppedStacks(builder));
		return drops;
	}

	@Override
	public ItemRequirement getRequiredItems(BlockState state) {
		ArrayList<ItemStack> reuiredItems = new ArrayList<ItemStack>();
		reuiredItems.add(new ItemStack(getRailItem(state)));
		reuiredItems.add(new ItemStack(asItem()));
		return new ItemRequirement(ItemUseType.CONSUME, reuiredItems);
	}

	@SuppressWarnings("deprecation")
	public List<ItemStack> getDropedAssembler(BlockState state, ServerWorld world, BlockPos pos,
		@Nullable BlockEntity p_220077_3_, @Nullable Entity p_220077_4_, ItemStack p_220077_5_) {
		return super.getDroppedStacks(state, (new LootContext.Builder(world)).random(world.random)
			.parameter(LootContextParameters.ORIGIN, Vec3d.of(pos))
			.parameter(LootContextParameters.TOOL, p_220077_5_)
			.optionalParameter(LootContextParameters.THIS_ENTITY, p_220077_4_)
			.optionalParameter(LootContextParameters.BLOCK_ENTITY, p_220077_3_));
	}

	@Override
	public ActionResult onSneakWrenched(BlockState state, ItemUsageContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		PlayerEntity player = context.getPlayer();
		if (world.isClient)
			return ActionResult.SUCCESS;

		if (player != null && !player.isCreative())
			getDropedAssembler(state, (ServerWorld) world, pos, world.getBlockEntity(pos), player, context.getStack())
				.forEach(itemStack -> {
					player.inventory.offerOrDrop(world, itemStack);
				});
		if(world instanceof ServerWorld)
			state.onStacksDropped((ServerWorld) world, pos, ItemStack.EMPTY);
		world.setBlockState(pos, getRailBlock(state));
		return ActionResult.SUCCESS;
	}

	public static class MinecartAnchorBlock extends Block {

		public MinecartAnchorBlock(Settings p_i48440_1_) {
			super(p_i48440_1_);
		}

		@Override
		protected void appendProperties(Builder<Block, BlockState> builder) {
			builder.add(Properties.HORIZONTAL_AXIS);
			super.appendProperties(builder);
		}

		@Override
		@Nonnull
		public VoxelShape getOutlineShape(@Nonnull BlockState p_220053_1_, @Nonnull BlockView p_220053_2_,
			@Nonnull BlockPos p_220053_3_, @Nonnull ShapeContext p_220053_4_) {
			return VoxelShapes.empty();
		}
	}

	@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		World world = context.getWorld();
		if (world.isClient)
			return ActionResult.SUCCESS;
		BlockPos pos = context.getBlockPos();
		BlockState newState = state.with(RAIL_SHAPE, state.get(RAIL_SHAPE) == RailShape.NORTH_SOUTH ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
		if (state.get(RAIL_TYPE) == CartAssembleRailType.CONTROLLER_RAIL || state.get(RAIL_TYPE) == CartAssembleRailType.CONTROLLER_RAIL_BACKWARDS) {
			newState = newState.with(RAIL_TYPE, AllBlocks.CONTROLLER_RAIL.get().rotate(AllBlocks.CONTROLLER_RAIL.getDefaultState()
				.with(ControllerRailBlock.SHAPE, state.get(RAIL_SHAPE)).with(ControllerRailBlock.BACKWARDS,
					state.get(RAIL_TYPE) == CartAssembleRailType.CONTROLLER_RAIL_BACKWARDS), BlockRotation.CLOCKWISE_90)
				.get(ControllerRailBlock.BACKWARDS) ? CartAssembleRailType.CONTROLLER_RAIL_BACKWARDS : CartAssembleRailType.CONTROLLER_RAIL);
		}
			context.getWorld().setBlockState(pos, newState, 3);
			world.updateNeighborsAlways(pos.down(), this);
		return ActionResult.SUCCESS;
	}
}
