package com.simibubi.create.content.logistics.block.redstone;

import java.util.Random;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.foundation.block.ITE;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AnalogLeverBlock extends WallMountedBlock implements ITE<AnalogLeverTileEntity> {

	public AnalogLeverBlock(Settings p_i48402_1_) {
		super(p_i48402_1_);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.ANALOG_LEVER.create();
	}

	@Override
	public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
			BlockHitResult hit) {
		if (worldIn.isClient) {
			addParticles(state, worldIn, pos, 1.0F);
			return ActionResult.SUCCESS;
		}

		try {
			boolean sneak = player.isSneaking();
			AnalogLeverTileEntity te = getTileEntity(worldIn, pos);
			te.changeState(sneak);
			float f = .25f + ((te.state + 5) / 15f) * .5f;
			worldIn.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.2F, f);
		} catch (TileEntityException e) {}

		return ActionResult.SUCCESS;
	}

	@Override
	public int getWeakRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side) {
		try {
			return getTileEntity(blockAccess, pos).state;
		} catch (TileEntityException e) {
			return 0;
		}
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public int getStrongRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side) {
		return getDirection(blockState) == side ? getWeakRedstonePower(blockState, blockAccess, pos, side) : 0;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		try {
			AnalogLeverTileEntity tileEntity = getTileEntity(worldIn, pos);
			if (tileEntity.state != 0 && rand.nextFloat() < 0.25F)
				addParticles(stateIn, worldIn, pos, 0.5F);
		} catch (TileEntityException e) {}
	}

	@Override
	public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		try {
			AnalogLeverTileEntity tileEntity = getTileEntity(worldIn, pos);
			if (!isMoving && state.getBlock() != newState.getBlock()) {
				if (tileEntity.state != 0)
					updateNeighbors(state, worldIn, pos);
				worldIn.removeBlockEntity(pos);
			}
		} catch (TileEntityException e) {}
	}

	private static void addParticles(BlockState state, WorldAccess worldIn, BlockPos pos, float alpha) {
		Direction direction = state.get(FACING).getOpposite();
		Direction direction1 = getDirection(state).getOpposite();
		double d0 = (double) pos.getX() + 0.5D + 0.1D * (double) direction.getOffsetX()
				+ 0.2D * (double) direction1.getOffsetX();
		double d1 = (double) pos.getY() + 0.5D + 0.1D * (double) direction.getOffsetY()
				+ 0.2D * (double) direction1.getOffsetY();
		double d2 = (double) pos.getZ() + 0.5D + 0.1D * (double) direction.getOffsetZ()
				+ 0.2D * (double) direction1.getOffsetZ();
		worldIn.addParticle(new DustParticleEffect(1.0F, 0.0F, 0.0F, alpha), d0, d1, d2, 0.0D, 0.0D, 0.0D);
	}

	static void updateNeighbors(BlockState state, World world, BlockPos pos) {
		world.updateNeighborsAlways(pos, state.getBlock());
		world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), state.getBlock());
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		return Blocks.LEVER.getOutlineShape(state, worldIn, pos, context);
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACING, FACE));
	}

	@Override
	public Class<AnalogLeverTileEntity> getTileEntityClass() {
		return AnalogLeverTileEntity.class;
	}

}
