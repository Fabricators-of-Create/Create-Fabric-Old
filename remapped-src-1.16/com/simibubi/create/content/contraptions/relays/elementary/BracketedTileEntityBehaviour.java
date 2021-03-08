package com.simibubi.create.content.contraptions.relays.elementary;

import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.advancement.ITriggerable;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class BracketedTileEntityBehaviour extends TileEntityBehaviour {

	public static BehaviourType<BracketedTileEntityBehaviour> TYPE = new BehaviourType<>();

	private Optional<BlockState> bracket;
	private boolean reRender;

	private Predicate<BlockState> pred;
	private Function<BlockState, ITriggerable> trigger;

	public BracketedTileEntityBehaviour(SmartTileEntity te) {
		this(te, state -> true);
	}

	public BracketedTileEntityBehaviour(SmartTileEntity te, Predicate<BlockState> pred) {
		super(te);
		this.pred = pred;
		bracket = Optional.empty();
	}
	
	public BracketedTileEntityBehaviour withTrigger(Function<BlockState, ITriggerable> trigger) {
		this.trigger = trigger;
		return this;
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	public void applyBracket(BlockState state) {
		this.bracket = Optional.of(state);
		reRender = true;
		tileEntity.notifyUpdate();
	}
	
	public void triggerAdvancements(World world, PlayerEntity player, BlockState state) {
		if (trigger == null)
			return;
		AllTriggers.triggerFor(trigger.apply(state), player);
	}

	public void removeBracket(boolean inOnReplacedContext) {
		World world = getWorld();
		if (!world.isClient)
			world.syncWorldEvent(2001, getPos(), Block.getRawIdFromState(getBracket()));
		this.bracket = Optional.empty();
		reRender = true;
		if (inOnReplacedContext)
			tileEntity.sendData();
		else
			tileEntity.notifyUpdate();
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
			NBTHelper.putMarker(nbt, "Redraw");
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
			getWorld().updateListeners(getPos(), tileEntity.getCachedState(), tileEntity.getCachedState(), 16);
		super.read(nbt, clientPacket);
	}

	public boolean canHaveBracket() {
		return pred.test(tileEntity.getCachedState());
	}

}
