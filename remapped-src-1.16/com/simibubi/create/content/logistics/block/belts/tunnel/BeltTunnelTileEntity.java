package com.simibubi.create.content.logistics.block.belts.tunnel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.block.belts.tunnel.BeltTunnelBlock.Shape;
import com.simibubi.create.content.logistics.block.funnel.BeltFunnelBlock;
import com.simibubi.create.foundation.gui.widgets.InterpolatedChasingValue;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BeltTunnelTileEntity extends SmartTileEntity {

	public HashMap<Direction, InterpolatedChasingValue> flaps;
	public Set<Direction> sides;
	
	protected LazyOptional<IItemHandler> cap = LazyOptional.empty();
	protected List<Pair<Direction, Boolean>> flapsToSend;

	public BeltTunnelTileEntity(BlockEntityType<? extends BeltTunnelTileEntity> type) {
		super(type);
		flaps = new HashMap<>();
		sides = new HashSet<>();
		flapsToSend = new LinkedList<>();
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		cap.invalidate();
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		ListTag flapsNBT = new ListTag();
		for (Direction direction : flaps.keySet())
			flapsNBT.add(IntTag.of(direction.getId()));
		compound.put("Flaps", flapsNBT);
		
		ListTag sidesNBT = new ListTag();
		for (Direction direction : sides)
			sidesNBT.add(IntTag.of(direction.getId()));
		compound.put("Sides", sidesNBT);
		
		super.write(compound, clientPacket);

		if (!clientPacket)
			return;

		flapsNBT = new ListTag();
		if (!flapsToSend.isEmpty()) {
			for (Pair<Direction, Boolean> pair : flapsToSend) {
				CompoundTag flap = new CompoundTag();
				flap.putInt("Flap", pair.getKey()
					.getId());
				flap.putBoolean("FlapInward", pair.getValue());
				flapsNBT.add(flap);
			}
			compound.put("TriggerFlaps", flapsNBT);
			flapsToSend.clear();
		}
	}

	@Override
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		Set<Direction> newFlaps = new HashSet<>(6);
		ListTag flapsNBT = compound.getList("Flaps", NBT.TAG_INT);
		for (Tag inbt : flapsNBT)
			if (inbt instanceof IntTag)
				newFlaps.add(Direction.byId(((IntTag) inbt).getInt()));
		
		sides.clear();
		ListTag sidesNBT = compound.getList("Sides", NBT.TAG_INT);
		for (Tag inbt : sidesNBT)
			if (inbt instanceof IntTag)
				sides.add(Direction.byId(((IntTag) inbt).getInt()));

		for (Direction d : Iterate.directions)
			if (!newFlaps.contains(d))
				flaps.remove(d);
			else if (!flaps.containsKey(d))
				flaps.put(d, new InterpolatedChasingValue().start(.25f)
					.target(0)
					.withSpeed(.05f));
		
		// Backwards compat
		if (!compound.contains("Sides") && compound.contains("Flaps"))
			sides.addAll(flaps.keySet());

		super.fromTag(state, compound, clientPacket);

		if (!clientPacket)
			return;
		if (!compound.contains("TriggerFlaps"))
			return;
		flapsNBT = compound.getList("TriggerFlaps", NBT.TAG_COMPOUND);
		for (Tag inbt : flapsNBT) {
			CompoundTag flap = (CompoundTag) inbt;
			Direction side = Direction.byId(flap.getInt("Flap"));
			flap(side, flap.getBoolean("FlapInward"));
		}
	}

	public void updateTunnelConnections() {
		flaps.clear();
		sides.clear();
		BlockState tunnelState = getCachedState();
		for (Direction direction : Iterate.horizontalDirections) {
			if (direction.getAxis() != tunnelState.get(Properties.HORIZONTAL_AXIS)) {
				boolean positive =
					direction.getDirection() == AxisDirection.POSITIVE ^ direction.getAxis() == Axis.Z;
				Shape shape = tunnelState.get(BeltTunnelBlock.SHAPE);
				if (BeltTunnelBlock.isStraight(tunnelState))
					continue;
				if (positive && shape == Shape.T_LEFT)
					continue;
				if (!positive && shape == Shape.T_RIGHT)
					continue;
			}
			
			sides.add(direction);
			
			// Flap might be occluded
			BlockState nextState = world.getBlockState(pos.offset(direction));
			if (nextState.getBlock() instanceof BeltTunnelBlock)
				continue;
			if (nextState.getBlock() instanceof BeltFunnelBlock)
				if (nextState.get(BeltFunnelBlock.SHAPE) == BeltFunnelBlock.Shape.EXTENDED
					&& nextState.get(BeltFunnelBlock.FACING) == direction.getOpposite())
					continue;

			flaps.put(direction, new InterpolatedChasingValue().start(.25f)
				.target(0)
				.withSpeed(.05f));
		}
		sendData();
	}

	public void flap(Direction side, boolean inward) {
		if (world.isClient) {
			if (flaps.containsKey(side))
				flaps.get(side)
					.set(inward ? -1 : 1);
			return;
		}

		flapsToSend.add(Pair.of(side, inward));
	}

	@Override
	public void initialize() {
		super.initialize();
//		updateTunnelConnections();
	}

	@Override
	public void tick() {
		super.tick();
		if (!world.isClient) {
			if (!flapsToSend.isEmpty())
				sendData();
			return;
		}
		flaps.forEach((d, value) -> value.tick());
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
		if (capability != CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return super.getCapability(capability, side);

		if (!this.cap.isPresent()) {
			if (AllBlocks.BELT.has(world.getBlockState(pos.down()))) {
				BlockEntity teBelow = world.getBlockEntity(pos.down());
				if (teBelow != null) {
					T capBelow = teBelow.getCapability(capability, Direction.UP)
						.orElse(null);
					if (capBelow != null) {
						cap = LazyOptional.of(() -> capBelow)
							.cast();
					}
				}
			}
		}
		return this.cap.cast();
	}

}
