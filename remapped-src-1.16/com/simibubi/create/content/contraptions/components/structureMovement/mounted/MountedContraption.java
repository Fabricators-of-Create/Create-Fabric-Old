package com.simibubi.create.content.contraptions.components.structureMovement.mounted;

import static com.simibubi.create.content.contraptions.components.structureMovement.mounted.CartAssemblerBlock.RAIL_SHAPE;

import java.util.Queue;

import org.apache.commons.lang3.tuple.Pair;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionLighter;
import com.simibubi.create.content.contraptions.components.structureMovement.ContraptionType;
import com.simibubi.create.content.contraptions.components.structureMovement.NonStationaryLighter;
import com.simibubi.create.content.contraptions.components.structureMovement.mounted.CartAssemblerTileEntity.CartMovementMode;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;

public class MountedContraption extends Contraption {

	public CartMovementMode rotationMode;
	public AbstractMinecartEntity connectedCart;

	public MountedContraption() {
		this(CartMovementMode.ROTATE);
	}

	public MountedContraption(CartMovementMode mode) {
		rotationMode = mode;
	}

	@Override
	protected ContraptionType getType() {
		return ContraptionType.MOUNTED;
	}
	
	@Override
	public boolean assemble(World world, BlockPos pos) throws AssemblyException {
		BlockState state = world.getBlockState(pos);
		if (!BlockHelper.hasBlockStateProperty(state, RAIL_SHAPE))
			return false;
		if (!searchMovedStructure(world, pos, null))
			return false;
		
		Axis axis = state.get(RAIL_SHAPE) == RailShape.EAST_WEST ? Axis.X : Axis.Z;
		addBlock(pos, Pair.of(new StructureBlockInfo(pos, AllBlocks.MINECART_ANCHOR.getDefaultState()
			.with(Properties.HORIZONTAL_AXIS, axis), null), null));
		
		if (blocks.size() == 1)
			return false;
		
		return true;
	}
	
	@Override
	protected boolean addToInitialFrontier(World world, BlockPos pos, Direction direction, Queue<BlockPos> frontier) {
		frontier.clear();
		frontier.add(pos.up());
		return true;
	}

	@Override
	protected Pair<StructureBlockInfo, BlockEntity> capture(World world, BlockPos pos) {
		Pair<StructureBlockInfo, BlockEntity> pair = super.capture(world, pos);
		StructureBlockInfo capture = pair.getKey();
		if (!AllBlocks.CART_ASSEMBLER.has(capture.state))
			return pair;

		Pair<StructureBlockInfo, BlockEntity> anchorSwap =
			Pair.of(new StructureBlockInfo(pos, CartAssemblerBlock.createAnchor(capture.state), null), pair.getValue());
		if (pos.equals(anchor) || connectedCart != null)
			return anchorSwap;

		for (Axis axis : Iterate.axes) {
			if (axis.isVertical() || !VecHelper.onSameAxis(anchor, pos, axis))
				continue;
			for (AbstractMinecartEntity abstractMinecartEntity : world
				.getNonSpectatingEntities(AbstractMinecartEntity.class, new Box(pos))) {
				if (!CartAssemblerBlock.canAssembleTo(abstractMinecartEntity))
					break;
				connectedCart = abstractMinecartEntity;
				connectedCart.updatePosition(pos.getX() + .5, pos.getY(), pos.getZ() + .5f);
			}
		}

		return anchorSwap;
	}

	@Override
	protected boolean movementAllowed(BlockState state, World world, BlockPos pos) {
		if (!pos.equals(anchor) && AllBlocks.CART_ASSEMBLER.has(state))
			return testSecondaryCartAssembler(world, state, pos);
		return super.movementAllowed(state, world, pos);
	}

	protected boolean testSecondaryCartAssembler(World world, BlockState state, BlockPos pos) {
		for (Axis axis : Iterate.axes) {
			if (axis.isVertical() || !VecHelper.onSameAxis(anchor, pos, axis))
				continue;
			for (AbstractMinecartEntity abstractMinecartEntity : world
				.getNonSpectatingEntities(AbstractMinecartEntity.class, new Box(pos))) {
				if (!CartAssemblerBlock.canAssembleTo(abstractMinecartEntity))
					break;
				return true;
			}
		}
		return false;
	}

	@Override
	public CompoundTag writeNBT(boolean spawnPacket) {
		CompoundTag tag = super.writeNBT(spawnPacket);
		NBTHelper.writeEnum(tag, "RotationMode", rotationMode);
		return tag;
	}

	@Override
	public void readNBT(World world, CompoundTag nbt, boolean spawnData) {
		rotationMode = NBTHelper.readEnum(nbt, "RotationMode", CartMovementMode.class);
		super.readNBT(world, nbt, spawnData);
	}

	@Override
	protected boolean customBlockPlacement(WorldAccess world, BlockPos pos, BlockState state) {
		return AllBlocks.MINECART_ANCHOR.has(state);
	}

	@Override
	protected boolean customBlockRemoval(WorldAccess world, BlockPos pos, BlockState state) {
		return AllBlocks.MINECART_ANCHOR.has(state);
	}
	
	@Override
	public boolean canBeStabilized(Direction facing, BlockPos localPos) {
		return true;
	}
	
	@Override
	public void addExtraInventories(Entity cart) {
		if (!(cart instanceof Inventory))
			return;
		IItemHandlerModifiable handlerFromInv = new InvWrapper((Inventory) cart);
		inventory = new CombinedInvWrapper(handlerFromInv, inventory);
	}

	@Override
	public ContraptionLighter<?> makeLighter() {
		return new NonStationaryLighter<>(this);
	}
}
