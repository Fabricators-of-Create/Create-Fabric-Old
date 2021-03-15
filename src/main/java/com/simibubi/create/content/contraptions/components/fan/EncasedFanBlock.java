package com.simibubi.create.content.contraptions.components.fan;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class EncasedFanBlock extends DirectionalKineticBlock implements IBE<EncasedFanBlockEntity> {

	public EncasedFanBlock(Settings properties) {
		super(properties);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return AllBlockEntities.ENCASED_FAN.instantiate();
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
		blockUpdate(state, worldIn, pos);
	}

	@Override
	public void prepare(BlockState stateIn, WorldAccess worldIn, BlockPos pos, int flags, int count) {
		super.prepare(stateIn, worldIn, pos, flags, count);
		blockUpdate(stateIn, (World) worldIn, pos);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState p_196243_4_, boolean p_196243_5_) {
		if (/*state.hasTileEntity() &&*/ (state.getBlock() != p_196243_4_.getBlock() /*|| !p_196243_4_.hasTileEntity()*/)) {
			withBlockEntityDo(world, pos, EncasedFanBlockEntity::updateChute);
			world.removeBlockEntity(pos);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
		boolean isMoving) {
		blockUpdate(state, worldIn, pos);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		Direction face = context.getSide();

		BlockState placedOn = world.getBlockState(pos.offset(face.getOpposite()));
		BlockState placedOnOpposite = world.getBlockState(pos.offset(face));
		/*if (AbstractChuteBlock.isChute(placedOn))
			return getDefaultState().with(FACING, face.getOpposite());
		if (AbstractChuteBlock.isChute(placedOnOpposite))
			return getDefaultState().with(FACING, face);*/

		Direction preferredFacing = getPreferredFacing(context);
		if (preferredFacing == null)
			preferredFacing = context.getPlayerLookDirection();
		return getDefaultState().with(FACING, context.getPlayer() != null && context.getPlayer()
			.isSneaking() ? preferredFacing : preferredFacing.getOpposite());
	}

	protected void blockUpdate(BlockState state, World worldIn, BlockPos pos) {
		if (worldIn instanceof WrappedWorld)
			return;
		notifyFanTile(worldIn, pos);
		if (worldIn.isClient())
			return;
		withBlockEntityDo(worldIn, pos, te -> te.queueGeneratorUpdate());
	}

	protected void notifyFanTile(World world, BlockPos pos) {
		withBlockEntityDo(world, pos, EncasedFanBlockEntity::blockInFrontChanged);
	}

	@Override
	public BlockState updateAfterWrenched(BlockState newState, ItemUsageContext context) {
		blockUpdate(newState, context.getWorld(), context.getBlockPos());
		return newState;
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.get(FACING)
			.getAxis();
	}

	@Override
	public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
		return face == state.get(FACING)
			.getOpposite();
	}

	@Override
	public boolean showCapacityWithAnnotation() {
		return true;
	}

	@Override
	public Class<EncasedFanBlockEntity> getBlockEntityClass() {
		return EncasedFanBlockEntity.class;
	}

}
