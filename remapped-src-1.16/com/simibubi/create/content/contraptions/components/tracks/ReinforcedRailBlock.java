package com.simibubi.create.content.contraptions.components.tracks;

import javax.annotation.Nonnull;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.RailShape;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ReinforcedRailBlock extends AbstractRailBlock {

    public static Property<RailShape> RAIL_SHAPE =
            EnumProperty.of("shape", RailShape.class, RailShape.EAST_WEST, RailShape.NORTH_SOUTH);

    public static Property<Boolean> CONNECTS_N = BooleanProperty.of("connects_n");
    public static Property<Boolean> CONNECTS_S = BooleanProperty.of("connects_s");

    public ReinforcedRailBlock(Settings properties) {
        super(true, properties);
    }

    @Override
    public void addStacksForDisplay(ItemGroup p_149666_1_, DefaultedList<ItemStack> p_149666_2_) {
    	// TODO re-add when finished
    }

    @Nonnull
    @Override
    public Property<RailShape> getShapeProperty() {
        return RAIL_SHAPE;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(RAIL_SHAPE, CONNECTS_N, CONNECTS_S);
        super.appendProperties(builder);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        boolean alongX = context.getPlayerFacing().getAxis() == Axis.X;
        return super.getPlacementState(context).with(RAIL_SHAPE, alongX ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH).with(CONNECTS_N, false).with(CONNECTS_S, false);
    }

    @Override
    public boolean canMakeSlopes(@Nonnull BlockState state, @Nonnull BlockView world, @Nonnull BlockPos pos) {
        return false;
    }

    @Override
    protected void updateBlockState(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block block) {
        super.updateBlockState(state, world, pos, block);
        world.setBlockState(pos, updateBlockState(world, pos, state, true));
    }

    @Override
    @Nonnull
    protected BlockState updateBlockState(@Nonnull World world, BlockPos pos, BlockState state,
                                         boolean p_208489_4_) {

        boolean alongX = state.get(RAIL_SHAPE) == RailShape.EAST_WEST;
        BlockPos sPos = pos.add(alongX ? -1 : 0, 0, alongX ? 0 : 1);
        BlockPos nPos = pos.add(alongX ? 1 : 0, 0, alongX ? 0 : -1);

        return super.updateBlockState(world, pos, state, p_208489_4_).with(CONNECTS_S, world.getBlockState(sPos).getBlock() instanceof ReinforcedRailBlock &&
                (world.getBlockState(sPos).get(RAIL_SHAPE) == state.get(RAIL_SHAPE)))
                .with(CONNECTS_N, world.getBlockState(nPos).getBlock() instanceof ReinforcedRailBlock &&
                        (world.getBlockState(nPos).get(RAIL_SHAPE) == state.get(RAIL_SHAPE)));
    }

    @Override
    @Nonnull
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockView worldIn, @Nonnull BlockPos pos,
                                        ShapeContext context) {    //FIXME
        if (context.getEntity() instanceof AbstractMinecartEntity)
            return VoxelShapes.empty();
        return getOutlineShape(state, worldIn, pos, null);
    }

    @Override
    @Nonnull
    public VoxelShape getOutlineShape(BlockState state, @Nonnull BlockView reader, @Nonnull BlockPos pos, ShapeContext context) {
        boolean alongX = state.get(RAIL_SHAPE) == RailShape.EAST_WEST;
        return VoxelShapes.union(createCuboidShape(0, -2, 0, 16, 2, 16), VoxelShapes.union(createCuboidShape(0, -2, 0, alongX ? 16 : -1, 12, alongX ? -1 : 16), createCuboidShape(alongX ? 0 : 17, -2, alongX ? 17 : 0, 16, 12, 16)));
    }

    @Override
    @Nonnull
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.BLOCK;
    }

    /* FIXME: Same thing as before, does this still matter? If so, what is the new way of doing it?
    @Override
    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }*/

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return !(world.getBlockState(pos.down()).getBlock() instanceof AbstractRailBlock || world.getBlockState(pos.up()).getBlock() instanceof AbstractRailBlock);
    }

    @Override
    public void neighborUpdate(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockPos pos2, boolean p_220069_6_) {
        if (!world.isClient) {
            if ((world.getBlockState(pos.down()).getBlock() instanceof AbstractRailBlock)) {
                if (!p_220069_6_) {
                    dropStacks(state, world, pos);
                }
                world.removeBlock(pos, false);
            } else {
                this.updateBlockState(state, world, pos, block);
            }
        }
    }
}
