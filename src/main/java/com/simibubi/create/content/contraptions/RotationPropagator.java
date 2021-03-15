package com.simibubi.create.content.contraptions;

import static com.simibubi.create.content.contraptions.relays.elementary.CogWheelBlock.isLargeCog;
import static net.minecraft.state.property.Properties.AXIS;

import java.util.LinkedList;
import java.util.List;

import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import com.simibubi.create.content.contraptions.base.Rotating;
import com.simibubi.create.content.contraptions.relays.encased.DirectionalShaftHalvesBlockEntity;
import com.simibubi.create.content.contraptions.relays.encased.SplitShaftBlockEntity;
import com.simibubi.create.content.contraptions.relays.gearbox.GearboxBlockEntity;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class RotationPropagator {

	private static final int MAX_FLICKER_SCORE = 128;

	/**
	 * Determines the change in rotation between two attached kinetic entities. For
	 * instance, an axis connection returns 1 while a 1-to-1 gear connection
	 * reverses the rotation and therefore returns -1.
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	private static float getRotationSpeedModifier(KineticBlockEntity from, KineticBlockEntity to) {
		final BlockState stateFrom = from.getCachedState();
		final BlockState stateTo = to.getCachedState();

		Block fromBlock = stateFrom.getBlock();
		Block toBlock = stateTo.getBlock();
		if (!(fromBlock instanceof Rotating && toBlock instanceof Rotating))
			return 0;

		final Rotating definitionFrom = (Rotating) fromBlock;
		final Rotating definitionTo = (Rotating) toBlock;
		final BlockPos diff = to.getPos()
			.subtract(from.getPos());
		final Direction direction = Direction.getFacing(diff.getX(), diff.getY(), diff.getZ());
		final World world = from.getWorld();

		boolean alignedAxes = true;
		for (Direction.Axis axis : Direction.Axis.values())
			if (axis != direction.getAxis())
				if (axis.choose(diff.getX(), diff.getY(), diff.getZ()) != 0)
					alignedAxes = false;

		boolean connectedByAxis =
			alignedAxes && definitionFrom.hasShaftTowards(world, from.getPos(), stateFrom, direction)
				&& definitionTo.hasShaftTowards(world, to.getPos(), stateTo, direction.getOpposite());

		boolean connectedByGears = definitionFrom.hasIntegratedCogwheel(world, from.getPos(), stateFrom)
			&& definitionTo.hasIntegratedCogwheel(world, to.getPos(), stateTo);

		float custom = from.propagateRotationTo(to, stateFrom, stateTo, diff, connectedByAxis, connectedByGears);
		if (custom != 0)
			return custom;

		// Axis <-> Axis
		if (connectedByAxis) {
	 		float axisModifier = getAxisModifier(to, direction.getOpposite());
	 		if (axisModifier != 0)
		 	axisModifier = 1 / axisModifier;
			 return getAxisModifier(from, direction) * axisModifier;
	 	}

		// Attached Encased Belts
		/**if (fromBlock instanceof EncasedBeltBlock && toBlock instanceof EncasedBeltBlock) { TODO ENCASED BELT BLOCK
		 boolean connected = EncasedBeltBlock.areBlocksConnected(stateFrom, stateTo, direction);
		 return connected ? EncasedBeltBlock.getRotationSpeedModifier(from, to) : 0;
		 }*/

		// Large Gear <-> Large Gear
		if (isLargeToLargeGear(stateFrom, stateTo, diff)) {
			Direction.Axis sourceAxis = stateFrom.get(AXIS);
			Direction.Axis targetAxis = stateTo.get(AXIS);
			int sourceAxisDiff = sourceAxis.choose(diff.getX(), diff.getY(), diff.getZ());
			int targetAxisDiff = targetAxis.choose(diff.getX(), diff.getY(), diff.getZ());

			return sourceAxisDiff > 0 ^ targetAxisDiff > 0 ? -1 : 1;
		}

		// Gear <-> Large Gear
		if (isLargeCog(stateFrom) && definitionTo.hasIntegratedCogwheel(world, to.getPos(), stateTo))
		 if (isLargeToSmallCog(stateFrom, stateTo, definitionTo, diff))
		 return -2f;
		 if (isLargeCog(stateTo) && definitionFrom.hasIntegratedCogwheel(world, from.getPos(), stateFrom))
		 if (isLargeToSmallCog(stateTo, stateFrom, definitionFrom, diff))
		 return -.5f;

		// Gear <-> Gear
		if (connectedByGears) {
			if (diff.getManhattanDistance(BlockPos.ZERO) != 1)
				return 0;
			if (isLargeCog(stateTo))
			 return 0;
			if (direction.getAxis() == definitionFrom.getRotationAxis(stateFrom))
				return 0;
			if (definitionFrom.getRotationAxis(stateFrom) == definitionTo.getRotationAxis(stateTo))
				return -1;
		}

		return 0;
	}

	private static float getConveyedSpeed(KineticBlockEntity from, KineticBlockEntity to) {
		final BlockState stateFrom = from.getCachedState();
		final BlockState stateTo = to.getCachedState();

		// Rotation Speed Controller <-> Large Gear
		/**if (isLargeCogToSpeedController(stateFrom, stateTo, to.getPos() TODO SPEED CONTROLLER CHECK THING
		 .subtract(from.getPos())))
		 return SpeedControllerTileEntity.getConveyedSpeed(from, to, true);
		 if (isLargeCogToSpeedController(stateTo, stateFrom, from.getPos()
		 .subtract(to.getPos())))
		 return SpeedControllerTileEntity.getConveyedSpeed(to, from, false);*/

		float rotationSpeedModifier = getRotationSpeedModifier(from, to);
		return from.getTheoreticalSpeed() * rotationSpeedModifier;
	}

	private static boolean isLargeToLargeGear(BlockState from, BlockState to, BlockPos diff) {
		if (!isLargeCog(from) || !isLargeCog(to))
		 return false;
		Direction.Axis fromAxis = from.get(AXIS);
		Direction.Axis toAxis = to.get(AXIS);
		if (fromAxis == toAxis)
			return false;
		for (Direction.Axis axis : Direction.Axis.values()) {
			int axisDiff = axis.choose(diff.getX(), diff.getY(), diff.getZ());
			if (axis == fromAxis || axis == toAxis) {
				if (axisDiff == 0)
					return false;

			} else if (axisDiff != 0)
				return false;
		}
		return true;
	}

	private static float getAxisModifier(KineticBlockEntity te, Direction direction) {
		if (!te.hasSource() || !(te instanceof DirectionalShaftHalvesBlockEntity))
			return 1;
		Direction source = ((DirectionalShaftHalvesBlockEntity) te).getSourceFacing();

		if (te instanceof GearboxBlockEntity)
			return direction.getAxis() == source.getAxis() ? direction == source ? 1 : -1
				: direction.getDirection() == source.getDirection() ? -1 : 1;

		if (te instanceof SplitShaftBlockEntity)
			return ((SplitShaftBlockEntity) te).getRotationSpeedModifier(direction);

		return 1;
	}

	private static boolean isLargeToSmallCog(BlockState from, BlockState to, Rotating defTo, BlockPos diff) {
		Direction.Axis axisFrom = from.get(AXIS);
		if (axisFrom != defTo.getRotationAxis(to))
			return false;
		if (axisFrom.choose(diff.getX(), diff.getY(), diff.getZ()) != 0)
			return false;
		for (Direction.Axis axis : Direction.Axis.values()) {
			if (axis == axisFrom)
				continue;
			if (Math.abs(axis.choose(diff.getX(), diff.getY(), diff.getZ())) != 1)
				return false;
		}
		return true;
	}

	/*private static boolean isLargeCogToSpeedController(BlockState from, BlockState to, BlockPos diff) {
		if (!isLargeCog(from) || !AllBlocks.ROTATION_SPEED_CONTROLLER.has(to))
			return false;
		if (!diff.equals(BlockPos.ZERO.down()))
			return false;
		Direction.Axis axis = from.get(CogWheelBlock.AXIS);
		if (axis.isVertical())
			return false;
		if (to.get(SpeedControllerBlock.HORIZONTAL_AXIS) == axis)
			return false;
		return true;
	}*/

	/**
	 * Insert the added position to the kinetic network.
	 *
	 * @param worldIn
	 * @param pos
	 */
	public static void handleAdded(World worldIn, BlockPos pos, KineticBlockEntity addedTE) {
		if (worldIn.isClient)
			return;
		if (!worldIn.canSetBlock(pos))
			return;
		propagateNewSource(addedTE);
	}

	/**
	 * Search for sourceless networks attached to the given entity and update them.
	 *
	 * @param currentTE
	 */
	private static void propagateNewSource(KineticBlockEntity currentTE) {
		BlockPos pos = currentTE.getPos();
		World world = currentTE.getWorld();

		for (KineticBlockEntity neighbourTE : getConnectedNeighbours(currentTE)) {
			float speedOfCurrent = currentTE.getTheoreticalSpeed();
			float speedOfNeighbour = neighbourTE.getTheoreticalSpeed();
			float newSpeed = getConveyedSpeed(currentTE, neighbourTE);
			float oppositeSpeed = getConveyedSpeed(neighbourTE, currentTE);

			if (newSpeed == 0 && oppositeSpeed == 0)
				continue;

			boolean incompatible =
				Math.signum(newSpeed) != Math.signum(speedOfNeighbour) && (newSpeed != 0 && speedOfNeighbour != 0);

			boolean tooFast = Math.abs(newSpeed) > 1000; // TODO MAX ROTATION SPEED CONFIG AllConfigs.SERVER.kinetics.maxRotationSpeed.get();
			boolean speedChangedTooOften = currentTE.getFlickerScore() > MAX_FLICKER_SCORE;
			if (tooFast || speedChangedTooOften) {
				world.removeBlock(pos, true);
				return;
			}

			// Opposite directions
			if (incompatible) {
				world.removeBlock(pos, true);
				return;

				// Same direction: overpower the slower speed
			} else {

				// Neighbour faster, overpower the incoming tree
				if (Math.abs(oppositeSpeed) > Math.abs(speedOfCurrent)) {
					float prevSpeed = currentTE.getSpeed();
					currentTE.setSource(neighbourTE.getPos());
					currentTE.setSpeed(getConveyedSpeed(neighbourTE, currentTE));
					currentTE.onSpeedChanged(prevSpeed);
					currentTE.sendData();

					propagateNewSource(currentTE);
					return;
				}

				// Current faster, overpower the neighbours' tree
				if (Math.abs(newSpeed) >= Math.abs(speedOfNeighbour)) {

					// Do not overpower you own network -> cycle
					if (!currentTE.hasNetwork() || currentTE.network.equals(neighbourTE.network)) {
						float epsilon = Math.abs(speedOfNeighbour) / 256f / 256f;
						if (Math.abs(newSpeed) > Math.abs(speedOfNeighbour) + epsilon)
							world.removeBlock(pos, true);
						continue;
					}

					if (currentTE.hasSource() && currentTE.source.equals(neighbourTE.getPos()))
						currentTE.removeSource();

					float prevSpeed = neighbourTE.getSpeed();
					neighbourTE.setSource(currentTE.getPos());
					neighbourTE.setSpeed(getConveyedSpeed(currentTE, neighbourTE));
					neighbourTE.onSpeedChanged(prevSpeed);
					neighbourTE.sendData();
					propagateNewSource(neighbourTE);
					continue;
				}
			}

			if (neighbourTE.getTheoreticalSpeed() == newSpeed)
				continue;

			float prevSpeed = neighbourTE.getSpeed();
			neighbourTE.setSpeed(newSpeed);
			neighbourTE.setSource(currentTE.getPos());
			neighbourTE.onSpeedChanged(prevSpeed);
			neighbourTE.sendData();
			propagateNewSource(neighbourTE);

		}
	}

	/**
	 * Remove the given entity from the network.
	 *
	 * @param world
	 * @param pos
	 * @param removedTE
	 */
	public static void handleRemoved(World world, BlockPos pos, KineticBlockEntity removedTE) {
		if (world.isClient) return;
		if (removedTE == null) return;
		if (removedTE.getTheoreticalSpeed() == 0) return;

		for (BlockPos neighbourPos : getPotentialNeighbourLocations(removedTE)) {
			BlockState neighbourState = world.getBlockState(neighbourPos);
			if (!(neighbourState.getBlock() instanceof Rotating)) continue;
			BlockEntity blockEntity = world.getBlockEntity(neighbourPos);
			if (!(blockEntity instanceof KineticBlockEntity)) continue;

			final KineticBlockEntity neighbourBe = (KineticBlockEntity) blockEntity;
			if (!neighbourBe.hasSource() || !neighbourBe.source.equals(pos)) continue;

			propagateMissingSource(neighbourBe);
		}

	}

	/**
	 * Clear the entire subnetwork depending on the given entity and find a new
	 * source
	 *
	 * @param updateTE
	 */
	private static void propagateMissingSource(KineticBlockEntity updateTE) {
		final World world = updateTE.getWorld();

		List<KineticBlockEntity> potentialNewSources = new LinkedList<>();
		List<BlockPos> frontier = new LinkedList<>();
		frontier.add(updateTE.getPos());
		BlockPos missingSource = updateTE.hasSource() ? updateTE.source : null;

		while (!frontier.isEmpty()) {
			final BlockPos pos = frontier.remove(0);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (!(blockEntity instanceof KineticBlockEntity))
				continue;
			final KineticBlockEntity currentTE = (KineticBlockEntity) blockEntity;

			currentTE.removeSource();
			currentTE.sendData();

			for (KineticBlockEntity neighbourBe : getConnectedNeighbours(currentTE)) {
				if (neighbourBe.getPos().equals(missingSource)) continue;
				if (!neighbourBe.hasSource()) continue;

				if (!neighbourBe.source.equals(pos)) {
					potentialNewSources.add(neighbourBe);
					continue;
				}

				if (neighbourBe.isSource()) potentialNewSources.add(neighbourBe);

				frontier.add(neighbourBe.getPos());
			}
		}

		for (KineticBlockEntity newSource : potentialNewSources) {
			if (newSource.hasSource() || newSource.isSource()) {
				propagateNewSource(newSource);
				return;
			}
		}
	}

	private static KineticBlockEntity findConnectedNeighbour(KineticBlockEntity currentTE, BlockPos neighbourPos) {
		BlockState neighbourState = currentTE.getWorld().getBlockState(neighbourPos);
		if (!(neighbourState.getBlock() instanceof Rotating)) return null;
		if (!neighbourState.getBlock().hasBlockEntity()) return null;
		BlockEntity neighbourTE = currentTE.getWorld().getBlockEntity(neighbourPos);
		if (!(neighbourTE instanceof KineticBlockEntity)) return null;
		KineticBlockEntity neighbourKbe = (KineticBlockEntity) neighbourTE;
		if (!(neighbourKbe.getCachedState().getBlock() instanceof Rotating)) return null;
		if (!isConnected(currentTE, neighbourKbe) && !isConnected(neighbourKbe, currentTE)) return neighbourKbe;
		return neighbourKbe;
	}

	public static boolean isConnected(KineticBlockEntity from, KineticBlockEntity to) {
		final BlockState stateFrom = from.getCachedState();
		final BlockState stateTo = to.getCachedState();
		return false; /*isLargeCogToSpeedController(stateFrom, stateTo, to.getPos() TODO IS LARGE COG TO SPEED CONTROLLER CHECK
			.subtract(from.getPos())) || getRotationSpeedModifier(from, to) != 0
			|| from.isCustomConnection(to, stateFrom, stateTo);*/
	}

	private static List<KineticBlockEntity> getConnectedNeighbours(KineticBlockEntity be) {
		List<KineticBlockEntity> neighbours = new LinkedList<>();
		for (BlockPos neighbourPos : getPotentialNeighbourLocations(be)) {
			final KineticBlockEntity neighbourTE = findConnectedNeighbour(be, neighbourPos);
			if (neighbourTE == null)
				continue;

			neighbours.add(neighbourTE);
		}
		return neighbours;
	}

	private static List<BlockPos> getPotentialNeighbourLocations(KineticBlockEntity be) {
		List<BlockPos> neighbours = new LinkedList<>();

		if (!be.getWorld()
			.isRegionLoaded(be.getPos().subtract(new Vec3i(1, 1, 1)), be.getPos().add(new Vec3i(1, 1, 1)))) // think i fixed this
			return neighbours;

		for (Direction facing : Iterate.directions)
			neighbours.add(be.getPos()
				.offset(facing));

		BlockState blockState = be.getCachedState();
		if (!(blockState.getBlock() instanceof Rotating))
			return neighbours;
		Rotating block = (Rotating) blockState.getBlock();
		return be.addPropagationLocations(block, blockState, neighbours);
	}

}
