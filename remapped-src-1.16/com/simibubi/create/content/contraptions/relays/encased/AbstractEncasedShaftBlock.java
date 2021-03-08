package com.simibubi.create.content.contraptions.relays.encased;

import javax.annotation.Nullable;

import com.simibubi.create.content.contraptions.base.RotatedPillarKineticBlock;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

@MethodsReturnNonnullByDefault
public abstract class AbstractEncasedShaftBlock extends RotatedPillarKineticBlock {
    public AbstractEncasedShaftBlock(Settings properties) {
        super(properties);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, WorldView world, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public PistonBehavior getPistonBehavior(@Nullable BlockState state) {
        return PistonBehavior.NORMAL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        if (context.getPlayer() != null && context.getPlayer()
                .isSneaking())
            return super.getPlacementState(context);
        Direction.Axis preferredAxis = getPreferredAxis(context);
        return this.getDefaultState()
                .with(AXIS, preferredAxis == null ? context.getPlayerLookDirection()
                        .getAxis() : preferredAxis);
    }

    @Override
    public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.get(AXIS);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.get(AXIS);
    }
}
