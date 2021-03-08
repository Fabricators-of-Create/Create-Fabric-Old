package com.simibubi.create.content.schematics.client;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.io.IOUtils;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllKeys;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.schematics.ClientSchematicLoader;
import com.simibubi.create.content.schematics.packet.InstantSchematicPacket;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.RaycastHelper;
import com.simibubi.create.foundation.utility.RaycastHelper.PredicateTraceResult;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.foundation.utility.outliner.Outliner;

import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.structure.Structure;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class SchematicAndQuillHandler {

	private Object outlineSlot = new Object();

	private BlockPos firstPos;
	private BlockPos secondPos;
	private BlockPos selectedPos;
	private Direction selectedFace;
	private int range = 10;

	public boolean mouseScrolled(double delta) {
		if (!isActive())
			return false;
		if (!AllKeys.ctrlDown())
			return false;
		if (secondPos == null)
			range = (int) MathHelper.clamp(range + delta, 1, 100);
		if (selectedFace == null)
			return true;

		Box bb = new Box(firstPos, secondPos);
		Vec3i vec = selectedFace.getVector();
		Vec3d projectedView = MinecraftClient.getInstance().gameRenderer.getCamera()
			.getPos();
		if (bb.contains(projectedView))
			delta *= -1;

		int x = (int) (vec.getX() * delta);
		int y = (int) (vec.getY() * delta);
		int z = (int) (vec.getZ() * delta);

		AxisDirection axisDirection = selectedFace.getDirection();
		if (axisDirection == AxisDirection.NEGATIVE)
			bb = bb.offset(-x, -y, -z);

		double maxX = Math.max(bb.maxX - x * axisDirection.offset(), bb.minX);
		double maxY = Math.max(bb.maxY - y * axisDirection.offset(), bb.minY);
		double maxZ = Math.max(bb.maxZ - z * axisDirection.offset(), bb.minZ);
		bb = new Box(bb.minX, bb.minY, bb.minZ, maxX, maxY, maxZ);

		firstPos = new BlockPos(bb.minX, bb.minY, bb.minZ);
		secondPos = new BlockPos(bb.maxX, bb.maxY, bb.maxZ);
		Lang.sendStatus(MinecraftClient.getInstance().player, "schematicAndQuill.dimensions", (int) bb.getXLength() + 1,
			(int) bb.getYLength() + 1, (int) bb.getZLength() + 1);

		return true;
	}

	public void onMouseInput(int button, boolean pressed) {
		if (!pressed || button != 1)
			return;
		if (!isActive())
			return;

		ClientPlayerEntity player = MinecraftClient.getInstance().player;

		if (player.isSneaking()) {
			discard();
			return;
		}

		if (secondPos != null) {
			ScreenOpener.open(new SchematicPromptScreen());
			return;
		}

		if (selectedPos == null) {
			Lang.sendStatus(player, "schematicAndQuill.noTarget");
			return;
		}

		if (firstPos != null) {
			secondPos = selectedPos;
			Lang.sendStatus(player, "schematicAndQuill.secondPos");
			return;
		}

		firstPos = selectedPos;
		Lang.sendStatus(player, "schematicAndQuill.firstPos");
	}
	
	public void discard() {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		firstPos = null;
		secondPos = null;
		Lang.sendStatus(player, "schematicAndQuill.abort");
	}

	public void tick() {
		if (!isActive())
			return;

		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (AllKeys.ACTIVATE_TOOL.isPressed()) {
			float pt = AnimationTickHolder.getPartialTicks();
			Vec3d targetVec = player.getCameraPosVec(pt)
				.add(player.getRotationVector()
					.multiply(range));
			selectedPos = new BlockPos(targetVec);

		} else {
			BlockHitResult trace = RaycastHelper.rayTraceRange(player.world, player, 75);
			if (trace != null && trace.getType() == Type.BLOCK) {

				BlockPos hit = trace.getBlockPos();
				boolean replaceable = player.world.getBlockState(hit)
					.canReplace(new ItemPlacementContext(new ItemUsageContext(player, Hand.MAIN_HAND, trace)));
				if (trace.getSide()
					.getAxis()
					.isVertical() && !replaceable)
					hit = hit.offset(trace.getSide());
				selectedPos = hit;
			} else
				selectedPos = null;
		}

		selectedFace = null;
		if (secondPos != null) {
			Box bb = new Box(firstPos, secondPos).stretch(1, 1, 1)
				.expand(.45f);
			Vec3d projectedView = MinecraftClient.getInstance().gameRenderer.getCamera()
				.getPos();
			boolean inside = bb.contains(projectedView);
			PredicateTraceResult result =
				RaycastHelper.rayTraceUntil(player, 70, pos -> inside ^ bb.contains(VecHelper.getCenterOf(pos)));
			selectedFace = result.missed() ? null
				: inside ? result.getFacing()
					.getOpposite() : result.getFacing();
		}

		Box currentSelectionBox = getCurrentSelectionBox();
		if (currentSelectionBox != null)
			outliner().chaseAABB(outlineSlot, currentSelectionBox)
				.colored(0x6886c5)
				.withFaceTextures(AllSpecialTextures.CHECKERED, AllSpecialTextures.HIGHLIGHT_CHECKERED)
				.lineWidth(1 / 16f)
				.highlightFace(selectedFace);
	}

	private Box getCurrentSelectionBox() {
		if (secondPos == null) {
			if (firstPos == null)
				return selectedPos == null ? null : new Box(selectedPos);
			return selectedPos == null ? new Box(firstPos)
				: new Box(firstPos, selectedPos).stretch(1, 1, 1);
		}
		return new Box(firstPos, secondPos).stretch(1, 1, 1);
	}

	private boolean isActive() {
		return isPresent() && AllItems.SCHEMATIC_AND_QUILL.isIn(MinecraftClient.getInstance().player.getMainHandStack());
	}

	private boolean isPresent() {
		return MinecraftClient.getInstance() != null && MinecraftClient.getInstance().world != null
			&& MinecraftClient.getInstance().currentScreen == null;
	}

	public void saveSchematic(String string, boolean convertImmediately) {
		Structure t = new Structure();
		BlockBox bb = new BlockBox(firstPos, secondPos);
		BlockPos origin = new BlockPos(bb.minX, bb.minY, bb.minZ);
		BlockPos bounds = new BlockPos(bb.getBlockCountX(), bb.getBlockCountY(), bb.getBlockCountZ());

		t.saveFromWorld(MinecraftClient.getInstance().world, origin, bounds, true, Blocks.AIR);

		if (string.isEmpty())
			string = Lang.translate("schematicAndQuill.fallbackName").getString();

		String folderPath = "schematics";
		FilesHelper.createFolderIfMissing(folderPath);
		String filename = FilesHelper.findFirstValidFilename(string, folderPath, "nbt");
		String filepath = folderPath + "/" + filename;

		Path path = Paths.get(filepath);
		OutputStream outputStream = null;
		try {
			outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE);
			CompoundTag nbttagcompound = t.toTag(new CompoundTag());
			NbtIo.writeCompressed(nbttagcompound, outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null)
				IOUtils.closeQuietly(outputStream);
		}
		firstPos = null;
		secondPos = null;
		Lang.sendStatus(MinecraftClient.getInstance().player, "schematicAndQuill.saved", filepath);

		if (!convertImmediately)
			return;
		if (!Files.exists(path)) {
			Create.logger.fatal("Missing Schematic file: " + path.toString());
			return;
		}
		try {
			if (!ClientSchematicLoader.validateSizeLimitation(Files.size(path)))
				return;
			AllPackets.channel.sendToServer(new InstantSchematicPacket(filename, origin, bounds));

		} catch (IOException e) {
			Create.logger.fatal("Error finding Schematic file: " + path.toString());
			e.printStackTrace();
			return;
		}
	}

	private Outliner outliner() {
		return CreateClient.outliner;
	}

}