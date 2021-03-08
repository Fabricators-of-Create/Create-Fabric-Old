package com.simibubi.create.content.contraptions.components.actors.dispenser;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class MovedOptionalDispenseBehaviour extends MovedDefaultDispenseItemBehaviour {
	protected boolean successful = true;

	@Override
	protected void playDispenseSound(WorldAccess world, BlockPos pos) {
		world.syncWorldEvent(this.successful ? 1000 : 1001, pos, 0);
	}
}
