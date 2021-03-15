package com.simibubi.create.content.contraptions.components.structureMovement;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.MechanicalBearingBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.StabilizedContraption;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.AbstractChassisBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.ChassisBlockEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueHandler;
import com.simibubi.create.content.contraptions.components.structureMovement.pulley.PulleyBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.pulley.PulleyBlockEntity;
import com.simibubi.create.foundation.render.backend.light.EmptyLighter;
import com.simibubi.create.foundation.utility.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.*;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class Contraption {

	private List<SuperGlueEntity> glueToRemove;

	private final Map<BlockPos, Entity> initialPassengers;
	private final List<BlockFace> pendingSubContraptions;
	public AbstractContraptionEntity entity;
	/*
	 * public CombinedInvWrapper inventory;
	 * public CombinedTankWrapper fluidInventory;
	 */
	public Box bounds;
	public BlockPos anchor;
	public boolean stalled;
	// Client
	public Map<BlockPos, BlockEntity> presentBlockEntities;
	public List<BlockEntity> maybeInstancedBlockEntities;
	public List<BlockEntity> specialRenderedBlockEntities;
	protected Map<BlockPos, Structure.StructureBlockInfo> blocks;
	/*
	 * protected Map<BlockPos, MountedStorage> storage;
	 * protected Map<BlockPos, MountedFluidStorage> fluidStorage;
	 */
	protected List<MutablePair<Structure.StructureBlockInfo, MovementContext>> actors;
	protected Set<Pair<BlockPos, Direction>> superglue;
	protected List<BlockPos> seats;
	protected Map<UUID, Integer> seatMapping;
	protected Map<UUID, BlockFace> stabilizedSubContraptions;

	public Contraption() {
		blocks = new HashMap<>();
		/*storage = new HashMap<>();*/
		seats = new ArrayList<>();
		actors = new ArrayList<>();
		superglue = new HashSet<>();
		seatMapping = new HashMap<>();
		/*fluidStorage = new HashMap<>();*/
		glueToRemove = new ArrayList<>();
		initialPassengers = new HashMap<>();
		presentBlockEntities = new HashMap<>();
		maybeInstancedBlockEntities = new ArrayList<>();
		specialRenderedBlockEntities = new ArrayList<>();
		pendingSubContraptions = new ArrayList<>();
		stabilizedSubContraptions = new HashMap<>();
	}

	public static Contraption fromNBT(World world, CompoundTag nbt, boolean spawnData) {
		String type = nbt.getString("Type");
		Contraption contraption = ContraptionType.fromType(type);
		contraption.readNBT(world, nbt, spawnData);
		return contraption;
	}

	private static Structure.StructureBlockInfo readBlockInfo(CompoundTag blockListEntry, BlockState blockstate) {
		return new Structure.StructureBlockInfo(
			BlockPos.fromLong(blockListEntry.getLong("Pos")),
			blockstate,
			blockListEntry.contains("Data") ? blockListEntry.getCompound("Data") : null
		);
	}

	private static Structure.StructureBlockInfo legacyReadBlockInfo(CompoundTag blockListEntry) {
		return new Structure.StructureBlockInfo(
			NbtHelper.toBlockPos(blockListEntry.getCompound("Pos")),
			NbtHelper.toBlockState(blockListEntry.getCompound("Block")),
			blockListEntry.contains("Data") ? blockListEntry.getCompound("Data") : null
		);
	}

	public abstract boolean assemble(World world, BlockPos pos) throws AssemblyException;

	public abstract boolean canBeStabilized(Direction facing, BlockPos localPos);

	protected abstract ContraptionType getType();

	protected boolean customBlockPlacement(World world, BlockPos pos, BlockState state) {
		return false;
	}

	protected boolean customBlockRemoval(World world, BlockPos pos, BlockState state) {
		return false;
	}

	protected boolean addToInitialFrontier(World world, BlockPos pos, Direction forcedDirection,
										   Queue<BlockPos> frontier) throws AssemblyException {
		return true;
	}

	public boolean searchMovedStructure(World world, BlockPos pos, @Nullable Direction forcedDirection) throws AssemblyException {
		initialPassengers.clear();
		Queue<BlockPos> frontier = new UniqueLinkedList<>();
		Set<BlockPos> visited = new HashSet<>();
		anchor = pos;

		if (bounds == null)
			bounds = new Box(BlockPos.ORIGIN);

		if (!BlockMovementTraits.isBrittle(world.getBlockState(pos)))
	 		frontier.add(pos);
	 	if (!addToInitialFrontier(world, pos, forcedDirection, frontier))
	 		return false;
	 	for (int limit = 100000; limit > 0; limit--) {
	 		if (frontier.isEmpty())
	 			return true;
	 		if (!moveBlock(world, forcedDirection, frontier, visited))
	 			return false;
		}
		throw AssemblyException.structureTooLarge();
	}

	public void onEntityCreated(AbstractContraptionEntity entity) {
		this.entity = entity;

		// Create subcontraptions
		for (BlockFace blockFace : pendingSubContraptions) {
			Direction face = blockFace.getFace();
			StabilizedContraption subContraption = new StabilizedContraption(face);
			World world = entity.world;
			BlockPos pos = blockFace.getPos();
			try {
				if (!subContraption.assemble(world, pos))
					continue;
			} catch (AssemblyException e) {
				continue;
			}
			subContraption.removeBlocksFromWorld(world, (BlockPos) BlockPos.ZERO);
			OrientedContraptionEntity movedContraption =
				OrientedContraptionEntity.create(world, subContraption, Optional.of(face));
			BlockPos anchor = blockFace.getConnectedPos();
			movedContraption.updatePosition(anchor.getX() + .5f, anchor.getY(), anchor.getZ() + .5f);
			world.spawnEntity(movedContraption);
			stabilizedSubContraptions.put(movedContraption.getUuid(), new BlockFace(toLocalPos(pos), face));
		}

		// Gather itemhandlers of mounted storage
		/*List<IItemHandlerModifiable> list = storage.values()
		 .stream()
		 .map(MountedStorage::getItemHandler)
		 .collect(Collectors.toList());
		 inventory = new CombinedInvWrapper(Arrays.copyOf(list.toArray(), list.size(), IItemHandlerModifiable[].class));*/

		/*List<IFluidHandler> fluidHandlers = fluidStorage.values()
		 .stream()
		 .map(MountedFluidStorage::getFluidHandler)
		 .collect(Collectors.toList());
		 fluidInventory = new CombinedTankWrapper(
		 Arrays.copyOf(fluidHandlers.toArray(), fluidHandlers.size(), IFluidHandler[].class));*/
	}

	public void onEntityInitialize(World world, AbstractContraptionEntity contraptionEntity) {
		if (world.isClient)
			return;

		// TODO THIS LOOP COULD BE VERY VERY WRONG
		for (Entity orientedCE : world.getEntitiesByClass(OrientedContraptionEntity.class,
			contraptionEntity.getBoundingBox()
				.expand(1), null)) {
			if (stabilizedSubContraptions.containsKey(orientedCE.getUuid()))
				orientedCE.startRiding(contraptionEntity);
		}

		for (BlockPos seatPos : getSeats()) {
			Entity passenger = initialPassengers.get(seatPos);
			if (passenger == null)
				continue;
			int seatIndex = getSeats().indexOf(seatPos);
			if (seatIndex == -1)
				continue;
			contraptionEntity.addSittingPassenger(passenger, seatIndex);
		}
	}

	/*protected void movePistonHead(World world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited,
	 BlockState state) {
	 Direction direction = state.get(MechanicalPistonHeadBlock.FACING);
	 BlockPos offset = pos.offset(direction.getOpposite());
	 if (!visited.contains(offset)) {
	 BlockState blockState = world.getBlockState(offset);
	 if (isExtensionPole(blockState) && blockState.get(PistonExtensionPoleBlock.FACING)
	 .getAxis() == direction.getAxis())
	 frontier.add(offset);
	 if (blockState.getBlock() instanceof MechanicalPistonBlock) {
	 Direction pistonFacing = blockState.get(MechanicalPistonBlock.FACING);
	 if (pistonFacing == direction && blockState.get(MechanicalPistonBlock.STATE) == PistonState.EXTENDED)
	 frontier.add(offset);
	 }
	 }
	 if (state.get(MechanicalPistonHeadBlock.TYPE) == PistonType.STICKY) {
	 BlockPos attached = pos.offset(direction);
	 if (!visited.contains(attached))
	 frontier.add(attached);
	 }
	 }*/

	/*protected void movePistonPole(World world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited,
	 BlockState state) {
	 for (Direction d : Iterate.directionsInAxis(state.get(PistonExtensionPoleBlock.FACING)
	 .getAxis())) {
	 BlockPos offset = pos.offset(d);
	 if (!visited.contains(offset)) {
	 BlockState blockState = world.getBlockState(offset);
	 if (isExtensionPole(blockState) && blockState.get(PistonExtensionPoleBlock.FACING)
	 .getAxis() == d.getAxis())
	 frontier.add(offset);
	 if (isPistonHead(blockState) && blockState.get(MechanicalPistonHeadBlock.FACING)
	 .getAxis() == d.getAxis())
	 frontier.add(offset);
	 if (blockState.getBlock() instanceof MechanicalPistonBlock) {
	 Direction pistonFacing = blockState.get(MechanicalPistonBlock.FACING);
	 if (pistonFacing == d || pistonFacing == d.getOpposite()
	 && blockState.get(MechanicalPistonBlock.STATE) == PistonState.EXTENDED)
	 frontier.add(offset);
	 }
	 }
	 }
	 }*/

	/*protected void moveGantryPinion(World world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited,
	 BlockState state) {
	 BlockPos offset = pos.offset(state.get(GantryPinionBlock.FACING));
	 if (!visited.contains(offset))
	 frontier.add(offset);
	 Direction.Axis rotationAxis = ((IRotate) state.getBlock()).getRotationAxis(state);
	 for (Direction d : Iterate.directionsInAxis(rotationAxis)) {
	 offset = pos.offset(d);
	 BlockState offsetState = world.getBlockState(offset);
	 if (AllBlocks.GANTRY_SHAFT.has(offsetState) && offsetState.get(GantryShaftBlock.FACING)
	 .getAxis() == d.getAxis())
	 if (!visited.contains(offset))
	 frontier.add(offset);
	 }
	 }*/

	/*
	 * protected void moveGantryShaft(World world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited,
	 * BlockState state) {
	 * for (Direction d : Iterate.directions) {
	 * BlockPos offset = pos.offset(d);
	 * if (!visited.contains(offset)) {
	 * BlockState offsetState = world.getBlockState(offset);
	 * Direction facing = state.get(GantryShaftBlock.FACING);
	 * if (d.getAxis() == facing.getAxis() && AllBlocks.GANTRY_SHAFT.has(offsetState)
	 * && offsetState.get(GantryShaftBlock.FACING) == facing)
	 * frontier.add(offset);
	 * else if (AllBlocks.GANTRY_PINION.has(offsetState) && offsetState.get(GantryPinionBlock.FACING) == d)
	 * frontier.add(offset);
	 * }
	 * }
	 * }
	 */

	public void onEntityTick(World world) {
		/*fluidStorage.forEach((pos, mfs) -> mfs.tick(entity, pos, world.isClient));*/
	}

	/*private void moveSeat(World world, BlockPos pos) {
	 BlockPos local = toLocalPos(pos);
	 getSeats().add(local);
	 List<SeatEntity> seatsEntities = world.getEntitiesByClass(SeatEntity.class, new Box(pos));
	 if (!seatsEntities.isEmpty()) {
	 SeatEntity seat = seatsEntities.get(0);
	 List<Entity> passengers = seat.getPassengers();
	 if (!passengers.isEmpty())
	 initialPassengers.put(local, passengers.get(0));
	 }
	 }*/

	private void movePulley(World world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited) {
 		int limit = 128; //AllConfigs.SERVER.kinetics.maxRopeLength.get();
 		BlockPos ropePos = pos;
 		while (limit-- >= 0) {
 			ropePos = ropePos.down();
 			if (!world.canSetBlock(ropePos))
 				break;
			BlockState ropeState = world.getBlockState(ropePos);
	 		Block block = ropeState.getBlock();
			if (!(block instanceof PulleyBlock.RopeBlock) && !(block instanceof PulleyBlock.MagnetBlock)) {
	 			if (!visited.contains(ropePos))
	 				frontier.add(ropePos);
	 			break;
			}
	 		addBlock(ropePos, capture(world, ropePos));
	 	}
	 }

	/*private boolean moveMechanicalPiston(World world, BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) throws AssemblyException {
	 Direction direction = state.get(MechanicalPistonBlock.FACING);
	 PistonState pistonState = state.get(MechanicalPistonBlock.STATE);
	 if (pistonState == PistonState.MOVING)
	 return false;

	 BlockPos offset = pos.offset(direction.getOpposite());
	 if (!visited.contains(offset)) {
	 BlockState poleState = world.getBlockState(offset);
	 if (AllBlocks.PISTON_EXTENSION_POLE.has(poleState) && poleState.get(PistonExtensionPoleBlock.FACING)
	 .getAxis() == direction.getAxis())
	 frontier.add(offset);
	 }

	 if (pistonState == PistonState.EXTENDED || MechanicalPistonBlock.isStickyPiston(state)) {
	 offset = pos.offset(direction);
	 if (!visited.contains(offset))
	 frontier.add(offset);
	 }

	 return true;
	 }*/

	private boolean moveChassis(World world, BlockPos pos, Direction movementDirection, Queue<BlockPos> frontier,
	 	Set<BlockPos> visited) {
	 	BlockEntity te = world.getBlockEntity(pos);
	 	if (!(te instanceof ChassisBlockEntity))
	 		return false;
	 	ChassisBlockEntity chassis = (ChassisBlockEntity) te;
	 	chassis.addAttachedChasses(frontier, visited);
	 	List<BlockPos> includedBlockPositions = chassis.getIncludedBlockPositions(movementDirection, false);
	 	if (includedBlockPositions == null)
	 		return false;
	 	for (BlockPos blockPos : includedBlockPositions)
 			if (!visited.contains(blockPos))
 				frontier.add(blockPos);
 		return true;
 	}

	/**
	 * move the first block in frontier queue
	 */
	protected boolean moveBlock(World world, @Nullable Direction forcedDirection, Queue<BlockPos> frontier,
								Set<BlockPos> visited) throws AssemblyException {
		BlockPos pos = frontier.poll();
		if (pos == null)
			return false;
		visited.add(pos);

		if (World.isOutOfBuildLimitVertically(pos))
			return true;
		if (!world.canSetBlock(pos))
			throw AssemblyException.unloadedChunk(pos);
		if (isAnchoringBlockAt(pos))
			return true;
		BlockState state = world.getBlockState(pos);
		if (!BlockMovementTraits.movementNecessary(state, world, pos))
			return true;
		if (!movementAllowed(state, world, pos))
			throw AssemblyException.unmovableBlock(pos, state);
		if (state.getBlock() instanceof AbstractChassisBlock && !moveChassis(world, pos, forcedDirection, frontier, visited))
			return false;

		/*if (AllBlocks.ADJUSTABLE_CRATE.has(state))
		 AdjustableCrateBlock.splitCrate(world, pos);*/

		/*if (AllBlocks.BELT.hasBlockEntity(state))
			moveBelt(pos, frontier, visited, state);*/

		/*if (AllBlocks.GANTRY_PINION.has(state))
		 moveGantryPinion(world, pos, frontier, visited, state);

		 if (AllBlocks.GANTRY_SHAFT.has(state))
		 moveGantryShaft(world, pos, frontier, visited, state);*/

		 // Bearings potentially create stabilized sub-contraptions
		if (AllBlocks.MECHANICAL_BEARING.getStateManager().getStates().contains(state))
			moveBearing(pos, frontier, visited, state);

		 // Seats transfer their passenger to the contraption
		 /*if (state.getBlock() instanceof SeatBlock)
		 moveSeat(world, pos);*/

		 // Pulleys drag their rope and their attached structure
	 	if (state.getBlock() instanceof PulleyBlock)
	 		movePulley(world, pos, frontier, visited);

		 // Pistons drag their attaches poles and extension
		 /*if (state.getBlock() instanceof MechanicalPistonBlock)
		 if (!moveMechanicalPiston(world, pos, frontier, visited, state))
		 return false;
		 if (isExtensionPole(state))
		 movePistonPole(world, pos, frontier, visited, state);
		 if (isPistonHead(state))
		 movePistonHead(world, pos, frontier, visited, state);*/

		// Doors try to stay whole
		if (state.getBlock() instanceof DoorBlock) {
			BlockPos otherPartPos = pos.up(state.get(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? 1 : -1);
			if (!visited.contains(otherPartPos))
				frontier.add(otherPartPos);
		}

		// Cart assemblers attach themselves
		BlockPos posDown = pos.down();
		BlockState stateBelow = world.getBlockState(posDown);
		/*if (!visited.contains(posDown) && AllBlocks.CART_ASSEMBLER.has(stateBelow))
		 frontier.add(posDown);*/

		Map<Direction, SuperGlueEntity> superglue = SuperGlueHandler.gatherGlue(world, pos);

		// Slime blocks and super glue drag adjacent blocks if possible
		for (Direction offset : Iterate.directions) {
	 		BlockPos offsetPos = pos.offset(offset);
		 	BlockState blockState = world.getBlockState(offsetPos);
		 	if (isAnchoringBlockAt(offsetPos))
		 		continue;
		 	if (!movementAllowed(blockState, world, offsetPos)) {
		 		if (offset == forcedDirection)
		 			throw AssemblyException.unmovableBlock(pos, state);
		 		continue;
		 	}

		 	boolean wasVisited = visited.contains(offsetPos);
		 	boolean faceHasGlue = superglue.containsKey(offset);
		 	boolean blockAttachedTowardsFace =
		 	BlockMovementTraits.isBlockAttachedTowards(world, offsetPos, blockState, offset.getOpposite());
		 	boolean brittle = BlockMovementTraits.isBrittle(blockState);
		 	boolean canStick = !brittle && canStickTo(state, blockState) && canStickTo(blockState, state);
		 	if (canStick) {
		 		if (state.getPistonBehavior() == PistonBehavior.PUSH_ONLY || blockState.getPistonBehavior() == PistonBehavior.PUSH_ONLY) {
		 			canStick = false;
		 		}
		 		if (BlockMovementTraits.notSupportive(state, offset)) {
		 			canStick = false;
		 		}
		 		if (BlockMovementTraits.notSupportive(blockState, offset.getOpposite())) {
		 			canStick = false;
		 		}
		 	}

		 	if (!wasVisited && (canStick || blockAttachedTowardsFace || faceHasGlue || (offset == forcedDirection && !BlockMovementTraits.notSupportive(state, forcedDirection))))
		 		frontier.add(offsetPos);
		 	if (faceHasGlue)
		 		addGlue(superglue.get(offset));
		}

		addBlock(pos, capture(world, pos));
		if (blocks.size() <= 1000) // TODO FIX CONFIG MAX BLOCKS MOVED  AllConfigs.SERVER.kinetics.maxBlocksMoved.get())
			return true;
		else
			throw AssemblyException.structureTooLarge();
	}

	boolean canStickTo(BlockState state, BlockState other) {
		if (state.getBlock() == Blocks.HONEY_BLOCK && other.getBlock() == Blocks.SLIME_BLOCK) return false;
		if (state.getBlock() == Blocks.SLIME_BLOCK && other.getBlock() == Blocks.HONEY_BLOCK) return false;
		return isStickyBlock(state) || isStickyBlock(other);
	}

	boolean isStickyBlock(BlockState state) {
		return state.getBlock() == Blocks.SLIME_BLOCK || state.getBlock() == Blocks.HONEY_BLOCK;
	}

	private void moveBearing(BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {
		Direction facing = state.get(MechanicalBearingBlock.FACING);
		if (!canBeStabilized(facing, pos.subtract(anchor))) {
			BlockPos offset = pos.offset(facing);
			if (!visited.contains(offset))
				frontier.add(offset);
			return;
		}
		pendingSubContraptions.add(new BlockFace(pos, facing));
	}

	/*private void moveBelt(BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {
		BlockPos nextPos = BeltBlock.nextSegmentPosition(state, pos, true);
		BlockPos prevPos = BeltBlock.nextSegmentPosition(state, pos, false);
		if (nextPos != null && !visited.contains(nextPos))
			frontier.add(nextPos);
		if (prevPos != null && !visited.contains(prevPos))
			frontier.add(prevPos);
	}*/


	protected Pair<Structure.StructureBlockInfo, BlockEntity> capture(World world, BlockPos pos) {
		BlockState blockstate = world.getBlockState(pos);
		if (blockstate.getBlock() instanceof ChestBlock)
			blockstate = blockstate.with(ChestBlock.CHEST_TYPE, ChestType.SINGLE);
	 	/*if (AllBlocks.ADJUSTABLE_CRATE.has(blockstate))
	 		blockstate = blockstate.with(AdjustableCrateBlock.DOUBLE, false);
	 	if (AllBlocks.REDSTONE_CONTACT.has(blockstate))
	 		blockstate = blockstate.with(RedstoneContactBlock.POWERED, true);*/
	 	if (blockstate.getBlock() instanceof AbstractButtonBlock) {
	 		blockstate = blockstate.with(AbstractButtonBlock.POWERED, false);
	 		world.getBlockTickScheduler()
	 		.schedule(pos, blockstate.getBlock(), -1);
	 	}
	 	if (blockstate.getBlock() instanceof PressurePlateBlock) {
	 		blockstate = blockstate.with(PressurePlateBlock.POWERED, false);
	 		world.getBlockTickScheduler()
	 		.schedule(pos, blockstate.getBlock(), -1);
	 	}
	 	CompoundTag compoundnbt = getBlockEntityNbt(world, pos);
	 	BlockEntity tileentity = world.getBlockEntity(pos);
	 	return Pair.of(new Structure.StructureBlockInfo(pos, blockstate, compoundnbt), tileentity);
	}

	protected void addBlock(BlockPos pos, Pair<Structure.StructureBlockInfo, BlockEntity> pair) {
		Structure.StructureBlockInfo captured = pair.getKey();
		BlockPos localPos = pos.subtract(anchor);
		Structure.StructureBlockInfo blockInfo = new Structure.StructureBlockInfo(localPos, captured.state, captured.tag);

		if (blocks.put(localPos, blockInfo) != null)
			return;
		bounds = bounds.union(new Box(localPos));

		/*BlockEntity te = pair.getValue();
		 if (te != null && MountedStorage.canUseAsStorage(te))
		 storage.put(localPos, new MountedStorage(te));
		 if (te != null && MountedFluidStorage.canUseAsStorage(te))
		 fluidStorage.put(localPos, new MountedFluidStorage(te));
		 if (AllMovementBehaviours.contains(captured.state.getBlock()))
		 actors.add(MutablePair.of(blockInfo, null));*/
	}

	@Nullable
	protected CompoundTag getBlockEntityNbt(World world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity == null)
			return null;
		CompoundTag nbt = blockEntity.toTag(new CompoundTag());
		nbt.remove("x");
		nbt.remove("y");
		nbt.remove("z");

		/*if (blockEntity instanceof FluidTankTileEntity && nbt.contains("Controller"))
		 nbt.put("Controller",
		 NbtHelper.fromBlockPos(toLocalPos(NbtHelper.toBlockPos(nbt.getCompound("Controller")))));*/

		return nbt;
	}

	protected void addGlue(SuperGlueEntity entity) {
		BlockPos pos = entity.getHangingPosition();
	 	Direction direction = entity.getFacingDirection();
		this.superglue.add(Pair.of(toLocalPos(pos), direction));
		glueToRemove.add(entity);
	}

	protected BlockPos toLocalPos(BlockPos globalPos) {
		return globalPos.subtract(anchor);
	}

	protected boolean movementAllowed(BlockState state, World world, BlockPos pos) {
		return BlockMovementTraits.movementAllowed(state, world, pos);
	}

	protected boolean isAnchoringBlockAt(BlockPos pos) {
		return pos.equals(anchor);
	}

	public void readNBT(World world, CompoundTag nbt, boolean spawnData) {
		blocks.clear();
		presentBlockEntities.clear();
		specialRenderedBlockEntities.clear();

		Tag blocks = nbt.get("Blocks");
		//used to differentiate between the 'old' and the paletted serialization
		boolean usePalettedDeserialization = blocks != null && blocks.getType() == 10 && ((CompoundTag) blocks).contains("Palette");
		readBlocksCompound(blocks, world, usePalettedDeserialization);

		actors.clear();
		nbt.getList("Actors", 10)
			.forEach(c -> {
				CompoundTag comp = (CompoundTag) c;
				Structure.StructureBlockInfo info = this.blocks.get(NbtHelper.toBlockPos(comp.getCompound("Pos")));
				MovementContext context = MovementContext.readNBT(world, info, comp, this);
				getActors().add(MutablePair.of(info, context));
			});

		superglue.clear();
		CNBTHelper.iterateCompoundList(nbt.getList("Superglue", NbtType.COMPOUND), c -> superglue
			.add(Pair.of(NbtHelper.toBlockPos(c.getCompound("Pos")), Direction.byId(c.getByte("Direction"))))); // TODO: BY ID INSTEAD OF BY INDEX?

		seats.clear();
		CNBTHelper.iterateCompoundList(nbt.getList("Seats", NbtType.COMPOUND), c -> seats.add(NbtHelper.toBlockPos(c)));

		seatMapping.clear();
		CNBTHelper.iterateCompoundList(nbt.getList("Passengers", NbtType.COMPOUND),
			c -> seatMapping.put(NbtHelper.toUuid(c.getCompound("Id")), c.getInt("Seat")));

		stabilizedSubContraptions.clear();
		CNBTHelper.iterateCompoundList(nbt.getList("SubContraptions", NbtType.COMPOUND), c -> stabilizedSubContraptions
			.put(NbtHelper.toUuid(c.getCompound("Id")), BlockFace.fromNBT(c.getCompound("Location"))));

		/*storage.clear();
		 NBTHelperC.iterateCompoundList(nbt.getList("Storage", NbtType.COMPOUND), c -> storage
		 .put(NbtHelper.toBlockPos(c.getCompound("Pos")), MountedStorage.deserialize(c.getCompound("Data"))));*/

		/*fluidStorage.clear();
		 NBTHelperC.iterateCompoundList(nbt.getList("FluidStorage", NbtType.COMPOUND), c -> fluidStorage
		 .put(NbtHelper.toBlockPos(c.getCompound("Pos")), MountedFluidStorage.deserialize(c.getCompound("Data"))));*/

		/*if (spawnData)
		 fluidStorage.forEach((pos, mfs) -> {
		 BlockEntity tileEntity = presentTileEntities.get(pos);
		 if (!(tileEntity instanceof FluidTankTileEntity))
		 return;
		 FluidTankTileEntity tank = (FluidTankTileEntity) tileEntity;
		 IFluidTank tankInventory = tank.getTankInventory();
		 if (tankInventory instanceof FluidTank)
		 ((FluidTank) tankInventory).setFluid(mfs.tank.getFluid());
		 tank.getFluidLevel()
		 .start(tank.getFillState());
		 mfs.assignTileEntity(tank);
		 });*/

		/*IItemHandlerModifiable[] handlers = new IItemHandlerModifiable[storage.size()];
		 int index = 0;
		 for (MountedStorage mountedStorage : storage.values())
		 handlers[index++] = mountedStorage.getItemHandler();

		 IFluidHandler[] fluidHandlers = new IFluidHandler[fluidStorage.size()];
		 index = 0;
		 for (MountedFluidStorage mountedStorage : fluidStorage.values())
		 fluidHandlers[index++] = mountedStorage.getFluidHandler();

		 inventory = new CombinedInvWrapper(handlers);
		 fluidInventory = new CombinedTankWrapper(fluidHandlers);*/

		if (nbt.contains("BoundsFront"))
			bounds = CNBTHelper.readAABB(nbt.getList("BoundsFront", 5));

		stalled = nbt.getBoolean("Stalled");
		anchor = NbtHelper.toBlockPos(nbt.getCompound("Anchor"));
	}

	public CompoundTag writeNBT(boolean spawnPacket) {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("Type", getType().id);

		CompoundTag blocksNBT = writeBlocksCompound();

		ListTag actorsNBT = new ListTag();
		for (MutablePair<Structure.StructureBlockInfo, MovementContext> actor : getActors()) {
			CompoundTag compound = new CompoundTag();
			compound.put("Pos", NbtHelper.fromBlockPos(actor.left.pos));
			AllMovementBehaviours.of(actor.left.state)
				.writeExtraData(actor.right);
			actor.right.writeToNBT(compound);
			actorsNBT.add(compound);
		}

		ListTag superglueNBT = new ListTag();
		ListTag storageNBT = new ListTag();
		if (!spawnPacket) {
			for (Pair<BlockPos, Direction> glueEntry : superglue) {
				CompoundTag c = new CompoundTag();
				c.put("Pos", NbtHelper.fromBlockPos(glueEntry.getKey()));
				c.putByte("Direction", (byte) glueEntry.getValue().getId());
				superglueNBT.add(c);
			}

			/*for (BlockPos pos : storage.keySet()) {
			 CompoundTag c = new CompoundTag();
			 MountedStorage mountedStorage = storage.get(pos);
			 if (!mountedStorage.isValid())
			 continue;
			 c.put("Pos", NbtHelper.fromBlockPos(pos));
			 c.put("Data", mountedStorage.serialize());
			 storageNBT.add(c);
			 }*/
		}

		ListTag fluidStorageNBT = new ListTag();
		/*for (BlockPos pos : fluidStorage.keySet()) {
		 CompoundTag c = new CompoundTag();
		 MountedFluidStorage mountedStorage = fluidStorage.get(pos);
		 if (!mountedStorage.isValid())
		 continue;
		 c.put("Pos", NbtHelper.fromBlockPos(pos));
		 c.put("Data", mountedStorage.serialize());
		 fluidStorageNBT.add(c);
		 }*/

		nbt.put("Seats", CNBTHelper.writeCompoundList(getSeats(), NbtHelper::fromBlockPos));
		nbt.put("Passengers", CNBTHelper.writeCompoundList(getSeatMapping().entrySet(), e -> {
			CompoundTag tag = new CompoundTag();
			tag.put("Id", NbtHelper.fromUuid(e.getKey()));
			tag.putInt("Seat", e.getValue());
			return tag;
		}));

		nbt.put("SubContraptions", CNBTHelper.writeCompoundList(stabilizedSubContraptions.entrySet(), e -> {
			CompoundTag tag = new CompoundTag();
			tag.put("Id", NbtHelper.fromUuid(e.getKey()));
			tag.put("Location", e.getValue()
				.serializeNBT());
			return tag;
		}));

		nbt.put("Blocks", blocksNBT);
		nbt.put("Actors", actorsNBT);
		nbt.put("Superglue", superglueNBT);
		nbt.put("Storage", storageNBT);
		nbt.put("FluidStorage", fluidStorageNBT);
		nbt.put("Anchor", NbtHelper.fromBlockPos(anchor));
		nbt.putBoolean("Stalled", stalled);

		if (bounds != null) {
			ListTag bb = CNBTHelper.writeAABB(bounds);
			nbt.put("BoundsFront", bb);
		}

		return nbt;
	}

	private CompoundTag writeBlocksCompound() {
		CompoundTag compound = new CompoundTag();
		ListTag blockList = new ListTag();

		for (Structure.StructureBlockInfo block : this.blocks.values()) {
			CompoundTag c = new CompoundTag();
			c.putLong("Pos", block.pos.asLong());
			c.put("State", (Tag) BlockState.CODEC.encodeStart(NbtOps.INSTANCE, block.state).get());
			if (block.tag != null)
				c.put("Data", block.tag);
			blockList.add(c);
		}

		compound.put("BlockList", blockList);

		return compound;
	}

	private void readBlocksCompound(Tag compound, World world, boolean usePalettedDeserialization) {
		ListTag blockList;
		if (usePalettedDeserialization) {
			CompoundTag c = ((CompoundTag) compound);

			blockList = c.getList("BlockList", 10);
		} else {
			blockList = (ListTag) compound;
		}

		blockList.forEach(e -> {
			CompoundTag c = (CompoundTag) e;

			Structure.StructureBlockInfo info = usePalettedDeserialization ? readBlockInfo(c,
				BlockState.CODEC.decode(NbtOps.INSTANCE, c.get("State")).map(com.mojang.datafixers.util.Pair::getFirst).result().get()) : legacyReadBlockInfo(c);

			this.blocks.put(info.pos, info);

			if (world.isClient) {
				Block block = info.state.getBlock();
				CompoundTag tag = info.tag;
				MovementBehaviour movementBehaviour = AllMovementBehaviours.of(block);
				if (tag == null)
					return;

				tag.putInt("x", info.pos.getX());
				tag.putInt("y", info.pos.getY());
				tag.putInt("z", info.pos.getZ());

				BlockEntity be = BlockEntity.createFromTag(info.state, tag);
				if (be == null)
					return;
				/*be.setLocation(new WrappedWorld(world) { //TODO POSITION THING IDK IF CORRECT

				 @Override public BlockState getBlockState(BlockPos pos) {
				 if (!pos.equals(be.getPos()))
				 return Blocks.AIR.getDefaultState();
				 return info.state;
				 }

				 }, be.getPos());*/
				if (be instanceof KineticBlockEntity)
					((KineticBlockEntity) be).setSpeed(0);
				be.getCachedState();

				if (movementBehaviour == null || !movementBehaviour.hasSpecialInstancedRendering())
					maybeInstancedBlockEntities.add(be);

				if (movementBehaviour != null && !movementBehaviour.renderAsNormalBlockEntity())
					return;

				presentBlockEntities.put(info.pos, be);
				specialRenderedBlockEntities.add(be);
			}

		});
	}

	public void removeBlocksFromWorld(World world, BlockPos offset) {
		/*storage.values()
		 .forEach(MountedStorage::removeStorageFromWorld);
		 fluidStorage.values()
		 .forEach(MountedFluidStorage::removeStorageFromWorld);*/
		 glueToRemove.forEach(SuperGlueEntity::remove);

		for (boolean brittles : Iterate.trueAndFalse) {
			for (Iterator<Structure.StructureBlockInfo> iterator = blocks.values()
				.iterator(); iterator.hasNext(); ) {
				Structure.StructureBlockInfo block = iterator.next();
				if (brittles != BlockMovementTraits.isBrittle(block.state))
					continue;

				BlockPos add = block.pos.add(anchor)
					.add(offset);
				if (customBlockRemoval(world, add, block.state))
					continue;
				BlockState oldState = world.getBlockState(add);
				Block blockIn = oldState.getBlock();
				if (block.state.getBlock() != blockIn)
					iterator.remove();
				world.removeBlockEntity(add);
				int flags = 1 << 6 | 1 << 5 | 1 << 4
					| 1 << 1 | 1 << 3;
				if (blockIn instanceof Waterloggable && oldState.contains(Properties.WATERLOGGED)
					&& oldState.get(Properties.WATERLOGGED)
					.booleanValue()) {
					world.setBlockState(add, Blocks.WATER.getDefaultState(), flags);
					continue;
				}
				world.setBlockState(add, Blocks.AIR.getDefaultState(), flags);
			}
		}
		for (Structure.StructureBlockInfo block : blocks.values()) {
			BlockPos add = block.pos.add(anchor)
				.add(offset);
			if (!shouldUpdateAfterMovement(block))
				continue;
			world.updateListeners(add, block.state, Blocks.AIR.getDefaultState(), // TODO MIGHT BE WRONG
				1 << 6 | 1 << 0 | 1 << 1);
		}
	}

	public void addBlocksToWorld(World world, StructureTransform transform) {
		for (boolean nonBrittles : Iterate.trueAndFalse) {
			for (Structure.StructureBlockInfo block : blocks.values()) {
				if (nonBrittles == BlockMovementTraits.isBrittle(block.state))
					continue;

				BlockPos targetPos = transform.apply(block.pos);
				BlockState state = transform.apply(block.state);

				if (customBlockPlacement(world, targetPos, state))
					continue;

				if (nonBrittles)
					for (Direction face : Iterate.directions)
						state = state.getStateForNeighborUpdate(face, world.getBlockState(targetPos.offset(face)), world,
							targetPos, targetPos.offset(face));

				BlockState blockState = world.getBlockState(targetPos);
				if (blockState.getHardness(world, targetPos) == -1 || (state.getCollisionShape(world, targetPos)
					.isEmpty()
					&& !blockState.getCollisionShape(world, targetPos)
					.isEmpty())) {
					if (targetPos.getY() == 0)
						targetPos = targetPos.up();
					world.syncWorldEvent(2001, targetPos, Block.getRawIdFromState(state));
					Block.dropStacks(state, world, targetPos, null);
					continue;
				}
				/*if (state.getBlock() instanceof Waterloggable && state.has(BlockStateProperties.WATERLOGGED)) {
				 IFluidState ifluidstate = world.getFluidState(targetPos);
				 state = state.with(Properties.WATERLOGGED,
				 Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
				 }*/

				world.removeBlock(targetPos, true);
				world.setBlockState(targetPos, state, 3 | 1 << 6);

				boolean verticalRotation = transform.rotationAxis == null || transform.rotationAxis.isHorizontal();
				verticalRotation = verticalRotation && transform.rotation != BlockRotation.NONE;
				if (verticalRotation) {
					/*if (state.getBlock() instanceof RopeBlock || state.getBlock() instanceof MagnetBlock)
					 world.removeBlock(targetPos, true);*/
				}

				BlockEntity blockEntity = world.getBlockEntity(targetPos);
				CompoundTag tag = block.tag;
				if (blockEntity != null)
					tag = NBTProcessors.process(blockEntity, tag, false);
				if (blockEntity != null && tag != null) {
					tag.putInt("x", targetPos.getX());
					tag.putInt("y", targetPos.getY());
					tag.putInt("z", targetPos.getZ());

					if (verticalRotation && blockEntity instanceof PulleyBlockEntity) {
				 		tag.remove("Offset");
				 		tag.remove("InitialOffset");
					}

				 	/*if (blockEntity instanceof FluidTankTileEntity && tag.contains("LastKnownPos"))
				 		tag.put("LastKnownPos", NbtHelper.fromBlockPos((BlockPos) BlockPos.ZERO.down()));*/

					blockEntity.fromTag(state, tag);

					/*if (storage.containsKey(block.pos)) {
					 MountedStorage mountedStorage = storage.get(block.pos);
					 if (mountedStorage.isValid())
					 mountedStorage.addStorageToWorld(blockEntity);
					 }

					 if (fluidStorage.containsKey(block.pos)) {
					 MountedFluidStorage mountedStorage = fluidStorage.get(block.pos);
					 if (mountedStorage.isValid())
					 mountedStorage.addStorageToWorld(blockEntity);
					 }*/
				}
			}
		}
		for (Structure.StructureBlockInfo block : blocks.values()) {
			if (!shouldUpdateAfterMovement(block))
				continue;
			BlockPos targetPos = transform.apply(block.pos);
			world.updateListeners(targetPos, block.state, block.state, // TODO MIGHT BE WRONG
				1 << 6 | 1 << 0);
		}

		/*for (int i = 0; i < inventory.getSlots(); i++)
		 inventory.setStackInSlot(i, ItemStack.EMPTY);
		 for (int i = 0; i < fluidInventory.getTanks(); i++)
		 fluidInventory.drain(fluidInventory.getFluidInTank(i), FluidAction.EXECUTE);

		 for (Pair<BlockPos, Direction> pair : superglue) {
		 BlockPos targetPos = transform.apply(pair.getKey());
		 Direction targetFacing = transform.transformFacing(pair.getValue());

		 SuperGlueEntity entity = new SuperGlueEntity(world, targetPos, targetFacing);
		 if (entity.onValidSurface()) {
		 if (!world.isClient)
		 world.addEntity(entity);
		 }
		 }*/
	}

	public void addPassengersToWorld(World world, StructureTransform transform, List<Entity> seatedEntities) {
		for (Entity seatedEntity : seatedEntities) {
			if (getSeatMapping().isEmpty())
				continue;
			Integer seatIndex = getSeatMapping().get(seatedEntity.getUuid());
			BlockPos seatPos = getSeats().get(seatIndex);
			seatPos = transform.apply(seatPos);
			/*if (!(world.getBlockState(seatPos)
			 .getBlock() instanceof SeatBlock))
			 continue;
			 if (SeatBlock.isSeatOccupied(world, seatPos))
			 continue;
			 SeatBlock.sitDown(world, seatPos, seatedEntity);*/
		}
	}

	public void startMoving(World world) {
		for (MutablePair<Structure.StructureBlockInfo, MovementContext> pair : actors) {
			MovementContext context = new MovementContext(world, pair.left, this);
			AllMovementBehaviours.of(pair.left.state)
				.startMoving(context);
			pair.setRight(context);
		}
	}

	public void stop(World world) {
		foreachActor(world, (behaviour, ctx) -> {
			behaviour.stopMoving(ctx);
			ctx.position = null;
			ctx.motion = Vec3d.ZERO;
			ctx.relativeMotion = Vec3d.ZERO;
			ctx.rotation = v -> v;
		});
	}

	public void foreachActor(World world, BiConsumer<MovementBehaviour, MovementContext> callBack) {
		for (MutablePair<Structure.StructureBlockInfo, MovementContext> pair : actors)
			callBack.accept(AllMovementBehaviours.of(pair.getLeft().state), pair.getRight());
	}

	protected boolean shouldUpdateAfterMovement(Structure.StructureBlockInfo info) {
		return true;
	}

	public void expandBoundsAroundAxis(Direction.Axis axis) {
		Box bb = bounds;
		double maxXDiff = Math.max(bb.maxX - 1, -bb.minX);
		double maxYDiff = Math.max(bb.maxY - 1, -bb.minY);
		double maxZDiff = Math.max(bb.maxZ - 1, -bb.minZ);
		double maxDiff = 0;

		if (axis == Direction.Axis.X)
			maxDiff = Math.max(maxZDiff, maxYDiff);
		if (axis == Direction.Axis.Y)
			maxDiff = Math.max(maxZDiff, maxXDiff);
		if (axis == Direction.Axis.Z)
			maxDiff = Math.max(maxXDiff, maxYDiff);

		Vec3d vec = new Vec3d(Direction.get(Direction.AxisDirection.POSITIVE, axis)
			.getUnitVector());
		Vec3d planeByNormal = VecHelper.axisAlingedPlaneOf(vec);
		Vec3d min = vec.multiply(bb.minX, bb.minY, bb.minZ)
			.add(planeByNormal.multiply(-maxDiff));
		Vec3d max = vec.multiply(bb.maxX, bb.maxY, bb.maxZ)
			.add(planeByNormal.multiply(maxDiff + 1));
		bounds = new Box(min, max);
	}

	public void addExtraInventories(Entity entity) {
	}

	public Map<UUID, Integer> getSeatMapping() {
		return seatMapping;
	}

	public void setSeatMapping(Map<UUID, Integer> seatMapping) {
		this.seatMapping = seatMapping;
	}

	public BlockPos getSeatOf(UUID entityId) {
		if (!getSeatMapping().containsKey(entityId))
			return null;
		int seatIndex = getSeatMapping().get(entityId);
		if (seatIndex >= getSeats().size())
			return null;
		return getSeats().get(seatIndex);
	}

	public BlockPos getBearingPosOf(UUID subContraptionEntityId) {
		if (stabilizedSubContraptions.containsKey(subContraptionEntityId))
			return stabilizedSubContraptions.get(subContraptionEntityId)
				.getConnectedPos();
		return null;
	}

	public List<BlockPos> getSeats() {
		return seats;
	}

	public Map<BlockPos, Structure.StructureBlockInfo> getBlocks() {
		return blocks;
	}

	public List<MutablePair<Structure.StructureBlockInfo, MovementContext>> getActors() {
		return actors;
	}

	/*
	 * public void updateContainedFluid(BlockPos localPos, FluidStack containedFluid) {
	 * MountedFluidStorage mountedFluidStorage = fluidStorage.get(localPos);
	 * if (mountedFluidStorage != null)
	 * mountedFluidStorage.updateFluid(containedFluid);
	 * }
	 */

	@Environment(EnvType.CLIENT)
	public ContraptionLighter<?> makeLighter() {
		return new EmptyLighter(this);
	}

}
