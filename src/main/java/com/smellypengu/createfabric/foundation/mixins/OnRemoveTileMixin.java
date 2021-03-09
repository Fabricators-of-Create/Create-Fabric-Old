package com.smellypengu.createfabric.foundation.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.CLIENT)
@Mixin(World.class)
public class OnRemoveTileMixin {

    @Shadow @Final public boolean isClient;

    /**
     * JUSTIFICATION: This method is called whenever a tile entity is removed due
     * to a change in block state, even on the client. By hooking into this method,
     * we gain easy access to the information while having no impact on performance.
     */
    /*@Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;removeBlockEntity(Lnet/minecraft/util/math/BlockPos;)V"), method = "removeBlockEntity", locals = LocalCapture.CAPTURE_FAILHARD)
    private void onRemoveTile(BlockPos pos, CallbackInfo ci, BlockEntity te) {
        if (isClient) CreateClient.kineticRenderer.remove(te);
    }*/
}
