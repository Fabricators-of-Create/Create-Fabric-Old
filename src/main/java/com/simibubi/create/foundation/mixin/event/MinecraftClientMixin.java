package com.simibubi.create.foundation.mixin.event;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.simibubi.create.events.custom.ClientWorldEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	private ClientWorld world;

	@Inject(at = @At("HEAD"), method = "joinWorld(Lnet/minecraft/client/world/ClientWorld;)V")
	public void onHeadJoinWorld(ClientWorld world, CallbackInfo ci) {
		if (this.world != null) {
			ClientWorldEvents.UNLOAD.invoker().onWorldUnload((MinecraftClient) (Object) this, this.world);
		}
	}

	//https://github.com/MinecraftForge/MinecraftForge/blob/a95b968c1397a1597d4a6228d8e1abce6495570e/patches/minecraft/net/minecraft/client/Minecraft.java.patch#L381
	@Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
	public void onDisconnect(Screen screen, CallbackInfo ci) {
//		ClientWorldEvents.UNLOAD.invoker().onWorldUnload((MinecraftClient) (Object) this, this.world);
	}
}
