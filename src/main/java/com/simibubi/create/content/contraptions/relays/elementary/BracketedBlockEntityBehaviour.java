package com.simibubi.create.content.contraptions.relays.elementary;

import java.util.Optional;
import java.util.function.Predicate;

import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.block.entity.behaviour.BehaviourType;
import com.simibubi.create.foundation.utility.CNBTHelper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.world.World;

// TODO Advancement CHECK s
public class BracketedBlockEntityBehaviour extends BlockEntityBehaviour {

	public static BehaviourType<BracketedBlockEntityBehaviour> TYPE = new BehaviourType<>();
	private final Predicate<BlockState> pred;
	private Optional<BlockState> bracket;
	private boolean reRender;

	/**
	 * private Function<BlockState, ITriggerable> trigger;
	 */

	public BracketedBlockEntityBehaviour(SmartBlockEntity te) {
		this(te, state -> true);
	}

	public BracketedBlockEntityBehaviour(SmartBlockEntity te, Predicate<BlockState> pred) {
		super(te);
		this.pred = pred;
		bracket = Optional.empty();
	}

	/**
	 * public BracketedTileEntityBehaviour withTrigger(Function<BlockState, ITriggerable> trigger) {
	 * this.trigger = trigger;
	 * return this;
	 * }
	 */

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	public void applyBracket(BlockState state) {
		this.bracket = Optional.of(state);
		reRender = true;
		blockEntity.notifyUpdate();
	}

	public void triggerAdvancements(World world, PlayerEntity player, BlockState state) {
		/**if (trigger == null)
		 return;
		 AllTriggers.triggerFor(trigger.apply(state), player);*/
	}

	public void removeBracket(boolean inOnReplacedContext) {
		World world = getWorld();
		if (!world.isClient)
			world.syncWorldEvent(2001, getPos(), Block.getRawIdFromState(getBracket()));
		this.bracket = Optional.empty();
		reRender = true;
		if (inOnReplacedContext)
			blockEntity.sendData();
		else
			blockEntity.notifyUpdate();
	}

	public boolean isBacketPresent() {
		return getBracket() != Blocks.AIR.getDefaultState();
	}

	public BlockState getBracket() {
		return bracket.orElse(Blocks.AIR.getDefaultState());
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		bracket.ifPresent(p -> nbt.put("Bracket", NbtHelper.fromBlockState(p)));
		if (clientPacket && reRender) {
			CNBTHelper.putMarker(nbt, "Redraw");
			reRender = false;
		}
		super.write(nbt, clientPacket);
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		bracket = Optional.empty();
		if (nbt.contains("Bracket"))
			bracket = Optional.of(NbtHelper.toBlockState(nbt.getCompound("Bracket")));
		if (clientPacket && nbt.contains("Redraw"))
			getWorld().updateListeners(getPos(), blockEntity.getCachedState(), blockEntity.getCachedState(), 16);
		super.read(nbt, clientPacket);
	}

	public boolean canHaveBracket() {
		return pred.test(blockEntity.getCachedState());
	}

}
