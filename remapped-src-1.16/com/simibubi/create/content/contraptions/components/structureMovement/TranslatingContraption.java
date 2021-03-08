package com.simibubi.create.content.contraptions.components.structureMovement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class TranslatingContraption extends Contraption {

	protected Set<BlockPos> cachedColliders;
	protected Direction cachedColliderDirection;

	public Set<BlockPos> getColliders(World world, Direction movementDirection) {
		if (getBlocks() == null)
			return Collections.emptySet();
		if (cachedColliders == null || cachedColliderDirection != movementDirection) {
			cachedColliders = new HashSet<>();
			cachedColliderDirection = movementDirection;

			for (StructureBlockInfo info : getBlocks().values()) {
				BlockPos offsetPos = info.pos.offset(movementDirection);
				if (info.state.getCollisionShape(world, offsetPos)
					.isEmpty())
					continue;
				if (getBlocks().containsKey(offsetPos)
					&& !getBlocks().get(offsetPos).state.getCollisionShape(world, offsetPos)
						.isEmpty())
					continue;
				cachedColliders.add(info.pos);
			}

		}
		return cachedColliders;
	}

	@Override
	public void removeBlocksFromWorld(World world, BlockPos offset) {
		int count = blocks.size();
		super.removeBlocksFromWorld(world, offset);
		if (count != blocks.size()) {
			cachedColliders = null;
		}
	}

	@Override
	public boolean canBeStabilized(Direction facing, BlockPos localPos) {
		return false;
	}
	
}
