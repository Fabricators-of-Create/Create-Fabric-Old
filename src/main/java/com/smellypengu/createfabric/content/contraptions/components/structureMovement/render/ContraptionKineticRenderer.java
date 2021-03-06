package com.smellypengu.createfabric.content.contraptions.components.structureMovement.render;

import com.smellypengu.createfabric.content.contraptions.base.KineticRenderMaterials;
import com.smellypengu.createfabric.content.contraptions.base.RotatingInstancedModel;
import com.smellypengu.createfabric.content.contraptions.components.actors.RotatingActorModel;
import com.smellypengu.createfabric.content.contraptions.relays.belt.BeltInstancedModel;
import com.smellypengu.createfabric.foundation.render.AllProgramSpecs;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedTileRenderer;
import com.smellypengu.createfabric.foundation.render.backend.instancing.RenderMaterial;
import net.minecraft.util.math.BlockPos;

public class ContraptionKineticRenderer extends InstancedTileRenderer<ContraptionProgram> {

    @Override
    public void registerMaterials() {
        materials.put(KineticRenderMaterials.BELTS, new RenderMaterial<>(this, AllProgramSpecs.CONTRAPTION_BELT, BeltInstancedModel::new));
        materials.put(KineticRenderMaterials.ROTATING, new RenderMaterial<>(this, AllProgramSpecs.CONTRAPTION_ROTATING, RotatingInstancedModel::new));
        materials.put(KineticRenderMaterials.ACTORS, new RenderMaterial<>(this, AllProgramSpecs.CONTRAPTION_ACTOR, RotatingActorModel::new));
    }

    @Override
    public BlockPos getOriginCoordinate() {
        return (BlockPos) BlockPos.ZERO;
    }
}

