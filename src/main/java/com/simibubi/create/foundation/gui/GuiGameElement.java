package com.simibubi.create.foundation.gui;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.foundation.utility.ColorHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GuiGameElement {

	public static GuiRenderBuilder of(ItemStack stack) {
		return new GuiItemRenderBuilder(stack);
	}

	public static GuiRenderBuilder of(ItemConvertible itemProvider) {
		return new GuiItemRenderBuilder(itemProvider);
	}

	public static GuiRenderBuilder of(BlockState state) {
		return new GuiBlockStateRenderBuilder(state);
	}

	public static GuiRenderBuilder of(AllBlockPartials partial) {
		return new GuiBlockPartialRenderBuilder(partial);
	}

	public static GuiRenderBuilder of(Fluid fluid) {
		return new GuiBlockStateRenderBuilder(fluid.getDefaultState()
			.getBlockState()
			.with(FluidBlock.LEVEL, 0));
	}

	public static abstract class GuiRenderBuilder {
		double xBeforeScale, yBeforeScale;
		double x, y, z;
		double xRot, yRot, zRot;
		double scale = 1;
		int color = 0xFFFFFF;
		Vec3d rotationOffset = Vec3d.ZERO;

		public GuiRenderBuilder atLocal(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
			return this;
		}

		public GuiRenderBuilder at(double x, double y) {
			this.xBeforeScale = x;
			this.yBeforeScale = y;
			return this;
		}

		public GuiRenderBuilder rotate(double xRot, double yRot, double zRot) {
			this.xRot = xRot;
			this.yRot = yRot;
			this.zRot = zRot;
			return this;
		}

		public GuiRenderBuilder rotateBlock(double xRot, double yRot, double zRot) {
			return this.rotate(xRot, yRot, zRot)
				.withRotationOffset(VecHelper.getCenterOf(BlockPos.ZERO));
		}

		public GuiRenderBuilder scale(double scale) {
			this.scale = scale;
			return this;
		}

		public GuiRenderBuilder color(int color) {
			this.color = color;
			return this;
		}

		public GuiRenderBuilder withRotationOffset(Vec3d offset) {
			this.rotationOffset = offset;
			return this;
		}

		public abstract void render();

		protected void prepare() {
			RenderSystem.pushMatrix();
			RenderSystem.enableBlend();
			RenderSystem.enableRescaleNormal();
			RenderSystem.enableAlphaTest();
			DiffuseLighting.enableGuiDepthLighting();
			RenderSystem.alphaFunc(516, 0.1F);
			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		protected void transform() {
			RenderSystem.translated(xBeforeScale, yBeforeScale, 0);
			RenderSystem.scaled(scale, scale, scale);
			RenderSystem.translated(x, y, z);
			RenderSystem.scaled(1, -1, 1);
			RenderSystem.translated(rotationOffset.x, rotationOffset.y, rotationOffset.z);
			RenderSystem.rotatef((float) zRot, 0, 0, 1);
			RenderSystem.rotatef((float) xRot, 1, 0, 0);
			RenderSystem.rotatef((float) yRot, 0, 1, 0);
			RenderSystem.translated(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
		}

		protected void cleanUp() {
			RenderSystem.popMatrix();
			RenderSystem.disableAlphaTest();
			RenderSystem.disableRescaleNormal();
		}
	}

	private static class GuiBlockModelRenderBuilder extends GuiRenderBuilder {

		protected BakedModel blockmodel;
		protected BlockState blockState;

		public GuiBlockModelRenderBuilder(BakedModel blockmodel, @Nullable BlockState blockState) {
			this.blockState = blockState == null ? Blocks.AIR.getDefaultState() : blockState;
			this.blockmodel = blockmodel;
		}

		@Override
		public void render() {
			prepare();

			MinecraftClient mc = MinecraftClient.getInstance();
			BlockRenderManager blockRenderer = mc.getBlockRenderManager();
			VertexConsumerProvider.Immediate buffer = mc.getBufferBuilders()
				.getEntityVertexConsumers();
			RenderLayer renderType = /**blockState.getBlock() == Blocks.AIR ? Atlases.getEntityTranslucent()
			 :*/RenderLayers.getEntityBlockLayer(blockState, false);
			VertexConsumer vb = buffer.getBuffer(renderType);
			MatrixStack ms = new MatrixStack();

			transform();

			mc.getTextureManager()
				.bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
			renderModel(blockRenderer, buffer, renderType, vb, ms);

			cleanUp();
		}

		protected void renderModel(BlockRenderManager blockRenderer, VertexConsumerProvider.Immediate buffer,
								   RenderLayer renderType, VertexConsumer vb, MatrixStack ms) {
			int color = MinecraftClient.getInstance().getBlockColors().getColor(blockState, null, null, 0);
			Vector3f rgb = ColorHelper.getRGB(color == -1 ? this.color : color);
			blockRenderer.getModelRenderer()
				.render(ms.peek(), vb, blockState, blockmodel, rgb.getX(), rgb.getY(), rgb.getZ(),
					0xF000F0, OverlayTexture.DEFAULT_UV);
			buffer.draw();
		}
	}

	public static class GuiBlockStateRenderBuilder extends GuiBlockModelRenderBuilder {

		public GuiBlockStateRenderBuilder(BlockState blockstate) {
			super(MinecraftClient.getInstance()
				.getBlockRenderManager()
				.getModel(blockstate), blockstate);
		}

		@Override
		protected void renderModel(BlockRenderManager blockRenderer, VertexConsumerProvider.Immediate buffer,
								   RenderLayer renderType, VertexConsumer vb, MatrixStack ms) {
			if (blockState.getBlock() instanceof FireBlock) {
		 		DiffuseLighting.disableGuiDepthLighting();
		 		blockRenderer.renderBlockAsEntity(blockState, ms, buffer, 0xF000F0, OverlayTexture.DEFAULT_UV);
		 		DiffuseLighting.enable();
		 		buffer.draw();
		 		return;
		 	}

			super.renderModel(blockRenderer, buffer, renderType, vb, ms);

			if (blockState.getFluidState()
				.isEmpty())
				return;

			RenderSystem.pushMatrix();
			DiffuseLighting.disable();
			/*FluidRenderer.renderTiledFluidBB(new FluidStack(blockState.getFluidState()
			 .getFluid(), 1000), 0, 0, 0, 1.0001f, 1.0001f, 1.0001f, buffer, ms, 0xf000f0, true);*/
			buffer.draw(RenderLayer.getTranslucent());
			DiffuseLighting.enable();
			RenderSystem.popMatrix();
		}
	}

	public static class GuiBlockPartialRenderBuilder extends GuiBlockModelRenderBuilder {

		public GuiBlockPartialRenderBuilder(AllBlockPartials partial) {
			super(partial.get(), null);
		}

	}

	public static class GuiItemRenderBuilder extends GuiRenderBuilder {

		private final ItemStack stack;

		public GuiItemRenderBuilder(ItemStack stack) {
			this.stack = stack;
		}

		public GuiItemRenderBuilder(ItemConvertible provider) {
			this(new ItemStack(provider));
		}

		@Override
		public void render() {
			prepare();
			transform();
			RenderSystem.scaled(1, -1, 1);
			RenderSystem.translated(0, 0, -75);
			MinecraftClient.getInstance()
				.getItemRenderer()
				.renderGuiItemIcon(stack, 0, 0);
			cleanUp();
		}

	}

}
