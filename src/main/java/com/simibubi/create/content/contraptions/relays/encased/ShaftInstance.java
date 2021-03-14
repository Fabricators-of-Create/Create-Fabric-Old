package com.simibubi.create.content.contraptions.relays.encased;

import com.simibubi.create.content.contraptions.base.KineticBlockEntity;
import com.simibubi.create.content.contraptions.base.SingleRotatingInstance;
import com.simibubi.create.foundation.render.backend.instancing.InstancedBlockRenderer;
import com.simibubi.create.foundation.render.backend.instancing.InstancedTileRenderRegistry;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;

public class ShaftInstance extends SingleRotatingInstance {

	public ShaftInstance(InstancedBlockRenderer dispatcher, KineticBlockEntity tile) {
		super(dispatcher, tile);
	}

	public static void register(BlockEntityType<? extends KineticBlockEntity> type) {
		/**DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->*/
		InstancedTileRenderRegistry.instance.register(type, ShaftInstance::new);
	}

	@Override
	protected BlockState getRenderedBlockState() {
		return shaft(getRotationAxis());
	}

}
