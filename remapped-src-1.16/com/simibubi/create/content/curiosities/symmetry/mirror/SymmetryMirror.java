package com.simibubi.create.content.curiosities.symmetry.mirror;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public abstract class SymmetryMirror {

	public static final String EMPTY = "empty";
	public static final String PLANE = "plane";
	public static final String CROSS_PLANE = "cross_plane";
	public static final String TRIPLE_PLANE = "triple_plane";

	protected Vec3d position;
	protected StringIdentifiable orientation;
	protected int orientationIndex;
	public boolean enable;

	public SymmetryMirror(Vec3d pos) {
		position = pos;
		enable = true;
		orientationIndex = 0;
	}

	public static List<Text> getMirrors() {
		return ImmutableList.of(Lang.translate("symmetry.mirror.plane"), Lang.translate("symmetry.mirror.doublePlane"),
			Lang.translate("symmetry.mirror.triplePlane"));
	}

	public StringIdentifiable getOrientation() {
		return orientation;
	}

	public Vec3d getPosition() {
		return position;
	}

	public int getOrientationIndex() {
		return orientationIndex;
	}

	public void rotate(boolean forward) {
		orientationIndex += forward ? 1 : -1;
		setOrientation();
	}

	public void process(Map<BlockPos, BlockState> blocks) {
		Map<BlockPos, BlockState> result = new HashMap<>();
		for (BlockPos pos : blocks.keySet()) {
			result.putAll(process(pos, blocks.get(pos)));
		}
		blocks.putAll(result);
	}

	public abstract Map<BlockPos, BlockState> process(BlockPos position, BlockState block);

	protected abstract void setOrientation();

	public abstract void setOrientation(int index);

	public abstract String typeName();

	public abstract AllBlockPartials getModel();

	public void applyModelTransform(MatrixStack ms) {}

	private static final String $ORIENTATION = "direction";
	private static final String $POSITION = "pos";
	private static final String $TYPE = "type";
	private static final String $ENABLE = "enable";

	public CompoundTag writeToNbt() {
		CompoundTag nbt = new CompoundTag();
		nbt.putInt($ORIENTATION, orientationIndex);

		ListTag floatList = new ListTag();
		floatList.add(FloatTag.of((float) position.x));
		floatList.add(FloatTag.of((float) position.y));
		floatList.add(FloatTag.of((float) position.z));
		nbt.put($POSITION, floatList);
		nbt.putString($TYPE, typeName());
		nbt.putBoolean($ENABLE, enable);

		return nbt;
	}

	public static SymmetryMirror fromNBT(CompoundTag nbt) {
		ListTag floatList = nbt.getList($POSITION, 5);
		Vec3d pos = new Vec3d(floatList.getFloat(0), floatList.getFloat(1), floatList.getFloat(2));
		SymmetryMirror element;

		switch (nbt.getString($TYPE)) {
		case PLANE:
			element = new PlaneMirror(pos);
			break;
		case CROSS_PLANE:
			element = new CrossPlaneMirror(pos);
			break;
		case TRIPLE_PLANE:
			element = new TriplePlaneMirror(pos);
			break;
		default:
			element = new EmptyMirror(pos);
			break;
		}

		element.setOrientation(nbt.getInt($ORIENTATION));
		element.enable = nbt.getBoolean($ENABLE);

		return element;
	}

	protected Vec3d getDiff(BlockPos position) {
		return this.position.multiply(-1)
			.add(position.getX(), position.getY(), position.getZ());
	}

	protected BlockPos getIDiff(BlockPos position) {
		Vec3d diff = getDiff(position);
		return new BlockPos((int) diff.x, (int) diff.y, (int) diff.z);
	}

	protected BlockState flipX(BlockState in) {
		return in.mirror(BlockMirror.FRONT_BACK);
	}

	protected BlockState flipY(BlockState in) {
		for (Property<?> property : in.getProperties()) {

			if (property == Properties.BLOCK_HALF)
				return in.cycle(property);
			// Directional Blocks
			if (property instanceof DirectionProperty) {
				if (in.get(property) == Direction.DOWN) {
					return in.with((DirectionProperty) property, Direction.UP);
				} else if (in.get(property) == Direction.UP) {
					return in.with((DirectionProperty) property, Direction.DOWN);
				}
			}
		}
		return in;
	}

	protected BlockState flipZ(BlockState in) {
		return in.mirror(BlockMirror.LEFT_RIGHT);
	}

	protected BlockState flipD1(BlockState in) {
		return in.rotate(BlockRotation.COUNTERCLOCKWISE_90)
			.mirror(BlockMirror.FRONT_BACK);
	}

	protected BlockState flipD2(BlockState in) {
		return in.rotate(BlockRotation.COUNTERCLOCKWISE_90)
			.mirror(BlockMirror.LEFT_RIGHT);
	}

	protected BlockPos flipX(BlockPos position) {
		BlockPos diff = getIDiff(position);
		return new BlockPos(position.getX() - 2 * diff.getX(), position.getY(), position.getZ());
	}

	protected BlockPos flipY(BlockPos position) {
		BlockPos diff = getIDiff(position);
		return new BlockPos(position.getX(), position.getY() - 2 * diff.getY(), position.getZ());
	}

	protected BlockPos flipZ(BlockPos position) {
		BlockPos diff = getIDiff(position);
		return new BlockPos(position.getX(), position.getY(), position.getZ() - 2 * diff.getZ());
	}

	protected BlockPos flipD2(BlockPos position) {
		BlockPos diff = getIDiff(position);
		return new BlockPos(position.getX() - diff.getX() + diff.getZ(), position.getY(),
			position.getZ() - diff.getZ() + diff.getX());
	}

	protected BlockPos flipD1(BlockPos position) {
		BlockPos diff = getIDiff(position);
		return new BlockPos(position.getX() - diff.getX() - diff.getZ(), position.getY(),
			position.getZ() - diff.getZ() - diff.getX());
	}

	public void setPosition(Vec3d pos3d) {
		this.position = pos3d;
	}

	public abstract List<Text> getAlignToolTips();

}
