package com.simibubi.create.foundation.mixin.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.events.custom.ClientWorldEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
@Environment(EnvType.CLIENT)
public class ClientWorldMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(at = @At("TAIL"), method = "<init>()V")
	public void onTailInit(CallbackInfo ci) {
		ClientWorldEvents.LOAD.invoker().onWorldLoad(client, (ClientWorld) (Object) this);
	}
}
