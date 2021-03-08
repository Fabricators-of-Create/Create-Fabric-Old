package com.simibubi.create.content.contraptions.components.actors.dispenser;

import net.minecraft.util.math.Position;

public class SimplePos implements Position {
	private final double x;
	private final double y;
	private final double z;

	public SimplePos(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getZ() {
		return z;
	}
}
