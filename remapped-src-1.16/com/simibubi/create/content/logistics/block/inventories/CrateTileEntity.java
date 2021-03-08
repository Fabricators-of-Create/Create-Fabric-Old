package com.simibubi.create.content.logistics.block.inventories;

import java.util.List;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.AxisDirection;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;

public abstract class CrateTileEntity extends SmartTileEntity {

	public CrateTileEntity(BlockEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {}

	public boolean isDoubleCrate() {
		return getCachedState().get(AdjustableCrateBlock.DOUBLE);
	}

	public boolean isSecondaryCrate() {
		if (!hasWorld())
			return false;
		if (!(getCachedState().getBlock() instanceof CrateBlock))
			return false;
		return isDoubleCrate() && getFacing().getDirection() == AxisDirection.NEGATIVE;
	}
	
	public Direction getFacing() {
		return getCachedState().get(AdjustableCrateBlock.FACING);
	}

}
