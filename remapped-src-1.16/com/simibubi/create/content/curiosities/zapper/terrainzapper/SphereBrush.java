package com.simibubi.create.content.curiosities.zapper.terrainzapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class SphereBrush extends Brush {

	public static final int MAX_RADIUS = 10;
	private Map<Integer, List<BlockPos>> cachedBrushes;

	public SphereBrush() {
		super(1);

		cachedBrushes = new HashMap<>();
		for (int i = 0; i <= MAX_RADIUS; i++) {
			int radius = i;
			List<BlockPos> positions =
				BlockPos.stream(BlockPos.ORIGIN.add(-i - 1, -i - 1, -i - 1), BlockPos.ORIGIN.add(i + 1, i + 1, i + 1))
						.map(BlockPos::new).filter(p -> VecHelper.getCenterOf(p)
								.distanceTo(VecHelper.getCenterOf(BlockPos.ORIGIN)) < radius + .5f)
						.collect(Collectors.toList());
			cachedBrushes.put(i, positions);
		}
	}

	@Override
	public BlockPos getOffset(Vec3d ray, Direction face, PlacementOptions option) {
		if (option == PlacementOptions.Merged)
			return BlockPos.ORIGIN;

		int offset = option == PlacementOptions.Attached ? 0 : -1;
		int r = (param0 + 1 + offset);

		return BlockPos.ORIGIN.offset(face, r * (option == PlacementOptions.Attached ? 1 : -1));
	}

	@Override
	int getMax(int paramIndex) {
		return MAX_RADIUS;
	}

	@Override
	Text getParamLabel(int paramIndex) {
		return Lang.translate("generic.radius");
	}

	@Override
	List<BlockPos> getIncludedPositions() {
		return cachedBrushes.get(Integer.valueOf(param0));
	}

}
