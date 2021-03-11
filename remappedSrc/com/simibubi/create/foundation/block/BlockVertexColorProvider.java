package com.simibubi.create.foundation.block;

@FunctionalInterface
public interface BlockVertexColorProvider {
	int getColor(float x, float y, float z);
}
