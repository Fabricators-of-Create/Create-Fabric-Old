package com.simibubi.create.foundation.renderState;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllSpecialTextures;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class RenderTypes extends RenderPhase {

	protected static final RenderPhase.Cull DISABLE_CULLING = new NoCullState();

	public static RenderLayer getOutlineTranslucent(Identifier texture, boolean cull) {
		RenderLayer.MultiPhaseParameters rendertype$state = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(texture, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.cull(cull ? ENABLE_CULLING : DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return RenderLayer.of("outline_translucent" + (cull ? "_cull" : ""),
			VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, rendertype$state);
	}

	private static final RenderLayer OUTLINE_SOLID =
		RenderLayer.of("outline_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true,
			false, RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(AllSpecialTextures.BLANK.getLocation(), false, false))
				.transparency(NO_TRANSPARENCY)
				.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(true));

	public static RenderLayer getGlowingSolid(Identifier texture) {
		RenderLayer.MultiPhaseParameters rendertype$state = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(texture, false, false))
			.transparency(NO_TRANSPARENCY)
			.diffuseLighting(DISABLE_DIFFUSE_LIGHTING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return RenderLayer.of("glowing_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256,
			true, false, rendertype$state);
	}

	public static RenderLayer getGlowingTranslucent(Identifier texture) {
		RenderLayer.MultiPhaseParameters rendertype$state = RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(texture, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.diffuseLighting(DISABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true);
		return RenderLayer.of("glowing_translucent", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7,
			256, true, true, rendertype$state);
	}

	private static final RenderLayer GLOWING_SOLID = RenderTypes.getGlowingSolid(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
	private static final RenderLayer GLOWING_TRANSLUCENT =
		RenderTypes.getGlowingTranslucent(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

	private static final RenderLayer ITEM_PARTIAL_SOLID =
		RenderLayer.of("item_solid", VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true,
			false, RenderLayer.MultiPhaseParameters.builder()
				.texture(new RenderPhase.Texture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, false, false))
				.transparency(NO_TRANSPARENCY)
				.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
				.lightmap(ENABLE_LIGHTMAP)
				.overlay(ENABLE_OVERLAY_COLOR)
				.build(true));

	private static final RenderLayer ITEM_PARTIAL_TRANSLUCENT = RenderLayer.of("entity_translucent",
		VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, 7, 256, true, true, RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, false, false))
			.transparency(TRANSLUCENT_TRANSPARENCY)
			.diffuseLighting(ENABLE_DIFFUSE_LIGHTING)
			.alpha(ONE_TENTH_ALPHA)
			.cull(DISABLE_CULLING)
			.lightmap(ENABLE_LIGHTMAP)
			.overlay(ENABLE_OVERLAY_COLOR)
			.build(true));

	public static RenderLayer getItemPartialSolid() {
		return ITEM_PARTIAL_SOLID;
	}
	
	public static RenderLayer getItemPartialTranslucent() {
		return ITEM_PARTIAL_TRANSLUCENT;
	}

	public static RenderLayer getOutlineSolid() {
		return OUTLINE_SOLID;
	}

	public static RenderLayer getGlowingSolid() {
		return GLOWING_SOLID;
	}

	public static RenderLayer getGlowingTranslucent() {
		return GLOWING_TRANSLUCENT;
	}

	protected static class NoCullState extends RenderPhase.Cull {
		public NoCullState() {
			super(false);
		}

		@Override
		public void startDrawing() {
			RenderSystem.disableCull();
		}
	}

	// Mmm gimme those protected fields
	public RenderTypes() {
		super(null, null, null);
	}
}
