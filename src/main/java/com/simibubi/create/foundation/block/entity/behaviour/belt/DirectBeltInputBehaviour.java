package com.simibubi.create.foundation.block.entity.behaviour.belt;

import java.util.function.Supplier;

import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.block.entity.behaviour.BehaviourType;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

/**
 * Behaviour for TileEntities to which belts can transfer items directly in a
 * backup-friendly manner. Example uses: Basin, Saw, Depot
 */
public class DirectBeltInputBehaviour extends BlockEntityBehaviour {

	public static BehaviourType<DirectBeltInputBehaviour> TYPE = new BehaviourType<>();

	private InsertionCallback tryInsert;
	private AvailabilityPredicate canInsert;
	private Supplier<Boolean> supportsBeltFunnels;

	public DirectBeltInputBehaviour(SmartBlockEntity te) {
		super(te);
		/**tryInsert = this::defaultInsertionCallback;*/
		canInsert = d -> true;
		supportsBeltFunnels = () -> false;
	}

	public DirectBeltInputBehaviour allowingBeltFunnelsWhen(Supplier<Boolean> pred) {
		supportsBeltFunnels = pred;
		return this;
	}

	public DirectBeltInputBehaviour allowingBeltFunnels() {
		supportsBeltFunnels = () -> true;
		return this;
	}

	public DirectBeltInputBehaviour onlyInsertWhen(AvailabilityPredicate pred) {
		canInsert = pred;
		return this;
	}

	public DirectBeltInputBehaviour setInsertionHandler(InsertionCallback callback) {
		tryInsert = callback;
		return this;
	}

	/**
	 * private ItemStack defaultInsertionCallback(TransportedItemStack inserted, Direction side, boolean simulate) {
	 * LazyOptional<IItemHandler> lazy = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
	 * if (!lazy.isPresent())
	 * return inserted.stack;
	 * return ItemHandlerHelper.insertItemStacked(lazy.orElse(null), inserted.stack.copy(), simulate);
	 * }
	 */

	public boolean canInsertFromSide(Direction side) {
		return canInsert.test(side);
	}

	public ItemStack handleInsertion(ItemStack stack, Direction side, boolean simulate) {
		return handleInsertion(new TransportedItemStack(stack), side, simulate);
	}

	public ItemStack handleInsertion(TransportedItemStack stack, Direction side, boolean simulate) {
		return tryInsert.apply(stack, side, simulate);
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	/**
	 * public ItemStack tryExportingToBeltFunnel(ItemStack stack, @Nullable Direction side) {
	 * BlockPos funnelPos = tileEntity.getPos()
	 * .up();
	 * World world = getWorld();
	 * BlockState funnelState = world.getBlockState(funnelPos);
	 * if (!(funnelState.getBlock() instanceof BeltFunnelBlock))
	 * return stack;
	 * if (funnelState.get(BeltFunnelBlock.SHAPE) != Shape.PULLING)
	 * return stack;
	 * if (side != null && FunnelBlock.getFunnelFacing(funnelState) != side)
	 * return stack;
	 * BlockEntity te = world.getBlockEntity(funnelPos);
	 * if (!(te instanceof FunnelTileEntity))
	 * return stack;
	 * ItemStack insert = FunnelBlock.tryInsert(world, funnelPos, stack, false);
	 * if (insert.getCount() != stack.getCount())
	 * ((FunnelTileEntity) te).flap(true);
	 * return insert;
	 * }
	 */

	public boolean canSupportBeltFunnels() {
		return supportsBeltFunnels.get();
	}

	@FunctionalInterface
	public interface InsertionCallback {
		ItemStack apply(TransportedItemStack stack, Direction side, boolean simulate);
	}

	@FunctionalInterface
	public interface AvailabilityPredicate {
		boolean test(Direction side);
	}

}
