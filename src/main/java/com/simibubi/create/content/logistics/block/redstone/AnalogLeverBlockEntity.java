package com.simibubi.create.content.logistics.block.redstone;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.content.contraptions.goggles.GoggleInformationProvider;
import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.gui.widgets.InterpolatedChasingValue;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class AnalogLeverBlockEntity extends SmartBlockEntity implements GoggleInformationProvider {

	int state = 0;
	int lastChange;
	InterpolatedChasingValue clientState = new InterpolatedChasingValue().withSpeed(.2f);

	public AnalogLeverBlockEntity() {
		super(AllBlockEntities.ANALOG_LEVER);
	}

	@Override
	public void toTag(CompoundTag compound, boolean clientPacket) {
		compound.putInt("State", state);
		compound.putInt("ChangeTimer", lastChange);
		super.toTag(compound, clientPacket);
	}

	@Override
	protected void fromTag(BlockState blockState, CompoundTag compound, boolean clientPacket) {
		state = compound.getInt("State");
		lastChange = compound.getInt("ChangeTimer");
		clientState.target(state);
		super.fromTag(blockState, compound, clientPacket);
	}

	@Override
	public void tick() {
		super.tick();
		if (lastChange > 0) {
			lastChange--;
			if (lastChange == 0)
				updateOutput();
		}
		if (world.isClient)
			clientState.tick();
	}

	private void updateOutput() {
		AnalogLeverBlock.updateNeighbors(getCachedState(), world, pos);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	public void changeState(boolean back) {
		int prevState = state;
		state += back ? -1 : 1;
		state = MathHelper.clamp(state, 0, 15);
		if (prevState != state)
			lastChange = 15;
		sendData();
	}

	@Override
	public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
		tooltip.add(componentSpacing.copy().append(Lang.translate("tooltip.analogStrength", this.state)));

		return true;
	}

	public int getState() {
		return state;
	}
}
