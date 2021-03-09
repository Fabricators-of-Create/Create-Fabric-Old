package com.smellypengu.createfabric.content.contraptions.base;

import com.smellypengu.createfabric.content.contraptions.components.actors.ContraptionActorData;
import com.smellypengu.createfabric.content.contraptions.relays.belt.BeltData;
import com.smellypengu.createfabric.foundation.render.backend.instancing.InstancedModel;
import com.smellypengu.createfabric.foundation.render.backend.instancing.MaterialType;

public class KineticRenderMaterials {
    public static final MaterialType<InstancedModel<RotatingData>> ROTATING = new MaterialType<>();
    public static final MaterialType<InstancedModel<BeltData>> BELTS = new MaterialType<>();

    public static final MaterialType<InstancedModel<ContraptionActorData>> ACTORS = new MaterialType<>();
}
