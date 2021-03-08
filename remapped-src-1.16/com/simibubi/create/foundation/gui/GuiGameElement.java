package com.simibubi.create.foundation.gui;

import javax.annotation.Nullable;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.utility.ColorHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.VirtualEmptyModelData;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fluids.FluidStack;

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
		double xBeforeScale, yBeforeScale, zBeforeScale = 0;
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

		public GuiRenderBuilder at(double x, double y, double z) {
			this.xBeforeScale = x;
			this.yBeforeScale = y;
			this.zBeforeScale = z;
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
				.withRotationOffset(VecHelper.getCenterOf(BlockPos.ORIGIN));
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

		public abstract void render(MatrixStack matrixStack);

		@Deprecated
		protected void prepare() {}

		protected void prepareMatrix(MatrixStack matrixStack) {
			matrixStack.push();
			RenderSystem.enableBlend();
			RenderSystem.enableRescaleNormal();
			RenderSystem.enableAlphaTest();
			DiffuseLighting.enableGuiDepthLighting();
			RenderSystem.alphaFunc(516, 0.1F);
			RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		@Deprecated
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

		protected void transformMatrix(MatrixStack matrixStack) {
			matrixStack.translate(xBeforeScale, yBeforeScale, zBeforeScale);
			matrixStack.scale((float) scale, (float) scale, (float) scale);
			matrixStack.translate(x, y, z);
			matrixStack.scale(1, -1, 1);
			matrixStack.translate(rotationOffset.x, rotationOffset.y, rotationOffset.z);
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float) zRot));
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((float) xRot));
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) yRot));
			matrixStack.translate(-rotationOffset.x, -rotationOffset.y, -rotationOffset.z);
		}

		@Deprecated
		protected void cleanUp() {}

		protected void cleanUpMatrix(MatrixStack matrixStack) {
			matrixStack.pop();
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
		public void render(MatrixStack matrixStack) {
			prepareMatrix(matrixStack);

			MinecraftClient mc = MinecraftClient.getInstance();
			BlockRenderManager blockRenderer = mc.getBlockRenderManager();
			VertexConsumerProvider.Immediate buffer = mc.getBufferBuilders()
				.getEntityVertexConsumers();
			RenderLayer renderType = blockState.getBlock() == Blocks.AIR ? TexturedRenderLayers.getEntityTranslucentCull()
				: RenderLayers.getEntityBlockLayer(blockState, true);
			VertexConsumer vb = buffer.getBuffer(renderType);

			transformMatrix(matrixStack);

			mc.getTextureManager()
				.bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
			renderModel(blockRenderer, buffer, renderType, vb, matrixStack);

			cleanUpMatrix(matrixStack);
		}

		protected void renderModel(BlockRenderManager blockRenderer, VertexConsumerProvider.Immediate buffer,
			RenderLayer renderType, VertexConsumer vb, MatrixStack ms) {
			int color = MinecraftClient.getInstance().getBlockColors().getColor(blockState, null, null, 0);
			Vec3d rgb = ColorHelper.getRGB(color == -1 ? this.color : color);
			blockRenderer.getModelRenderer()
				.renderModel(ms.peek(), vb, blockState, blockmodel, (float) rgb.x, (float) rgb.y, (float) rgb.z,
					0xF000F0, OverlayTexture.DEFAULT_UV, VirtualEmptyModelData.INSTANCE);
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
				blockRenderer.renderBlock(blockState, ms, buffer, 0xF000F0, OverlayTexture.DEFAULT_UV,
					VirtualEmptyModelData.INSTANCE);
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
			FluidRenderer.renderTiledFluidBB(new FluidStack(blockState.getFluidState()
				.getFluid(), 1000), 0, 0, 0, 1.0001f, 1.0001f, 1.0001f, buffer, ms, 0xf000f0, true);
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
		public void render(MatrixStack matrixStack) {
			prepareMatrix(matrixStack);
//			matrixStack.translate(0, 80, 0);
			transformMatrix(matrixStack);
			renderItemIntoGUI(matrixStack, stack);
			cleanUpMatrix(matrixStack);
		}
		/*
		public void render() {
			prepare();
			transform();
			RenderSystem.scaled(1, -1, 1);
			RenderSystem.translated(0, 0, -75);
			Minecraft.getInstance()
				.getItemRenderer()
				.renderItemIntoGUI(stack, 0, 0);
			cleanUp();
			}
		 */

		public static void renderItemIntoGUI(MatrixStack matrixStack, ItemStack stack) {
			ItemRenderer renderer = MinecraftClient.getInstance()
				.getItemRenderer();
			BakedModel bakedModel = renderer.getHeldItemModel(stack, null, null);
			matrixStack.push();
			renderer.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
			renderer.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
				.setFilter(false, false);
			RenderSystem.enableRescaleNormal();
			RenderSystem.enableAlphaTest();
			RenderSystem.defaultAlphaFunc();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA,
				GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			matrixStack.translate((float) 0, (float) 0, 100.0F + renderer.zOffset);
			matrixStack.translate(8.0F, 8.0F, 0.0F);
			matrixStack.scale(16.0F, 16.0F, 16.0F);
			VertexConsumerProvider.Immediate irendertypebuffer$impl = MinecraftClient.getInstance()
				.getBufferBuilders()
				.getEntityVertexConsumers();
			boolean flag = !bakedModel.isSideLit();
			if (flag) {
				DiffuseLighting.disableGuiDepthLighting();
			}

			renderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack,
				irendertypebuffer$impl, 15728880, OverlayTexture.DEFAULT_UV, bakedModel);
			irendertypebuffer$impl.draw();
			RenderSystem.enableDepthTest();
			if (flag) {
				DiffuseLighting.enableGuiDepthLighting();
			}

			RenderSystem.disableAlphaTest();
			RenderSystem.disableRescaleNormal();
			RenderSystem.enableCull();
			matrixStack.pop();
		}

	}

}
