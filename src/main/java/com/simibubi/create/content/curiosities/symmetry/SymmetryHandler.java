package com.simibubi.create.content.curiosities.symmetry;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.curiosities.symmetry.mirror.EmptyMirror;
import com.simibubi.create.content.curiosities.symmetry.mirror.SymmetryMirror;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class SymmetryHandler {

	private static int tickCounter = 0;

	public static void onBlockPlaced(PlayerEntity player, World world, Hand hand, Entity entity, HitResult hitResult) {
		if (world
			.isClient())
			return;

		PlayerInventory inv = player.inventory;
		for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
			if (!inv.getStack(i)
				.isEmpty()
				&& inv.getStack(i)
					.getItem() == AllItems.WAND_OF_SYMMETRY) {
				SymmetryWandItem.apply(player.world, inv.getStack(i), player, entity.getBlockPos(),
					player.getBlockState());
			}
		}
	}

	public static void onBlockDestroyed(World world, PlayerEntity player, BlockPos blockPos, BlockState blockState, BlockEntity entity) {
		if (world
			.isClient())
			return;

		PlayerInventory inv = player.inventory;
		for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
			if (!inv.getStack(i)
				.isEmpty() && inv.getStack(i).isItemEqualIgnoreDamage(AllItems.WAND_OF_SYMMETRY.getDefaultStack())) {
				SymmetryWandItem.remove(world, inv.getStack(i), player, blockPos);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static void render(WorldRenderContext context) {
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayerEntity player = mc.player;

		for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
			ItemStack stackInSlot = player.inventory.getStack(i);
			if (!stackInSlot.isItemEqualIgnoreDamage(AllItems.WAND_OF_SYMMETRY.getDefaultStack()))
				continue;
			if (!SymmetryWandItem.isEnabled(stackInSlot))
				continue;
			SymmetryMirror mirror = SymmetryWandItem.getMirror(stackInSlot);
			if (mirror instanceof EmptyMirror)
				continue;

			BlockPos pos = new BlockPos(mirror.getPosition());

			float yShift = 0;
			double speed = 1 / 16d;
			yShift = MathHelper.sin((float) (AnimationTickHolder.getRenderTick() * speed)) / 5f;

			VertexConsumerProvider.Immediate buffer = MinecraftClient.getInstance()
				.getBufferBuilders()
				.getEntityVertexConsumers();
			Camera info = mc.gameRenderer.getCamera();
			Vec3d view = info.getPos();

			MatrixStack ms = context.matrixStack();
			ms.push();
			ms.translate(-view.getX(), -view.getY(), -view.getZ());
			ms.translate(pos.getX(), pos.getY(), pos.getZ());
			ms.translate(0, yShift + .2f, 0);
			mirror.applyModelTransform(ms);
			BakedModel model = mirror.getModel()
				.get();
			VertexConsumer builder = buffer.getBuffer(RenderLayer.getSolid());

			mc.getBlockRenderManager()
				.getModelRenderer()
				.render(player.world, model, Blocks.AIR.getDefaultState(), pos, ms, builder, true,
					player.world.getRandom(), MathHelper.hashCode(pos), OverlayTexture.DEFAULT_UV);

			buffer.draw();
			ms.pop();
		}
	}

	@Environment(EnvType.CLIENT)
	public static void onClientTick(MinecraftClient mc) {
		ClientPlayerEntity player = mc.player;

		if (mc.world == null)
			return;
		if (mc.isPaused())
			return;

		tickCounter++;

		if (tickCounter % 10 == 0) {
			for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
				ItemStack stackInSlot = player.inventory.getStack(i);

				if (stackInSlot != null && stackInSlot.isItemEqualIgnoreDamage(AllItems.WAND_OF_SYMMETRY.getDefaultStack())
					&& SymmetryWandItem.isEnabled(stackInSlot)) {

					SymmetryMirror mirror = SymmetryWandItem.getMirror(stackInSlot);
					if (mirror instanceof EmptyMirror)
						continue;

					Random r = new Random();
					double offsetX = (r.nextDouble() - 0.5) * 0.3;
					double offsetZ = (r.nextDouble() - 0.5) * 0.3;

					Vec3d pos = mirror.getPosition()
						.add(0.5 + offsetX, 1 / 4d, 0.5 + offsetZ);
					Vec3d speed = new Vec3d(0, r.nextDouble() * 1 / 8f, 0);
					mc.world.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, speed.x, speed.y, speed.z);
				}
			}
		}

	}

	public static void drawEffect(BlockPos from, BlockPos to) {
		double density = 0.8f;
		Vec3d start = Vec3d.of(from).add(0.5, 0.5, 0.5);
		Vec3d end = Vec3d.of(to).add(0.5, 0.5, 0.5);
		Vec3d diff = end.subtract(start);

		Vec3d step = diff.normalize()
			.multiply(density);
		int steps = (int) (diff.length() / step.length());

		Random r = new Random();
		for (int i = 3; i < steps - 1; i++) {
			Vec3d pos = start.add(step.multiply(i));
			Vec3d speed = new Vec3d(0, r.nextDouble() * -40f, 0);

			MinecraftClient.getInstance().world.addParticle(new DustParticleEffect(1, 1, 1, 1), pos.x, pos.y, pos.z,
				speed.x, speed.y, speed.z);
		}

		Vec3d speed = new Vec3d(0, r.nextDouble() * 1 / 32f, 0);
		Vec3d pos = start.add(step.multiply(2));
		MinecraftClient.getInstance().world.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, speed.x, speed.y,
			speed.z);

		speed = new Vec3d(0, r.nextDouble() * 1 / 32f, 0);
		pos = start.add(step.multiply(steps));
		MinecraftClient.getInstance().world.addParticle(ParticleTypes.END_ROD, pos.x, pos.y, pos.z, speed.x, speed.y,
			speed.z);
	}

}
