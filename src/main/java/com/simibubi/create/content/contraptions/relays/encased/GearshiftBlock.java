package com.simibubi.create.content.contraptions.relays.encased;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.content.contraptions.RotationPropagator;
import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import com.simibubi.create.content.contraptions.relays.gearbox.GearshiftBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

import java.util.Random;

public class GearshiftBlock extends AbstractEncasedShaftBlock implements IBE<GearshiftBlockEntity> {

	public static final BooleanProperty POWERED = Properties.POWERED;

	public GearshiftBlock(AbstractBlock.Settings properties) {
		super(properties);
		setDefaultState(getDefaultState().with(POWERED, false));
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return AllBlockEntities.GEARSHIFT.instantiate();
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(POWERED);
		super.appendProperties(builder);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return super.getPlacementState(context).with(POWERED,
				context.getWorld().isReceivingRedstonePower(context.getBlockPos()));
	}

	@Override
	public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		if (worldIn.isClient)
			return;

		boolean previouslyPowered = state.get(POWERED);
		if (previouslyPowered != worldIn.isReceivingRedstonePower(pos)) {
			detachKinetics(worldIn, pos, true);
			worldIn.setBlockState(pos, state.cycle(POWERED), 2);
		}
	}

	@Override
	public Class<GearshiftBlockEntity> getBlockEntityClass() {
		return GearshiftBlockEntity.class;
	}

	public void detachKinetics(World worldIn, BlockPos pos, boolean reAttachNextTick) {
		BlockEntity te = worldIn.getBlockEntity(pos);
		if (te == null || !(te instanceof KineticBlockEntity))
			return;
		RotationPropagator.handleRemoved(worldIn, pos, (KineticBlockEntity) te);

		// Re-attach next tick
		if (reAttachNextTick)
			worldIn.getBlockTickScheduler().schedule(pos, this, 0, TickPriority.EXTREMELY_HIGH);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
		BlockEntity te = worldIn.getBlockEntity(pos);
		if (te == null || !(te instanceof KineticBlockEntity))
			return;
		KineticBlockEntity kte = (KineticBlockEntity) te;
		RotationPropagator.handleAdded(worldIn, pos, kte);
	}
}
