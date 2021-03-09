package com.smellypengu.createfabric.content.contraptions.components.motor;

import com.smellypengu.createfabric.AllBlockEntities;
import com.smellypengu.createfabric.content.contraptions.base.DirectionalKineticBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

public class CreativeMotorBlock extends DirectionalKineticBlock {

    public CreativeMotorBlock(Settings properties) {
        super(properties);
    }

    /**
     * @Override public VoxelShape getShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
     * return AllShapes.MOTOR_BLOCK.get(state.get(FACING));
     * }
     */

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return AllBlockEntities.MOTOR.instantiate(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer()
                .isSneaking()) || preferred == null)
            return super.getPlacementState(context);
        return getDefaultState().with(FACING, preferred);
    }

    // IRotate:

    @Override
    public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face) {
        return face == state.get(FACING);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.get(FACING)
                .getAxis();
    }

    @Override
    public boolean hideStressImpact() {
        return true;
    }
}
