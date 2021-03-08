package com.simibubi.create.content.contraptions.components.structureMovement;

import java.util.function.BiPredicate;
import com.simibubi.create.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.MatrixStacker;

import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class DirectionalExtenderScrollOptionSlot extends CenteredSideValueBoxTransform {

	public DirectionalExtenderScrollOptionSlot(BiPredicate<BlockState, Direction> allowedDirections) {
		super(allowedDirections);
	}

	@Override
	protected Vec3d getLocalOffset(BlockState state) {
		return super.getLocalOffset(state)
				.add(Vec3d.of(state.get(Properties.FACING).getVector()).multiply(-2 / 16f));
	}

	@Override
	protected void rotate(BlockState state, MatrixStack ms) {
		if (!getSide().getAxis().isHorizontal())
			MatrixStacker.of(ms).rotateY(AngleHelper.horizontalAngle(state.get(Properties.FACING)) - 90);
		super.rotate(state, ms);
	}
}