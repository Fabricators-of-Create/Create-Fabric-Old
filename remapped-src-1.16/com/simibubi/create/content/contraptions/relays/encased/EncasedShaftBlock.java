package com.simibubi.create.content.contraptions.relays.encased;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.CasingBlock;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.schematics.ISpecialBlockItemRequirement;
import com.simibubi.create.content.schematics.ItemRequirement;
import com.tterrag.registrate.util.entry.BlockEntry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.BlockView;

public class EncasedShaftBlock extends AbstractEncasedShaftBlock implements ISpecialBlockItemRequirement {

	private BlockEntry<CasingBlock> casing;

	public static EncasedShaftBlock andesite(Settings properties) {
		return new EncasedShaftBlock(properties, AllBlocks.ANDESITE_CASING);
	}
	
	public static EncasedShaftBlock brass(Settings properties) {
		return new EncasedShaftBlock(properties, AllBlocks.BRASS_CASING);
	}
	
	protected EncasedShaftBlock(Settings properties, BlockEntry<CasingBlock> casing) {
		super(properties);
		this.casing = casing;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.ENCASED_SHAFT.create();
	}
	
	public BlockEntry<CasingBlock> getCasing() {
		return casing;
	}

	@Override
	public ActionResult onSneakWrenched(BlockState state, ItemUsageContext context) {
		if (context.getWorld().isClient)
			return ActionResult.SUCCESS;
		context.getWorld().syncWorldEvent(2001, context.getBlockPos(), Block.getRawIdFromState(state));
		KineticTileEntity.switchToBlockState(context.getWorld(), context.getBlockPos(), AllBlocks.SHAFT.getDefaultState().with(AXIS, state.get(AXIS)));
		return ActionResult.SUCCESS;
	}
	
	@Override
	public ItemRequirement getRequiredItems(BlockState state) {
		return ItemRequirement.of(AllBlocks.SHAFT.getDefaultState());
	}

}
