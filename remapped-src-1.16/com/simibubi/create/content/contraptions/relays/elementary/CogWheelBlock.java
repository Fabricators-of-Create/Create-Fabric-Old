package com.simibubi.create.content.contraptions.relays.elementary;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.content.contraptions.relays.advanced.SpeedControllerBlock;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class CogWheelBlock extends AbstractShaftBlock {

    boolean isLarge;

    private CogWheelBlock(boolean large, Settings properties) {
        super(properties);
        isLarge = large;
    }

    public static CogWheelBlock small(Settings properties) {
        return new CogWheelBlock(false, properties);
    }

    public static CogWheelBlock large(Settings properties) {
        return new CogWheelBlock(true, properties);
    }

    public static boolean isSmallCog(BlockState state) {
        return AllBlocks.COGWHEEL.has(state);
    }

    public static boolean isLargeCog(BlockState state) {
        return AllBlocks.LARGE_COGWHEEL.has(state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return (isLarge ? AllShapes.LARGE_GEAR : AllShapes.SMALL_GEAR).get(state.get(AXIS));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView worldIn, BlockPos pos) {
        for (Direction facing : Iterate.directions) {
            if (facing.getAxis() == state.get(AXIS))
                continue;

            BlockState blockState = worldIn.getBlockState(pos.offset(facing));
            if (blockState.contains(AXIS) && facing.getAxis() == blockState.get(AXIS))
            	continue;
            
            if (isLargeCog(blockState) || isLarge && isSmallCog(blockState))
                return false;
        }
        return true;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        BlockPos placedOnPos = context.getBlockPos().offset(context.getSide().getOpposite());
        World world = context.getWorld();
        BlockState placedAgainst = world.getBlockState(placedOnPos);
        Block block = placedAgainst.getBlock();

        if (context.getPlayer() != null && context.getPlayer().isSneaking())
			return this.getDefaultState().with(AXIS, context.getSide().getAxis());
        
        BlockState stateBelow = world.getBlockState(context.getBlockPos()
                .down());
        FluidState FluidState = context.getWorld().getFluidState(context.getBlockPos());
        if (AllBlocks.ROTATION_SPEED_CONTROLLER.has(stateBelow) && isLarge) {
            return this.getDefaultState()
                    .with(Properties.WATERLOGGED, FluidState.getFluid() == Fluids.WATER)
                    .with(AXIS, stateBelow.get(SpeedControllerBlock.HORIZONTAL_AXIS) == Axis.X ? Axis.Z : Axis.X);
        }

        if (!(block instanceof IRotate)
                || !(((IRotate) block).hasIntegratedCogwheel(world, placedOnPos, placedAgainst))) {
            Axis preferredAxis = getPreferredAxis(context);
            if (preferredAxis != null)
                return this.getDefaultState()
                        .with(AXIS, preferredAxis)
                        .with(Properties.WATERLOGGED, FluidState.getFluid() == Fluids.WATER);
            return this.getDefaultState()
                    .with(AXIS, context.getSide().getAxis())
                    .with(Properties.WATERLOGGED, FluidState.getFluid() == Fluids.WATER);
        }

        return getDefaultState().with(AXIS, ((IRotate) block).getRotationAxis(placedAgainst));
    }

    @Override
    public float getParticleTargetRadius() {
        return isLarge ? 1.125f : .65f;
    }

    @Override
    public float getParticleInitialRadius() {
        return isLarge ? 1f : .75f;
    }

    public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    // IRotate

    @Override
    public boolean hasIntegratedCogwheel(WorldView world, BlockPos pos, BlockState state) {
        return !isLarge;
    }
}
