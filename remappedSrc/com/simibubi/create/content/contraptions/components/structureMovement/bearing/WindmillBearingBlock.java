package com.simibubi.create.content.contraptions.components.structureMovement.bearing;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WindmillBearingBlock extends BearingBlock implements IBE<WindmillBearingBlockEntity> {

	public WindmillBearingBlock(Settings properties) {
		super(properties);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return AllBlockEntities.WINDMILL_BEARING.instantiate();
	}

	@Override
	public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockHitResult hit) {
		if (!player.canModifyBlocks())
			return ActionResult.FAIL;
		if (player.isSneaking())
			return ActionResult.FAIL;
		if (player.getStackInHand(handIn)
			.isEmpty()) {
			if (worldIn.isClient)
				return ActionResult.SUCCESS;
			withBlockEntityDo(worldIn, pos, te -> {
				if (te.running) {
					te.disassemble();
					return;
				}
				te.assembleNextTick = true;
			});
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Override
	public Class<WindmillBearingBlockEntity> getBlockEntityClass() {
		return WindmillBearingBlockEntity.class;
	}

}
