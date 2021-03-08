package com.smellypengu.createfabric.content.contraptions.components.motor;

import com.smellypengu.createfabric.AllBlocks;
import com.smellypengu.createfabric.AllTileEntities;
import com.smellypengu.createfabric.content.contraptions.base.GeneratingKineticTileEntity;
import com.smellypengu.createfabric.foundation.tileEntity.TileEntityBehaviour;
import com.smellypengu.createfabric.foundation.tileEntity.behaviour.CenteredSideValueBoxTransform;
import com.smellypengu.createfabric.foundation.tileEntity.behaviour.scrollvalue.ScrollValueBehaviour;
import com.smellypengu.createfabric.foundation.utility.Lang;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class CreativeMotorTileEntity extends GeneratingKineticTileEntity {

	public static final int DEFAULT_SPEED = 16;
	protected ScrollValueBehaviour generatedSpeed;

	public CreativeMotorTileEntity(BlockPos pos, BlockState state) {
		super(AllTileEntities.MOTOR, pos, state);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		Integer max = 256; //AllConfigs.SERVER.kinetics.maxMotorSpeed.get(); TODO CONFIG maxMotorSpeed

		CenteredSideValueBoxTransform slot = new CenteredSideValueBoxTransform(
			(motor, side) -> motor.get(CreativeMotorBlock.FACING) == side.getOpposite());

		generatedSpeed = new ScrollValueBehaviour(Lang.translate("generic.speed"), this, slot);
		generatedSpeed.between(-max, max);
		generatedSpeed.value = DEFAULT_SPEED;
		generatedSpeed.scrollableValue = DEFAULT_SPEED;
		generatedSpeed.withUnit(i -> Lang.translate("generic.unit.rpm"));
		generatedSpeed.withCallback(i -> this.updateGeneratedRotation());
		generatedSpeed.withStepFunction(CreativeMotorTileEntity::step);
		behaviours.add(generatedSpeed);
	}

	@Override
	public void initialize() {
		super.initialize();
		if (!hasSource() || getGeneratedSpeed() > getTheoreticalSpeed())
			updateGeneratedRotation();
	}

	@Override
	public float getGeneratedSpeed() {
		if (!AllBlocks.CREATIVE_MOTOR.getStateManager().getStates().contains(getCachedState()))
			return 0;
		return convertToDirection(generatedSpeed.getValue(), getCachedState().get(CreativeMotorBlock.FACING));
	}

	public static int step(ScrollValueBehaviour.StepContext context) {
		int current = context.currentValue;
		int step = 1;

		if (!context.shift) {
			int magnitude = Math.abs(current) - (context.forward == current > 0 ? 0 : 1);

			if (magnitude >= 4)
				step *= 4;
			if (magnitude >= 32)
				step *= 4;
			if (magnitude >= 128)
				step *= 4;
		}

		return (int) (current + (context.forward ? step : -step) == 0 ? step + 1 : step);
	}

}
