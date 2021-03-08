package com.simibubi.create.content.contraptions.components.structureMovement.train;

import java.util.Random;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class CouplingHandlerClient {

	static AbstractMinecartEntity selectedCart;
	static Random r = new Random();

	public static void tick() {
		if (selectedCart == null)
			return;
		spawnSelectionParticles(selectedCart.getBoundingBox(), false);
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		ItemStack heldItemMainhand = player.getMainHandStack();
		ItemStack heldItemOffhand = player.getOffHandStack();
		if (AllItems.MINECART_COUPLING.isIn(heldItemMainhand) || AllItems.MINECART_COUPLING.isIn(heldItemOffhand))
			return;
		selectedCart = null;
	}

	static void onCartClicked(PlayerEntity player, AbstractMinecartEntity entity) {
		if (MinecraftClient.getInstance().player != player)
			return;
		if (selectedCart == null || selectedCart == entity) {
			selectedCart = entity;
			spawnSelectionParticles(selectedCart.getBoundingBox(), true);
			return;
		}
		spawnSelectionParticles(entity.getBoundingBox(), true);
		AllPackets.channel.sendToServer(new CouplingCreationPacket(selectedCart, entity));
		selectedCart = null;
	}

	static void sneakClick() {
		selectedCart = null;
	}

	private static void spawnSelectionParticles(Box axisAlignedBB, boolean highlight) {
		ClientWorld world = MinecraftClient.getInstance().world;
		Vec3d center = axisAlignedBB.getCenter();
		int amount = highlight ? 100 : 2;
		ParticleEffect particleData = highlight ? ParticleTypes.END_ROD : new DustParticleEffect(1, 1, 1, 1);
		for (int i = 0; i < amount; i++) {
			Vec3d v = VecHelper.offsetRandomly(Vec3d.ZERO, r, 1);
			double yOffset = v.y;
			v = v.multiply(1, 0, 1)
				.normalize()
				.add(0, yOffset / 8f, 0)
				.add(center);
			world.addParticle(particleData, v.x, v.y, v.z, 0, 0, 0);
		}
	}

}
