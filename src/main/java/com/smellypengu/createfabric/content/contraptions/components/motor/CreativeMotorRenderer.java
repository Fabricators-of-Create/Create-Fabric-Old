package com.smellypengu.createfabric.content.contraptions.components.motor;

import com.smellypengu.createfabric.AllBlockPartials;
import com.smellypengu.createfabric.content.contraptions.base.KineticBlockEntity;
import com.smellypengu.createfabric.content.contraptions.base.KineticBlockEntityRenderer;
import com.smellypengu.createfabric.foundation.render.SuperByteBuffer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public class CreativeMotorRenderer extends KineticBlockEntityRenderer {

    public CreativeMotorRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(KineticBlockEntity te) {
        return AllBlockPartials.SHAFT_HALF.renderOnDirectionalSouth(te.getCachedState());
    }

}
