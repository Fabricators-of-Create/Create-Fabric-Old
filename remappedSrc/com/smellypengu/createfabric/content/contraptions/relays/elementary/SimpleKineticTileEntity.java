package com.smellypengu.createfabric.content.contraptions.relays.elementary;

import com.smellypengu.createfabric.AllTileEntities;
import com.smellypengu.createfabric.content.contraptions.base.IRotate;
import com.smellypengu.createfabric.content.contraptions.base.KineticTileEntity;
import com.smellypengu.createfabric.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class SimpleKineticTileEntity extends KineticTileEntity {

	public SimpleKineticTileEntity(BlockPos pos, BlockState state) {
		super(AllTileEntities.SIMPLE_KINETIC, pos, state);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		/**behaviours.add( TODO trigger
			new BracketedTileEntityBehaviour(this, state -> state.getBlock() instanceof AbstractShaftBlock).withTrigger(
				state -> AllTriggers.BRACKET_APPLY_TRIGGER.constructTriggerFor(state.getBlock())));*/
		super.addBehaviours(behaviours);
	}

	/**@Override
	public Box makeRenderBoundingBox() {
		return new Box(pos).expand(1);
	}*/

	@Override
	public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
		/**if (!AllBlocks.LARGE_COGWHEEL.has(state)) TODO LARGE_COGWHEEL CHECK
			return super.addPropagationLocations(block, state, neighbours);*/

		BlockPos.stream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1))
			.forEach(offset -> {
				if (offset.getSquaredDistance(0, 0, 0, false) == BlockPos.ZERO.getSquaredDistance(1, 1, 0, false))
					neighbours.add(pos.add(offset));
			});
		return neighbours;
	}

}
