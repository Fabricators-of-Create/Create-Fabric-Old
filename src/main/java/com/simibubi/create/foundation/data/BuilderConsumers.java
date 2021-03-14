package com.simibubi.create.foundation.data;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.components.crank.ValveHandleBlock;
import com.simibubi.create.foundation.config.StressConfigDefaults;
import com.simibubi.create.foundation.item.TooltipHelper;

import me.pepperbell.reghelper.BlockRegBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.DyeColor;

public class BuilderConsumers {
	public static <B extends Block> Consumer<BlockRegBuilder<B>> cuckooClock() {
		return b -> b.initialProperties(SharedProperties::wooden)
//			.blockstate((c, p) -> p.horizontalBlock(c.get(), p.models()
//				.getExistingFile(p.modLoc("block/cuckoo_clock/block"))))
			.addLayer(() -> RenderLayer::getCutoutMipped)
			.consume(StressConfigDefaults.impactConsumer(1.0))
			.item()
//			.transform(ModelGen.customItemModel("cuckoo_clock", "item"));
			.build();
	}

//	public static <B extends EncasedShaftBlock> Consumer<BlockHelper<B>> encasedShaft(String casing,
//		CTSpriteShiftEntry casingShift) {
//		return builder -> builder.initialProperties(SharedProperties::stone)
//			.properties(Block.Properties::nonOpaque)
//			.onRegister(CreateRegistrate.connectedTextures(new EncasedCTBehaviour(casingShift)))
//			.onRegister(CreateRegistrate.casingConnectivity(
//				(block, cc) -> cc.make(block, casingShift, (s, f) -> f.getAxis() != s.get(EncasedShaftBlock.AXIS))))
//			.blockstate((c, p) -> axisBlock(c, p, blockState -> p.models()
//				.getExistingFile(p.modLoc("block/encased_shaft/block_" + casing)), true))
//			.transform(StressConfigDefaults.setNoImpact())
//			.loot((p, b) -> p.addDrop(b, AllBlocks.SHAFT.get()))
//			.item()
//			.model(AssetLookup.customItemModel("encased_shaft", "item_" + casing))
//			.build();
//	}

	public static <B extends ValveHandleBlock> Consumer<BlockRegBuilder<B>> valveHandle(
		@Nullable DyeColor color) {
		return b -> b.initialProperties(SharedProperties::softMetal)
//			.blockstate((c, p) -> {
//				String variant = color == null ? "copper" : color.asString();
//				p.directionalBlock(c.get(), p.models()
//					.withExistingParent(variant + "_valve_handle", p.modLoc("block/valve_handle"))
//					.texture("3", p.modLoc("block/valve_handle/valve_handle_" + variant)));
//			})
			.onRegisterItem(item -> {
				if (color != null)
					TooltipHelper.referTo(item, () -> AllBlocks.COPPER_VALVE_HANDLE);
			})
//			.tag(AllBlockTags.BRITTLE.tag, AllBlockTags.VALVE_HANDLES.tag)
			.item()
//			.tag(AllItemTags.VALVE_HANDLES.tag)
			.build();
	}

//	public static <B extends CasingBlock> Consumer<BlockHelper<B>> casing(
//		CTSpriteShiftEntry ct) {
//		return b -> b.initialProperties(SharedProperties::stone)
//			.blockstate((c, p) -> p.simpleBlock(c.get()))
//			.onRegister(connectedTextures(new EncasedCTBehaviour(ct)))
//			.onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, ct)))
//			.simpleItem();
//	}

//	public static <B extends FunnelBlock> Consumer<BlockHelper<B>> funnel(String type,
//		Identifier particleTexture) {
//		return b -> {
//			return b.blockstate((c, p) -> {
//				Function<BlockState, ModelFile> model = s -> {
//					String powered = s.get(FunnelBlock.POWERED) ? "_powered" : "";
//					String extracting = s.get(FunnelBlock.EXTRACTING) ? "_push" : "_pull";
//					String face = s.get(FunnelBlock.FACE)
//						.asString();
//					return p.models()
//						.withExistingParent("block/" + type + "_funnel_" + face + extracting + powered,
//							p.modLoc("block/funnel/block_" + face))
//						.texture("particle", particleTexture)
//						.texture("7", p.modLoc("block/" + type + "_funnel_plating"))
//						.texture("6", p.modLoc("block/" + type + "_funnel" + powered))
//						.texture("5", p.modLoc("block/" + type + "_funnel_tall" + powered))
//						.texture("2_2", p.modLoc("block/" + type + "_funnel" + extracting))
//						.texture("3", p.modLoc("block/" + type + "_funnel_back"));
//				};
//				p.horizontalFaceBlock(c.get(), model);
//			})
//				.item(FunnelItem::new)
//				.model((c, p) -> {
//					p.withExistingParent("item/" + type + "_funnel", p.modLoc("block/funnel/item"))
//						.texture("particle", particleTexture)
//						.texture("7", p.modLoc("block/" + type + "_funnel_plating"))
//						.texture("2", p.modLoc("block/" + type + "_funnel_neutral"))
//						.texture("6", p.modLoc("block/" + type + "_funnel"))
//						.texture("5", p.modLoc("block/" + type + "_funnel_tall"))
//						.texture("3", p.modLoc("block/" + type + "_funnel_back"));
//				})
//				.build();
//		};
//	}

//	public static <B extends BeltTunnelBlock> Consumer<BlockHelper<B>> beltTunnel(
//		String type, Identifier particleTexture) {
//		return b -> b.initialProperties(SharedProperties::stone)
//			.addLayer(() -> RenderLayer::getCutoutMipped)
//			.properties(Block.Properties::nonOpaque)
//			.blockstate((c, p) -> p.getVariantBuilder(c.get())
//				.forAllStates(state -> {
//					String id = "block/" + type + "_tunnel";
//					Shape shape = state.get(BeltTunnelBlock.SHAPE);
//					if (shape == BeltTunnelBlock.Shape.CLOSED)
//						shape = BeltTunnelBlock.Shape.STRAIGHT;
//					String shapeName = shape.asString();
//					return ConfiguredModel.builder()
//						.modelFile(p.models()
//							.withExistingParent(id + "/" + shapeName, p.modLoc("block/belt_tunnel/" + shapeName))
//							.texture("1", p.modLoc(id + "_top"))
//							.texture("2", p.modLoc(id))
//							.texture("3", p.modLoc(id + "_top_window"))
//							.texture("particle", particleTexture))
//						.rotationY(state.get(BeltTunnelBlock.HORIZONTAL_AXIS) == Axis.X ? 0 : 90)
//						.build();
//				}))
//			.item(BeltTunnelItem::new)
//			.model((c, p) -> {
//				String id = type + "_tunnel";
//				p.withExistingParent("item/" + id, p.modLoc("block/belt_tunnel/item"))
//					.texture("1", p.modLoc("block/" + id + "_top"))
//					.texture("2", p.modLoc("block/" + id))
//					.texture("particle", particleTexture);
//			})
//			.build();
//	}

	public static <B extends Block> Consumer<BlockRegBuilder<B>> mechanicalPiston(PistonType type) {
		return b -> b.initialProperties(SharedProperties::stone)
			.properties(p -> p.nonOpaque())
//			.blockstate(new MechanicalPistonGenerator(type)::generate)
			.addLayer(() -> RenderLayer::getCutoutMipped)
			.consume(StressConfigDefaults.impactConsumer(4.0))
			.onRegisterItem(item -> TooltipHelper.referTo(item, "block.create.mechanical_piston"))
			.item()
//			.transform(ModelGen.customItemModel("mechanical_piston", type.asString(), "item"));
			.build();
	}

	public static <B extends Block> Consumer<BlockRegBuilder<B>> bearing(String prefix,
		String backTexture, boolean woodenTop) {
//		Identifier baseBlockModelLocation = Create.asResource("block/bearing/block");
//		Identifier baseItemModelLocation = Create.asResource("block/bearing/item");
//		Identifier topTextureLocation = Create.asResource("block/bearing_top" + (woodenTop ? "_wooden" : ""));
//		Identifier nookTextureLocation =
//			Create.asResource("block/" + (woodenTop ? "andesite" : "brass") + "_casing");
//		Identifier sideTextureLocation = Create.asResource("block/" + prefix + "_bearing_side");
//		Identifier backTextureLocation = Create.asResource("block/" + backTexture);
		return b -> b.initialProperties(SharedProperties::stone)
			.properties(p -> p.nonOpaque())
//			.blockstate((c, p) -> p.directionalBlock(c.get(), p.models()
//				.withExistingParent(c.getName(), baseBlockModelLocation)
//				.texture("side", sideTextureLocation)
//				.texture("nook", nookTextureLocation)
//				.texture("back", backTextureLocation)))
			.item()
//			.model((c, p) -> p.withExistingParent(c.getName(), baseItemModelLocation)
//				.texture("top", topTextureLocation)
//				.texture("side", sideTextureLocation)
//				.texture("back", backTextureLocation))
			.build();
	}

	public static <B extends Block> Consumer<BlockRegBuilder<B>> crate(String type) {
		return b -> b.initialProperties(SharedProperties::stone)
//			.blockstate((c, p) -> {
//				String[] variants = { "single", "top", "bottom", "left", "right" };
//				Map<String, ModelFile> models = new HashMap<>();
//
//				Identifier crate = p.modLoc("block/crate_" + type);
//				Identifier side = p.modLoc("block/crate_" + type + "_side");
//				Identifier casing = p.modLoc("block/" + type + "_casing");
//
//				for (String variant : variants)
//					models.put(variant, p.models()
//						.withExistingParent("block/crate/" + type + "/" + variant, p.modLoc("block/crate/" + variant))
//						.texture("crate", crate)
//						.texture("side", side)
//						.texture("casing", casing));
//
//				p.getVariantBuilder(c.get())
//					.forAllStates(state -> {
//						String variant = "single";
//						int yRot = 0;
//
//						if (state.get(CrateBlock.DOUBLE)) {
//							Direction direction = state.get(CrateBlock.FACING);
//							if (direction.getAxis() == Axis.X)
//								yRot = 90;
//
//							switch (direction) {
//							case DOWN:
//								variant = "top";
//								break;
//							case NORTH:
//							case EAST:
//								variant = "right";
//								break;
//							case UP:
//								variant = "bottom";
//								break;
//							case SOUTH:
//							case WEST:
//							default:
//								variant = "left";
//
//							}
//						}
//
//						return ConfiguredModel.builder()
//							.modelFile(models.get(variant))
//							.rotationY(yRot)
//							.build();
//					});
//			})
			.item()
//			.transform(ModelGen.customItemModel("crate", type, "single"));
			.build();
	}
}
