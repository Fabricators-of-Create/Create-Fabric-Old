package com.simibubi.create.content.contraptions.components.structureMovement;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.chassis.AbstractChassisBlock;
import com.simibubi.create.content.contraptions.relays.belt.BeltBlock;
import com.simibubi.create.content.contraptions.relays.belt.BeltSlope;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.DirectionHelper;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.block.WallMountedBlock.FACE;
import static net.minecraft.state.property.Properties.*;

public class StructureTransform {
	// Assuming structures cannot be rotated around multiple axes at once
	BlockRotation rotation;
	int angle;
	Direction.Axis rotationAxis;
	BlockPos offset;

	private StructureTransform(BlockPos offset, int angle, Direction.Axis axis, BlockRotation rotation) {
		this.offset = offset;
		this.angle = angle;
		rotationAxis = axis;
		this.rotation = rotation;
	}

	public StructureTransform(BlockPos offset, float xRotation, float yRotation, float zRotation) {
		this.offset = offset;
		if (xRotation != 0) {
			rotationAxis = Direction.Axis.X;
			angle = Math.round(xRotation / 90) * 90;
		}
		if (yRotation != 0) {
			rotationAxis = Direction.Axis.Y;
			angle = Math.round(yRotation / 90) * 90;
		}
		if (zRotation != 0) {
			rotationAxis = Direction.Axis.Z;
			angle = Math.round(zRotation / 90) * 90;
		}

		angle %= 360;
		if (angle < -90)
			angle += 360;

		this.rotation = BlockRotation.NONE;
		if (angle == -90 || angle == 270)
			this.rotation = BlockRotation.CLOCKWISE_90;
		if (angle == 90)
			this.rotation = BlockRotation.COUNTERCLOCKWISE_90;
		if (angle == 180)
			this.rotation = BlockRotation.CLOCKWISE_180;

	}

 	private BlockState rotateChassis(BlockState state) {
 		if (rotation == BlockRotation.NONE)
			return state;

 		BlockState rotated = state.with(AXIS, transformAxis(state.get(AXIS)));
	 	AbstractChassisBlock block = (AbstractChassisBlock) state.getBlock();

	 	for (Direction face : Iterate.directions) {
	 		BooleanProperty glueableSide = block.getGlueableSide(rotated, face);
			if (glueableSide != null)
	 			rotated = rotated.with(glueableSide, false);
		}

		for (Direction face : Iterate.directions) {
 			BooleanProperty glueableSide = block.getGlueableSide(state, face);
	 		if (glueableSide == null || !state.get(glueableSide))
	 			continue;
	 		Direction rotatedFacing = transformFacing(face);
	 		BooleanProperty rotatedGlueableSide = block.getGlueableSide(rotated, rotatedFacing);
	 		if (rotatedGlueableSide != null)
	 			rotated = rotated.with(rotatedGlueableSide, true);
	 	}

	 	return rotated;
 	}

	public static StructureTransform fromBuffer(PacketByteBuf buffer) {
		BlockPos readBlockPos = buffer.readBlockPos();
		int readAngle = buffer.readInt();
		int axisIndex = buffer.readVarInt();
		int rotationIndex = buffer.readVarInt();
		return new StructureTransform(readBlockPos, readAngle, axisIndex == -1 ? null : Direction.Axis.values()[axisIndex],
			rotationIndex == -1 ? null : BlockRotation.values()[rotationIndex]);
	}

	public Vec3d apply(Vec3d localVec) {
		Vec3d vec = localVec;
		if (rotationAxis != null)
			vec = VecHelper.rotateCentered(vec, angle, rotationAxis);
		vec = vec.add(new Vec3d(offset.getX(), offset.getY(), offset.getZ())); // TODO MIGHT NOT BE BEST SOLUTION?
		return vec;
	}

	public BlockPos apply(BlockPos localPos) {
		Vec3d vec = VecHelper.getCenterOf(localPos);
		if (rotationAxis != null)
			vec = VecHelper.rotateCentered(vec, angle, rotationAxis);
		localPos = new BlockPos(vec);
		return localPos.add(offset);
	}

	/**
	 * Minecraft does not support blockstate rotation around axes other than y. Add
	 * specific cases here for blockstates, that should react to rotations around
	 * horizontal axes
	 */
	public BlockState apply(BlockState state) {
		Block block = state.getBlock();

		if (rotationAxis == Direction.Axis.Y) {
			if (block instanceof BellBlock) {
				if (state.get(Properties.ATTACHMENT) == Attachment.DOUBLE_WALL) {
					state = state.with(Properties.ATTACHMENT, Attachment.SINGLE_WALL);
				}
				return state.with(WallMountedBlock.FACING,
					rotation.rotate(state.get(WallMountedBlock.FACING)));
			}
			return state.rotate(rotation);
		}

		if (block instanceof AbstractChassisBlock)
			return rotateChassis(state);

		if (block instanceof HorizontalFacingBlock) {
			Direction stateFacing = state.get(WallMountedBlock.FACING);
			WallMountLocation stateFace = state.get(FACE);
			Direction forcedAxis = rotationAxis == Direction.Axis.Z ? Direction.EAST : Direction.SOUTH;

			if (stateFacing.getAxis() == rotationAxis && stateFace == WallMountLocation.WALL)
				return state;

			for (int i = 0; i < rotation.ordinal(); i++) {
				stateFace = state.get(FACE);
				stateFacing = state.get(WallMountedBlock.FACING);

				boolean b = state.get(FACE) == WallMountLocation.CEILING;
				state = state.with(HORIZONTAL_FACING, b ? forcedAxis : forcedAxis.getOpposite());

				if (stateFace != WallMountLocation.WALL) {
					state = state.with(FACE, WallMountLocation.WALL);
					continue;
				}

				if (stateFacing.getDirection() == Direction.AxisDirection.POSITIVE) {
					state = state.with(FACE, WallMountLocation.FLOOR);
					continue;
				}
				state = state.with(FACE, WallMountLocation.CEILING);
			}

			return state;
		}

		boolean halfTurn = rotation == BlockRotation.CLOCKWISE_180;
		if (block instanceof StairsBlock) {
			state = transformStairs(state, halfTurn);
			return state;
		}

		if (AllBlocks.BELT.getStateManager().getStates().contains(state)) {
			state = transformBelt(state, halfTurn);
			return state;
		}

		if (BlockHelper.hasBlockStateProperty(state, FACING)) {
			Direction newFacing = transformFacing(state.get(FACING));
			if (state.get(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE) != null) {
				if (rotationAxis == newFacing.getAxis() && rotation.ordinal() % 2 == 1)
					state = state.cycle(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE);
			}
			state = state.with(FACING, newFacing);

		} else if (BlockHelper.hasBlockStateProperty(state, AXIS)) {
			state = state.with(AXIS, transformAxis(state.get(AXIS)));

		} else if (halfTurn) {

			if (BlockHelper.hasBlockStateProperty(state, AXIS)) {
				Direction stateFacing = state.get(FACING);
				if (stateFacing.getAxis() == rotationAxis)
					return state;
			}

			if (BlockHelper.hasBlockStateProperty(state, HORIZONTAL_FACING)) {
				Direction stateFacing = state.get(HORIZONTAL_FACING);
				if (stateFacing.getAxis() == rotationAxis)
					return state;
			}

			state = state.rotate(rotation);
			if (BlockHelper.hasBlockStateProperty(state, SlabBlock.TYPE) && state.get(SlabBlock.TYPE) != SlabType.DOUBLE)
				state = state.with(SlabBlock.TYPE,
					state.get(SlabBlock.TYPE) == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM);
		}

		return state;
	}

	protected BlockState transformStairs(BlockState state, boolean halfTurn) {
		if (state.get(StairsBlock.FACING)
			.getAxis() != rotationAxis) {
			for (int i = 0; i < rotation.ordinal(); i++) {
				Direction direction = state.get(StairsBlock.FACING);
				BlockHalf half = state.get(StairsBlock.HALF);
				if (direction.getDirection() == Direction.AxisDirection.POSITIVE ^ half == BlockHalf.BOTTOM
					^ direction.getAxis() == Direction.Axis.Z)
					state = state.cycle(StairsBlock.HALF);
				else
					state = state.with(StairsBlock.FACING, direction.getOpposite());
			}
		} else {
			if (halfTurn) {
				state = state.cycle(StairsBlock.HALF);
			}
		}
		return state;
	}

	protected BlockState transformBelt(BlockState state, boolean halfTurn) {
		Direction initialDirection = state.get(BeltBlock.HORIZONTAL_FACING);
		boolean diagonal =
			state.get(BeltBlock.SLOPE) == BeltSlope.DOWNWARD || state.get(BeltBlock.SLOPE) == BeltSlope.UPWARD;

		if (!diagonal) {
			for (int i = 0; i < rotation.ordinal(); i++) {
				Direction direction = state.get(BeltBlock.HORIZONTAL_FACING);
				BeltSlope slope = state.get(BeltBlock.SLOPE);
				boolean vertical = slope == BeltSlope.VERTICAL;
				boolean horizontal = slope == BeltSlope.HORIZONTAL;
				boolean sideways = slope == BeltSlope.SIDEWAYS;

				Direction newDirection = direction.getOpposite();
				BeltSlope newSlope = BeltSlope.VERTICAL;

				if (vertical) {
					if (direction.getAxis() == rotationAxis) {
						newDirection = direction.rotateYCounterclockwise();
						newSlope = BeltSlope.SIDEWAYS;
					} else {
						newSlope = BeltSlope.HORIZONTAL;
						newDirection = direction;
						if (direction.getAxis() == Direction.Axis.Z)
							newDirection = direction.getOpposite();
					}
				}

				if (sideways) {
					newDirection = direction;
					if (direction.getAxis() == rotationAxis)
						newSlope = BeltSlope.HORIZONTAL;
					else
						newDirection = direction.rotateYCounterclockwise();
				}

				if (horizontal) {
					newDirection = direction;
					if (direction.getAxis() == rotationAxis)
						newSlope = BeltSlope.SIDEWAYS;
					else if (direction.getAxis() != Direction.Axis.Z)
						newDirection = direction.getOpposite();
				}

				state = state.with(BeltBlock.HORIZONTAL_FACING, newDirection);
				state = state.with(BeltBlock.SLOPE, newSlope);
			}

		} else if (initialDirection.getAxis() != rotationAxis) {
			for (int i = 0; i < rotation.ordinal(); i++) {
				Direction direction = state.get(BeltBlock.HORIZONTAL_FACING);
				Direction newDirection = direction.getOpposite();
				BeltSlope slope = state.get(BeltBlock.SLOPE);
				boolean upward = slope == BeltSlope.UPWARD;
				boolean downward = slope == BeltSlope.DOWNWARD;

				// Rotate diagonal
				if (direction.getDirection() == Direction.AxisDirection.POSITIVE ^ downward ^ direction.getAxis() == Direction.Axis.Z) {
					state = state.with(BeltBlock.SLOPE, upward ? BeltSlope.DOWNWARD : BeltSlope.UPWARD);
				} else {
					state = state.with(BeltBlock.HORIZONTAL_FACING, newDirection);
				}
			}

		} else if (halfTurn) {
			Direction direction = state.get(BeltBlock.HORIZONTAL_FACING);
			Direction newDirection = direction.getOpposite();
			BeltSlope slope = state.get(BeltBlock.SLOPE);
			boolean vertical = slope == BeltSlope.VERTICAL;

			if (diagonal) {
				state = state.with(BeltBlock.SLOPE, slope == BeltSlope.UPWARD ? BeltSlope.DOWNWARD
					: slope == BeltSlope.DOWNWARD ? BeltSlope.UPWARD : slope);
			} else if (vertical) {
				state = state.with(BeltBlock.HORIZONTAL_FACING, newDirection);
			}
		}
		return state;
	}

	public Direction.Axis transformAxis(Direction.Axis axisIn) {
		Direction facing = Direction.get(Direction.AxisDirection.POSITIVE, axisIn);
		facing = transformFacing(facing);
		Direction.Axis axis = facing.getAxis();
		return axis;
	}

	public Direction transformFacing(Direction facing) {
		for (int i = 0; i < rotation.ordinal(); i++)
			facing = DirectionHelper.rotateAround(facing, rotationAxis);
		return facing;
	}

	public void writeToBuffer(PacketByteBuf buffer) {
		buffer.writeBlockPos(offset);
		buffer.writeInt(angle);
		buffer.writeVarInt(rotationAxis == null ? -1 : rotationAxis.ordinal());
		buffer.writeVarInt(rotation == null ? -1 : rotation.ordinal());
	}

}
