package com.smellypengu.createfabric.content.contraptions.relays.encased;

import com.smellypengu.createfabric.content.contraptions.base.KineticBlockEntity;
import com.smellypengu.createfabric.content.contraptions.base.SingleRotatingInstance;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedTileRenderRegistry;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedTileRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;

public class ShaftInstance extends SingleRotatingInstance {

	public static void register(BlockEntityType<? extends KineticBlockEntity> type) {
		/**DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->*/
				InstancedTileRenderRegistry.instance.register(type, ShaftInstance::new);
	}

	public ShaftInstance(InstancedTileRenderer dispatcher, KineticBlockEntity tile) {
		super(dispatcher, tile);
	}

	@Override
	protected BlockState getRenderedBlockState() {
		return shaft(getRotationAxis());
	}

}
