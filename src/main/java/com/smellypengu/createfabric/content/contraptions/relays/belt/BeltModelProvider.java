package com.smellypengu.createfabric.content.contraptions.relays.belt;

import com.smellypengu.createfabric.Create;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

public class BeltModelProvider implements ModelResourceProvider {

    public static final Identifier BELT = new Identifier(Create.ID, "block/belt");
    @Override
    public UnbakedModel loadModelResource(Identifier identifier, ModelProviderContext modelProviderContext) throws ModelProviderException {
        if(identifier.equals(BELT)) {
            System.out.println("test - don't know why this doesn't work");
            return new BeltModel();
        } else {
            return null;
        }
    }
}
