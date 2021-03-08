package com.simibubi.create.foundation.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.foundation.render.backend.OptifineHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoOptionsScreen;

@Mixin(MinecraftClient.class)
public class ShaderCloseMixin {

    @Shadow @Nullable public Screen currentScreen;

    @Inject(at = @At("HEAD"), method = "openScreen")
    private void whenScreenChanges(Screen screen, CallbackInfo info) {
        if (OptifineHandler.optifineInstalled() && screen instanceof VideoOptionsScreen) {
            Screen old = this.currentScreen;
            if (old != null && old.getClass().getName().startsWith(OptifineHandler.SHADER_PACKAGE)) {
                OptifineHandler.refresh();
            }
        }
    }
}
