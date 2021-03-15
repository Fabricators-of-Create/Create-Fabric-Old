package com.simibubi.create.content.contraptions.relays.elementary;

import java.util.List;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import com.simibubi.create.content.contraptions.base.Rotating;
import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public class SimpleKineticBlockEntity extends KineticBlockEntity {
	public SimpleKineticBlockEntity(BlockEntityType<? extends SimpleKineticBlockEntity> type) {
		super(type);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
//		behaviours.add(
//			new BracketedBlockEntityBehaviour(this, state -> state.getBlock() instanceof AbstractShaftBlock).withTrigger(
//				state -> AllTriggers.BRACKET_APPLY_TRIGGER.constructTriggerFor(state.getBlock())));
		super.addBehaviours(behaviours);
	}

	@Override
	public Box makeRenderBoundingBox() {
		return new Box(pos).expand(1);
	}

	@Override
	public List<BlockPos> addPropagationLocations(Rotating block, BlockState state, List<BlockPos> neighbours) {
		if (AllBlocks.LARGE_COGWHEEL != state.getBlock())
			return super.addPropagationLocations(block, state, neighbours);

		BlockPos.stream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1))
			.forEach(offset -> {
				if (offset.getSquaredDistance(0, 0, 0, false) == BlockPos.ORIGIN.getSquaredDistance(1, 1, 0, false))
					neighbours.add(pos.add(offset));
			});
		return neighbours;
	}

	public static SimpleKineticBlockEntity create() {
		return new SimpleKineticBlockEntity(AllBlockEntities.SIMPLE_KINETIC);
	}
}
