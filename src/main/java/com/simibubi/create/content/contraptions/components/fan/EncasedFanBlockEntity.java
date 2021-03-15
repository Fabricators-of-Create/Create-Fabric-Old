package com.simibubi.create.content.contraptions.components.fan;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags.AllBlockTags;
import com.simibubi.create.content.contraptions.base.GeneratingKineticBlockEntity;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.registrate.util.nullness.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNonnullByDefault
public class EncasedFanBlockEntity extends GeneratingKineticBlockEntity implements AirCurrentSource {

	public AirCurrent airCurrent;
	protected int airCurrentUpdateCooldown;
	protected int entitySearchCooldown;
	protected boolean isGenerator;
	protected boolean updateAirFlow;
	protected boolean updateGenerator;

	public EncasedFanBlockEntity() {
		super(AllBlockEntities.ENCASED_FAN);
		isGenerator = false;
		airCurrent = new AirCurrent(this);
		updateAirFlow = true;
		updateGenerator = false;
	}

	@Override
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		super.fromTag(state, compound, clientPacket);
		if (!wasMoved) 
			isGenerator = compound.getBoolean("Generating");
		if (clientPacket)
			airCurrent.rebuild();
	}

	@Override
	public void toTag(CompoundTag compound, boolean clientPacket) {
		compound.putBoolean("Generating", isGenerator);
		super.toTag(compound, clientPacket);
	}

	@Override
	public float calculateAddedStressCapacity() {
		return isGenerator ? super.calculateAddedStressCapacity() : 0;
	}

	@Override
	public float calculateStressApplied() {
		return isGenerator ? 0 : super.calculateStressApplied();
	}

	@Override
	public float getGeneratedSpeed() {
		return isGenerator ? 4 /*AllConfigs.SERVER.kinetics.generatingFanSpeed.get()*/ : 0;
	}

	public void queueGeneratorUpdate() {
		updateGenerator = true;
	}

	public void updateGenerator() {
		BlockState blockState = getCachedState();
		if (!AllBlocks.ENCASED_FAN.getStateManager().getStates().contains(blockState))
			return;
		if (blockState.get(EncasedFanBlock.FACING) != Direction.DOWN)
			return;
		
		boolean shouldGenerate = world.isReceivingRedstonePower(pos) && world.canSetBlock(pos.down()) && blockBelowIsHot();
		if (shouldGenerate == isGenerator)
			return;
		isGenerator = shouldGenerate;
		updateGeneratedRotation();
	}

	public boolean blockBelowIsHot() {
		if (world == null)
			return false;
		BlockState checkState = world.getBlockState(pos.down());

		if (!checkState.getBlock()
			.isIn(AllBlockTags.FAN_HEATERS.tag))
			return false;

		/*if (BlockHelper.hasBlockStateProperty(checkState, BlazeBurnerBlock.HEAT_LEVEL) && !checkState.get(BlazeBurnerBlock.HEAT_LEVEL)
			.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING))
			return false;*/

		if (BlockHelper.hasBlockStateProperty(checkState, Properties.LIT) && !checkState.get(Properties.LIT))
			return false;

		return true;
	}

	@Override
	public AirCurrent getAirCurrent() {
		return airCurrent;
	}

	@Nullable
	@Override
	public World getAirCurrentWorld() {
		return world;
	}

	@Override
	public BlockPos getAirCurrentPos() {
		return pos;
	}

	@Override
	public Direction getAirflowOriginSide() {
		return this.getCachedState()
			.get(EncasedFanBlock.FACING);
	}

	@Override
	public Direction getAirFlowDirection() {
		float speed = getSpeed();
		if (speed == 0)
			return null;
		Direction facing = getCachedState().get(Properties.FACING);
		speed = convertToDirection(speed, facing);
		return speed > 0 ? facing : facing.getOpposite();
	}

	@Override
	public boolean isSourceRemoved() {
		return removed;
	}

	@Override
	public void onSpeedChanged(float prevSpeed) {
		super.onSpeedChanged(prevSpeed);
		updateAirFlow = true;
		updateChute();
	}

	public void updateChute() {
		Direction direction = getCachedState().get(EncasedFanBlock.FACING);
		if (!direction.getAxis()
			.isVertical())
			return;
		BlockEntity poweredChute = world.getBlockEntity(pos.offset(direction));
		/*if (!(poweredChute instanceof ChuteTileEntity))
			return;
		ChuteTileEntity chuteTE = (ChuteTileEntity) poweredChute;
		if (direction == Direction.DOWN)
			chuteTE.updatePull();
		else
			chuteTE.updatePush(1);*/
	}

	public void blockInFrontChanged() {
		updateAirFlow = true;
	}

	@Override
	public void tick() {
		super.tick();

		if (!world.isClient && airCurrentUpdateCooldown-- <= 0) {
			airCurrentUpdateCooldown = 30; //AllConfigs.SERVER.kinetics.fanBlockCheckRate.get();
			updateAirFlow = true;
		}

		if (updateAirFlow) {
			updateAirFlow = false;
			airCurrent.rebuild();
			sendData();
		}
		
		if (updateGenerator) {
			updateGenerator = false;
			updateGenerator();
		}

		if (getSpeed() == 0 || isGenerator)
			return;

		if (entitySearchCooldown-- <= 0) {
			entitySearchCooldown = 5;
			airCurrent.findEntities();
		}

		airCurrent.tick();
	}

}
