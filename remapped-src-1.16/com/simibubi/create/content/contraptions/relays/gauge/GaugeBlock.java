package com.simibubi.create.content.contraptions.relays.gauge;

import java.util.Random;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.foundation.utility.ColorHelper;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GaugeBlock extends DirectionalAxisKineticBlock {

	public static final GaugeShaper GAUGE = GaugeShaper.make();
	protected Type type;

	public enum Type implements StringIdentifiable {
		SPEED, STRESS;

		@Override
		public String asString() {
			return Lang.asId(name());
		}
	}

	public static GaugeBlock speed(Settings properties) {
		return new GaugeBlock(properties, Type.SPEED);
	}
	
	public static GaugeBlock stress(Settings properties) {
		return new GaugeBlock(properties, Type.STRESS);
	}
	
	protected GaugeBlock(Settings properties, Type type) {
		super(properties);
		this.type = type;
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockView world) {
		switch (type) {
		case SPEED:
			return AllTileEntities.SPEEDOMETER.create();
		case STRESS:
			return AllTileEntities.STRESSOMETER.create();
		default:
			return null;
		}
	}

	/* FIXME: Is there a new way of doing this in 1.16? Or cn we just delete it?
	@SuppressWarnings("deprecation")
	@Override
	public MaterialColor getMaterialColor(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return Blocks.SPRUCE_PLANKS.getMaterialColor(state, worldIn, pos);
	} */

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		World world = context.getWorld();
		Direction face = context.getSide();
		BlockPos placedOnPos = context.getBlockPos().offset(context.getSide().getOpposite());
		BlockState placedOnState = world.getBlockState(placedOnPos);
		Block block = placedOnState.getBlock();

		if (block instanceof IRotate && ((IRotate) block).hasShaftTowards(world, placedOnPos, placedOnState, face)) {
			BlockState toPlace = getDefaultState();
			Direction horizontalFacing = context.getPlayerFacing();
			Direction nearestLookingDirection = context.getPlayerLookDirection();
			boolean lookPositive = nearestLookingDirection.getDirection() == AxisDirection.POSITIVE;
			if (face.getAxis() == Axis.X) {
				toPlace = toPlace
						.with(FACING, lookPositive ? Direction.NORTH : Direction.SOUTH)
						.with(AXIS_ALONG_FIRST_COORDINATE, true);
			} else if (face.getAxis() == Axis.Y) {
				toPlace = toPlace
						.with(FACING, horizontalFacing.getOpposite())
						.with(AXIS_ALONG_FIRST_COORDINATE, horizontalFacing.getAxis() == Axis.X);
			} else {
				toPlace = toPlace
						.with(FACING, lookPositive ? Direction.WEST : Direction.EAST)
						.with(AXIS_ALONG_FIRST_COORDINATE, false);
			}

			return toPlace;
		}

		return super.getPlacementState(context);
	}

	@Override
	protected Direction getFacingForPlacement(ItemPlacementContext context) {
		return context.getSide();
	}

	@Override
	protected boolean getAxisAlignmentForPlacement(ItemPlacementContext context) {
		return context.getPlayerFacing().getAxis() != Axis.X;
	}

	public boolean shouldRenderHeadOnFace(World world, BlockPos pos, BlockState state, Direction face) {
		if (face.getAxis().isVertical())
			return false;
		if (face == state.get(FACING).getOpposite())
			return false;
		if (face.getAxis() == getRotationAxis(state))
			return false;
		if (getRotationAxis(state) == Axis.Y && face != state.get(FACING))
			return false;
		if (!Block.shouldDrawSide(state, world, pos, face)
				&& !(world instanceof WrappedWorld))
			return false;
		return true;
	}

	@Override
	public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		BlockEntity te = worldIn.getBlockEntity(pos);
		if (te == null || !(te instanceof GaugeTileEntity))
			return;
		GaugeTileEntity gaugeTE = (GaugeTileEntity) te;
		if (gaugeTE.dialTarget == 0)
			return;
		int color = gaugeTE.color;

		for (Direction face : Iterate.directions) {
			if (!shouldRenderHeadOnFace(worldIn, pos, stateIn, face))
				continue;

			Vec3d rgb = ColorHelper.getRGB(color);
			Vec3d faceVec = Vec3d.of(face.getVector());
			Direction positiveFacing = Direction.get(AxisDirection.POSITIVE, face.getAxis());
			Vec3d positiveFaceVec = Vec3d.of(positiveFacing.getVector());
			int particleCount = gaugeTE.dialTarget > 1 ? 4 : 1;

			if (particleCount == 1 && rand.nextFloat() > 1 / 4f)
				continue;

			for (int i = 0; i < particleCount; i++) {
				Vec3d mul = VecHelper
						.offsetRandomly(Vec3d.ZERO, rand, .25f)
						.multiply(new Vec3d(1, 1, 1).subtract(positiveFaceVec))
						.normalize()
						.multiply(.3f);
				Vec3d offset = VecHelper.getCenterOf(pos).add(faceVec.multiply(.55)).add(mul);
				worldIn
						.addParticle(new DustParticleEffect((float) rgb.x, (float) rgb.y, (float) rgb.z, 1), offset.x,
								offset.y, offset.z, mul.x, mul.y, mul.z);
			}

		}

	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
		return GAUGE.get(state.get(FACING), state.get(AXIS_ALONG_FIRST_COORDINATE));
	}

	@Override
	public boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState blockState, World worldIn, BlockPos pos) {
		BlockEntity te = worldIn.getBlockEntity(pos);
		if (te instanceof GaugeTileEntity) {
			GaugeTileEntity gaugeTileEntity = (GaugeTileEntity) te;
			return MathHelper.ceil(MathHelper.clamp(gaugeTileEntity.dialTarget * 14, 0, 15));
		}
		return 0;
	}

}
