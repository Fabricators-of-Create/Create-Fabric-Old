package com.simibubi.create.content.contraptions.components.crusher;

import java.util.Random;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class CrushingWheelControllerBlock extends FacingBlock
		implements ITE<CrushingWheelControllerTileEntity> {

	public CrushingWheelControllerBlock(Settings p_i48440_1_) {
		super(p_i48440_1_);
	}

	public static final BooleanProperty VALID = BooleanProperty.of("valid");

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext useContext) {
		return false;
	}

	@Override
	public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
		return true;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		return AllTileEntities.CRUSHING_WHEEL_CONTROLLER.create();
	}

	@Override
	protected void appendProperties(Builder<Block, BlockState> builder) {
		builder.add(VALID);
		builder.add(FACING);
		super.appendProperties(builder);
	}

	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (!state.get(VALID))
			return;

		Direction facing = state.get(FACING);
		Axis axis = facing.getAxis();

		checkEntityForProcessing(worldIn, pos, entityIn);

		withTileEntityDo(worldIn, pos, te -> {
			if (te.processingEntity == entityIn)

				entityIn.slowMovement(state, new Vec3d(axis == Axis.X ? (double) 0.05F : 0.25D
						, axis == Axis.Y ? (double) 0.05F : 0.25D
						, axis == Axis.Z ? (double) 0.05F : 0.25D));
		});
	}

	public void checkEntityForProcessing(World worldIn, BlockPos pos, Entity entityIn){
		try {
			CrushingWheelControllerTileEntity te = getTileEntity(worldIn, pos);
			if (te.crushingspeed == 0)
				return;
			if (entityIn instanceof ItemEntity)
				((ItemEntity) entityIn).setPickupDelay(10);
			CompoundTag data = entityIn.getPersistentData();
			if (data.contains("BypassCrushingWheel")) {
				if (pos.equals(NbtHelper.toBlockPos(data.getCompound("BypassCrushingWheel"))))
					return;
			}
			if (te.isOccupied())
				return;
			boolean isPlayer = entityIn instanceof PlayerEntity;
			if (isPlayer && ((PlayerEntity) entityIn).isCreative())
				return;
			if (isPlayer && entityIn.world.getDifficulty() == Difficulty.PEACEFUL)
				return;

			te.startCrushing(entityIn);
		} catch (TileEntityException e) {}
	}

	@Override
	public void onEntityLand(BlockView worldIn, Entity entityIn) {
		super.onEntityLand(worldIn, entityIn);
		//Moved to onEntityCollision to allow for omnidirectional input
	}

	@Override
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		if (!stateIn.get(VALID))
			return;
		if (rand.nextInt(1) != 0)
			return;
		double d0 = (double) ((float) pos.getX() + rand.nextFloat());
		double d1 = (double) ((float) pos.getY() + rand.nextFloat());
		double d2 = (double) ((float) pos.getZ() + rand.nextFloat());
		worldIn.addParticle(ParticleTypes.CRIT, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState stateIn, Direction facing, BlockState facingState, WorldAccess worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		updateSpeed(stateIn, worldIn, currentPos);
		return stateIn;
	}

	public void updateSpeed(BlockState state, WorldAccess world, BlockPos pos) {
		withTileEntityDo(world, pos, te -> {
			if (!state.get(VALID)) {
				if (te.crushingspeed != 0) {
					te.crushingspeed = 0;
					te.sendData();
				}
				return;
			}

			for (Direction d : Iterate.directions) {
				BlockState neighbour = world.getBlockState(pos.offset(d));
				if (!AllBlocks.CRUSHING_WHEEL.has(neighbour))
					continue;
				if (neighbour.get(Properties.AXIS) == d.getAxis())
					continue;
				KineticTileEntity wheelTe = (KineticTileEntity) world.getBlockEntity(pos.offset(d));
				te.crushingspeed = Math.abs(wheelTe.getSpeed() / 50f);
				te.sendData();
				break;
			}
		});
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos,
										ShapeContext context) {
		if (!state.get(VALID))
			return AllShapes.CRUSHING_WHEEL_CONTROLLER_COLLISION.get(state.get(FACING));

		Entity entity = context.getEntity();
		if (entity != null) {

			CompoundTag data = entity.getPersistentData();
			if (data.contains("BypassCrushingWheel")) {
				if (pos.equals(NbtHelper.toBlockPos(data.getCompound("BypassCrushingWheel"))))
					if (state.get(FACING) != Direction.UP) //Allow output items to land on top of the block rather than falling back through.
						return VoxelShapes.empty();
			}

			try {
				CrushingWheelControllerTileEntity te = getTileEntity(worldIn, pos);
				if (te.processingEntity == entity)
					return VoxelShapes.empty();
			} catch (TileEntityException e) {}
		}
		return AllShapes.CRUSHING_WHEEL_CONTROLLER_COLLISION.get(state.get(FACING));
	}

	@Override
	public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.hasTileEntity() || state.getBlock() == newState.getBlock())
			return;

		withTileEntityDo(worldIn, pos, te -> ItemHelper.dropContents(worldIn, pos, te.inventory));
		worldIn.removeBlockEntity(pos);
	}

	@Override
	public Class<CrushingWheelControllerTileEntity> getTileEntityClass() {
		return CrushingWheelControllerTileEntity.class;
	}

}
