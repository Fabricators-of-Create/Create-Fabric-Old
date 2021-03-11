package com.simibubi.create.content.contraptions.relays.belt;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class BeltModel implements UnbakedModel, BakedModel, FabricBakedModel {

	SpriteShiftEntry spriteShift = AllSpriteShifts.ANDESIDE_BELT_CASING;
	private Mesh mesh;

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		context.meshConsumer().accept(mesh);
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {

	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return null;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean hasDepth() {
		return false;
	}

	@Override
	public boolean isSideLit() {
		return false;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getSprite() {
		return spriteShift.getTarget();
	}

	@Override
	public ModelTransformation getTransformation() {
		return null;
	}

	@Override
	public ModelOverrideList getOverrides() {
		return null;
	}

	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
		return null;
	}

	@Nullable
	@Override
	public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {

		Renderer renderer = RendererAccess.INSTANCE.getRenderer();
		MeshBuilder builder = renderer.meshBuilder();
		QuadEmitter emitter = builder.getEmitter();
		System.out.println("bake - this doesn't get printed for some reason");

		for (Direction direction : Direction.values()) {
			if (spriteShift == null)
				continue;
			/**if (quad.getSprite() != spriteShift.getOriginal())
			 continue;*/

			Sprite target = spriteShift.getTarget();

			emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
			emitter.spriteBake(0, target, MutableQuadView.BAKE_LOCK_UV);

			emitter.spriteColor(0, -1, -1, -1, -1);

			emitter.emit();
		}
		mesh = builder.build();

		return this;
	}

	/**@Override public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, ModelData extraData) {
	List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, rand, extraData));
	if (!extraData.hasProperty(CASING_PROPERTY))
	return quads;
	CasingType type = extraData.getData(CASING_PROPERTY);
	if (type == CasingType.NONE || type == CasingType.BRASS)
	return quads;

	SpriteShiftEntry spriteShift = AllSpriteShifts.ANDESIDE_BELT_CASING;

	for (int i = 0; i < quads.size(); i++) {
	BakedQuad quad = quads.get(i);
	if (spriteShift == null)
	continue;
	if (quad.getSprite() != spriteShift.getOriginal())
	continue;

	TextureAtlasSprite original = quad.getSprite();
	TextureAtlasSprite target = spriteShift.getTarget();
	BakedQuad newQuad = new BakedQuad(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length),
	quad.getTintIndex(), quad.getFace(), target, quad.shouldApplyDiffuseLighting());

	VertexFormat format = DefaultVertexFormats.BLOCK;
	int[] vertexData = newQuad.getVertexData();

	for (int vertex = 0; vertex < vertexData.length; vertex += format.getIntegerSize()) {
	int uvOffset = 16 / 4;
	int uIndex = vertex + uvOffset;
	int vIndex = vertex + uvOffset + 1;
	float u = Float.intBitsToFloat(vertexData[uIndex]);
	float v = Float.intBitsToFloat(vertexData[vIndex]);
	vertexData[uIndex] =
	Float.floatToRawIntBits(target.getInterpolatedU((SuperByteBuffer.getUnInterpolatedU(original, u))));
	vertexData[vIndex] =
	Float.floatToRawIntBits(target.getInterpolatedV((SuperByteBuffer.getUnInterpolatedV(original, v))));
	}

	quads.set(i, newQuad);
	}
	return quads;
	}*/

}
