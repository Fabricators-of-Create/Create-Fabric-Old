package com.simibubi.create.content.schematics.item;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.schematics.SchematicProcessor;
import com.simibubi.create.content.schematics.client.SchematicEditScreen;
import com.simibubi.create.content.schematics.filtering.SchematicInstances;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.utility.Lang;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.PositionTracker;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

public class SchematicItem extends Item {

	private static final Logger LOGGER = LogManager.getLogger();

	public SchematicItem(Settings properties) {
		super(properties.maxCount(1));
	}

	public static ItemStack create(String schematic, String owner) {
		ItemStack blueprint = AllItems.SCHEMATIC.asStack();

		CompoundTag tag = new CompoundTag();
		tag.putBoolean("Deployed", false);
		tag.putString("Owner", owner);
		tag.putString("File", schematic);
		tag.put("Anchor", NbtHelper.fromBlockPos(BlockPos.ORIGIN));
		tag.putString("Rotation", BlockRotation.NONE.name());
		tag.putString("Mirror", BlockMirror.NONE.name());
		blueprint.setTag(tag);

		writeSize(blueprint);
		return blueprint;
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
		if (stack.hasTag()) {
			if (stack.getTag()
				.contains("File"))
				tooltip.add(new LiteralText(Formatting.GOLD + stack.getTag()
					.getString("File")));
		} else {
			tooltip.add(Lang.translate("schematic.invalid").formatted(Formatting.RED));
		}
		super.appendTooltip(stack, worldIn, tooltip, flagIn);
	}

	public static void writeSize(ItemStack blueprint) {
		CompoundTag tag = blueprint.getTag();
		Structure t = loadSchematic(blueprint);
		tag.put("Bounds", NbtHelper.fromBlockPos(t.getSize()));
		blueprint.setTag(tag);
		SchematicInstances.clearHash(blueprint);
	}

	public static StructurePlacementData getSettings(ItemStack blueprint) {
		CompoundTag tag = blueprint.getTag();
		StructurePlacementData settings = new StructurePlacementData();
		settings.setRotation(BlockRotation.valueOf(tag.getString("Rotation")));
		settings.setMirror(BlockMirror.valueOf(tag.getString("Mirror")));
		settings.addProcessor(SchematicProcessor.INSTANCE);
		return settings;
	}

	public static Structure loadSchematic(ItemStack blueprint) {
		Structure t = new Structure();
		String owner = blueprint.getTag()
			.getString("Owner");
		String schematic = blueprint.getTag()
			.getString("File");

		if (!schematic.endsWith(".nbt"))
			return t;

		Path dir;
		Path file;

		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
			dir = Paths.get("schematics", "uploaded").toAbsolutePath();
			file = Paths.get(owner, schematic);
		} else {
			dir = Paths.get("schematics").toAbsolutePath();
			file = Paths.get(schematic);
		}

		Path path = dir.resolve(file).normalize();
		if (!path.startsWith(dir))
			return t;

		try (DataInputStream stream = new DataInputStream(new BufferedInputStream(
				new GZIPInputStream(Files.newInputStream(path, StandardOpenOption.READ))))) {
			CompoundTag nbt = NbtIo.read(stream, new PositionTracker(0x20000000L));
			t.fromTag(nbt);
		} catch (IOException e) {
			LOGGER.warn("Failed to read schematic", e);
		}

		return t;
	}

	@Nonnull
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		if (context.getPlayer() != null && !onItemUse(context.getPlayer(), context.getHand()))
			return super.useOnBlock(context);
		return ActionResult.SUCCESS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!onItemUse(playerIn, handIn))
			return super.use(worldIn, playerIn, handIn);
		return new TypedActionResult<>(ActionResult.SUCCESS, playerIn.getStackInHand(handIn));
	}

	private boolean onItemUse(PlayerEntity player, Hand hand) {
		if (!player.isSneaking() || hand != Hand.MAIN_HAND)
			return false;
		if (!player.getStackInHand(hand)
			.hasTag())
			return false;
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::displayBlueprintScreen);
		return true;
	}

	@Environment(EnvType.CLIENT)
	protected void displayBlueprintScreen() {
		ScreenOpener.open(new SchematicEditScreen());
	}

}
