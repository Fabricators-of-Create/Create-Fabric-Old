package com.smellypengu.createfabric.foundation.render.backend.light;

import com.smellypengu.createfabric.content.contraptions.components.structureMovement.Contraption;
import com.smellypengu.createfabric.content.contraptions.components.structureMovement.ContraptionLighter;

// so other contraptions don't crash before they have a lighter
public class EmptyLighter extends ContraptionLighter<Contraption> {
    public EmptyLighter(Contraption contraption) {
        super(contraption);
    }

    @Override
    public GridAlignedBB getContraptionBounds() {
        return new GridAlignedBB(0, 0, 0, 1, 1, 1);
    }
}
