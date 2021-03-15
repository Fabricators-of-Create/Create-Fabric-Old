package com.simibubi.create.foundation.utility.outliner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.foundation.renderState.RenderTypes;
import com.simibubi.create.foundation.renderState.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BlockClusterOutline extends Outline {

	private Cluster cluster;

	public BlockClusterOutline(Iterable<BlockPos> selection) {
		cluster = new Cluster();
		selection.forEach(cluster::include);
	}

	@Override
	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		for (MergeEntry edge : cluster.visibleEdges) {
			Vec3d start = new Vec3d(edge.pos.getX(), edge.pos.getY(), edge.pos.getZ());
			Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, edge.axis);
			BlockPos pos = edge.pos.offset(direction);
			renderAACuboidLine(ms, buffer, start, new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
		}

		for (MergeEntry face : cluster.visibleFaces.keySet()) {
			Direction.AxisDirection axisDirection = cluster.visibleFaces.get(face);
			Direction direction = Direction.get(axisDirection, face.axis);
			BlockPos pos = face.pos;
			if (axisDirection == Direction.AxisDirection.POSITIVE)
				pos = pos.offset(direction.getOpposite());
			renderBlockFace(ms, buffer, pos, direction);
		}
	}

	protected void renderBlockFace(MatrixStack ms, SuperRenderTypeBuffer buffer, BlockPos pos, Direction face) {
		Optional<AllSpecialTextures> faceTexture = params.faceTexture;
		if (!faceTexture.isPresent())
			return;

		RenderLayer translucentType = RenderTypes.getOutlineTranslucent(faceTexture.get()
			.getLocation(), true);
		VertexConsumer builder = buffer.getLateBuffer(translucentType);

		Vec3d center = VecHelper.getCenterOf(pos);
		Vec3d offset = new Vec3d(face.getUnitVector());
		Vec3d plane = VecHelper.axisAlingedPlaneOf(offset);
		Direction.Axis axis = face.getAxis();

		offset = offset.multiply(1 / 2f + 1 / 64d);
		plane = plane.multiply(1 / 2f)
			.add(offset);

		int deg = face.getDirection()
			.offset() * 90;
		Vec3d a1 = plane.add(center);
		plane = VecHelper.rotate(plane, deg, axis);
		Vec3d a2 = plane.add(center);
		plane = VecHelper.rotate(plane, deg, axis);
		Vec3d a3 = plane.add(center);
		plane = VecHelper.rotate(plane, deg, axis);
		Vec3d a4 = plane.add(center);

		putQuad(ms, builder, a1, a2, a3, a4, face);
	}

	private static class Cluster {

		Map<MergeEntry, Direction.AxisDirection> visibleFaces;
		Set<MergeEntry> visibleEdges;

		public Cluster() {
			visibleEdges = new HashSet<>();
			visibleFaces = new HashMap<>();
		}

		public void include(BlockPos pos) {

			// 6 FACES
			for (Direction.Axis axis : Direction.Axis.values()) {
				Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
				for (int offset : new int[] { 0, 1 }) {
					MergeEntry entry = new MergeEntry(axis, pos.offset(direction, offset));
					if (visibleFaces.remove(entry) == null)
						visibleFaces.put(entry, offset == 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
				}
			}

			// 12 EDGES
			for (Direction.Axis axis : Direction.Axis.values()) {
				for (Direction.Axis axis2 : Direction.Axis.values()) {
					if (axis == axis2)
						continue;
					for (Direction.Axis axis3 : Direction.Axis.values()) {
						if (axis == axis3)
							continue;
						if (axis2 == axis3)
							continue;

						Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis2);
						Direction direction2 = Direction.get(Direction.AxisDirection.POSITIVE, axis3);

						for (int offset : new int[] { 0, 1 }) {
							BlockPos entryPos = pos.offset(direction, offset);
							for (int offset2 : new int[] { 0, 1 }) {
								entryPos = entryPos.offset(direction2, offset2);
								MergeEntry entry = new MergeEntry(axis, entryPos);
								if (!visibleEdges.remove(entry))
									visibleEdges.add(entry);
							}
						}
					}

					break;
				}
			}

		}

	}

	private static class MergeEntry {

		Direction.Axis axis;
		BlockPos pos;

		public MergeEntry(Direction.Axis axis, BlockPos pos) {
			this.axis = axis;
			this.pos = pos;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof MergeEntry))
				return false;

			MergeEntry other = (MergeEntry) o;
			return this.axis == other.axis && this.pos.equals(other.pos);
		}

		@Override
		public int hashCode() {
			return this.pos.hashCode() * 31 + axis.ordinal();
		}
	}

}
