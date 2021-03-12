package me.pepperbell.simplenetworking;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class SimpleChannel {
	private static final Logger LOGGER = LogManager.getLogger("Simple Networking API");

	private final Identifier channelName;
	private final BiMap<Integer, Class<?>> c2sIdMap = HashBiMap.create();
	private final BiMap<Integer, Class<?>> s2cIdMap = HashBiMap.create();
	private C2SHandler c2sHandler;
	private S2CHandler s2cHandler;

	public SimpleChannel(Identifier channelName) {
		this.channelName = channelName;
	}

	public void initServerListener() {
		c2sHandler = new C2SHandler();
		ServerPlayNetworking.registerGlobalReceiver(channelName, c2sHandler);
	}

	@Environment(EnvType.CLIENT)
	public void initClientListener() {
		s2cHandler = new S2CHandler();
		ClientPlayNetworking.registerGlobalReceiver(channelName, s2cHandler);
	}

	/**
	 * The registered class <b>must</b> have a nullary constructor or else an error will be thrown.
	 *
	 * <p>A nullary constructor is one that has no arguments and in this case should not do anything.
	 * For example, if the class name is {@code ExamplePacket}, the public nullary constructor would be <code>public ExamplePacket() {}</code>.
	 * The visibility of this constructor does not matter for this method.
	 */
	public <T extends C2SPacket> void registerC2SPacket(Class<T> clazz, int id) {
		c2sIdMap.put(id, clazz);
	}

	/**
	 * The registered class <b>must</b> have a nullary constructor or else an error will be thrown.
	 *
	 * <p>A nullary constructor is one that has no arguments and in this case should not do anything.
	 * For example, if the class name is {@code ExamplePacket}, the public nullary constructor would be <code>public ExamplePacket() {}</code>.
	 * The visibility of this constructor does not matter for this method.
	 */
	public <T extends S2CPacket> void registerS2CPacket(Class<T> clazz, int id) {
		s2cIdMap.put(id, clazz);
	}

	private PacketByteBuf createBuf(C2SPacket packet) {
		Integer id = c2sIdMap.inverse().get(packet.getClass());
		if (id == null) {
			LOGGER.error("Could not get id for c2s packet " + packet.toString() + " in channel " + channelName);
			return null;
		}
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(id);
		packet.write(buf);
		return buf;
	}

	private PacketByteBuf createBuf(S2CPacket packet) {
		Integer id = s2cIdMap.inverse().get(packet.getClass());
		if (id == null) {
			LOGGER.error("Could not get id for s2c packet " + packet.toString() + " in channel " + channelName);
			return null;
		}
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(id);
		packet.write(buf);
		return buf;
	}

	@Environment(EnvType.CLIENT)
	public void sendToServer(C2SPacket packet) {
		PacketByteBuf buf = createBuf(packet);
		if (buf == null) return;
		ClientPlayNetworking.send(channelName, buf);
	}

	public void sendToClient(S2CPacket packet, ServerPlayerEntity player) {
		PacketByteBuf buf = createBuf(packet);
		if (buf == null) return;
		ServerPlayNetworking.send(player, channelName, buf);
	}

	public void sendToClients(S2CPacket packet, Collection<ServerPlayerEntity> players) {
		PacketByteBuf buf = createBuf(packet);
		if (buf == null) return;
		for (ServerPlayerEntity player : players) {
			ServerPlayNetworking.send(player, channelName, buf);
		}
	}

	public void sendToClientsInServer(S2CPacket packet, MinecraftServer server) {
		sendToClients(packet, PlayerLookup.all(server));
	}

	public void sendToClientsInWorld(S2CPacket packet, ServerWorld world) {
		sendToClients(packet, PlayerLookup.world(world));
	}

	public void sendToClientsTracking(S2CPacket packet, ServerWorld world, BlockPos pos) {
		sendToClients(packet, PlayerLookup.tracking(world, pos));
	}

	public void sendToClientsTracking(S2CPacket packet, ServerWorld world, ChunkPos pos) {
		sendToClients(packet, PlayerLookup.tracking(world, pos));
	}

	public void sendToClientsTracking(S2CPacket packet, Entity entity) {
		sendToClients(packet, PlayerLookup.tracking(entity));
	}

	public void sendToClientsTracking(S2CPacket packet, BlockEntity blockEntity) {
		sendToClients(packet, PlayerLookup.tracking(blockEntity));
	}

	public void sendToClientsAround(S2CPacket packet, ServerWorld world, Vec3d pos, double radius) {
		sendToClients(packet, PlayerLookup.around(world, pos, radius));
	}

	public void sendToClientsAround(S2CPacket packet, ServerWorld world, Vec3i pos, double radius) {
		sendToClients(packet, PlayerLookup.around(world, pos, radius));
	}

	@Environment(EnvType.CLIENT)
	public void sendResponseToServer(ResponseTarget target, C2SPacket packet) {
		PacketByteBuf buf = createBuf(packet);
		if (buf == null) return;
		target.sender.sendPacket(channelName, buf);
	}

	public void sendResponseToClient(ResponseTarget target, S2CPacket packet) {
		PacketByteBuf buf = createBuf(packet);
		if (buf == null) return;
		target.sender.sendPacket(channelName, buf);
	}

	public static class ResponseTarget {
		private final PacketSender sender;

		private ResponseTarget(PacketSender sender) {
			this.sender = sender;
		}
	}

	private class C2SHandler implements ServerPlayNetworking.PlayChannelHandler {
		@Override
		public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
			int id = buf.readVarInt();
			C2SPacket packet = null;
			try {
				Class<?> clazz = c2sIdMap.get(id);
				Constructor<?> ctor = clazz.getDeclaredConstructor();
				ctor.setAccessible(true);
				packet = (C2SPacket) ctor.newInstance();
			} catch (Exception e) {
				LOGGER.error("Could not create c2s packet in channel " + channelName + " with id " + id, e);
			}
			if (packet != null) {
				packet.read(buf);
				packet.handle(server, player, handler, new ResponseTarget(responseSender));
			}
		}
	}

	@Environment(EnvType.CLIENT)
	private class S2CHandler implements ClientPlayNetworking.PlayChannelHandler {
		@Override
		public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
			int id = buf.readVarInt();
			S2CPacket packet = null;
			try {
				Class<?> clazz = s2cIdMap.get(id);
				Constructor<?> ctor = clazz.getDeclaredConstructor();
				ctor.setAccessible(true);
				packet = (S2CPacket) ctor.newInstance();
			} catch (Exception e) {
				LOGGER.error("Could not create s2c packet in channel " + channelName + " with id " + id, e);
			}
			if (packet != null) {
				packet.read(buf);
				packet.handle(client, handler, new ResponseTarget(responseSender));
			}
		}
	}
}
