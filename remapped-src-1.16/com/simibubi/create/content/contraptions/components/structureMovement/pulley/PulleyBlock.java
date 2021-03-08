package com.simibubi.create.content.contraptions.components.structureMovement.pulley;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.HorizontalAxisKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.utility.BlockHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PulleyBlock extends HorizontalAxisKineticBlock implements ITE<PulleyTileEntity> {

    public static EnumProperty<Axis> HORIZONTAL_AXIS = Properties.HORIZONTAL_AXIS;

    public PulleyBlock(Settings properties) {
        super(properties);
    }

    private static void onRopeBroken(World world, BlockPos pulleyPos) {
        BlockEntity te = world.getBlockEntity(pulleyPos);
        if (!(te instanceof PulleyTileEntity))
            return;
        PulleyTileEntity pulley = (PulleyTileEntity) te;
        pulley.offset = 0;
        pulley.sendData();
    }

    @Override
    public BlockEntity createTileEntity(BlockState state, BlockView world) {
        return AllTileEntities.ROPE_PULLEY.create();
    }

    @Override
    public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (!worldIn.isClient) {
                BlockState below = worldIn.getBlockState(pos.down());
                if (below.getBlock() instanceof RopeBlockBase)
                    worldIn.breakBlock(pos.down(), true);
            }
            if (state.hasTileEntity())
                worldIn.removeBlockEntity(pos);
        }
    }

    public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
                                  BlockHitResult hit) {
        if (!player.canModifyBlocks())
            return ActionResult.PASS;
        if (player.isSneaking())
            return ActionResult.PASS;
        if (player.getStackInHand(handIn)
                .isEmpty()) {
            withTileEntityDo(worldIn, pos, te -> te.assembleNextTick = true);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
        return AllShapes.PULLEY.get(state.get(HORIZONTAL_AXIS));
    }

    @Override
    public Class<PulleyTileEntity> getTileEntityClass() {
        return PulleyTileEntity.class;
    }

    private static class RopeBlockBase extends Block implements Waterloggable {

        public RopeBlockBase(Settings properties) {
            super(properties);
            setDefaultState(super.getDefaultState().with(Properties.WATERLOGGED, false));
        }

        @Override
        public PistonBehavior getPistonBehavior(BlockState state) {
            return PistonBehavior.BLOCK;
        }

        @Override
        public ItemStack getPickBlock(BlockState state, HitResult target, BlockView world, BlockPos pos,
                                      PlayerEntity player) {
            return AllBlocks.ROPE_PULLEY.asStack();
        }

        @Override
        public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
            if (!isMoving && (!BlockHelper.hasBlockStateProperty(state, Properties.WATERLOGGED) || !BlockHelper.hasBlockStateProperty(newState, Properties.WATERLOGGED) || state.get(Properties.WATERLOGGED) == newState.get(Properties.WATERLOGGED))) {
                onRopeBroken(worldIn, pos.up());
                if (!worldIn.isClient) {
                    BlockState above = worldIn.getBlockState(pos.up());
                    BlockState below = worldIn.getBlockState(pos.down());
                    if (above.getBlock() instanceof RopeBlockBase)
                        worldIn.breakBlock(pos.up(), true);
                    if (below.getBlock() instanceof RopeBlockBase)
                        worldIn.breakBlock(pos.down(), true);
                }
            }
            if (state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
                worldIn.removeBlockEntity(pos);
            }
        }


        @Override
        public FluidState getFluidState(BlockState state) {
            return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : Fluids.EMPTY.getDefaultState();
        }

        @Override
        protected void appendProperties(Builder<Block, BlockState> builder) {
            builder.add(Properties.WATERLOGGED);
            super.appendProperties(builder);
        }

        @Override
        public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbourState,
                                              WorldAccess world, BlockPos pos, BlockPos neighbourPos) {
            if (state.get(Properties.WATERLOGGED)) {
                world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            }
            return state;
        }

        @Override
        public BlockState getPlacementState(ItemPlacementContext context) {
            FluidState FluidState = context.getWorld().getFluidState(context.getBlockPos());
            return super.getPlacementState(context).with(Properties.WATERLOGGED, Boolean.valueOf(FluidState.getFluid() == Fluids.WATER));
        }

    }

    public static class MagnetBlock extends RopeBlockBase {

        public MagnetBlock(Settings properties) {
            super(properties);
        }

        @Override
        public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
            return AllShapes.PULLEY_MAGNET;
        }

    }

    public static class RopeBlock extends RopeBlockBase {

        public RopeBlock(Settings properties) {
            super(properties);
        }

        @Override
        public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
            return AllShapes.FOUR_VOXEL_POLE.get(Direction.UP);
        }
    }

}
