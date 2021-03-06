package com.smellypengu.createfabric.content.contraptions.relays.belt;

import com.smellypengu.createfabric.AllBlocks;
import com.smellypengu.createfabric.AllItems;
import com.smellypengu.createfabric.AllTileEntities;
import com.smellypengu.createfabric.content.contraptions.base.HorizontalKineticBlock;
import com.smellypengu.createfabric.content.schematics.ISpecialBlockItemRequirement;
import com.smellypengu.createfabric.foundation.block.ITE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.LinkedList;
import java.util.List;

public class BeltBlock extends HorizontalKineticBlock implements ITE<BeltTileEntity>, ISpecialBlockItemRequirement {

	public static final Property<BeltSlope> SLOPE = EnumProperty.of("slope", BeltSlope.class);
	public static final Property<BeltPart> PART = EnumProperty.of("part", BeltPart.class);
	public static final BooleanProperty CASING = BooleanProperty.of("casing");

	public BeltBlock(Settings properties) {
		super(properties);
		setDefaultState(getDefaultState().with(SLOPE, BeltSlope.HORIZONTAL)
			.with(PART, BeltPart.START)
			.with(CASING, false));
	}

	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> list) {
		list.add(AllItems.BELT_CONNECTOR.getDefaultStack());
	}

	@Override
	protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
		return super.areStatesKineticallyEquivalent(oldState.with(CASING, false), newState.with(CASING, false))
			&& oldState.get(PART) == newState.get(PART);
	}

	@Override
	public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
		/**if (face.getAxis() != getRotationAxis(state))
			return false;
		try {
			return getTileEntity(world, pos).hasPulley();
		} catch (ITE.TileEntityException e) {
		}*/
		return false;
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		if (state.get(SLOPE) == BeltSlope.SIDEWAYS)
			return Direction.Axis.Y;
		return state.get(HORIZONTAL_FACING)
			.rotateYClockwise() // TODO I THINK ITS rotateYClockwise INSTEAD OF rotateY
			.getAxis();
	}

	/**@Override
	public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
		return AllItems.BELT_CONNECTOR.asStack();
	}*/

	/**@Override
	public Material getMaterial(BlockState state) {
		return state.get(CASING) ? Material.WOOD : Material.WOOL;
	}*/

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		List<ItemStack> drops = super.getDroppedStacks(state, builder);
		BlockEntity tileEntity = builder.get(LootContextParameters.BLOCK_ENTITY);
		/**if (tileEntity instanceof BeltTileEntity && ((BeltTileEntity) tileEntity).hasPulley())
			drops.addAll(AllBlocks.SHAFT.getDefaultState()
					.getDrops(builder));*/
		return drops;
	}

	/**@Override
	public boolean isFlammable(BlockState state, World world, BlockPos pos, Direction face) {
		return false;
	}*/

	@Override
	public void onEntityLand(BlockView worldIn, Entity entityIn) {
		super.onEntityLand(worldIn, entityIn);
		BlockPos entityPosition = entityIn.getBlockPos();
		BlockPos beltPos = null;

		/**if (AllBlocks.BELT.has(worldIn.getBlockState(entityPosition)))
			beltPos = entityPosition;
		else if (AllBlocks.BELT.has(worldIn.getBlockState(entityPosition.down())))
			beltPos = entityPosition.down();
		if (beltPos == null)
			return;
		if (!(worldIn instanceof World))
			return;*/

		onEntityCollision(worldIn.getBlockState(beltPos), (World) worldIn, beltPos, entityIn);
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!canTransportObjects(state))
			return;
		/**if (entityIn instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entityIn;
			if (player.isSneaking())
				return;
			if (player.abilities.isFlying)
				return;
		}

		BeltTileEntity belt = BeltHelper.getSegmentTE(worldIn, pos);
		if (belt == null)
			return;
		if (entityIn instanceof ItemEntity && entityIn.isAlive()) {
			if (worldIn.isClient)
				return;
			if (entityIn.getMotion().y > 0)
				return;
			if (!entityIn.isAlive())
				return;
			withTileEntityDo(worldIn, pos, te -> {
				ItemEntity itemEntity = (ItemEntity) entityIn;
				IItemHandler handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					.orElse(null);
				if (handler == null)
					return;
				ItemStack remainder = handler.insertItem(0, itemEntity.getItem()
					.copy(), false);
				if (remainder.isEmpty())
					itemEntity.remove();
			});
			return;
		}

		BeltTileEntity controller = BeltHelper.getControllerTE(worldIn, pos);
		if (controller == null || controller.passengers == null)
			return;
		if (controller.passengers.containsKey(entityIn)) {
			TransportedEntityInfo info = controller.passengers.get(entityIn);
			if (info.getTicksSinceLastCollision() != 0 || pos.equals(entityIn.getPosition()))
				info.refresh(pos, state);
		} else {
			controller.passengers.put(entityIn, new TransportedEntityInfo(pos, state));
			entityIn.onGround = true;
		}*/
	}

	public static boolean canTransportObjects(BlockState state) {
		if (!AllBlocks.BELT.stateManager.getStates().contains(state))
			return false;
		BeltSlope slope = state.get(SLOPE);
		return slope != BeltSlope.VERTICAL && slope != BeltSlope.SIDEWAYS;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		/**if (player.isSneaking() || !player.isAllowEdit())
			return ActionResult.PASS;
		ItemStack heldItem = player.getHeldItem(handIn);
		boolean isShaft = AllBlocks.SHAFT.isIn(heldItem);
		boolean isDye = Tags.Items.DYES.contains(heldItem.getItem());
		boolean hasWater = EmptyingByBasin.emptyItem(world, heldItem, true)
				.getFirst()
				.getFluid()
				.isEquivalentTo(Fluids.WATER);
		boolean isHand = heldItem.isEmpty() && handIn == Hand.MAIN_HAND;

		if (isDye || hasWater) {
			if (!world.isClient)
				withTileEntityDo(world, pos, te -> te.applyColor(DyeColor.getColor(heldItem)));
			return ActionResult.SUCCESS;
		}

		BeltTileEntity belt = BeltHelper.getSegmentTE(world, pos);
		if (belt == null)
			return ActionResult.PASS;

		if (isHand) {
			BeltTileEntity controllerBelt = belt.getControllerTE();
			if (controllerBelt == null)
				return ActionResult.PASS;
			if (world.isClient)
				return ActionResult.SUCCESS;
			controllerBelt.getInventory()
					.applyToEachWithin(belt.index + .5f, .55f, (transportedItemStack) -> {
						player.inventory.placeItemBackInInventory(world, transportedItemStack.stack);
						return TransportedResult.removeItem();
					});
		}

		if (isShaft) {
			if (state.get(PART) != BeltPart.MIDDLE)
				return ActionResult.PASS;
			if (world.isClient)
				return ActionResult.SUCCESS;
			if (!player.isCreative())
				heldItem.shrink(1);
			KineticTileEntity.switchToBlockState(world, pos, state.with(PART, BeltPart.PULLEY));
			return ActionResult.SUCCESS;
		}

		if (AllBlocks.BRASS_CASING.isIn(heldItem)) {
			if (world.isClient)
				return ActionResult.SUCCESS;
			AllTriggers.triggerFor(AllTriggers.CASING_BELT, player);
			withTileEntityDo(world, pos, te -> te.setCasingType(BeltTileEntity.CasingType.BRASS));
			return ActionResult.SUCCESS;
		}

		if (AllBlocks.ANDESITE_CASING.isIn(heldItem)) {
			if (world.isClient)
				return ActionResult.SUCCESS;
			AllTriggers.triggerFor(AllTriggers.CASING_BELT, player);
			withTileEntityDo(world, pos, te -> te.setCasingType(BeltTileEntity.CasingType.ANDESITE));
			return ActionResult.SUCCESS;
		}*/

		return ActionResult.PASS;
	}

	/**@Override
	public ActionResult onWrenched(BlockState state, ItemUsageContext context) {
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();
		BlockPos pos = context.getBlockPos();

		if (state.get(CASING)) {
			if (world.isClient)
				return ActionResult.SUCCESS;
			//withTileEntityDo(world, pos, te -> te.setCasingType(BeltTileEntity.CasingType.NONE));
			return ActionResult.SUCCESS;
		}

		if (state.get(PART) == BeltPart.PULLEY) {
			if (world.isClient)
				return ActionResult.SUCCESS;
			KineticTileEntity.switchToBlockState(world, pos, state.with(PART, BeltPart.MIDDLE));
			if (player != null && !player.isCreative())
				player.inventory.placeItemBackInInventory(world, AllBlocks.SHAFT.asStack());
			return ActionResult.SUCCESS;
		}

		return ActionResult.FAIL;
	}*/

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(SLOPE, PART, CASING);
		super.appendProperties(builder);
	}

	/**@Override TODO MIGHT NEED FIXING BELT BLOCK IMPORTANT
	public boolean hasTileEntity(BlockState state) {
		return true;
	}*/

	/**@Override
	public PathNodeType getAiPathNodeType(BlockState state, BlockView world, BlockPos pos, MobEntity entity) {
		return PathNodeType.RAIL;
	}*/



	/**@Override
	@Environment(EnvType.CLIENT)
	public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
		BlockHelper.addReducedDestroyEffects(state, world, pos, manager);
		return true;
	}*/

	/*@Override
	public VoxelShape getVisualShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return BeltShapes.getShape(state);
	}*/

	/*@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		if (state.getBlock() != this)
			return VoxelShapes.empty();

		VoxelShape shape = getVisualShape(state, worldIn, pos, context);
		try {
			if (context.getEntity() == null)
				return shape;

			BeltTileEntity belt = getTileEntity((World) worldIn, pos);
			BeltTileEntity controller = belt.getControllerTE();

			if (controller == null)
				return shape;
			if (controller.passengers == null || !controller.passengers.containsKey(context.getEntity())) {
				return BeltShapes.getCollisionShape(state);
			}

		} catch (TileEntityException e) {
		}
		return shape;
	}*/

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return AllTileEntities.BELT.instantiate(pos, state); // TODO COULD BE WRONG HERE WHEN CREATING BELT ENTITY
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return state.get(CASING) ? BlockRenderType.MODEL : BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	public static void initBelt(World world, BlockPos pos) {
		if (world.isClient || world.isDebugWorld()) // TODO isDebugWorld INSTEAD OF world.getWorldType() == WorldType.DEBUG_ALL_BLOCK_STATES ??
			return;

		/**BlockState state = world.getBlockState(pos);
		if (!AllBlocks.BELT.has(state))
			return;
		// Find controller
		int limit = 1000;
		BlockPos currentPos = pos;
		while (limit-- > 0) {
			BlockState currentState = world.getBlockState(currentPos);
			if (!AllBlocks.BELT.has(currentState)) {
				world.removeBlock(pos, true);
				return;
			}
			BlockPos nextSegmentPosition = nextSegmentPosition(currentState, currentPos, false);
			if (nextSegmentPosition == null)
				break;
			if (!world.isAreaLoaded(nextSegmentPosition, 0))
				return;
			currentPos = nextSegmentPosition;
		}

		// Init belts
		int index = 0;
		List<BlockPos> beltChain = getBeltChain(world, currentPos);
		if (beltChain.size() < 2) {
			world.removeBlock(currentPos, true);
			return;
		}

		for (BlockPos beltPos : beltChain) {
			BlockEntity tileEntity = world.getBlockEntity(beltPos);
			BlockState currentState = world.getBlockState(beltPos);

			if (tileEntity instanceof BeltTileEntity && AllBlocks.BELT.has(currentState)) {
				BeltTileEntity te = (BeltTileEntity) tileEntity;
				te.setController(currentPos);
				te.beltLength = beltChain.size();
				te.index = index;
				te.attachKinetics();
				te.markDirty();
				te.sendData();

				if (te.isController() && !canTransportObjects(currentState))
					te.getInventory()
						.ejectAll();
			} else {
				world.removeBlock(currentPos, true);
				return;
			}
			index++;
		}*/

	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		/**if (world.isClient)
			return;
		if (state.getBlock() == newState.getBlock())
			return;
		if (isMoving)
			return;

		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof BeltTileEntity) {
			BeltTileEntity beltTileEntity = (BeltTileEntity) te;
			if (beltTileEntity.isController())
				beltTileEntity.getInventory()
						.ejectAll();
			world.removeBlockEntity(pos);
		}

		// Destroy chain
		for (boolean forward : Iterate.trueAndFalse) {
			BlockPos currentPos = nextSegmentPosition(state, pos, forward);
			if (currentPos == null)
				continue;
			BlockState currentState = world.getBlockState(currentPos);
			if (!AllBlocks.BELT.has(currentState))
				continue;

			boolean hasPulley = false;
			BlockEntity tileEntity = world.getBlockEntity(currentPos);
			if (tileEntity instanceof BeltTileEntity) {
				BeltTileEntity belt = (BeltTileEntity) tileEntity;
				if (belt.isController())
					belt.getInventory()
							.ejectAll();

				belt.remove();
				hasPulley = belt.hasPulley();
			}

			BlockState shaftState = AllBlocks.SHAFT.getDefaultState()
					.with(BlockStateProperties.AXIS, getRotationAxis(currentState));
			world.setBlockState(currentPos, hasPulley ? shaftState : Blocks.AIR.getDefaultState(), 3);
			world.playEvent(2001, currentPos, Block.getStateId(currentState));
		}*/
	}

	/**@Override
	public BlockState updatePostPlacement(BlockState state, Direction side, BlockState p_196271_3_, World world,
		BlockPos pos, BlockPos p_196271_6_) {
		if (side.getAxis()
			.isHorizontal())
			updateTunnelConnections(world, pos.up());
		return state;
	}*/

	private void updateTunnelConnections(World world, BlockPos pos) {
		Block tunnelBlock = world.getBlockState(pos)
			.getBlock();
		/**if (tunnelBlock instanceof BeltTunnelBlock)
			((BeltTunnelBlock) tunnelBlock).updateTunnel(world, pos);*/
	}

	public static List<BlockPos> getBeltChain(World world, BlockPos controllerPos) {
		List<BlockPos> positions = new LinkedList<>();

		BlockState blockState = world.getBlockState(controllerPos);
		if (!AllBlocks.BELT.hasTileEntity(blockState))
			return positions;

		int limit = 1000;
		BlockPos current = controllerPos;
		while (limit-- > 0 && current != null) {
			BlockState state = world.getBlockState(current);
			if (!AllBlocks.BELT.hasTileEntity(state))
				break;
			positions.add(current);
			current = nextSegmentPosition(state, current, true);
		}

		return positions;
	}

	public static BlockPos nextSegmentPosition(BlockState state, BlockPos pos, boolean forward) {
		Direction direction = state.get(HORIZONTAL_FACING);
		BeltSlope slope = state.get(SLOPE);
		BeltPart part = state.get(PART);

		int offset = forward ? 1 : -1;

		if (part == BeltPart.END && forward || part == BeltPart.START && !forward)
			return null;
		if (slope == BeltSlope.VERTICAL)
			return pos.up(direction.getDirection() == Direction.AxisDirection.POSITIVE ? offset : -offset);
		pos = pos.offset(direction, offset);
		if (slope != BeltSlope.HORIZONTAL && slope != BeltSlope.SIDEWAYS)
			return pos.up(slope == BeltSlope.UPWARD ? offset : -offset);
		return pos;
	}

	public static boolean canAccessFromSide(Direction facing, BlockState belt) {
//		if (facing == null)
//			return true;
//		if (!belt.get(BeltBlock.CASING))
//			return false;
//		BeltPart part = belt.get(BeltBlock.PART);
//		if (part != BeltPart.MIDDLE && facing.getAxis() == belt.get(HORIZONTAL_FACING)
//			.rotateY()
//			.getAxis())
//			return false;
//
//		BeltSlope slope = belt.get(BeltBlock.SLOPE);
//		if (slope != BeltSlope.HORIZONTAL) {
//			if (slope == BeltSlope.DOWNWARD && part == BeltPart.END)
//				return true;
//			if (slope == BeltSlope.UPWARD && part == BeltPart.START)
//				return true;
//			Direction beltSide = belt.get(HORIZONTAL_FACING);
//			if (slope == BeltSlope.DOWNWARD)
//				beltSide = beltSide.getOpposite();
//			if (beltSide == facing)
//				return false;
//		}

		return true;
	}

	@Override
	public Class<BeltTileEntity> getTileEntityClass() {
		return BeltTileEntity.class;
	}

	/**@Override
	public ItemRequirement getRequiredItems(BlockState state) {
		List<ItemStack> required = new ArrayList<>();
		if (state.get(PART) != BeltPart.MIDDLE)
			required.add(AllBlocks.SHAFT.asStack());
		if (state.get(PART) == BeltPart.START)
			required.add(AllItems.BELT_CONNECTOR.asStack());
		if (required.isEmpty())
			return ItemRequirement.NONE;
		return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, required);
	}*/

	@Override
	public BlockState rotate(BlockState state, BlockRotation rot) {
		BlockState rotate = super.rotate(state, rot);

		if (state.get(SLOPE) != BeltSlope.VERTICAL)
			return rotate;
		if (state.get(HORIZONTAL_FACING)
			.getDirection() != rotate.get(HORIZONTAL_FACING)
				.getDirection()) {
			if (state.get(PART) == BeltPart.START)
				return rotate.with(PART, BeltPart.END);
			if (state.get(PART) == BeltPart.END)
				return rotate.with(PART, BeltPart.START);
		}

		return rotate;
	}

}
