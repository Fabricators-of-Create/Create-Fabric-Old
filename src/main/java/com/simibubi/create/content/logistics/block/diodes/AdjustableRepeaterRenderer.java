package com.simibubi.create.content.logistics.block.diodes;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.foundation.block.entity.render.ColoredOverlayBlockEntityRenderer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.ColorHelper;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;

public class AdjustableRepeaterRenderer extends ColoredOverlayBlockEntityRenderer<AdjustableRepeaterBlockEntity> {

	public AdjustableRepeaterRenderer(BlockEntityRenderDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	protected int getColor(AdjustableRepeaterBlockEntity te, float partialTicks) {
		return ColorHelper.mixColors(0x2C0300, 0xCD0000, te.state / (float) te.maxState.getValue());
	}

	@Override
	protected SuperByteBuffer getOverlayBuffer(AdjustableRepeaterBlockEntity te) {
		return AllBlockPartials.FLEXPEATER_INDICATOR.renderOn(te.getCachedState());
	}

}
