package com.smellypengu.createfabric.events;

import com.smellypengu.createfabric.Create;
import com.smellypengu.createfabric.foundation.command.AllCommands;
import com.smellypengu.createfabric.foundation.utility.WorldAttached;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class CommonEvents {
	public static void register() {
		ServerWorldEvents.LOAD.register(CommonEvents::onLoadWorld);
		ServerWorldEvents.UNLOAD.register(CommonEvents::onUnloadWorld);
		ServerLifecycleEvents.SERVER_STARTED.register(CommonEvents::serverStarted);
	}

	public static void onLoadWorld(MinecraftServer server, ServerWorld world) {
		//Create.redstoneLinkNetworkHandler.onLoadWorld(world);
		Create.torquePropagator.onLoadWorld(world);
	}

	public static void onUnloadWorld(MinecraftServer server, ServerWorld world) {
		//Create.redstoneLinkNetworkHandler.onUnloadWorld(world);
		Create.torquePropagator.onUnloadWorld(world);
		WorldAttached.invalidateWorld(world);
	}

	public static void serverStarted(MinecraftServer minecraftServer) {
		AllCommands.register(minecraftServer.getCommandManager().getDispatcher());
	}
}
